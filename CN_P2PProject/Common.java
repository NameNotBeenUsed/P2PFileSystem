
import java.util.Map;
import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;
//import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Common {
	private String configFileName;
	private int NumberOfPreferredNeighbors;
	private int UnchokingInterval;
	private int OptimisticUnchockingInterval;
	private String FileName;
	private int FileSize;
	private int PieceSize;
	private int NumOfPieces;
	private int SizeOfLastPiece;
	private int bitLength;
	
	public Common() {
		this.configFileName = "Common.cfg";
		setVariable();
	}
	
	private void setVariable() {
		Map<String, String> commonVar = new HashMap<>();
		String line;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(this.configFileName));
			
			while((line = bufferedReader.readLine()) != null) {
				String[] var = line.split(" ");
				commonVar.put(var[0], var[1]);
			}
			
			bufferedReader.close();
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.NumberOfPreferredNeighbors = Integer.parseInt(commonVar.get("NumberOfPreferredNeighbors"));
		this.UnchokingInterval = Integer.parseInt(commonVar.get("UnchokingInterval"));
		this.OptimisticUnchockingInterval = Integer.parseInt(commonVar.get("OptimisticUnchokingInterval"));
		this.FileName = commonVar.get("FileName");
		this.FileSize = Integer.parseInt(commonVar.get("FileSize"));
		this.PieceSize = Integer.parseInt(commonVar.get("PieceSize"));
		if(this.FileSize % this.PieceSize == 0) {
			this.NumOfPieces = this.FileSize / this.PieceSize;
			this.SizeOfLastPiece = this.PieceSize;
		}
		else {
			this.NumOfPieces = this.FileSize / this.PieceSize + 1;
			this.SizeOfLastPiece = this.FileSize - (this.NumOfPieces - 1) * this.PieceSize;
		}
		if(this.NumOfPieces%8 == 0){
			this.bitLength = this.NumOfPieces / 8;
		}
		else{
			this.bitLength = this.NumOfPieces / 8 + 1;
		}
	}
	
	public void printCommonVar() {
		System.out.println("NumberOfPreferredNeighbors: " + this.NumberOfPreferredNeighbors);
		System.out.println("UnchokingInterval: " + this.UnchokingInterval);
		System.out.println("OptimisticUnchockingInterval: " + this.OptimisticUnchockingInterval);
		System.out.println("FileName: " + this.FileName);
		System.out.println("FileSize: " + this.FileSize);
		System.out.println("PieceSize: " + this.PieceSize);
		System.out.println("Number of pieces:" + this.NumOfPieces);
		System.out.println("Size of the last piece: " + this.SizeOfLastPiece);
	}
	
	public int getNumberOfPreferredNeighbors() {
		return this.NumberOfPreferredNeighbors;
	}
	
	public int getUnchokingInterval() {
		return this.UnchokingInterval;
	}
	
	public int getOptimisticUnchockingInterval() {
		return this.OptimisticUnchockingInterval;
	}
	
	public String getFileName() {
		return this.FileName;
	}
	
	public int getFileSize() {
		return this.FileSize;
	}
	
	public int getPieceSize() {
		return this.PieceSize;
	}
	
	public int getNumOfPieces() {
		return this.NumOfPieces;
	}
	
	public int getSizeOfLastPiece() {
		return this.SizeOfLastPiece;
	}

	public int getBitLength() {return this.bitLength;}
	
	public static void main(String[] args) {
		Common test = new Common();
		test.printCommonVar();
		//System.out.println(new File(".").getAbsolutePath());
	}
}
