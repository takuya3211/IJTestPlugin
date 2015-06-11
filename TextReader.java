import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TextReader {
	//å„Ç≈ÇøÇ·ÇÒÇ∆ì«Ç›çûÇ›ïîï™ÇçÏÇÈ
	public static void main(String args[]){
		try {
			File readFile = new File("/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan/Contour.txt");
			FileReader fr = new FileReader(readFile);
			BufferedReader br = new BufferedReader(fr);
			System.out.println("Hello world");			
			countLines(readFile);
			
			fr.close();
		}catch(FileNotFoundException e) {
			System.out.println(e);
		}catch(IOException e) {
			System.out.println(e);
		}
	}
	
	public static int countLines(File readFile) {
		int count = 0;

		try {
			FileReader fr = new FileReader(readFile);
			BufferedReader br = new BufferedReader(fr);

			String str = br.readLine();
			while(str != null) {
				System.out.println(str);
				count ++;
				str = br.readLine();
			}
		}catch(FileNotFoundException e){
			System.out.println(e);
		}catch(IOException e) {
			System.out.println(e);
		}
		return count;
	}
}
