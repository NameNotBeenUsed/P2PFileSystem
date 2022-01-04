import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
//import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.ByteArrayOutputStream;

public class FilePieces {
	private Common comVar;
	private Map<Integer, byte[]> pieces;
//	public byte[] emptyByteArr;
//	public byte[] fullByteArr;

	public FilePieces(boolean hasFile) {
		this.comVar = new Common();
		this.pieces = new HashMap<>();
//		this.emptyByteArr = new byte[comVar.getNumOfPieces()];
//		this.fullByteArr = new byte[comVar.getNumOfPieces()];
//		Arrays.fill(this.emptyByteArr, (byte)0x00);
//		Arrays.fill(this.fullByteArr, (byte)0xff);
		
		if(hasFile) {
			byte[] data = {};
			try {
				data = Files.readAllBytes(Paths.get(comVar.getFileName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			for(int i = 0; i < comVar.getNumOfPieces(); i++) {
				if(i != comVar.getNumOfPieces() - 1) {
					stream.write(data, i * comVar.getPieceSize(), comVar.getPieceSize());
					pieces.put(i, stream.toByteArray());
					stream.reset();
					
				}
				else {
					stream.write(data, i * comVar.getPieceSize(), comVar.getSizeOfLastPiece());
					pieces.put(i, stream.toByteArray());
					stream.reset();
				}
			}
		}
	}
		
	public byte[] getPieces(int pieceId) {
		if(pieces.containsKey(pieceId)) {
			return pieces.get(pieceId);
		}
		else {
			return null;
		}
	}

	public void putPieces(int pieceID, byte[] data) {
		pieces.put(pieceID, data);
	}

	public int getNumOfPieces() {
		return this.pieces.size();
	}
	
	public static void main(String[] args) {
		FilePieces test = new FilePieces(true);
		System.out.println(test.getNumOfPieces());
	}
}
