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
	float refDose = 200;
	public void run(String arg) {
        String fileDirectory = "/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan2/";
        String fileName = "19831222_test1_Dose.DCM";
        //String fileName = "test.png";
    	ImagePlus Dose = new ImagePlus(fileDirectory + fileName);
    	System.out.println("Calbration: " + Dose.getCalibration());
    	DicomDecomposer dd = new DicomDecomposer();
    	dd.DicomDecomposer(null);
    	TextReader tr = new TextReader();
    	tr.TextReader();
    	new StackConverter(Dose).convertToGray32();
    	ImageProcessor DoseIP = Dose.getProcessor();
    	float data = 0, doseFactor = 2.49912e-4F * 100F;
    	//setZDepth(Dose,3.0);
    	Dose = replaceToDose(Dose, DoseIP, doseFactor);
    	Dose.show();
	}
	
	public static ImagePlus replaceToDose(ImagePlus Dose, ImageProcessor DoseIP, float doseFactor) {
    	int i = 0, j =0, k = 0;
    	float data = 0;
    	for(k = 1; k <= Dose.getNSlices(); k ++) {
    		Dose.setSlice(k);
    		 for(j =0; j < Dose.getHeight() ; j ++){
    			 for(i = 0; i < Dose.getWidth(); i ++) {
    				 data =  DoseIP.getPixelValue(i, j) * doseFactor;//dose‚É•ÏŠ·
    				 DoseIP.setf(i, j, data);
    			 }
    		 }
    	}
    	System.out.println("hello");
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
}
