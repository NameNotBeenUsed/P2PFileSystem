
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Map;
import java.util.Timer;

public class PeerProcess {
	private PeerInfo peerinfo = new PeerInfo();
	private int peerId;
	private String hostName;
	private int portNumber;
	private boolean hasFile;
	private int numOfPeers;

	private Common commonVars = new Common();
	private int numOfPieces;
	private int pieceSize;
	private static byte[][] bitFields;

	//who is interested in me
	private static Set<Integer> ifInterested;
	//Is it necessary to save who I am interested in?

	private boolean complete;
	//private byte[][] data;
	private static FilePieces filedata;

	private static Map<Integer, Handler> conn= new HashMap<>();
	private Logger eventLogger;
	private static int id;
	public Set<Integer> otherPeersID = new HashSet<Integer>();

	public PeerProcess(int _peerId) {

		System.out.println("peerId:"+_peerId);
		eventLogger = new Logger(_peerId);
		this.peerId = _peerId;
		id=_peerId-1001;
		System.out.println("==read peerinfo==");
		this.peerinfo.printInfo();
		this.hostName = peerinfo.getHostName(_peerId);
		this.portNumber = peerinfo.getPortNumber(_peerId);
		this.hasFile = peerinfo.getHasFileOrNot(_peerId);
		this.numOfPeers = peerinfo.getSize();
		System.out.println("hostName:"+hostName);
		System.out.println("hasFile:"+hasFile);
		for (int i = 1001;i < 1001+numOfPeers; i++) {
			otherPeersID.add(i);
		}
		System.out.println("NumOfOtherPeer"+otherPeersID.size());
		System.out.println("==comVars==");
		this.commonVars.printCommonVar();
		this.numOfPieces = commonVars.getNumOfPieces();
		this.pieceSize = commonVars.getPieceSize();
		System.out.println("pieces:"+numOfPieces);
		System.out.println("pieceSize:"+pieceSize);
		this.bitFields = new byte[this.numOfPeers][commonVars.getBitLength()];
		this.ifInterested = new HashSet<>();
		this.complete = false;
		//this.data = new byte[this.numOfPieces][this.pieceSize];
		this.filedata = new FilePieces(this.hasFile);
	}
	/**
	 * A handler thread class.  Handlers are spawned from the listening
	 * loop and are responsible for dealing with a single client's requests.
	 */
	public class Handler extends Thread {

		private String message;    //message received from the client
		private String MESSAGE;    //uppercase message send to the client
		private Socket connection;
		private ObjectInputStream stringIn;	//stream read from the socket
		private ObjectOutputStream stringOut;    //stream write to the socket
		private InputStream in;	//stream read from the socket
		private OutputStream out;    //stream write to the socket
		private int no;		//The index number of the client
		public int totalBytesRead;

		//static FilePieces test = new FilePieces(true);
		public boolean isChokingClient = false;
		public Handler(Socket connection, int no) {
			System.out.println("no:"+no);
			this.connection = connection;
			this.no = no;
			this.totalBytesRead = 0;
		}

		public void unchoke() throws IOException {
			this.isChokingClient = false;
			byte[] msg=Message.createActualMessage((byte) 1);
			System.out.println("unchoke at 90: ");
			sendMessage(msg);
		}

