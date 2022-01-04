/*
 *                     CEN5501C Project2
 * This is the program starting remote processes.
 * This program was only tested on CISE SunOS environment.
 * If you use another environment, for example, linux environment in CISE 
 * or other environments not in CISE, it is not guaranteed to work properly.
 * It is your responsibility to adapt this program to your running environment.
 */

import java.io.*;
import java.util.Iterator;
import java.util.Set;

/*
 * The StartRemotePeers class begins remote peer processes. 
 * It reads configuration file PeerInfo.cfg and starts remote peer processes.
 * You must modify this program a little bit if your peer processes are written in C or C++.
 * Please look at the lines below the comment saying IMPORTANT.
 */
public class StartRemotePeers {

	private static PeerInfo peerinfo = new PeerInfo();

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		try {
//			new StartRemotePeers();
//			String path = System.getProperty("user.dir");
//			Set<Integer> peerIds = peerinfo.getPeerIds();
//			Iterator var4 = peerIds.iterator();
//
//			//while(var4.hasNext()) {
//			Integer id = (Integer)var4.next();
//			System.out.println("Start remote peer " + id + " at " + peerinfo.getHostName(id));
//			String var10001 = peerinfo.getHostName(id);
//			System.out.println("ssh yumingjun@" + var10001 + " cd CN_p2p; java Try2 ");
			Runtime var10000 = Runtime.getRuntime();

			Process p = var10000.exec("ssh yumingjun@thunder.cise.ufl.edu");


//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
//                bw.write("Ljy(19971029");
//                bw.flush();
			//}


			System.out.println("Starting all remote peers has done.");
		} catch (Exception var8) {
			System.out.println(var8);
		}

	}

}
