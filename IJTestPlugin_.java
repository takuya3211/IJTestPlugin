import ij.*;
import ij.process.*;
import ij.gui.*;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.math.BigDecimal;

import ij.plugin.*;
//import ij.plugin.Thresholder.*;
import ij.io.*;
import ij.measure.*;
import ij.util.DicomTools;
import ij.plugin.frame.*;

public class IJTestPlugin_ implements PlugIn {
	ImageProcessor ip;
	ImagePlus imp;
	static ImagePlus Dose;
	static ImageProcessor DoseIP;
	static double refDose = 7800.0;
	static double [] imagePosition;
	static double [] pixelSpacing;
	static double doseScaling;
	static double [] zSlices;
	static double[][] rectumDose;
	static int roiCount = 0;
	RoiManager rm = new RoiManager();
	PointRoi pr = new PointRoi(0,0);
	
	//-10.8 -183.7 -594.0
	public void run(String arg) {
		
		//GUIでのファイルオープン
		OpenDialog od = new OpenDialog("filename");
		System.out.println(od.getPath());
		//System.out.println(od.getLastDirectory());
		String fileDirectory = od.getLastDirectory();
		String fileNameDoseDCM = od.getLastName();
		System.out.println(fileNameDoseDCM);
        //String fileDirectory = "/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan2/";
        //String fileNameDoseDCM = "19831222_test1_Dose.DCM";
       // String fileNameContourDCM = "19831222_test1_Dose.dcm";
        String fileNameContour = "Contour.txt";
        String fileNameDose = "Dose.txt";
        //String fileNameDose = "test.png";
    	Dose = new ImagePlus(fileDirectory + fileNameDoseDCM);
    	//System.out.println("Calbration: " + Dose.getCalibration());
    	
    	//DicomDecomposerを動作させてContour.txtとDose.txtを作る
    	DicomDecomposer dd = new DicomDecomposer();
    	
    	//TextReaderを起動させて直腸のROIファイルを作る
    	TextReader tr = new TextReader();
    	
    	new StackConverter(Dose).convertToGray32();
    	DoseIP = Dose.getProcessor();

    	float data = 0;
    	imagePosition = tr.imagePosition;
    	pixelSpacing = tr.pixelSpacing;
    	doseScaling = tr.doseScaling * 100.0;//cGy単位にするために100かける
    	double [][] zSlices = tr.getSlices(tr.rectumDouble);
    	//System.out.println(zSlices[0][0]);
    	System.out.println(tr.rectumDouble[0][0] + " " + tr.rectumDouble[0][1] + " " + tr.rectumDouble[0][2]);
    	
    	setZDepth(Dose,pixelSpacing[2]);
    	System.out.println("Calbration: " + Dose.getCalibration());
    	Dose = replaceToDose(Dose, DoseIP, (float)doseScaling);
    	//Dose.show();
    	/*int i = 0;
    	float test = 8000.0f;
    	for ( i = 0; i < 70; i++) {
    		DoseIP.setf(i, i, test);
    	}*/
    	//printAllStrings(tr.rectumString);
    	/*StackProcessor sp = new StackProcessor(Dose.getStack(),DoseIP);
    	ImageStack  resizedStack = sp.resize((int)(Dose.getWidth()*pixelSpacing[0]), (int)(Dose.getHeight()*pixelSpacing[1]) );
		Dose.setStack(resizedStack);*/
		
    	//Dose.show();
    	//1mmボクセルにリスケール
    	ScalerKai_ sk = new ScalerKai_();
    	sk.setXYZScaling(pixelSpacing[0], pixelSpacing[1], pixelSpacing[2],(int)(Dose.getImageStackSize()*pixelSpacing[2] ));
    	//sk.setXYZScaling(3,3,3,(int)(Dose.getImageStackSize()*pixelSpacing[2] ));
    	sk.setIMP(Dose);
    	sk.run("");
    	
    	
    	//DoseIP.setf(30, 30,8000.0f);
    	ImagePlus Dose2 = WindowManager.getCurrentImage();
    	ImageProcessor DoseIP2 = Dose2.getProcessor();
    	float [] putValue = {8000.0f, 0.0f};
    	int i = 0;
    	String[] rectumDoseTextData = new String[tr.rectumDouble.length];
    	double [][] rectumDoseDouble = new double[tr.rectumDouble.length][4];
    	for (i = 0; i < tr.rectumDouble.length; i ++){
    		rectumDoseTextData[i] = returnString(tr.rectumDouble[i]) + " " + getDicomValue(Dose2, DoseIP2, tr.rectumDouble[i]);
    		rectumDoseDouble[i][0] = tr.rectumDouble[i][0];
    		rectumDoseDouble[i][1] = tr.rectumDouble[i][1];
    		rectumDoseDouble[i][2] = tr.rectumDouble[i][2];
    		rectumDoseDouble[i][3] = getDicomValue(Dose2, DoseIP2, tr.rectumDouble[i]);
    		
    	}

    	tr.makeFile(fileDirectory + "RectumDoseTest.txt", rectumDoseTextData);
    	tr.makeFile(fileDirectory + "RectumRingDoseTest.txt", makeRingDose(tr, rectumDoseDouble));
    	
	
		Dose2.updateAndDraw();
	}

		 
	public String[] makeRingDose(TextReader tr, double [][] rectumDoseDouble){
		String[] returnRingDoseTemp = new String[tr.countSlice(rectumDoseDouble)];
		String[] returnRingDose = new String[tr.countSlice(rectumDoseDouble)];
		int sliceNumber = tr.countSlice(rectumDoseDouble);
		int i = 0;
		int [] ringSize = new int[tr.countSlice(rectumDoseDouble)];
		int ringSizeMax =0;
		
		for(i =0; i < returnRingDoseTemp.length; i++) returnRingDoseTemp[i]="";
		int sliceCount = 0;
		double zTemp = rectumDoseDouble[0][2];
		for(i = 0; i < rectumDoseDouble.length; i ++) {
			//System.out.print(rectumDoseDouble[i][3] + " ");
			returnRingDoseTemp[sliceCount] += String.valueOf(rectumDoseDouble[i][3]) + " ";
			ringSize[sliceCount] ++;
			if (rectumDoseDouble[i][2] != zTemp){
				//System.out.print(" " + sliceCount + "\n");
				sliceCount++;
			}
			zTemp = rectumDoseDouble[i][2];
		}
		for( i = 0; i < returnRingDoseTemp.length; i ++) {
			returnRingDoseTemp[i] = rotateString(returnRingDoseTemp[i]);
		}
		
		ringSizeMax = searchMax(ringSize);
		///空白を0で埋める
		String[] zeroTemp = new String[sliceNumber];
		for(i = 0; i < zeroTemp.length; i ++) {
			zeroTemp[i] = "";
		}
		int j = 0;
		for (i = 0; i < sliceNumber; i ++) {
			//System.out.println(ringSizeMax - ringSize[i]);
			for(j = 0;j < (ringSizeMax - ringSize[i])/2; j ++) {
				zeroTemp[i] += "0 ";
			}
		}
		for(i =0; i < sliceNumber; i ++) {
			if(ringSize[i] % 2 == 0) {
				returnRingDose[i] = zeroTemp[i] + returnRingDoseTemp[i] + zeroTemp[i];
			} else if(ringSize[i] % 2 == 1) {
				returnRingDose[i] = "0 " + zeroTemp[i] + returnRingDoseTemp[i] + zeroTemp[i];
			}
		}
		//System.out.print(returnRingDose[0]);
		//printAllStrings(returnRingDose);
		//System.out.println(sliceCount);
		//printAllStrings(returnRingDose);
		//System.out.println(returnRingDose[0]);
		
		return returnRingDose;
	}
	
