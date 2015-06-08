import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class ContourAnalyze {
	static int i = 0, count = 0;
	static double points[] = new double[10000];	
	static String sampleString = "-3.89\\190.11\\-130.5\\-2.89\\190.11\\-130.5\\-1.99\\190.11\\-130.5\\-0.99\\191.11\\-130.5\\-0.99\\192.01\\-130.5\\-0.99\\193.01\\-130.5\\-1.99\\194.01\\-130.5\\-2.89\\194.01\\-130.5\\-3.89\\194.01\\-130.5\\-4.89\\193.01\\-130.5\\-4.89\\192.01\\-130.5\\-4.89\\191.11\\-130.5 ";
	//static String sampleString = "hello world!";
	
	
	
	public static void ContourAnalyze (String args[]) {
		printAll(getString(analyzeIt(sampleString)));
		//String[] stringArray = {"Sunday", "Monday", "Tuesday"};
		//printAll(stringArray);
		//analyzeIt();
	}
	
	public static void printAll(double [] inputArray){
		int i = 0;
		for (i =0; i < inputArray.length; i ++){
			System.out.println(inputArray[i]);
		}
	}
	
	public static void printAll(double [][] input2DArray) {
		int i = 0, j = 0;
		//System.out.println(input2dArray[0].length + " " + input2dArray.length);
		for(j = 0; j < input2DArray.length; j ++) {
			for(i =0; i < input2DArray[0].length; i++) {
				System.out.print(input2DArray[j][i] + " ");
			}
			System.out.println("");
		}
	}
	
	public static void printAll(String [] inputString) {
		int i = 0;
		for(i = 0; i < inputString.length; i ++){
			System.out.println(inputString[i]);
		}
	}
	
	public static String[] getString (double [][] input2DArray){//doubleŒ^‚Ì‚QŽŸŒ³”z—ñ‚ðString‚Ì”z—ñ‚É•ÏŠ·
		int i = 0;
		String[] stringValue = new String[input2DArray.length];
		for(i=0 ; i < input2DArray.length; i ++) {
			stringValue[i] = input2DArray[i][0] + " " +  input2DArray[i][1] + " " + input2DArray[i][2];
		}
		return stringValue;
	}
	

	static double[][] analyzeIt () {
		File contourTarget = new File("/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan/Contour2.txt");
		File contourTargetTemp = new File("/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan/ContourTemp.txt");
		
		backslashToSpace(contourTarget);
		double [] stringValue = valueDecompose(contourTargetTemp);
		double [][] stringValue2D = convert2D(stringValue);
		return stringValue2D;
	}
	
	static double[][] analyzeIt (String inputString) {
		String tempString = "";
		
		tempString = backslashToSpace(inputString);
		//System.out.println(tempString);
		
		double [] stringValue = valueDecompose(tempString);
		double [][] stringValue2D = convert2D(stringValue);
		return stringValue2D;
		//valueDecompose(contourTargetTemp);

	}
	
	static void backslashToSpace (File contourTarget) {
		try{
			FileReader input = new FileReader(contourTarget);
			FileWriter output = new FileWriter(contourTarget.getParent() + "/ContourTemp.txt");
			int data = 0;
			while((data = input.read()) != -1) {
				if(data != 92) {
					output.write(data);	
				} else {
					output.write(' ');
				}
			}
			input.close();
			output.close();
		}catch(IOException e) {
			System.out.println(e);
		}
	}
	
	static String backslashToSpace (String inputString) {
		return inputString.replace("\\", " ");

	}
	
	static double[] valueDecompose(File contourTargetTemp) {
		try {
			FileReader input2 = new FileReader(contourTargetTemp);
			//FileWriter output2 = new FileWriter(coutourTarget2.getParent() + "/ContourOutput.txt");
			StreamTokenizer st = new StreamTokenizer(input2);
			count = 0;
			while(st.nextToken() != StreamTokenizer.TT_EOF)
			{
				//System.out.print(st.nval + " ");
				points[count] = st.nval;
				count++;
			}
			//int filesize = input.available();
				
		}catch(IOException e) {
			System.out.println(e);
		}
		
		double [] splitStringDouble = new double[count];
		for(i = 0; i < splitStringDouble.length ; i ++) {
			splitStringDouble[i] = points[i];
			//System.out.print(points[i] + " ");
		}
		
		return splitStringDouble;
	}
	
	static double [] valueDecompose(String contourTargetTemp) {
		String[] splitString =contourTargetTemp.split(" ",0);
		double [] splitStringDouble = new double[splitString.length];
		
		int i = 0;
		for(i = 0; i < splitStringDouble.length ; i ++) {
			splitStringDouble[i] = Double.valueOf(splitString[i]);
		}
		
		return splitStringDouble;
	}

	static double[][] convert2D(double [] inputvalue) {
	 	int j = 0;
		double position[][] =new double[inputvalue.length/3][3];
		//System.out.println(" here " + count/3 + " here ");
		for( j = 0; j < inputvalue.length ; j++) {
			if ((j + 3) %3 == 0) position[j/3][0] = inputvalue[j];
			if ((j + 3) %3 == 1) position[j/3][1] = inputvalue[j];
			if ((j + 3) %3 == 2) position[j/3][2] = inputvalue[j];
			//System.out.println(inputvalue[j]);
		}
		return position;
	}
}