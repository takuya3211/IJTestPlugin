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
    	double [] imagePosition = tr.imagePosition;
    	double [] pixelSpacing = tr.pixelSpacing;
    	double doseScaling = tr.doseScaling * 100.0;//cGy単位にするために100かける

    	setZDepth(Dose,pixelSpacing[2]);
    	System.out.println("Calbration: " + Dose.getCalibration());
    	Dose = replaceToDose(Dose, DoseIP, (float)doseScaling);
    	//Dose.show();
    	/*int i = 0;
    	float test = 8000.0f;
    	for ( i = 0; i < 70; i++) {
    		DoseIP.setf(i, i, test);
    	}*/
    	printAllStrings(tr.rectumString);
    	putPoint();
    	Dose.show();
	}
	
	public static void putPoint (){
    	int i = 0;
    	float [] putValue = {8000.0f, 0.0f};
    	int x = 90;
    	int y = 38;
    	for(i = 0; i < Dose.getNSlices(); i ++){
    		Dose.setSlice(i);
    		//System.out.println(i + " " + DoseIP.getPixelValue(x, y) );
    		if(DoseIP.getPixelValue(x, y) > 1000) {
    			
            	DoseIP.setf(x, y, putValue[1]);//明るいところには黒点を    			
    		} else{
    			DoseIP.setf(x, y, putValue[0]);
    		}
    	}
    	Roi test = new Roi(50, 50, 20, 20);
    	int [] xpoints = {10,10,20};
    	int [] ypoints = {10,20,30,};
    	PolygonRoi test2 = new PolygonRoi(xpoints, ypoints, 3, PolygonRoi.POLYGON);
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
