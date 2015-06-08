import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TextReader {
	//後でちゃんと読み込み部分を作る
	public static void main(String args[]){
		try {
			File readFile = new File("/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan/Contour.txt");
			FileReader fr = new FileReader(readFile);
			
			int ch;
			while((ch = fr.read() ) != -1){
				System.out.print((char)ch);
			}
			fr.close();
		}catch(FileNotFoundException e) {
			System.out.println(e);
		}catch(IOException e) {
			System.out.println(e);
		}
	}
}
