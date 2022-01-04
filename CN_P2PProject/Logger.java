import java.io.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Logger {
	private int peerId;
	private String dirPath;
	private String fileName;
	private String wholePath;
	
	public Logger(int _peerId) {
		this.peerId = _peerId;
		this.dirPath = "./" + String.format("/peer_%s/",this.peerId);
		this.fileName = "log_peer_"+this.peerId+".log";
		this.wholePath = this.dirPath + this.fileName;
		boolean success = false;
		File directory = new File(dirPath);
		if(directory.exists()){
			System.out.println("Directory already exists.");
		}
		else {
			System.out.println("Directory does not exist, creating now.");
			success = directory.mkdir();
			if(success){
				System.out.println("Create directory successfully.");
			}
			else{
				System.out.println("Failed to create a directory.");
			}
		}
	}
	
	private void writeLogFile(String str) {
		try(FileWriter fw = new FileWriter(this.wholePath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);){
			out.print(str);
		}catch(IOException e) {
			e.printStackTrace();
			System.out.format("Function writeLogFile cannot write to %s\n", this.wholePath);
		}
	}
	
	public void connectTo(int otherPeerId) {
		this.writeLogFile(String.format("%s: Peer %s makes a connection to Peer %s.\n", LocalDateTime.now(), this.peerId, otherPeerId));
	}
	
	public void connectFrom(int otherPeerId) {
		this.writeLogFile(String.format("%s: Peer %s is connected from Peer %s.\n", LocalDateTime.now(), this.peerId, otherPeerId));
	}

	public void changePreferedNeighbors(int[] neighborId) {
		String peerIds = IntStream.of(neighborId)
                		 .mapToObj(Integer::toString)
                		 .collect(Collectors.joining(", "));
		this.writeLogFile(String.format("%s: Peer %s has the preferred neighbors %s.\n", LocalDateTime.now(), this.peerId, peerIds));
	}
	
	public void optimisticallyUnchokeNeighbor(int otherPeerId) {
		this.writeLogFile(String.format("%s: Peer %s has the optimistically unchoked neighbor Peer %s.\n", LocalDateTime.now(), this.peerId, otherPeerId));
	}
	
	public void receiveUnchoke(int otherPeerId) {
		this.writeLogFile(String.format("%s: Peer %s is unchoked by Peer %s.\n", LocalDateTime.now(), this.peerId, otherPeerId));
	}
	
	public void receiveChoke(int otherPeerId) {
		this.writeLogFile(String.format("%s: Peer %s is chocked by Peer %s.\n", LocalDateTime.now(), this.peerId, otherPeerId));
	}
	
	public void receiveHave(int otherPeerId, int pieceId) {
		this.writeLogFile(String.format("%s: Peer %s received the \'have\' message from Peer %s for the piece %s.\n", LocalDateTime.now(), this.peerId, otherPeerId, pieceId));
	}
	
	public void receiveInterested(int otherPeerId) {
		this.writeLogFile(String.format("%s: Peer %s received the \'interested\' message from Peer %s.\n", LocalDateTime.now(), this.peerId, otherPeerId));
	}
	
	public void receiveNotInterested(int otherPeerId) {
		this.writeLogFile(String.format("%s: Peer %s received the \'not interested\' message from Peer %s.\n", LocalDateTime.now(), this.peerId, otherPeerId));
	}

	public void receiveRequest(int otherPeerId, int pieceIndex){
		this.writeLogFile(String.format("%s: Peer %s received the \'request\' message from Peer %s. The index of the piece is %s. \n", LocalDateTime.now(), this.peerId, otherPeerId, pieceIndex));
	}
	
	public void downloadPiece(int otherPeerId, int pieceId, int numOfPieces) {
		//Ҫ����pieceId��
		this.writeLogFile(String.format("%s: Peer %s has downloaded the piece %s from Peer %s. "
				+ "Now the number of pieces it has is %d.\n", LocalDateTime.now(), this.peerId, pieceId, otherPeerId, numOfPieces));
	}
	
	public void downloadComplete() {
		this.writeLogFile(String.format("%s: Peer %s has downloaded the complete file.", LocalDateTime.now(), this.peerId));
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Logger test = new Logger(1001);
		test.connectTo(1002);
		test.connectFrom(1002);
		int [] arr = {1002,1003,1004};
		test.changePreferedNeighbors(arr);
		test.optimisticallyUnchokeNeighbor(1002);
		test.receiveChoke(1002);
		test.receiveUnchoke(1002);
		test.receiveInterested(1002);
		test.receiveNotInterested(1002);
		test.receiveHave(1002, 5);
		test.receiveInterested(1002);
		test.receiveNotInterested(1002);
		test.downloadPiece(1002, 5, 10);
		test.downloadComplete();
	}

}
