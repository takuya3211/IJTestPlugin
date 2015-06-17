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
        String fileNameDoseDCM = "19831222_test1_Dose.DCM";
        String fileNameContourDCM = "19831222_test1_Dose.dcm";
        String fileNameContour = "Contour.txt";
        String fileNameDose = "Dose.txt";
        //String fileNameDose = "test.png";
    	ImagePlus Dose = new ImagePlus(fileDirectory + fileNameDoseDCM);
    	System.out.println("Calbration: " + Dose.getCalibration());
    	
    	//DicomDecomposer�𓮍삳����Contour.txt��Dose.txt�����
    	DicomDecomposer dd = new DicomDecomposer();
    	
    	//TextReader���N�������Čʂ�ROI�t�@�C�������
    	TextReader tr = new TextReader();
    	System.out.println("dose scale is " + tr.doseScaling);
    	
    	new StackConverter(Dose).convertToGray32();
    	ImageProcessor DoseIP = Dose.getProcessor();
    	float data = 0;
    	double [] imagePosition = tr.imagePosition;
    	double [] pixelSpacing = tr.pixelSpacing;
    	double doseScaling = tr.doseScaling * 100.0;//cGy�P�ʂɂ��邽�߂�100������
    	
    	//setZDepth(Dose,3.0);
    	Dose = replaceToDose(Dose, DoseIP, (float)doseScaling);
    	Dose.show();
	}
	
	public static ImagePlus replaceToDose(ImagePlus Dose, ImageProcessor DoseIP, float doseScaling) {
    	int i = 0, j =0, k = 0;
    	float data = 0;
    	for(k = 1; k <= Dose.getNSlices(); k ++) {
    		Dose.setSlice(k);
    		 for(j =0; j < Dose.getHeight() ; j ++){
    			 for(i = 0; i < Dose.getWidth(); i ++) {
    				 data =  DoseIP.getPixelValue(i, j) * doseScaling;//dose�ɕϊ�
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
}