	String rotateString(String inputString) {
		int i = 0;
		String [] tempSplit = inputString.split(" ");
		//System.out.println(tempSplit.length);
		String rotationString ="";
		for(i = tempSplit.length/2; i < tempSplit.length; i++){
			rotationString += tempSplit[i] + " ";
		}
		for(i = 0; i < tempSplit.length/2; i++){
			rotationString += tempSplit[i] + " ";
		}
		return rotationString;
	}
	
	int searchMax(int [] input) {
		int i=0, returnInt = 0;
		for(i = 0;i < input.length; i ++){
			//System.out.println(input[i]);
			if (input[i] > returnInt) returnInt = input[i];
		}
		return returnInt;
	}
	public String returnString(double [] inputDouble) {
		if(inputDouble.length == 3) {
			return String.valueOf(inputDouble[0]) + " " + String.valueOf(inputDouble[1]) + " " + String.valueOf(inputDouble[2]) ;
		}
		return "inputDouble is not 3 dimension";
	}
	
	public static double[] getX(double [][] rectumDouble, double[][] zSlices){
		int i = 0;
		int init = (int)zSlices[0][1];
		int end = (int)zSlices[0][2];
		
		int number = end - init;
		double [] returnX = new double[number];
		for(i = 0; i < number; i ++) {
			returnX[i] = rectumDouble[init + i][0];
			System.out.println(returnX[i]);
		}
		return returnX;
	}
	public static void putPoint (){
    	int i = 0;
    	float [] putValue = {8000.0f, 0.0f};
    	/*int x = 90;
    	int y = 38;
    	for(i = 0; i < Dose.getNSlices(); i ++){
    		Dose.setSlice(i);
    		//System.out.println(i + " " + DoseIP.getPixelValue(x, y) );
    		if(DoseIP.getPixelValue(x, y) > 1000) {
    			
            	DoseIP.setf(x, y, putValue[1]);//明るいところには黒点を    			
    		} else{
    			DoseIP.setf(x, y, putValue[0]);
    		}
    	}*/
    	Roi test = new Roi(50, 50, 20, 20);
    	float[] xpoints ={-10.8f, -9.5f, -8.3f, -7f, -5.7f, -4.4f, -3.2f, -1.9f, -0.6f, -0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f, -0.6f, -0.6f, -1.9f, -3.2f, -4.4f, -5.7f, -7f, -8.3f, -9.5f, -10.8f, -12.1f, -13.3f, -14.6f, -14.6f, -14.6f, -14.6f, -14.6f, -14.6f, -14.6f, -14.6f, -14.6f, -14.6f, -13.3f, -12.1f};
    	float[] ypoints = {-183.7f, -183.7f, -183.7f, -183.7f, -183.7f, -182.5f, -182.5f, -181.2f, -179.9f, -178.7f, -177.4f, -176.1f, -174.9f, -173.6f, -172.3f, -171.1f, -169.8f, -168.5f, -167.2f, -167.2f, -166f, -166f, -166f, -166f, -166f, -166f, -167.2f, -168.5f, -169.8f, -171.1f, -172.3f, -173.6f, -174.9f, -176.1f, -177.4f, -178.7f, -179.9f, -181.2f, -182.5f};
    	int [] xpointsInt = new int[xpoints.length];
    	int [] ypointsInt = new int[ypoints.length];
    	for (i = 0; i < xpoints.length; i ++){
    		xpointsInt[i] = (int)((xpoints[i] - (float)imagePosition[0]));
    		ypointsInt[i] = (int)((ypoints[i] - (float)imagePosition[0]));
    		System.out.println(xpointsInt[i] + " , " + ypointsInt[i]);
    	}
    	ImageProcessor DoseIP = Dose.getProcessor();
    	
    	Dose.setSlice(45);
    	for (i = 0; i < xpoints.length; i ++){ 
    			DoseIP.setf(xpointsInt[i], ypointsInt[i], putValue[1]);
    	}

    	PolygonRoi test2 = new PolygonRoi(xpointsInt , ypointsInt, xpointsInt.length, PolygonRoi.POLYLINE);
    	Dose.setRoi(test2);
		Dose.updateAndDraw();
	}