		public void choke() throws IOException {
			this.isChokingClient = true;
			System.out.println("choke at 100: ");
			byte[] msg=Message.createActualMessage((byte) 0);
			sendMessage(msg);
		}
		public void removeID(int i) {
			otherPeersID.remove(i);
		}
		@Override
		public void run() {
			try{
				//initialize Input and Output streams
				in=connection.getInputStream();
				out =connection.getOutputStream();
				out.flush();
				stringOut = new ObjectOutputStream(out);
				stringOut.flush();
				stringIn = new ObjectInputStream(in);
				String send="P2PFILESHARINGPROJ0000000000"+peerId;
				System.out.println("send:"+send);
				sendMessage((send).getBytes());//handshake

				System.out.println("handles");
				while(true)
				{
					//receive the message sent from the client
					byte[] msg=null;
					byte[] len=new byte[4];
					int read_len = in.read(len);
					if(read_len != 4) {
						continue;
					}
					int type=in.read();
					byte typ=(byte)type;
					System.out.println("Received type:"+type);
					//System.out.println(Integer.toBinaryString(typ & 255 | 256).substring(1));
					if(type<8){
						int int_len=Message.byteArrayToInt(len);
						System.out.println("type:"+type);
						System.out.println("Get int length at 135: " + int_len);
						msg=new byte[int_len-1];
						in.read(msg);
						//System.out.println("msgLen:"+msg.length);
					}else{
						msg=new byte[27];
						in.read(msg);
					}
					switch(type){
						case 0:
							System.out.println(no+"this is case 0");
							eventLogger.receiveChoke(no);
							this.isChokingClient = true;
							break;
						case 1:
							System.out.println(no+"this is case 1");
							this.isChokingClient = false;
							eventLogger.receiveUnchoke(no);
							sendRequest();
							break;
						case 2 :
							System.out.println(no+"this is case 2");
							ifInterested.add(this.no);
							eventLogger.receiveInterested(no);
							break;
						case 3 :
							System.out.println(no+"this is case 3");
							ifInterested.remove(this.no);
							eventLogger.receiveNotInterested(no);
							break;
						case 4 :
							//recieve have,read index in piece
							//update bitfield
							System.out.println(no+"this is case 4");

							//if(isCompleted()) return;
							int pId4 = updateBitField(Message.readMsgPayload(msg),no-1001);
							//System.out.println("Receive ‘have’ message and update related bitfield.");
							if(isInterested(bitFields[no-1001])){
								sendMessage(Message.createActualMessage((byte) 2));
							}else{
								sendMessage(Message.createActualMessage((byte) 3));
							}
							eventLogger.receiveHave(no, pId4);
							break;
						case 5 :
							System.out.println(no+"this is case 5");
							System.out.println("Receive bitfield message from peer " + no);
							bitFields[no-1001] = Message.readMsgPayload(msg);
//							System.out.println("no:"+bitFields[no-1001][0]);
//							System.out.println("mine:"+bitFields[id][0]);
							if(isInterested(msg)){
								sendMessage(Message.createActualMessage((byte) 2));
							}else{
								sendMessage(Message.createActualMessage((byte) 3));
							}
							break;
						case 6 :
							System.out.println(no+"this is case 6");
							byte[] byte_pId6 = Message.readMsgPayload(msg);
							int pId6 = Message.byteArrayToInt(byte_pId6);
							eventLogger.receiveRequest(no, pId6);
							//receive a request
							if(!this.isChokingClient){
								sendPiece(msg);
							}

							break;
						case 7 :
							//recieve pieces,update bitfield about itself, send have to others
							System.out.println(no+"this is case 7");
							byte[] receivedPiecePayload = Message.readMsgPayload(msg);
							//get piece's data:
							//byte[] receivedPiece = test.getPieces(3);//?
							byte[] receivedPiece = new byte[receivedPiecePayload.length-4];
							System.arraycopy(receivedPiecePayload,4,receivedPiece,0,receivedPiecePayload.length-4);
							//get piece's index
							byte[] pieceIndex = new byte[4];
							System.arraycopy(receivedPiecePayload,0,pieceIndex,0,4);
							int pId7 = updateBitField(pieceIndex,id);
							//save piece
							filedata.putPieces(Message.byteArrayToInt(pieceIndex),receivedPiece);
							eventLogger.downloadPiece(no, pId7, filedata.getNumOfPieces());
							for(int i = 0;i<conn.size()+1;i++) {
								//why not 4?
								if(i + 1001 != peerId) {
									conn.get(i + 1001).out.write(Message.createActualMessage((byte)4, pieceIndex));
								}
								else {
									continue;
								}
							}

							sendRequest();
							//if(isCompleted()) return;
							break;
						default :
							if(handShake(Message.byteMerger(len, typ,msg))){
								System.out.println("Handshake succeeded.");
								System.out.println(bitFields[id].length);
								sendMessage(Message.createActualMessage((byte) 5,bitFields[id]));
								System.out.println("Send bitfield message to peer " + no);
							}
					}

				}

			}
			catch(IOException ioException){
				System.out.println("IO/Disconnect with Client " + no);
			}
			finally{
				//Close connections
				try{
					in.close();
					out.close();
					connection.close();
				}
				catch(IOException ioException){
					System.out.println("close/Disconnect with Client " + no);
				}
			}
		}
		boolean isTargetPeer(int i) {
			//System.out.println("istargetpeer");
			if(otherPeersID.contains(i)) {
				return true;
			}
			return false;
		}
		boolean handShake(byte[] msg){
			String s = new String(msg);
			System.out.println("Receive Handshake message: " + s);
			int otherPeerID = Integer.parseInt(s.substring(s.length()-4));
			System.out.println("other:"+otherPeerID);
			//unfinished?
			if(isTargetPeer(otherPeerID)) {
				this.removeID(otherPeerID);
				System.out.println("Shake hand with "+otherPeerID);
				return true;
			}
			return false;
		}
		boolean isCompleted(){
			int complete=0;
			for(int i=0;i<numOfPeers;i++){
				if(Arrays.equals(Utility.returnFullbitfield(numOfPieces), bitFields[i])){
					complete++;
				}
			}
			if(complete==numOfPeers){
				return true;
			}
			return false;
		}
		boolean isInterested(byte[] bitField){
			byte[] interested=Utility.ifInterested(bitField,bitFields[id]);
			//??intersted unfinished?
			for(byte b : interested) {
				for(int i = 7; i >= 0; i--) {
					if(Utility.getBit(b, i) == 1) {
						System.out.println("Interested");
						return true;
					}
				}
			}
			System.out.println("Not Interested");
			return false;
		}
		//send a message to the output stream
		public void sendMessage(byte[] msg)
		{
			try{
				out.write(msg);
				out.flush();
				System.out.println("Send message: " + msg + " to Client " + no);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
		//        public byte[] recieveMessage()
//        {
//            byte[] msg=null;
//            try{
//                byte[] len=new byte[4]; //¶à³¤
//                in.read(len);
//                int int_len=Message.byteArrayToInt(len);
//                msg=new byte[int_len];
//                in.read(msg);
//                return msg;
//            }
//            catch(IOException ioException){
//                ioException.printStackTrace();
//            }
//            return msg;
//        }
		// update the bitField based on the the received index read from the message
		// return the int type index of a piece from the message
		public int updateBitField(byte[] IndexByte,int who) {

			int receivedIndexInt = Message.byteArrayToInt(IndexByte);
			int i = receivedIndexInt/8;
			int j = receivedIndexInt%8;
			int temp = (int)Math.pow(2,7-j);
			bitFields[who][i] = (byte) (bitFields[who][i] | temp);
			return receivedIndexInt;
		}

		public void sendRequest() {
//			System.out.println("my:"+Arrays.toString(bitFields[id]));
			byte[] msg=Utility.ifInterested(bitFields[no-1001],bitFields[id]);
//			System.out.println("msg[0]"+msg[0]);
			for(int i=0;i<msg.length;i++) {
				if (msg[i] != 0x00) {
					for (int k = 7; k >= 0; k--) {
						if (Utility.getBit(msg[i], k) == 1) {
							int ri = i * 8 + 7 - k;
//							System.out.println("requestIndex:" + ri);
							System.out.println("Send request to peer " + no + "for piece " + ri);
							sendMessage(Message.createActualMessage((byte) 6, ri));
							return;
						}
					}
				}
			}
		}

		public void sendPiece(byte[] msg) {
			int requestPieceIndex = updateBitField(Message.readMsgPayload(msg),no-1001);
			byte[] forSendPiece = filedata.getPieces(requestPieceIndex);
			System.out.println("Send piece " + requestPieceIndex + " to peer " + no);
			sendMessage(Message.createActualMessage((byte)7,Message.byteMerger( Message.readMsgPayload(msg),forSendPiece)));
		}
	}

	public class PeerChoice implements Runnable {


		Timer timer = new Timer();
		public void run() {
//			System.out.println("Hello from a server thread!");
			TimerTask taskOptimisticUnchoking = new TimerTask() {
				@Override
				public void run() {
					Vector<Handler> chokedNeighbors  = new Vector<Handler>();
					for (Integer otherPeerId : conn.keySet()) {
						if (conn.get(otherPeerId).isChokingClient&&ifInterested.contains(otherPeerId)) {
							chokedNeighbors.add(conn.get(otherPeerId));
						}
					}
					System.out.println("OptimisticUnchoking: " + chokedNeighbors.size());
					Random rand = new Random();
					if (chokedNeighbors.size() > 0) {
						int n = Math.abs(rand.nextInt(chokedNeighbors.size()));
						try {
							chokedNeighbors.elementAt(n).unchoke();
						} catch (IOException e) {
							e.printStackTrace();
						}
						eventLogger.optimisticallyUnchokeNeighbor(chokedNeighbors.elementAt(n).no);
						System.out.println("OptimisticUnchoking: " + chokedNeighbors.elementAt(n).no);
					}
				}
			};

			//conn need totalBytesRead
			//commonVars need getUnchokingInterval();getNumberOfPrefferedNeighbors()
			TimerTask taskChoking = new TimerTask() {
				@Override
				public void run() {
					//System.out.println("Choking interval start02");
					double downloadRate = 0.0;
					Vector<DownloadRates> preferredNeighbors = new Vector<DownloadRates>();
					Vector<DownloadRates> rates = new Vector<DownloadRates>();

					for (Integer otherPeerId : conn.keySet()) {
						if(ifInterested.contains(otherPeerId)){
							downloadRate = conn.get(otherPeerId).totalBytesRead/commonVars.getUnchokingInterval();
							rates.addElement(new DownloadRates(otherPeerId, downloadRate));
							conn.get(otherPeerId).totalBytesRead = 0;
						}

					}
					Collections.sort(rates);
//					System.out.println("Size of Rates:" + rates.size());
					String listOfPrefNeighbors = "";
					int[] logN = new int[(int)commonVars.getNumberOfPreferredNeighbors()];
					if (!Arrays.equals(Utility.returnFullbitfield(numOfPieces), bitFields[id])) {
						for (int j = 0; j < commonVars.getNumberOfPreferredNeighbors(); j++) {
							if (j>rates.size()-1){
								break;
							}
							preferredNeighbors.addElement(rates.elementAt(j));
							listOfPrefNeighbors += rates.elementAt(j).peerId + " ";
							logN[j] = rates.elementAt(j).peerId;
						}
					} else {
						Random rand = new Random();
						Set<Integer> myNeighbor = new HashSet<Integer>();

						for (int j = 0; j < commonVars.getNumberOfPreferredNeighbors() && j<rates.size() ; j++) {
							int randomIndex = rand.nextInt(rates.size());
							while(myNeighbor.contains(randomIndex)){
								randomIndex = rand.nextInt(rates.size());
							}
							myNeighbor.add(randomIndex);
							preferredNeighbors.addElement(rates.elementAt(randomIndex));
							listOfPrefNeighbors += rates.elementAt(randomIndex).peerId + " ";
							logN[j] = rates.elementAt(randomIndex).peerId;
							rates.remove(randomIndex);
						}
					}
					System.out.println("List of Preferred Neighbors: " + listOfPrefNeighbors);
					eventLogger.changePreferedNeighbors(logN);


					for (Integer otherPeerId : conn.keySet()) {
						boolean unchokedNeighbor = false;
						for (int i = 0; i < preferredNeighbors.size(); i++) {
							if (conn.get(preferredNeighbors.elementAt(i).peerId) == conn.get(otherPeerId)) {
								try {
									conn.get(otherPeerId).unchoke();
								} catch (IOException e) {
									e.printStackTrace();
								}

								System.out.println("Unchoked PeerId: " + preferredNeighbors.elementAt(i).peerId);
								unchokedNeighbor = true;
								break;
							}
						}
						if (!unchokedNeighbor) {
							try {
								conn.get(otherPeerId).choke();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			};

			TimerTask isCompleted = new TimerTask() {
				@Override
				public void run() {
					//System.out.println("################Start to check");
					int complete=0;
					for(int i=0;i<numOfPeers;i++){
						//System.out.println("###########fullbitfield" + Arrays.toString(Utility.returnFullbitfield(numOfPieces)));
						//System.out.println("###########fullbitfield" + Arrays.toString(bitFields[i])+bitFields[i].length);
						if(Arrays.equals(Utility.returnFullbitfield(numOfPieces), bitFields[i])){
							complete++;
							System.out.println("###############complete" + complete);
						}
					}
					if(complete==numOfPeers){
						System.out.println("##################FINISHED##################");
						System.out.println("Every peer received the full document.");
						eventLogger.downloadComplete();
						Utility.writeFile(peerId, filedata);
						timer.cancel();
						System.exit(0);
					}else{
						//System.out.println("############wait");
					}
				}
			};
			timer.schedule(taskChoking, 0, (int)(commonVars.getUnchokingInterval() * 1000));
			timer.schedule(taskOptimisticUnchoking, 0, (int)(commonVars.getOptimisticUnchockingInterval() * 1000));
			timer.schedule(isCompleted, 0, 5000);

			for (Integer otherPeerId : conn.keySet()) {
				//System.out.println("waiting for servers to finish");
				try {
					System.out.println("Server for " + otherPeerId + " is waiting to finish.");
					if (otherPeerId != peerId) {
						conn.get(otherPeerId).join();
						//connectionThread.join
						System.out.println("Server for " + otherPeerId + " has closed.");
					}
				} catch (Exception e) {
					System.out.println("Server execution failed\n" + e + "\n");
				}
			}

		}
	}
	public void peerActions() throws IOException{
		//System.out.println("peerAction begins");
		if(hasFile==true){
			//bitFields[0] = Utility.initBitfield(true, commonVars.getNumOfPieces());
			bitFields[id] = Utility.initBitfield(true, commonVars.getNumOfPieces());
		}else{
			//bitFields[0] = Utility.initBitfield(false, commonVars.getNumOfPieces());
			bitFields[id] = Utility.initBitfield(false, commonVars.getNumOfPieces());
		}

		int i = 0;
		while(i < numOfPeers) {
			if(i != id) {
				bitFields[i] = Utility.initBitfield(false, commonVars.getNumOfPieces());
				i++;
			}
			else {
				i++;
				continue;
			}
		}

		ServerSocket listener = new ServerSocket(peerinfo.getPortNumber(peerId));
		//server start listening
		System.out.println("The server is running.");
		try {
			int serverNo = 0;
			//connect forward
			while(serverNo<id) {
				int serverId=serverNo+1001;
				Socket requestSocket = new Socket(peerinfo.getHostName(serverId), peerinfo.getPortNumber(serverId));
				System.out.println("Connected to server "+serverId);
				this.eventLogger.connectTo(serverId);
				Handler h=new Handler(requestSocket, serverId);
				conn.put(serverId,h);
				serverNo++;
				h.start();
			}

			//wait to be conn
			int clientNo = 0;
			while(clientNo<peerinfo.getSize()-id-1) {
				int clientId = peerId+clientNo+1;
				Handler h=new Handler(listener.accept(),clientId);
				conn.put(clientId,h);
				System.out.println("Connection received: " + conn.keySet());
				h.start();
				System.out.println("Client "  + clientNo + " is connected!");
				this.eventLogger.connectFrom(clientId);
				clientNo++;
				System.out.println("&&&&&&&&&&&"+clientNo);

			}
			System.out.println("------"+conn.size());
			Thread pc = new Thread(new PeerChoice());
			pc.start();
			System.out.println("pc start");
			pc.join();
			System.out.println("pc joined");
		} catch(IOException e){
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			listener.close();
		}

	}
	public static void main(String[] args) throws IOException {
		PeerProcess pp=new PeerProcess(Integer.valueOf(args[0]));
		//PeerProcess pp=new PeerProcess(Integer.valueOf(1002));
		pp.peerActions();
	}

}
