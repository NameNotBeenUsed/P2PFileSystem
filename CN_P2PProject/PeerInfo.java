import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.FileReader;
import java.io.BufferedReader;

public class PeerInfo {
	private Map<Integer, String[]> peer_info = new HashMap<>();
	
	public PeerInfo() {
		String file_name = "PeerInfo.cfg";
		readConfig(file_name);
	}
	
	private void readConfig(String file_name) {
		String line;
		try {
			FileReader fr = new FileReader(file_name);
			BufferedReader br = new BufferedReader(fr);
			
			while((line = br.readLine()) != null) {
				//each line: peerId host_name port has_file_or_not
				String[] parsed_peer_cfg = line.split(" ");
				int peer_id = Integer.parseInt(parsed_peer_cfg[0]);
				String[] cfg = Arrays.copyOfRange(parsed_peer_cfg, 1, parsed_peer_cfg.length);
				this.peer_info.put(peer_id, cfg);
			}
			br.close();
		}
		catch(FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void printInfo() {
		for(Integer peerId : this.peer_info.keySet()) {
			System.out.format("PeerId: %s, Hostname: %s, Port Number: %s, Has file or not: %s.\n", 
					peerId, this.peer_info.get(peerId)[0], this.peer_info.get(peerId)[1], this.peer_info.get(peerId)[2]);
		}
	}
	
	public String getHostName(int peerId) {
		return this.peer_info.get(peerId)[0];
	}
	
	public int getPortNumber(int peerId) {
		return Integer.parseInt(this.peer_info.get(peerId)[1]);
	}
	
	public boolean getHasFileOrNot(int peerId) {
		return this.peer_info.get(peerId)[2].equals("1");
	}
	
	public void setHasCompleteFile(int peerId) {
		String[] peer_cfg = this.peer_info.get(peerId);
		peer_cfg[2] = "1";
		this.peer_info.put(peerId, peer_cfg);
		return;
	}
	
	public int getSize() {
		return this.peer_info.size();
	}
	
	public Set<Integer> getPeerIds(){
		return this.peer_info.keySet();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("current User's path:"+System.getProperty("user.dir"));
		//read peerInfo
		PeerInfo test = new PeerInfo();
		test.printInfo();
	}

}