	public static ImagePlus replaceToDose(ImagePlus Dose, ImageProcessor DoseIP, float doseScaling) {
    	int i = 0, j =0, k = 0;
    	float data = 0;
    	for(k = 1; k <= Dose.getNSlices(); k ++) {
    		Dose.setSlice(k);
    		 for(j =0; j < Dose.getHeight() ; j ++){
    			 for(i = 0; i < Dose.getWidth(); i ++) {
    				 data =  DoseIP.getPixelValue(i, j) * doseScaling;//doseに変換
    				 DoseIP.setf(i, j, data);
    			 }
    		 }
    	}
    	//System.out.println("hello");
		Dose.updateAndDraw();
		Dose.updateAndRepaintWindow();
    	return Dose;
	}
	public void setZDepth(ImagePlus imp, double depth){
    	Calibration DoseCal = imp.getCalibration();
    	DoseCal.pixelDepth = depth;
    	imp.setCalibration(DoseCal);
	}
	
	
	public void printZSlices(){
		int i = 0;
		for(i = 0; i < zSlices.length; i ++) {
			System.out.println(zSlices[i]);
		}
	}
	
	public double getMin(float refDose) {
		double minDose = (double) refDose*0.3;
		return minDose;
	}
	
	public double getMax(float refDose) {
		double maxDose = (double) refDose*1.07;
		return maxDose;
	}
	
