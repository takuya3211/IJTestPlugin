import ij.*;
import ij.process.*;
import ij.gui.*;

import java.awt.*;

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
	//-10.8 -183.7 -594.0
	public void run(String arg) {
        String fileDirectory = "/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan2/";
        String fileNameDoseDCM = "19831222_test1_Dose.DCM";
        String fileNameContourDCM = "19831222_test1_Dose.dcm";
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
    	ScalerKai sk = new ScalerKai();
    	sk.setXYZScaling(pixelSpacing[0], pixelSpacing[1], pixelSpacing[2],(int)(Dose.getImageStackSize()*pixelSpacing[2] ));
    	//sk.setXYZScaling(3,3,3,(int)(Dose.getImageStackSize()*pixelSpacing[2] ));
    	sk.setIMP(Dose);
    	sk.run("");
    	
    	
    	//DoseIP.setf(30, 30,8000.0f);
    	ImagePlus Dose2 = WindowManager.getCurrentImage();
    	ImageProcessor DoseIP2 = Dose2.getProcessor();
    	float [] putValue = {8000.0f, 0.0f};
    	int i = 0;
    	//Dose.show();
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
    	//ImageProcessor DoseIP2 = Dose.getProcessor();
    	
    	Dose.setSlice(45);
    	for (i = 0; i < xpoints.length; i ++){ 
    			DoseIP2.setf(xpointsInt[i], ypointsInt[i], putValue[1]);
    	}
    	//double [] testte = tr.getSlices(tr.rectumDouble);
    	/*for(i = 0; i < testte.length; i ++){
    		System.out.println(testte[i]);
    	}*/
    	PolygonRoi test2 = new PolygonRoi(xpointsInt , ypointsInt, xpointsInt.length, PolygonRoi.POLYLINE);
    	Dose2.setRoi(test2);
		Dose.updateAndDraw();
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
}
