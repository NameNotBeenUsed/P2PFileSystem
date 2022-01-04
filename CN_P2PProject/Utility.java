import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Utility {
	public static byte[] ifInterested(final byte[] arr1, final byte[] arr2) {
		int len = arr1.length;
		byte[] interested = new byte[len];

		for(int i = 0; i < len; i++) {
			int compare = ~arr2[i];
			interested[i] = (byte) (arr1[i] & compare);
		}
		System.out.println("interested:"+Arrays.toString(interested));
		return interested;
	}
	public static boolean isInterested(final byte[] arr1, final byte[] arr2) {
		byte[] msg=ifInterested(arr1, arr2);
		for(byte b:msg){
			if(b==(byte)0x01){
				return true;
			}
		}
		return false;
	}
	public static byte fillLastBitfield(int num) {
		int sum = 0;
        for (int i = 1; i < num + 1; i++) {
            sum += Math.pow(2, 8 - i);
        }
        return (byte)sum;
	}
	public static int getBit(byte index, int position) {
		return (index >> position) & 1;
	}
	
	public static byte[] initBitfield(boolean full, int numOfPieces) {
		byte[] bitfield;
		if(numOfPieces%8 != 0) {
			bitfield = new byte[numOfPieces/8 + 1];
			if(full) {
				Arrays.fill(bitfield, 0, bitfield.length - 1, (byte)0xff);
				bitfield[bitfield.length - 1] = fillLastBitfield(numOfPieces % 8);
			}
			else {
				Arrays.fill(bitfield, (byte)0x00);
			}
		}
		else {
			bitfield = new byte[numOfPieces/8];
			if(full) {
				Arrays.fill(bitfield, (byte)0xff);
			}
			else {
				Arrays.fill(bitfield, (byte)0x00);
			}
		}
		
		return bitfield;
	}
	
	public static byte[] returnFullbitfield(int numOfPieces) {
		int length = 0;
		if(numOfPieces%8 == 0){
			length = numOfPieces/8;
		}
		else{
			length = numOfPieces/8 + 1;
		}
		byte[] fullbitfield = new byte[length];
		int remainder = numOfPieces % 8;
		Arrays.fill(fullbitfield, 0, fullbitfield.length - 1, (byte)0xff);
		if(remainder != 0) {
			fullbitfield[length - 1] = fillLastBitfield(remainder);
		}
		else {
			fullbitfield[length - 1] = -1;
		}
		return fullbitfield;
	}
	
	public static byte[] returnEmptybitfield(int numOfPieces) {
		int length = 0;
		if(numOfPieces%8 == 0){
			length = numOfPieces%8;
		}
		else{
			length = numOfPieces%8 + 1;
		}
		byte[] emptybitfield = new byte[length];
		Arrays.fill(emptybitfield, (byte)0x00);
		return emptybitfield;
	}

	//create a new directory and file if they do not exist, and then write the file
	public static  void writeFile(int peerId, FilePieces allPieces){
		boolean success = false;
		String dirPath = "./" + String.format("/peer_%s/",peerId);
		String fileName = "tree.jpg";
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

		File f = new File(dirPath + fileName);
		if(f.exists()){
			f.delete();
		}
		try {
			success = f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(success){
			System.out.println("Create a file successfully.");
		}
		else{
			System.out.println("Failed to create a file.");
		}

		try {
			FileOutputStream fos = new FileOutputStream(f, true);
			for(int i = 0; i < allPieces.getNumOfPieces(); i++){
				fos.write(allPieces.getPieces(i));
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