	public void printAllStrings(String[] inputString){
		int i = 0;
		for (i = 0; i < inputString.length ; i ++){
			//if ((inputString[i] != null) && (inputString[i] != "\n")){
				System.out.println(inputString[i]);
			//}
		}
	}
	
	public double getDicomValue(ImagePlus Dose2, ImageProcessor DoseIP2, double [] inputPosition){
		double returnDouble;
		int[] dicomPosition = new int[inputPosition.length];
		int i = 0;
		for(i = 0; i < inputPosition.length; i ++) {
			dicomPosition[i] =(int) ( inputPosition[i] - imagePosition[i]);		//1 mm Voxelだからこれでもいいが	
		}
		Dose2.setSlice(dicomPosition[2]);
		//小数点下1桁までにする。もっと良い方法はある
		returnDouble = DoseIP2.getf(dicomPosition[0], dicomPosition[1]);
		returnDouble *= 10;
		returnDouble = (int)(returnDouble);
		returnDouble /= 10;
		returnDouble = (double)returnDouble; 
		
		pr.setLocation(dicomPosition[0], dicomPosition[1]);
		rm.addRoi(pr);
		//DoseIP2.setf(dicomPosition[0], dicomPosition[1], 12000.0f);

		return returnDouble;
	}
	
	
	
	
	/////GUIのテスト
	 static int brightLimit = 50;

	    // static でなければ実行毎に初期化されます。
	 private boolean ifChange = false;
	 
	 private boolean getParam()
    {
        GenericDialog gd = new GenericDialog("Enter Parameters.",
                                             IJ.getInstance());
        gd.addNumericField("", brightLimit, 0);
        gd.addCheckbox("change image", ifChange);
	// 同じようにして他のParameterも取得できます。        

	// dialog表示
        gd.showDialog();
        
	// dialogでキャンセルボタンが押された時
	// （ちょっと汚い処理の仕方です。Javaらしくするなら例外を使うのかな。）
        if (gd.wasCanceled()) 
	    {
                return true;
                }
        
	// ここで取得結果を代入。詳しくはAPI docを参照して下さい。
        brightLimit = (int) gd.getNextNumber();
	ifChange = (boolean) gd.getNextBoolean();
	// 他のParameterもここで取得可能です。

	return false;
    }
}
