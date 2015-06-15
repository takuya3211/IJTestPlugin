import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.*;

public class TextReader {
	//後でちゃんと読み込み部分を作る
	public static final String ENDOFROI = "EndofROI";
	public static final String ROINAME ="ROIName";
	public static List<Integer> roiListGyou = new ArrayList<Integer>();
	public static List<String> roiNameList = new ArrayList<String>();
	public static String targetDir = "/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan2/";
	public static String targetReadFileName = "Contour.txt";
	public static String[] textLines = new String[1];
	
	public static void main (String args[]){
		TextReader tr = new TextReader();
		tr.TextReader();
	}
	public static void TextReader(){
		List<String> roiListTemp = new ArrayList<String>();
		List<String> roiNameListTemp = new ArrayList<String>();
		List<String> roiList = new ArrayList<String>();
		//System.out.println("hello world");
		
		List<String> textList = new ArrayList<String>();
		

		//File writeFile = new File(targetDir + roiNameListTemp.get(0) +  ".txt");
		//FileWriter fw = new FileWriter(writeFile);
		try {
			File readFile = new File(targetDir + targetReadFileName);
			//File writeFile;
			FileReader fr = new FileReader(readFile);
			BufferedReader br = new BufferedReader(fr);
			
			int i =0;
			int count = countLines(readFile);
			roiListGyou.add(getFirstLine(readFile));
			//System.out.println("get line is " + firstLine);
			textLines = new String[count];
			for (i =0; i < count ; i ++) {
				textLines[i] = br.readLine();
				if (textLines[i] != null ){
					if(textLines[i].indexOf(ENDOFROI) != -1){
						//System.out.println(textLines[i] + " " + i);
						roiListTemp.add(textLines[i]);
						roiListGyou.add(i);
						//roiListTempの重複を削除
						roiList = distinct(roiListTemp);
					}
					if(textLines[i].indexOf(ROINAME) != -1){
						//System.out.println(textLines[i] + " " + i);
						roiNameListTemp.add(splitROIName(textLines[i]));
						roiNameList = distinct(roiNameListTemp);//重複削除したやつ
					}
					
				}
				//if (textLines[i].indexOf("EndofROI") != -1) roiListTemp.add(textLines[i]);
				//System.out.println(textLines[i]);
			}
			br.close();
			double [][] valueList = new double[count][3];
			
			
		}catch(FileNotFoundException e) {
			System.out.println(e);
		}catch(IOException e) {
			System.out.println(e);
		}
		makeRectumROIFile();
		//makeAllROIFile();
		
		//printList(roiNameList);
		//printList(roiListGyou);

	}

	public static void printList(List inputList){
		int i = 0;
		for(i = 0; i < inputList.size(); i ++) {
			System.out.println(inputList.get(i));
		}
	}

	
	public static String[] getROIText(String [] inputText, int start, int end) {
		String [] exportString = new String[end - start];
		int i = 0;
		for (i = 0; i < end - start; i ++) {
			exportString[i] = inputText[start + i];
		}
		return exportString;
	}
	
	public static void makeRectumROIFile(){
		int i = 0;
		int rectumROINumber = 0;
		for(i = 0; i < roiNameList.size(); i ++) {
			if ((roiNameList.get(i).indexOf("Protect") != -1) ||  (roiNameList.get(i).indexOf("protect") != -1) || (roiNameList.get(i).indexOf("PROTECT") != -1)) {
			}//protectを含むのelseで含まず　もっと良い方法があるはず
			else{
				if ((roiNameList.get(i).indexOf("Rectum") != -1) ||  (roiNameList.get(i).indexOf("rectum") != -1) || (roiNameList.get(i).indexOf("RECTUM") != -1)) {
					//System.out.println("rectumあった "+ roiNameList.get(i));
					rectumROINumber = i;
				}

			}
		}
		makeROIFile(rectumROINumber);
		System.out.println("RectumFile finished");
	}
	public static void makeAllROIFile(){
		int i = 0;
		for(i = 0; i < roiNameList.size(); i ++) {
			makeROIFile(i);
		}
	}
	
	public static void makeROIFile (int roiNumber){
		if (roiNumber == 0) {
			makeFile(targetDir + roiNameList.get(roiNumber) + ".txt", getROIText(textLines,roiListGyou.get(roiNumber),roiListGyou.get(roiNumber+1)) );
		} else {
			makeFile(targetDir + roiNameList.get(roiNumber) + ".txt", getROIText(textLines,roiListGyou.get(roiNumber) + 1,roiListGyou.get(roiNumber+1)) );
		}
	}
	
	public static void makeFile(String filePathName, String[] textData) {
		int i = 0;
		try{
			File writeFile = new File(filePathName);
			FileWriter fw = new FileWriter(writeFile);
			for (i = 0; i < textData.length; i ++) {
				fw.write(textData[i] + "\n");
			}
			fw.close();
		}catch(FileNotFoundException e) {
			System.out.println(e);
		}catch(IOException e) {
			System.out.println(e);
		}
	}
	
	public static List<String> distinct(List<String> slist) {
		List<String> dlist = new ArrayList<String>();

		Set<String> set = new HashSet<String>();
		for (String s : slist) {
			if (!set.contains(s)) {
				set.add(s);
				dlist.add(s);
			}
		}

		return dlist;
	}
	
	public static double [] valueDecompose(String contourTargetTemp) {
		String[] splitString =contourTargetTemp.split(" ",0);
		double [] splitStringDouble = new double[splitString.length];
		
		int i = 0;
		for(i = 0; i < splitStringDouble.length ; i ++) {
			splitStringDouble[i] = Double.valueOf(splitString[i]);
		}
		
		return splitStringDouble;
	}
	
	public static int getFirstLine(File readFile){//ROINameが先頭につかなくなる行を探す
		int count = 0;
		int i = 1;//1行目だから
		try {
			//File readFile = new File("/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan/Contour.txt");
			FileReader fr = new FileReader(readFile);
			BufferedReader br = new BufferedReader(fr);
			String str="initial";// = br.readLine();
			
			while(str != null){
				str = br.readLine();
				if(str != null) {
					if(str.indexOf(ROINAME) != -1) {
						count = i;
						//break;
						//System.out.println("kokokara ROI: " + count + " " + str);
					}
					i++;					
				}
				//textLines[i].indexOf("EndofROI") != -1)
			}
			fr.close();
			br.close();
		}catch(FileNotFoundException e) {
			System.out.println(e);
		}catch(IOException e) {
			System.out.println(e);
		}
		return count;//最後のROINameの行の次を返す
	}
	
	public static String splitROIName(String inputString) {
		//ROIName: XXXXX からXXXXXを分離するメソッド
		String splitString =inputString.substring(7,inputString.length());
		return splitString;
		
	}
	
	public static int countLines(File readFile){//テキストファイルの行数を数えるだけ　なにか他の方法がありそう
		int count = 0;
		try {
			//File readFile = new File("/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan/Contour.txt");
			FileReader fr = new FileReader(readFile);
			BufferedReader br = new BufferedReader(fr);
			String str = br.readLine();
			
			while(str != null){
				str = br.readLine();
				count ++;
			}
			br.close();
		}catch(FileNotFoundException e) {
			System.out.println(e);
		}catch(IOException e) {
			System.out.println(e);
		}
		return count + 1;//0行からカウントするから
	}
}
