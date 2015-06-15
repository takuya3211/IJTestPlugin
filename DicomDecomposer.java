
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class DicomDecomposer{
	//static File targetDir = new File("c:/pic/");
    static String fileDirectory = "/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan2/";
    static String fileNameDoseDCM = "19831222_test1_Dose.DCM";
    static String fileNameContourDCM = "19831222_StrctrSets.dcm";
    static String fileNameDose = "Dose.txt";
    static String fileNameContour = "Contour.txt";
	static File targetFile = new File("/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan2/19831222_StrctrSets.dcm");
	static File targetDoseFile = new File("/Users/takuya/Dropbox/program/workspace/DicomDecomposer/src/MonacoPlan2/19831222_test1_Dose.dcm");
	static String targetPath = targetFile.getParent() + "/";
	//static File targetFile = new File("/Users/takuya/Documents/workspace/DicomDecomposer/src/PinnalcePlan/testfile3.dcm");
	//static File targetFile = new File("c:/pic/RTPLAN16212.2.dcm");
	//test
	static int flag = 0;
	static long location = 0;
	static int SQlocation = 0;
	static int count = 0;
	static byte[] tempBuffer = new byte[4];
	static byte[] groupTag = new byte[2];
	static byte[] elementTag = new byte[2];
	static byte[] VR = new byte[2];
	static int segmentSize = 0;
	static String VRString ="";
	static byte[] preamble = new byte[128];//�v���A���u����128byte
	static List<byte[]> groupArray = new ArrayList<byte[]>();
	static List<byte[]> elementArray = new ArrayList<byte[]>();
	static List<String> VRArray = new ArrayList<String>();
	static List<byte[]> lengthArray = new ArrayList<byte[]>();
	static List<byte[]> dataArray = new ArrayList<byte[]>();
	static List<String> MLCArray = new ArrayList<String>();
	static List<String> MLCMonacoArray = new ArrayList<String>();
	private static Properties dictionary;
	
	static ArrayList<String> roiNameList = new ArrayList<String>();
	static ArrayList<String> roiNumberList = new ArrayList<String>();
	
	public static void refreshAll(){
		flag = 0;
		location = 0;
		SQlocation = 0;
		count = 0;
		tempBuffer = new byte[4];
		groupTag = new byte[2];
		elementTag = new byte[2];
		VR = new byte[2];
		segmentSize = 0;
		VRString ="";
		preamble = new byte[128];//�v���A���u����128byte
		groupArray = new ArrayList<byte[]>();
		elementArray = new ArrayList<byte[]>();
		VRArray = new ArrayList<String>();
		lengthArray = new ArrayList<byte[]>();
		dataArray = new ArrayList<byte[]>();
		MLCArray = new ArrayList<String>();
		MLCMonacoArray = new ArrayList<String>();
		//Properties dictionary;
		
		roiNameList = new ArrayList<String>();
		roiNumberList = new ArrayList<String>();
	}
	
	public static void main(String[] args) {
		DicomDecomposer dd = new DicomDecomposer();
	}
	
	public DicomDecomposer(){
		makeContourFile();
		refreshAll();
		makeDoseFile();
	}
	
    public static void makeContourFile() {
    	try{
    		FileInputStream input = new FileInputStream(targetFile);
    		long filesize = input.available();
    		//System.out.println(input.available());
    		//System.out.println(targetFile.getParent());
    		//FileOutputStream output = new FileOutputStream(targetFile.getParent() + "/testfile.dat");
    		FileWriter output = new FileWriter(targetFile.getParent() + "/outputfile.txt"); //��͌��ʂ��o��
    		FileWriter selectedOutput = new FileWriter(targetFile.getParent() + "/Contour.txt"); //ROI���ʂ��o��
    		int i = 0;
    		//getPreamble(input);//�v���A���u����128�o�C�g�ǂݍ���
    		DicomDictionary d = new  DicomDictionary();
    		dictionary = d.getDictionary();
    		
    		//if(checkDicomFile(input)){//128�o�C�g�������オDICM������
    			while (location < filesize){
					getAll(input);
					//System.out.print(groupArray.size());
					//System.out.print("," + count + "\n");
					count ++;
    			}
    			printAllResults(output, selectedOutput);
    			
				
    			//putMLC(); //Pinnacle��MLC����Monaco�ɕϊ����邽��
    			//MLCMonaco();//Pinnacle��MLC����Monaco�ɕϊ����邽��
    			//System.out.println(location);
    			System.out.println("Finished!!");
	    		output.flush();
	    		output.close();
	    		selectedOutput.flush();
	    		selectedOutput.close();
	    		input.close();
	    		

    		//}
    	}
    	catch(IOException e) {
            System.out.println(e);
    		
    	}
	}
    
    public static void makeDoseFile() {
    	try{
    		FileInputStream input = new FileInputStream(targetDoseFile);
    		long filesize = input.available();
    		System.out.println(targetDoseFile.getParent());
    		FileWriter doseOutput = new FileWriter(targetDoseFile.getParent() + "/outputDosefile.txt"); //��͌��ʂ��o��
    		FileWriter selectedDoseOutput = new FileWriter(targetDoseFile.getParent() + "/Dose.txt"); //ROI���ʂ��o��
    		int i = 0;
    		DicomDictionary d = new  DicomDictionary();
    		dictionary = d.getDictionary();
    			while (location < filesize){
					getAll(input);
					count ++;
    			}
    			printDoseResults(doseOutput, selectedDoseOutput);
    			System.out.println("Dose Finished!!");
    			doseOutput.flush();
    			doseOutput.close();
	    		selectedDoseOutput.flush();
	    		selectedDoseOutput.close();
	    		input.close();
	    		

    		//}
    	}
    	catch(IOException e) {
            System.out.println(e);
    		
    	}
	}
    public static void putMLC(){
    	int i = 0;
    	String dataString = "";
    	for(i = 0; i < count; i ++ ){
    		if(bytetoHexString(groupArray.get(i)).equals("300A") && (bytetoHexString(elementArray.get(i)).equals("011C"))){
    			if((new String(dataArray.get(i-1))).equals("MLCX")){
	    			dataString = new String(dataArray.get(i));
	    			MLCArray.add(dataString);
    			}
    		}
    	}
    	/*for(i = 0; i < MLCArray.size(); i ++){
    		System.out.println(MLCArray.get(i));//print�p�̃��[�v
    	}*/
    }
    
    public static void MLCMonaco(){
    	int k = 0;
    	for(k = 0; k < MLCArray.size(); k ++ ){
	    	String tempMLC ="";
	    	tempMLC = MLCArray.get(k);
	    	//System.out.println(tempMLC);
	    	int [] separatorPosition = new int[80];
	    	double [] MLCPosition = new double[80];
	    	separatorPosition[0] = -1;
	    	int tempPosition = 0;
	    	int i = 0, j  = 1;
	    	for(i  = 0; i < tempMLC.length() -1; i ++ ){
	    		//System.out.println(tempMLC.substring(i, i+1));
	    		if(tempMLC.substring(i, i+1).equals("\\") ){
	    			separatorPosition[j] = i;
	    			j++;
	    		}
	    	}
	    	for(i = 0; i < 80; i ++){
	    		if(i != 79) {
	    			//System.out.print(tempMLC.substring(separatorPosition[i]+1, separatorPosition[i+1]) +",");
	    			MLCPosition[i] = Double.parseDouble(tempMLC.substring(separatorPosition[i]+1, separatorPosition[i+1]));
	    		}
	    		else{
	    			MLCPosition[i] =Double.parseDouble(tempMLC.substring(separatorPosition[i]+1));
	    		}
	    		
	    	}
	    	for(i = 0; i < 40; i++) {
	    		if ((i+1) % 5 == 0 && i != 0){
	    			//System.out.println("koo");
	    			System.out.println(String.format("%1$5.1f", MLCPosition[i]) + "," + String.format("%1$5.1f", MLCPosition[i + 40]));
	    		}else{
	    			System.out.print(String.format("%1$5.1f", MLCPosition[i]) + "," + String.format("%1$5.1f", MLCPosition[i + 40]) + ",");
	    		}
	    		//if ((i+1) % 5 == 0 && i != 0) System.out.println("");
	    	}
	    	System.out.println("------------------------");
    	}
    }
    

    public static void printAllResults(FileWriter output){
    	int i = 0;
    	String tempString = "";
    	for(i = 0; i < count ; i ++) {
    		if (!VRArray.get(i).equals("SQ")) {//VR��SQ�ȊO�̎��̏���
    			tempString = 
    					"(" + 
    					bytetoHexString(groupArray.get(i)) + "," + 
    					bytetoHexString(elementArray.get(i)) + ")" +
    					" " + VRArray.get(i) + " " +
    					" [" + dictionary.get(bytetoHexString(groupArray.get(i)) + bytetoHexString(elementArray.get(i))).toString().substring(2) +  "] " +
    					  
    					new String(dataArray.get(i));
    			
    		} else {//SQ�������ꍇ
    			tempString = 
    					"(" + 
    					bytetoHexString(groupArray.get(i)) + "," + 
    					bytetoHexString(elementArray.get(i)) + ")" +
    					" " + VRArray.get(i) + " " +
    					" [" + dictionary.get(bytetoHexString(groupArray.get(i)) + bytetoHexString(elementArray.get(i))).toString().substring(2) +  "] " +
    					"Here is SQ segment!!!!!" //+ 
    					//bytetoHexString(dataArray.get(i)
    					;
    		}
    		//System.out.println(tempString);
    		try {
    			output.write(tempString + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public static void printAllResults(FileWriter output, FileWriter selectedOutput){
    	int i = 0;
    	int roiCount = 0;
    	String tempString = "";
    	String tempString2 = "";
    	String avtiveROIName = "", activeROINumber = "";
    	for(i = 0; i < count ; i ++) {
    		if (!VRArray.get(i).equals("SQ")) {//VR��SQ�ȊO�̎��̏���
    			tempString = 
    					"(" + 
    					bytetoHexString(groupArray.get(i)) + "," + 
    					bytetoHexString(elementArray.get(i)) + ")" +
    					" " + VRArray.get(i) + " " +
    					" [" + dictionary.get(bytetoHexString(groupArray.get(i)) + bytetoHexString(elementArray.get(i))).toString().substring(2) +  "] " +
    					  
    					new String(dataArray.get(i));
    			tempString2 = new String(dataArray.get(i));
    			
    		} else {//SQ�������ꍇ
    			tempString = 
    					"(" + 
    					bytetoHexString(groupArray.get(i)) + "," + 
    					bytetoHexString(elementArray.get(i)) + ")" +
    					" " + VRArray.get(i) + " " +
    					" [" + dictionary.get(bytetoHexString(groupArray.get(i)) + bytetoHexString(elementArray.get(i))).toString().substring(2) +  "] " +
    					"Here is SQ segment!!!!!" //+ 
    					//bytetoHexString(dataArray.get(i)
    					;
    		}
    		//System.out.println(tempString);
    		try {//������selectedOutput�ɓ���̃^�O�̏�����������
    			if (bytetoHexString(groupArray.get(i)).equals("3006") && bytetoHexString(elementArray.get(i)).equals("0050") ){
    				//Contour Data
    				//selectedOutput.write(tempString2 + "\n");
    				ContourAnalyze ca = new ContourAnalyze();
    				String [] tempStringArray = ca.getString(ca.analyzeIt(tempString2));//�o�b�N�X���b�V����؂���X�y�[�X��؂��String�ɕϊ�
    				int tempStringCount = 0;
    				for (tempStringCount = 0; tempStringCount < tempStringArray.length; tempStringCount++){
    					selectedOutput.write(tempStringArray[tempStringCount] + "\n");
    				}
    				selectedOutput.write("\n");  				
    				//ca.printAll(ca.getString(ca.analyzeIt(tempString2)));
    				//System.out.println("-----------------------");
    			}
    			else if (bytetoHexString(groupArray.get(i)).equals("3006") && bytetoHexString(elementArray.get(i)).equals("0022") ){
    				//ROI Number�@�Ƃ肠����String�^�̂܂܊i�[ �����̌�ɂO�f�[�^��������parseInt�o���Ȃ�
    				//selectedOutput.write(tempString2 + "\n");
    				roiNumberList.add(tempString2);
    			}
    			else if (bytetoHexString(groupArray.get(i)).equals("3006") && bytetoHexString(elementArray.get(i)).equals("0026") ){
    				//ROIName
    				selectedOutput.write("ROIName" + tempString2 + "\n");
    				roiNameList.add(tempString2);
    			}
    			else if (bytetoHexString(groupArray.get(i)).equals("3006") && bytetoHexString(elementArray.get(i)).equals("0084") ){
    				//Referenced ROI Number
    				selectedOutput.write(tempString2 + "EndofROI\n");
    				//roiNumberList.add(tempString2);
    			}
    			output.write(tempString + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	for (i = 0; i < roiNumberList.size(); i ++){
    		System.out.println(roiNumberList.get(i) + " " + roiNameList.get(i));
    	}
    }
    public static void printDoseResults(FileWriter output, FileWriter selectedOutput){
    	int i = 0;
    	int roiCount = 0;
    	String tempString = "";
    	String tempString2 = "";
    	String avtiveROIName = "", activeROINumber = "";
    	for(i = 0; i < count ; i ++) {
    		if (!VRArray.get(i).equals("SQ")) {//VR��SQ�ȊO�̎��̏���
    			tempString = 
    					"(" + 
    					bytetoHexString(groupArray.get(i)) + "," + 
    					bytetoHexString(elementArray.get(i)) + ")" +
    					" " + VRArray.get(i) + " " +
    					" [" + dictionary.get(bytetoHexString(groupArray.get(i)) + bytetoHexString(elementArray.get(i))).toString().substring(2) +  "] " +
    					  
    					new String(dataArray.get(i));
    			tempString2 = new String(dataArray.get(i));
    			
    		} else {//SQ�������ꍇ
    			tempString = 
    					"(" + 
    					bytetoHexString(groupArray.get(i)) + "," + 
    					bytetoHexString(elementArray.get(i)) + ")" +
    					" " + VRArray.get(i) + " " +
    					" [" + dictionary.get(bytetoHexString(groupArray.get(i)) + bytetoHexString(elementArray.get(i))).toString().substring(2) +  "] " +
    					"Here is SQ segment!!!!!" //+ 
    					//bytetoHexString(dataArray.get(i)
    					;
    		}
    		//System.out.println(tempString);
    		try {//������selectedOutput�ɓ���̃^�O�̏�����������
    			if (bytetoHexString(groupArray.get(i)).equals("3004") && bytetoHexString(elementArray.get(i)).equals("000E") ){
    				//DoseScaling�̃^�O���
    				selectedOutput.write("DoseScaling" + tempString2 + "\n");
    				
    			}
    			else if (bytetoHexString(groupArray.get(i)).equals("0020") && bytetoHexString(elementArray.get(i)).equals("0032") ){
    				//ImagePosition�̃^�O���
    				selectedOutput.write("ImagePosition");
    				ContourAnalyze ca = new ContourAnalyze();
    				String [] tempStringArray = ca.getString(ca.analyzeIt(tempString2));//�o�b�N�X���b�V����؂���X�y�[�X��؂��String�ɕϊ�
    				int tempStringCount = 0;
    				for (tempStringCount = 0; tempStringCount < tempStringArray.length; tempStringCount++){
    					selectedOutput.write(tempStringArray[tempStringCount] + "\n");
    				}
    				//selectedOutput.write("ImagePosition" + tempString2 + "\n");
    			}
    			else if (bytetoHexString(groupArray.get(i)).equals("0028") && bytetoHexString(elementArray.get(i)).equals("0030") ){
    				//ImagePosition�̃^�O���
    				selectedOutput.write("PixelSpacing");
    				//�o�b�N�X���b�V���̒u�����������Ǒ��̏ꏊ�ł��p�ɂɎg���Ă邩�烁�\�b�h�ɂ�������������
    				ContourAnalyze ca = new ContourAnalyze();
    				//x,y,z�̍��W�̂悤�ɂR�łP�΂̃f�[�^�\����z�肵�Ă������肭�����Ȃ��AtempString2�̍ŏ��̕����łƂ肠�����떂����
    				String [] tempStringArray = ca.getString(ca.analyzeIt(tempString2 + tempString2.substring(0, 1)));//�o�b�N�X���b�V����؂���X�y�[�X��؂��String�ɕϊ�
    				int tempStringCount = 0;
    				for (tempStringCount = 0; tempStringCount < tempStringArray.length; tempStringCount++){
    					selectedOutput.write(tempStringArray[tempStringCount] + "\n");
    				}
    			}
    			//������Ɠ����7FE0,0010�̃^�O�͉摜�f�[�^�Ȃ̂Ńe�L�X�g�o�͂���e���o�͐��output
    			else if (bytetoHexString(groupArray.get(i)).equals("7FE0") && bytetoHexString(elementArray.get(i)).equals("0010") ){
    				//Referenced ROI Number
    				tempString ="(" + 
					bytetoHexString(groupArray.get(i)) + "," + 
					bytetoHexString(elementArray.get(i)) + ")" +
					" " + VRArray.get(i) + " " +
					" [" + dictionary.get(bytetoHexString(groupArray.get(i)) + bytetoHexString(elementArray.get(i))).toString().substring(2) +  "] " +
    				 "�����͉摜�f�[�^�ł�";
    				//roiNumberList.add(tempString2);
    			}
    			output.write(tempString + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	for (i = 0; i < roiNumberList.size(); i ++){
    		System.out.println(roiNumberList.get(i) + " " + roiNameList.get(i));
    	}
    }
    
    
    public static void getPreamble(FileInputStream partInput){
		try{
			partInput.read(preamble);
		}catch(IOException e) {
            System.out.println(e);
    	}
		location += 128;
    }
    
    public static boolean checkDicomFile(FileInputStream partInput){
    	boolean CheckDicom = false;
		tempBuffer = partRead(4,partInput);
		String result = new String(tempBuffer);

		//System.out.print(result + " ");
		if(result.equals("DICM")) {
			CheckDicom = true;
			System.out.println("It's a DICOM file!");
		}else{
			System.out.println("It's not a DICOM file!");
		}
    	return CheckDicom;
    }
    
    public static void getAll(FileInputStream partInput){
    	getTags(partInput);
    	getVR(partInput);
    	getData(partInput);
    }
    public static void getData(FileInputStream partInput){
    	dataArray.add(partRead(segmentSize,partInput));
    }
    
    public static void getData(byte[] partInput){
    	dataArray.add(partRead(segmentSize,partInput));
    }
    
    public static void getTags(FileInputStream partInput){
    	groupTag =  exchangeOrderByte(partRead(2,partInput));//�o�C�g�̏��Ԃ�����ւ��Ă�
    	elementTag =  exchangeOrderByte(partRead(2,partInput));
		groupArray.add(groupTag);
		elementArray.add(elementTag);
		//SQFFFE000�̎��͕ʂ̏���������
		if(bytetoHexString(groupTag).equals("FFFE") && bytetoHexString(elementTag).equals("E000") ){
			//System.out.println("hihihihihihit");
			partRead(4,partInput);
	    	groupTag =  exchangeOrderByte(partRead(2,partInput));//�o�C�g�̏��Ԃ�����ւ��Ă�
	    	elementTag =  exchangeOrderByte(partRead(2,partInput));
			groupArray.add(groupTag);
			elementArray.add(elementTag);
			VRArray.add("BB");
			location -= 8;
			byte [] tempByte = {(byte)0x20,(byte)0x20};
			dataArray.add(tempByte);
			//dataArray.add(null);
			//VRArray.add(null);
		}
    	//System.out.println(bytetoHexString(groupTag) + "," + bytetoHexString(elementTag));//�\���@�v��Ȃ��Ȃ��������
    }
    
    public static void getTags(byte[] partInput){
    	groupTag =  exchangeOrderByte(partRead(2,partInput));//�o�C�g�̏��Ԃ�����ւ��Ă�
    	elementTag =  exchangeOrderByte(partRead(2,partInput));
		groupArray.add(groupTag);
		elementArray.add(elementTag);
    	//System.out.println(bytetoHexString(groupTag) + "," + bytetoHexString(elementTag));//�\���@�v��Ȃ��Ȃ��������
    }

    
    public static void getVR(FileInputStream partInput){
    	byte[] emptyVR = new byte[2];
    	byte[] twoByteSize = new byte[2];
    	byte[] fourByteSize = new byte[4];
    	VR = partRead(2,partInput);
    	VRString = new String(VR);
    	if ( VRString.equals("OB") || VRString.equals("OW") || VRString.equals("OF") 
    			|| VRString.equals("UT") || VRString.equals("OR") || VRString.equals("UN") ){
    		//VR��OB,OW,OF,SQ,UT,OR,UN�̏ꍇ��2�o�C�g�X�L�b�v����4�o�C�g���Z�O�����g�����Ƃ��ēǂށc����ȊO��2�o�C�g���Z�O�����g��
    		//System.out.println("hit!");
    		emptyVR = partRead(2,partInput);
    		fourByteSize =  exchangeOrderByte(partRead(4,partInput));//�o�C�g��������ւ��ēǂݍ���
    		segmentSize = exchangeToInt(bytetoHexString(fourByteSize));//long�^�ɕϊ�
    	}else if (VRString.equals("AE") || VRString.equals("AS") || VRString.equals("AT") || VRString.equals("CS") || VRString.equals("DA") || VRString.equals("DS") || VRString.equals("DT") ||  VRString.equals("FD") ||
    			VRString.equals("FL") || VRString.equals("IS") || VRString.equals("LO") || VRString.equals("LT") || VRString.equals("PN") || VRString.equals("SH") || VRString.equals("SL") || VRString.equals("SS") ||
    					VRString.equals("ST") || VRString.equals("TM") || VRString.equals("UI") || VRString.equals("UL") || VRString.equals("US") || VRString.equals("QQ"))
    	{
    		//System.out.println("not hit!");
    		twoByteSize = exchangeOrderByte(partRead(2,partInput));//�o�C�g��������ւ��ēǂݍ���
    		segmentSize = exchangeToInt(bytetoHexString(twoByteSize));
    	}
    	else {//implicit����������SQ���������̏���
    		if(VRString.equals("SQ") ){
    			//System.out.println("SQSQ1");
    		}
    		else{
    			VRString = getVRString();
    			if(VRString.equals("SQ")){
    				//System.out.println("SQSQ2");
	    			twoByteSize = partRead(2,partInput);
	        		//VR��Implicit�������ꍇVR�Ƃ��ēǂݍ���2�o�C�g�Ƃ��̎���2�o�C�g���o�C�g���Ƃ���
	        		segmentSize = 8;//exchangeToInt(bytetoHexString(fourByteSize));//long�^�ɕϊ�
	        		//System.out.println("kokokara " + bytetoHexString(VR) + "," + segmentSize + "kokomade");
    			}
    			else {//�S�Ăɓ��Ă͂܂�Ȃ�implicit�������ꍇ�̏���
	    			twoByteSize = partRead(2,partInput);
	        		fourByteSize =  exchangeOrderByte( combineBytes(VR,twoByteSize) );
	        		//VR��Implicit�������ꍇVR�Ƃ��ēǂݍ���2�o�C�g�Ƃ��̎���2�o�C�g���o�C�g���Ƃ���
	        		segmentSize = exchangeToInt(bytetoHexString(fourByteSize));//long�^�ɕϊ�
	        		//System.out.println("kokokara " + bytetoHexString(VR) + "," + segmentSize + "kokomade");
    			}
    		}
    	}
    	VRArray.add(VRString);
    }
    
    public static void getVR(byte []  partInput){
    	byte[] emptyVR = new byte[2];
    	byte[] twoByteSize = new byte[2];
    	byte[] fourByteSize = new byte[4];
    	VR = partRead(2,partInput);
    	VRString = new String(VR);
    	if ( VRString.equals("OB") || VRString.equals("OW") || VRString.equals("OF") || VRString.equals("SQ") 
    			|| VRString.equals("UT") || VRString.equals("OR") || VRString.equals("UN") ){
    		//VR��OB,OW,OF,SQ,UT,OR,UN�̏ꍇ��2�o�C�g�X�L�b�v����4�o�C�g���Z�O�����g�����Ƃ��ēǂށc����ȊO��2�o�C�g���Z�O�����g��
    		//System.out.println("hit!");
    		emptyVR = partRead(2,partInput);
    		fourByteSize =  exchangeOrderByte(partRead(4,partInput));//�o�C�g��������ւ��ēǂݍ���
    		segmentSize = exchangeToInt(bytetoHexString(fourByteSize));//long�^�ɕϊ�
    	}else if (VRString.equals("AE") || VRString.equals("AS") || VRString.equals("AT") || VRString.equals("CS") || VRString.equals("DA") || VRString.equals("DS") || VRString.equals("DT") ||  VRString.equals("FD") ||
    			VRString.equals("FL") || VRString.equals("IS") || VRString.equals("LO") || VRString.equals("LT") || VRString.equals("PN") || VRString.equals("SH") || VRString.equals("SL") || VRString.equals("SS") ||
    					VRString.equals("ST") || VRString.equals("TM") || VRString.equals("UI") || VRString.equals("UL") || VRString.equals("US") || VRString.equals("QQ"))
    	{
    		//System.out.println("not hit!");
    		twoByteSize = exchangeOrderByte(partRead(2,partInput));//�o�C�g��������ւ��ēǂݍ���
    		segmentSize = exchangeToInt(bytetoHexString(twoByteSize));
    	}
    	else {//implicit��VR���������̏���
    		VRString = getVRString();
    		twoByteSize = partRead(2,partInput);
    		fourByteSize =  exchangeOrderByte( combineBytes(VR,twoByteSize) );
    		//VR��Implicit�������ꍇVR�Ƃ��ēǂݍ���2�o�C�g�Ƃ��̎���2�o�C�g���o�C�g���Ƃ���
    		segmentSize = exchangeToInt(bytetoHexString(fourByteSize));//long�^�ɕϊ�
    		//System.out.println("kokokara " + bytetoHexString(VR) + "," + segmentSize + "kokomade");
    	}
    	VRArray.add(VRString);
    }
    
/*case AE: case AS: case AT: case CS: case DA: case DS: case DT:  case FD:
case FL: case IS: case LO: case LT: case PN: case SH: case SL: case SS:
case ST: case TM:case UI: case UL: case US: case QQ:*/
    public static String getVRString(){
    	String VRString = "";
		DicomDictionary d = new  DicomDictionary();
		dictionary = d.getDictionary();
    	VRString =  (String) dictionary.get(bytetoHexString(groupTag) + bytetoHexString(elementTag));
    	if (VRString == null) {
    		VRString = "AA";
    	}else{
    		VRString = VRString.substring(0, 2);
    	}
    	//System.out.println(VRString);
    	return VRString;
    }
    
    
    public static byte[] partRead(int size, FileInputStream partInput){
    	byte inputBuffer[] = new byte[size];
    	try{
    		partInput.read(inputBuffer);
    	}
    	catch(IOException e) {
    		System.out.println(e);
    	}
    	location += size;
		return inputBuffer;
    }
    
    public static byte[] partRead(int size, byte[] a){
    	byte returnByte[] = new byte[size];
    	int i = 0;
    	for (i = 0; i < size; i++){
    		returnByte[i] = a[SQlocation + i];
    	}
    	SQlocation += size;
    	return returnByte;
    }
    
	public static String bytetoHexString(byte inputByte){//int��Hex�̕��͂ɂ��Đ擪���O�Ŗ��߂�
		String kaeshi;
		
		kaeshi = Integer.toHexString(inputByte & 0xff);
		switch (kaeshi.length()+2) {
			case 1: kaeshi = "000" + kaeshi; break;
			case 2: kaeshi = "00" + kaeshi; break;
			case 3: kaeshi = "0" + kaeshi; break;
			case 4: break;
			default: break;
		}
				
		return kaeshi.toUpperCase();
	}
	public static String bytetoHexString(byte inputByte[]){//int��Hex�̕��͂ɂ��Đ擪���O�Ŗ��߂�
		String kaeshi ="";
		String kaeshiTemp = "";
		int count = 0;
		for (count = 0; count < inputByte.length ; count ++){
			kaeshiTemp = Integer.toHexString(inputByte[count] & 0xff);	
			switch (kaeshiTemp.length()+2) {
			case 1: kaeshiTemp = "000" + kaeshiTemp; break;
			case 2: kaeshiTemp = "00" + kaeshiTemp; break;
			case 3: kaeshiTemp = "0" + kaeshiTemp; break;
			case 4: break;
			default: break;
			}
			kaeshi += kaeshiTemp;
		}	
		return kaeshi.toUpperCase();
	}
	
	public static long exchangeToLong (String a){
		//16�i���\�L�̕������Int�^�ɕϊ�
		return Long.parseLong(a,16);
	}
	
	public static int exchangeToInt (String a){
		//16�i���\�L�̕������Int�^�ɕϊ�
		return Integer.parseInt(a,16);
	}
	
	public static int exchangeToInt (String a, String b){
		//16�i���\�L�̕������Int�^�ɕϊ�
		return Integer.parseInt(b + a,16);
	}
	
	public static byte[] exchangeOrderByte(byte[] a){
		byte[] tempByte = new byte[a.length];
		int i = 0;
		for(i = 0; i < a.length; i++){
			tempByte[i] = a[a.length - i - 1];
		}
		return tempByte;
	}
	public static String exchangeOrderString(String a){
		String returnString = "";
		String tempString1 = "";
		String tempString2="";
		tempString1 = a.substring(a.length()/2, a.length());
		tempString2 = a.substring(0,a.length()/2);
		returnString = tempString1 + tempString2;
		return returnString;
	}
	public static String exchangeOrderString(String a, String b){
		return b + a;
	}
	//������4�̏ꍇ�B�f�[�^�̃T�C�Y�̃Z�O�����g�Ɏg��
	public static String exchangeOrderString(String a, String b, String c, String d){
			return d + c + b + a;
	}
	
	public static byte[] combineBytes(byte[] a, byte [] b){
		byte[] returnByte = new byte[a.length + b.length];
		int i = 0;
		for(i =0; i < a.length; i++){
			returnByte[i] = a[i];
		}
		for(i = a.length; i < a.length + b.length; i ++) {
			returnByte[i] = b[i - a.length];
		}
		return returnByte;
	}
	


    public static FilenameFilter getFileExtensionFilter(String extension) {  //listtargetDir�Ŏg�����\�b�h
        final String _extension = extension;  
        return new FilenameFilter() {  
            public boolean accept(File file, String name) {  
                boolean ret = name.endsWith(_extension) || name.endsWith(_extension.toUpperCase()) || name.endsWith(_extension.toLowerCase());   
                return ret;  
            }  
        };  
    }  
    
    public static void  listtargetDir(File targetDir){
		if (targetDir.exists() && targetDir.isDirectory()) {
		File[] fileList = targetDir.listFiles(getFileExtensionFilter(".dcm")); 
		for (int i = 0; i < fileList.length; i++) 
			{
			//System.out.println(fileList[i].getName());
			//System.out.println(fileList[i].getAbsolutePath());
			}
		}
    }
}






