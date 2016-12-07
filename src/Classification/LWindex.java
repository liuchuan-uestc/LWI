package Classification;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import PreProcess.DataPreProcess;
import PreProcess.DimensionReduction;
import PreProcess.Point2;
import SVM.*;
import SVM.libsvm.svm_model;

public class LWindex {
	static int FilterMapNum = 100;
	public static void main(String arg[]) throws Exception{
		try {
			LWindexMain();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void LWindexMain() throws Exception {
		double dReturn = 0.0;
		int TrainTestNum = 5;
		String StrSrcDir = "./DataMiningSample/LW";
		Map<Integer, Double> IndexValueMap = new TreeMap<Integer, Double>();
		Map<Integer, Double> IndexValueMapNew = new TreeMap<Integer, Double>();
		Map<Integer, Double> IndexDisValueMap = new TreeMap<Integer, Double>();
		Map<Integer, Double> IndexDisValueMapNew = new TreeMap<Integer, Double>();
		Map<String, Double> WordTFMap = new TreeMap<String, Double>();
		Map<String, Map<Integer, Double>> trainFileMapVsm  = new TreeMap<String, Map<Integer, Double>>();
		Map<String, Map<Integer, Double>> testFileMapVsm   = new TreeMap<String, Map<Integer, Double>>();
		Map<String, Map<Integer, Double>> trainMapNew      = new TreeMap<String, Map<Integer, Double>>();
		Map<Integer,String> classes = new TreeMap<Integer, String>();
		Map<String,String>  pred    = new TreeMap<String, String>();
		Map<String,String>  actual  = new TreeMap<String, String>();

		
		Map<Integer, Map<Integer, Double>> resultMap       = new TreeMap<Integer, Map<Integer, Double>>();
		Map<Integer, Double> accuracyMap = new TreeMap<Integer, Double>();
		Map<String, Double> TempResultMap = new TreeMap<String, Double>();

		DataPreProcess     DataPP = new DataPreProcess();
		DimensionReduction DataDR = new DimensionReduction();
		Date d = new Date();
		long longtimeA = 0;
		long longtimeB = 0;
		
		System.out.println("LWindexMain start...");
		  
		DataPP.StemData(StrSrcDir + "/orginSample");
		DataPP.CopyDirVsm(StrSrcDir + "/0_StemedSample");
		DataPP.ComputeTFIDF(StrSrcDir+"/1_StemedSample", 4);
		// 3 拆分训练集和测试集
		DataPP.CreatTrainAndTestDir(StrSrcDir+"/2_IDFTFSample", TrainTestNum);
		// 4 DRByIGandTFIDF降维处理，返回过滤剩下的词条map
		for(int f=1; f<=5; f++)
		{
			String StrSrcDir_output = StrSrcDir+"/0_outputfile_"+f+"/";
			DataPP.CreatDir(StrSrcDir_output);

			IndexValueMap = DataPP.SlectedDRMethod(StrSrcDir + "/1_StemedSample", 1, 4, f);
			
			double[][] SVMTempResult_macroF1 = new double[TrainTestNum+1][FilterMapNum+1];
			double[][] SVMTempResult_microF1 = new double[TrainTestNum+1][FilterMapNum+1];
			double[][] SVMTempResult_Entropy = new double[TrainTestNum+1][FilterMapNum+1];

			double[][] CBCTempResult_macroF1 = new double[TrainTestNum+1][FilterMapNum+1];
			double[][] CBCTempResult_microF1 = new double[TrainTestNum+1][FilterMapNum+1];
			double[][] CBCTempResult_Entropy = new double[TrainTestNum+1][FilterMapNum+1];

			double[] LWITempResult1 = new double[FilterMapNum+1];
			double[] LWATempResult1 = new double[FilterMapNum+1];
			double[] LWIANDRADIUS_1 = new double[FilterMapNum+1];

			double[] LWITempResult2 = new double[FilterMapNum+1];
			double[] LWATempResult2 = new double[FilterMapNum+1];
			double[] LWIANDRADIUS_2 = new double[FilterMapNum+1];


			double[] LWITempResu_P1 = new double[FilterMapNum+1];
			double[] LWATempResu_P1 = new double[FilterMapNum+1];
			double[] LWIANDRADIU_P1 = new double[FilterMapNum+1];

			double[] LWITempResu_P2 = new double[FilterMapNum+1];
			double[] LWATempResu_P2 = new double[FilterMapNum+1];
			double[] LWIANDRADIU_P2 = new double[FilterMapNum+1];

			double[] LWCptDistance1 = new double[FilterMapNum+1];

			double[] DBITempResult = new double[FilterMapNum+1];
			double[] SILTempResult = new double[FilterMapNum+1];

			double[] BHITempResult = new double[FilterMapNum+1];
			double[] CHITempResult = new double[FilterMapNum+1];
			double[] HAITempResult = new double[FilterMapNum+1];
			double[] XUITempResult = new double[FilterMapNum+1];
			double[] WBITempResult = new double[FilterMapNum+1];
			
			double[] DITempResu_11 = new double[FilterMapNum+1];
			double[] DITempResu_12 = new double[FilterMapNum+1];
			double[] DITempResu_13 = new double[FilterMapNum+1];
			double[] DITempResu_21 = new double[FilterMapNum+1];
			double[] DITempResu_22 = new double[FilterMapNum+1];
			double[] DITempResu_23 = new double[FilterMapNum+1];
			double[] DITempResu_31 = new double[FilterMapNum+1];
			double[] DITempResu_32 = new double[FilterMapNum+1];
			double[] DITempResu_33 = new double[FilterMapNum+1];
			double[] DITempResu_41 = new double[FilterMapNum+1];
			double[] DITempResu_42 = new double[FilterMapNum+1];
			double[] DITempResu_43 = new double[FilterMapNum+1];
			double[] DITempResu_51 = new double[FilterMapNum+1];
			double[] DITempResu_52 = new double[FilterMapNum+1];
			double[] DITempResu_53 = new double[FilterMapNum+1];
			double[] DITempResu_61 = new double[FilterMapNum+1];
			double[] DITempResu_62 = new double[FilterMapNum+1];
			double[] DITempResu_63 = new double[FilterMapNum+1];
			
			int[] IndexMapNewNum = new int[FilterMapNum+1];

			resultMap.clear();
			for(int i=0; i<FilterMapNum-10; i=i+1)
			{
				int j = 0;
				double k = ((double)i)/FilterMapNum;
				accuracyMap.clear();

				if(f<5){
					// 4 SlectWordByValue截取不同长度的map，返回过滤剩下的词条map
					IndexValueMapNew = DataDR.SlectWordByValue(IndexValueMap, 1-k);
				}else{
					IndexValueMapNew = DataDR.SlectWordByRandom(IndexValueMap, 1-k);
				}

				for(j=0; j<TrainTestNum; j=j+1)
				{
					String VsmTestSrcDir  = StrSrcDir+"/4_DocVector/VsmTFIDFMapTestSample"+j+".txt";
					String VsmTrainSrcDir = StrSrcDir+"/4_DocVector/VsmTFIDFMapTrainSample"+j+".txt";
					// 5 分别读入训练集测试集，并保存原始文本TFIDF向量
					testFileMapVsm  = DataPP.ReadTestFileVsm(VsmTestSrcDir);
					trainFileMapVsm = DataPP.ReadTrainFileVsm(VsmTrainSrcDir);
					
					// 6 基于词条map过滤第4步中的训练和测试集
					testFileMapVsm   = DataPP.FilterMapByMapSID(StrSrcDir+"/stopword/", testFileMapVsm, IndexValueMapNew);
					trainFileMapVsm  = DataPP.FilterMapByMapSID(StrSrcDir+"/stopword/", trainFileMapVsm, IndexValueMapNew);
	
					d = new Date();
					longtimeA = d.getTime();
					System.out.println("Time1:"+longtimeA);
					
					for(double m=1; m<2; m=m+3)
					{
						System.out.println("SVM start...i = "+i+", j = "+j);
						
						String SVM_train_argv = "-s 0 -c 100 -t 2 -g "+m/10.0+" -e 0.1 -l 1 VsmTFIDFMapTrainSample"+j;
						String SVM_test_argv  = "-m 1 VsmTFIDFMapTestSample"+j+" VsmTFIDFMapTrainSample"+j+".model "+StrSrcDir_output+"output"+j;
						String[] train_argv_Split = SVM_train_argv.split(" ");
						String[] test_argv_Split  = SVM_test_argv.split(" ");

						svm_train   SVM_train = new svm_train();
						svm_predict SVM_test  = new svm_predict();
						svm_model model = SVM_train.run(train_argv_Split, trainFileMapVsm);
						
						actual.clear();
						pred.clear();
						classes.clear();
						dReturn = SVM_test.run(test_argv_Split, model, testFileMapVsm, actual, pred);
					
						String DesFile = StrSrcDir_output+"SVM_"+f+"_"+i+"_"+j+"_"+(int)m+".txt";
						TempResultMap = DataPP.compute_accuracy_F_RetMap(actual, pred, classes, DesFile, 2, 1);

						SVMTempResult_macroF1[j][i] = TempResultMap.get("macro_F1");
						SVMTempResult_microF1[j][i] = TempResultMap.get("micro_F1");
						SVMTempResult_Entropy[j][i] = TempResultMap.get("Entropy");						
					}

					d = new Date();
					longtimeB = d.getTime();
					System.out.println("Time2:"+longtimeB);
					
					if(true){
						actual.clear();
						pred.clear();
						classes.clear();
						trainMapNew = DataPP.copyMaptoMap(trainFileMapVsm); 
						dReturn = CBC.CBCMain(StrSrcDir_output+"f",trainMapNew,testFileMapVsm,actual,pred);
						String DesFileCBC1 = StrSrcDir_output+"CBC_"+f+"_"+i+"_"+j+".txt";
						TempResultMap = DataPP.compute_accuracy_F_RetMap(actual, pred, classes, DesFileCBC1, 2, 1);

						CBCTempResult_macroF1[j][i] = TempResultMap.get("macro_F1");
						CBCTempResult_microF1[j][i] = TempResultMap.get("micro_F1");
						CBCTempResult_Entropy[j][i] = TempResultMap.get("Entropy");
					}
					
					d = new Date();
					longtimeA = d.getTime();
					System.out.println("Time3:"+longtimeA);
					
					//for LW-index
					if(j==0)
					{
						if(true){
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							LWATempResult1[i] = CalculateLWradius(StrSrcDir_output+j,3,trainMapNew,testFileMapVsm);

							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							LWATempResu_P1[i] = CalculatePercentLWradius(StrSrcDir_output+j,0.1,trainMapNew,testFileMapVsm);
						
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							Map<String, Double> ReturnMap = CalculateSumofSquaresbasedIndices(StrSrcDir_output+j,3,trainMapNew,testFileMapVsm);
							
							BHITempResult[i] = ReturnMap.get("BallHallIndex");
							CHITempResult[i] = ReturnMap.get("CHIndex");
							HAITempResult[i] = ReturnMap.get("HIndex");
							XUITempResult[i] = ReturnMap.get("XUIndex");
							WBITempResult[i] = ReturnMap.get("WBIndex");
						}
						
						if(true){
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DBITempResult[i] = CalculateDaviesBouldinIndex(StrSrcDir_output+j,1,trainMapNew,testFileMapVsm);

							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							SILTempResult[i] = CalculateSilhouetteIndex(StrSrcDir_output+j,1,trainMapNew,testFileMapVsm);

							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_11[i] = CalculateDunnIndex(StrSrcDir_output+j,1,1,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_12[i] = CalculateDunnIndex(StrSrcDir_output+j,1,2,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_13[i] = CalculateDunnIndex(StrSrcDir_output+j,1,3,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_21[i] = CalculateDunnIndex(StrSrcDir_output+j,2,1,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_22[i] = CalculateDunnIndex(StrSrcDir_output+j,2,2,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_23[i] = CalculateDunnIndex(StrSrcDir_output+j,2,3,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_31[i] = CalculateDunnIndex(StrSrcDir_output+j,3,1,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_32[i] = CalculateDunnIndex(StrSrcDir_output+j,3,2,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_33[i] = CalculateDunnIndex(StrSrcDir_output+j,3,3,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_41[i] = CalculateDunnIndex(StrSrcDir_output+j,4,1,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_42[i] = CalculateDunnIndex(StrSrcDir_output+j,4,2,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_43[i] = CalculateDunnIndex(StrSrcDir_output+j,4,3,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_51[i] = CalculateDunnIndex(StrSrcDir_output+j,5,1,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_52[i] = CalculateDunnIndex(StrSrcDir_output+j,5,2,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_53[i] = CalculateDunnIndex(StrSrcDir_output+j,5,3,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_61[i] = CalculateDunnIndex(StrSrcDir_output+j,6,1,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_62[i] = CalculateDunnIndex(StrSrcDir_output+j,6,2,3,trainMapNew,testFileMapVsm);
							trainMapNew = DataPP.MergeMaptoMap(trainFileMapVsm, testFileMapVsm); 
							DITempResu_63[i] = CalculateDunnIndex(StrSrcDir_output+j,6,3,3,trainMapNew,testFileMapVsm);
						}
					}
				}

				for(int m=0; m<TrainTestNum; m++)
				{
					SVMTempResult_macroF1[TrainTestNum][i] += SVMTempResult_macroF1[m][i];
					SVMTempResult_microF1[TrainTestNum][i] += SVMTempResult_microF1[m][i];
					SVMTempResult_Entropy[TrainTestNum][i] += SVMTempResult_Entropy[m][i];

					CBCTempResult_macroF1[TrainTestNum][i] += CBCTempResult_macroF1[m][i];
					CBCTempResult_microF1[TrainTestNum][i] += CBCTempResult_microF1[m][i];
					CBCTempResult_Entropy[TrainTestNum][i] += CBCTempResult_Entropy[m][i];

				}

					IndexMapNewNum[i] = IndexValueMapNew.size();
					SVMTempResult_macroF1[TrainTestNum][i] = SVMTempResult_macroF1[TrainTestNum][i]/5.0;
					SVMTempResult_microF1[TrainTestNum][i] = SVMTempResult_microF1[TrainTestNum][i]/5.0;
					SVMTempResult_Entropy[TrainTestNum][i] = SVMTempResult_Entropy[TrainTestNum][i]/5.0;

					CBCTempResult_macroF1[TrainTestNum][i] = CBCTempResult_macroF1[TrainTestNum][i]/5.0;
					CBCTempResult_microF1[TrainTestNum][i] = CBCTempResult_microF1[TrainTestNum][i]/5.0;
					CBCTempResult_Entropy[TrainTestNum][i] = CBCTempResult_Entropy[TrainTestNum][i]/5.0;
	
					Map<String, Map<Integer, String>> RESULTMAP = new TreeMap<String, Map<Integer, String>>();
					Map<Integer,String> IndexMapNewNumMap  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTempResult_macroF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTempResult_microF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTempResult_EntropyMap  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTempResult_macroF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTempResult_microF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTempResult_EntropyMap  = new TreeMap<Integer, String>();
					Map<Integer,String> LWITempResultMap1  = new TreeMap<Integer, String>();
					Map<Integer,String> LWATempResultMap1  = new TreeMap<Integer, String>();
					Map<Integer,String> LWIANDRADIUS_Map1  = new TreeMap<Integer, String>();
	
					Map<Integer,String> LWITempResultMap2  = new TreeMap<Integer, String>();
					Map<Integer,String> LWATempResultMap2  = new TreeMap<Integer, String>();
					Map<Integer,String> LWIANDRADIUS_Map2  = new TreeMap<Integer, String>();
	
					Map<Integer,String> LWITempResu_PMap1  = new TreeMap<Integer, String>();
					Map<Integer,String> LWATempResu_PMap1  = new TreeMap<Integer, String>();
					Map<Integer,String> LWIANDRADIU_PMap1  = new TreeMap<Integer, String>();
	
					Map<Integer,String> LWITempResu_PMap2  = new TreeMap<Integer, String>();
					Map<Integer,String> LWATempResu_PMap2  = new TreeMap<Integer, String>();
					Map<Integer,String> LWIANDRADIU_PMap2  = new TreeMap<Integer, String>();
	
					Map<Integer,String> LWCptDistanceMap1  = new TreeMap<Integer, String>();
	
					Map<Integer,String> DBITempResultMap  = new TreeMap<Integer, String>();
					Map<Integer,String> SILTempResultMap  = new TreeMap<Integer, String>();
					
					Map<Integer,String> BHITempResultMap  = new TreeMap<Integer, String>();
					Map<Integer,String> CHITempResultMap  = new TreeMap<Integer, String>();
					Map<Integer,String> HAITempResultMap  = new TreeMap<Integer, String>();
					Map<Integer,String> XUITempResultMap  = new TreeMap<Integer, String>();
					Map<Integer,String> WBITempResultMap  = new TreeMap<Integer, String>();
					
					Map<Integer,String> DITempResu_11Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_12Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_13Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_21Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_22Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_23Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_31Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_32Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_33Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_41Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_42Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_43Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_51Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_52Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_53Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_61Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_62Map  = new TreeMap<Integer, String>();
					Map<Integer,String> DITempResu_63Map  = new TreeMap<Integer, String>();
	
					Map<Integer,String> SVMTemp_First_macroF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Secon_macroF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Three_macroF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Fours_macroF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Fives_macroF1Map  = new TreeMap<Integer, String>();
	
					Map<Integer,String> SVMTemp_First_microF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Secon_microF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Three_microF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Fours_microF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Fives_microF1Map  = new TreeMap<Integer, String>();
	
					Map<Integer,String> SVMTemp_First_EntropyMap  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Secon_EntropyMap  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Three_EntropyMap  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Fours_EntropyMap  = new TreeMap<Integer, String>();
					Map<Integer,String> SVMTemp_Fives_EntropyMap  = new TreeMap<Integer, String>();
	
					Map<Integer,String> CBCTemp_First_macroF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Secon_macroF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Three_macroF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Fours_macroF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Fives_macroF1Map  = new TreeMap<Integer, String>();
	
					Map<Integer,String> CBCTemp_First_microF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Secon_microF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Three_microF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Fours_microF1Map  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Fives_microF1Map  = new TreeMap<Integer, String>();
	
					Map<Integer,String> CBCTemp_First_EntropyMap  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Secon_EntropyMap  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Three_EntropyMap  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Fours_EntropyMap  = new TreeMap<Integer, String>();
					Map<Integer,String> CBCTemp_Fives_EntropyMap  = new TreeMap<Integer, String>();
					
				for(int m=0; m<=i; m++)
				{
					IndexMapNewNumMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+IndexMapNewNum[m],20));
					SVMTempResult_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_macroF1[TrainTestNum][m],20));
					SVMTempResult_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_microF1[TrainTestNum][m],20));
					SVMTempResult_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_Entropy[TrainTestNum][m],20));
					CBCTempResult_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_macroF1[TrainTestNum][m],20));
					CBCTempResult_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_microF1[TrainTestNum][m],20));
					CBCTempResult_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_Entropy[TrainTestNum][m],20));
					LWITempResultMap1.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWITempResult1[m],20));
					LWATempResultMap1.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWATempResult1[m],20));
					LWIANDRADIUS_Map1.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWIANDRADIUS_1[m],20));

					LWITempResultMap2.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWITempResult2[m],20));
					LWATempResultMap2.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWATempResult2[m],20));
					LWIANDRADIUS_Map2.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWIANDRADIUS_2[m],20));

					LWITempResu_PMap1.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWITempResu_P1[m],20));
					LWATempResu_PMap1.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWATempResu_P1[m],20));
					LWIANDRADIU_PMap1.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWIANDRADIU_P1[m],20));

					LWITempResu_PMap2.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWITempResu_P2[m],20));
					LWATempResu_PMap2.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWATempResu_P2[m],20));
					LWIANDRADIU_PMap2.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWIANDRADIU_P2[m],20));

					LWCptDistanceMap1.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+LWCptDistance1[m],20));

					DBITempResultMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DBITempResult[m],20));
					SILTempResultMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SILTempResult[m],20));

					BHITempResultMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+BHITempResult[m],20));
					CHITempResultMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CHITempResult[m],20));
					HAITempResultMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+HAITempResult[m],20));
					XUITempResultMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+XUITempResult[m],20));
					WBITempResultMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+WBITempResult[m],20));
				
					DITempResu_11Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_11[m],20));
					DITempResu_12Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_12[m],20));
					DITempResu_13Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_13[m],20));
					DITempResu_21Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_21[m],20));
					DITempResu_22Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_22[m],20));
					DITempResu_23Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_23[m],20));
					DITempResu_31Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_31[m],20));
					DITempResu_32Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_32[m],20));
					DITempResu_33Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_33[m],20));
					DITempResu_41Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_41[m],20));
					DITempResu_42Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_42[m],20));
					DITempResu_43Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_43[m],20));
					DITempResu_51Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_51[m],20));
					DITempResu_52Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_52[m],20));
					DITempResu_53Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_53[m],20));
					DITempResu_61Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_61[m],20));
					DITempResu_62Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_62[m],20));
					DITempResu_63Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+DITempResu_63[m],20));

					System.out.println("=============================================");
					System.out.println("IndexMapNewNum        :"+DataPP.fixedWidthIntegertoSpace(""+IndexMapNewNum[m],20));
					System.out.println("SVMTempResult_macroF1 :"+DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_macroF1[TrainTestNum][m],20));
					System.out.println("SVMTempResult_microF1 :"+DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_microF1[TrainTestNum][m],20));
					System.out.println("SVMTempResult_Entropy :"+DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_Entropy[TrainTestNum][m],20));
					System.out.println("CBCTempResult_macroF1 :"+DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_macroF1[TrainTestNum][m],20));
					System.out.println("CBCTempResult_microF1 :"+DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_microF1[TrainTestNum][m],20));
					System.out.println("CBCTempResult_Entropy :"+DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_Entropy[TrainTestNum][m],20));

					System.out.println("LWITempResult1        :"+DataPP.fixedWidthIntegertoSpace(""+LWITempResult1[m],20));
					System.out.println("LWATempResult1        :"+DataPP.fixedWidthIntegertoSpace(""+LWATempResult1[m],20));
					System.out.println("LWIANDRADIUS_1        :"+DataPP.fixedWidthIntegertoSpace(""+LWIANDRADIUS_1[m],20));

					System.out.println("LWITempResult2        :"+DataPP.fixedWidthIntegertoSpace(""+LWITempResult2[m],20));
					System.out.println("LWATempResult2        :"+DataPP.fixedWidthIntegertoSpace(""+LWATempResult2[m],20));
					System.out.println("LWIANDRADIUS_2        :"+DataPP.fixedWidthIntegertoSpace(""+LWIANDRADIUS_2[m],20));

					System.out.println("LWITempResu_P1        :"+DataPP.fixedWidthIntegertoSpace(""+LWITempResu_P1[m],20));
					System.out.println("LWATempResu_P1        :"+DataPP.fixedWidthIntegertoSpace(""+LWATempResu_P1[m],20));
					System.out.println("LWIANDRADIU_P1        :"+DataPP.fixedWidthIntegertoSpace(""+LWIANDRADIU_P1[m],20));

					System.out.println("LWITempResu_P2        :"+DataPP.fixedWidthIntegertoSpace(""+LWITempResu_P2[m],20));
					System.out.println("LWATempResu_P2        :"+DataPP.fixedWidthIntegertoSpace(""+LWATempResu_P2[m],20));
					System.out.println("LWIANDRADIU_P2        :"+DataPP.fixedWidthIntegertoSpace(""+LWIANDRADIU_P2[m],20));

					System.out.println("LWCptDistance1        :"+DataPP.fixedWidthIntegertoSpace(""+LWCptDistance1[m],20));

					SVMTemp_First_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_macroF1[0][m],20));
					SVMTemp_Secon_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_macroF1[1][m],20));
					SVMTemp_Three_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_macroF1[2][m],20));
					SVMTemp_Fours_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_macroF1[3][m],20));
					SVMTemp_Fives_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_macroF1[4][m],20));

					SVMTemp_First_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_microF1[0][m],20));
					SVMTemp_Secon_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_microF1[1][m],20));
					SVMTemp_Three_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_microF1[2][m],20));
					SVMTemp_Fours_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_microF1[3][m],20));
					SVMTemp_Fives_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_microF1[4][m],20));

					SVMTemp_First_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_Entropy[0][m],20));
					SVMTemp_Secon_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_Entropy[1][m],20));
					SVMTemp_Three_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_Entropy[2][m],20));
					SVMTemp_Fours_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_Entropy[3][m],20));
					SVMTemp_Fives_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+SVMTempResult_Entropy[4][m],20));

					CBCTemp_First_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_macroF1[0][m],20));
					CBCTemp_Secon_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_macroF1[1][m],20));
					CBCTemp_Three_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_macroF1[2][m],20));
					CBCTemp_Fours_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_macroF1[3][m],20));
					CBCTemp_Fives_macroF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_macroF1[4][m],20));

					CBCTemp_First_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_microF1[0][m],20));
					CBCTemp_Secon_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_microF1[1][m],20));
					CBCTemp_Three_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_microF1[2][m],20));
					CBCTemp_Fours_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_microF1[3][m],20));
					CBCTemp_Fives_microF1Map.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_microF1[4][m],20));

					CBCTemp_First_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_Entropy[0][m],20));
					CBCTemp_Secon_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_Entropy[1][m],20));
					CBCTemp_Three_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_Entropy[2][m],20));
					CBCTemp_Fours_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_Entropy[3][m],20));
					CBCTemp_Fives_EntropyMap.put(IndexMapNewNum[m], DataPP.fixedWidthIntegertoSpace(""+CBCTempResult_Entropy[4][m],20));
					
				}
				
				RESULTMAP.put("001_IndexMapNewNumMap       ", IndexMapNewNumMap);
				RESULTMAP.put("002_SVMTempResult_macroF1Map", SVMTempResult_macroF1Map);
				RESULTMAP.put("003_SVMTempResult_microF1Map", SVMTempResult_microF1Map);
				RESULTMAP.put("004_SVMTempResult_EntropyMap", SVMTempResult_EntropyMap);
				RESULTMAP.put("005_CBCTempResult_macroF1Map", CBCTempResult_macroF1Map);
				RESULTMAP.put("006_CBCTempResult_microF1Map", CBCTempResult_microF1Map);
				RESULTMAP.put("007_CBCTempResult_EntropyMap", CBCTempResult_EntropyMap);

				RESULTMAP.put("101_LWITempResultMap1       ", LWITempResultMap1);
				RESULTMAP.put("102_LWATempResultMap1       ", LWATempResultMap1);
				RESULTMAP.put("103_LWIANDRADIUS_Map1       ", LWIANDRADIUS_Map1);

				RESULTMAP.put("104_LWITempResultMap2       ", LWITempResultMap2);
				RESULTMAP.put("105_LWATempResultMap2       ", LWATempResultMap2);
				RESULTMAP.put("106_LWIANDRADIUS_Map2       ", LWIANDRADIUS_Map2);

				RESULTMAP.put("107_LWITempResu_PMap1       ", LWITempResu_PMap1);
				RESULTMAP.put("108_LWATempResu_PMap1       ", LWATempResu_PMap1);
				RESULTMAP.put("109_LWIANDRADIU_PMap1       ", LWIANDRADIU_PMap1);

				RESULTMAP.put("110_LWITempResu_PMap2       ", LWITempResu_PMap2);
				RESULTMAP.put("111_LWATempResu_PMap2       ", LWATempResu_PMap2);
				RESULTMAP.put("112_LWIANDRADIU_PMap2       ", LWIANDRADIU_PMap2);

				RESULTMAP.put("113_LWCptDistanceMap1       ", LWCptDistanceMap1);

				RESULTMAP.put("200_DBITempResultMap        ", DBITempResultMap);
				RESULTMAP.put("201_SILTempResultMap        ", SILTempResultMap);

				RESULTMAP.put("202_BHITempResultMap        ", BHITempResultMap);
				RESULTMAP.put("203_CHITempResultMap        ", CHITempResultMap);
				RESULTMAP.put("204_HAITempResultMap        ", HAITempResultMap);
				RESULTMAP.put("205_XUITempResultMap        ", XUITempResultMap);
				RESULTMAP.put("206_WBITempResultMap        ", WBITempResultMap);
				
				RESULTMAP.put("211_DITempResu_11Map        ", DITempResu_11Map);
				RESULTMAP.put("212_DITempResu_12Map        ", DITempResu_12Map);
				RESULTMAP.put("213_DITempResu_13Map        ", DITempResu_13Map);

				RESULTMAP.put("221_DITempResu_21Map        ", DITempResu_21Map);
				RESULTMAP.put("222_DITempResu_22Map        ", DITempResu_22Map);
				RESULTMAP.put("223_DITempResu_23Map        ", DITempResu_23Map);

				RESULTMAP.put("231_DITempResu_31Map        ", DITempResu_31Map);
				RESULTMAP.put("232_DITempResu_32Map        ", DITempResu_32Map);
				RESULTMAP.put("233_DITempResu_33Map        ", DITempResu_33Map);

				RESULTMAP.put("241_DITempResu_41Map        ", DITempResu_41Map);
				RESULTMAP.put("242_DITempResu_42Map        ", DITempResu_42Map);
				RESULTMAP.put("243_DITempResu_43Map        ", DITempResu_43Map);

				RESULTMAP.put("251_DITempResu_51Map        ", DITempResu_51Map);
				RESULTMAP.put("252_DITempResu_52Map        ", DITempResu_52Map);
				RESULTMAP.put("253_DITempResu_53Map        ", DITempResu_53Map);

				RESULTMAP.put("261_DITempResu_61Map        ", DITempResu_61Map);
				RESULTMAP.put("262_DITempResu_62Map        ", DITempResu_62Map);
				RESULTMAP.put("263_DITempResu_63Map        ", DITempResu_63Map);


				RESULTMAP.put("311_SVMTemp_First_macroF1Map", SVMTemp_First_macroF1Map);
				RESULTMAP.put("312_SVMTemp_Secon_macroF1Map", SVMTemp_Secon_macroF1Map);
				RESULTMAP.put("313_SVMTemp_Three_macroF1Map", SVMTemp_Three_macroF1Map);
				RESULTMAP.put("314_SVMTemp_Fours_macroF1Map", SVMTemp_Fours_macroF1Map);
				RESULTMAP.put("315_SVMTemp_Fives_macroF1Map", SVMTemp_Fives_macroF1Map);

				RESULTMAP.put("321_SVMTemp_First_microF1Map", SVMTemp_First_microF1Map);
				RESULTMAP.put("322_SVMTemp_Secon_microF1Map", SVMTemp_Secon_microF1Map);
				RESULTMAP.put("323_SVMTemp_Three_microF1Map", SVMTemp_Three_microF1Map);
				RESULTMAP.put("324_SVMTemp_Fours_microF1Map", SVMTemp_Fours_microF1Map);
				RESULTMAP.put("325_SVMTemp_Fives_microF1Map", SVMTemp_Fives_microF1Map);

				RESULTMAP.put("331_SVMTemp_First_EntropyMap", SVMTemp_First_EntropyMap);
				RESULTMAP.put("332_SVMTemp_Secon_EntropyMap", SVMTemp_Secon_EntropyMap);
				RESULTMAP.put("333_SVMTemp_Three_EntropyMap", SVMTemp_Three_EntropyMap);
				RESULTMAP.put("334_SVMTemp_Fours_EntropyMap", SVMTemp_Fours_EntropyMap);
				RESULTMAP.put("335_SVMTemp_Fives_EntropyMap", SVMTemp_Fives_EntropyMap);

				RESULTMAP.put("411_CBCTemp_First_macroF1Map", CBCTemp_First_macroF1Map);
				RESULTMAP.put("412_CBCTemp_Secon_macroF1Map", CBCTemp_Secon_macroF1Map);
				RESULTMAP.put("413_CBCTemp_Three_macroF1Map", CBCTemp_Three_macroF1Map);
				RESULTMAP.put("414_CBCTemp_Fours_macroF1Map", CBCTemp_Fours_macroF1Map);
				RESULTMAP.put("415_CBCTemp_Fives_macroF1Map", CBCTemp_Fives_macroF1Map);

				RESULTMAP.put("421_CBCTemp_First_microF1Map", CBCTemp_First_microF1Map);
				RESULTMAP.put("422_CBCTemp_Secon_microF1Map", CBCTemp_Secon_microF1Map);
				RESULTMAP.put("423_CBCTemp_Three_microF1Map", CBCTemp_Three_microF1Map);
				RESULTMAP.put("424_CBCTemp_Fours_microF1Map", CBCTemp_Fours_microF1Map);
				RESULTMAP.put("425_CBCTemp_Fives_microF1Map", CBCTemp_Fives_microF1Map);

				RESULTMAP.put("431_CBCTemp_First_EntropyMap", CBCTemp_First_EntropyMap);
				RESULTMAP.put("432_CBCTemp_Secon_EntropyMap", CBCTemp_Secon_EntropyMap);
				RESULTMAP.put("433_CBCTemp_Three_EntropyMap", CBCTemp_Three_EntropyMap);
				RESULTMAP.put("434_CBCTemp_Fours_EntropyMap", CBCTemp_Fours_EntropyMap);
				RESULTMAP.put("435_CBCTemp_Fives_EntropyMap", CBCTemp_Fives_EntropyMap);

				String FileName = "LWindex_DRMethod_"+f+".txt";
				DataPP.printSISMapForLW(StrSrcDir_output, FileName , RESULTMAP);

			}
		}
		System.out.println("LWindexMain Finished!!!");
	}
	
//==========================================================================

	public static double CalculateLWradius(String strDir, int Mnum, Map<String, Map<Integer, Double>> TrainFileMapVsm, 
								              Map<String, Map<Integer, Double>> TestFileMapVsm) throws IOException {
		//训练集中相同的类放在一个map中，形成同类样本map
		int  Tsize = TrainFileMapVsm.size();
		Map<String, Map<String, Integer>> CateMap = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<Integer, Double>> CateCenterMap = new TreeMap<String, Map<Integer, Double>>();

		Set<Map.Entry<String, Map<Integer, Double>>> TrainFileMapVsmSet = TrainFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainFileMapVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			
			String Cate = me.getKey().split("_")[0];
			if(CateMap.containsKey(Cate)){
				if(CateMap.get(Cate).containsKey(me.getKey())){
					int count = CateMap.get(Cate).get(me.getKey());
					CateMap.get(Cate).put(me.getKey(), count+1);
				}
				else{
					CateMap.get(Cate).put(me.getKey(),1);
				}
			}
			else{
			    Map<String, Integer> tempMap = new TreeMap<String, Integer>();
				tempMap.put(me.getKey(),1);
				CateMap.put(Cate, tempMap);
			}
		}

        //printSSIMap(strDir, "CateMap.txt", CateMap);
		//寻找每个类别的质心点
		int Ksize = 0;
		int j = 0;
		int i = 0;
		int[] CateNum = new int[CateMap.size()];

		Set<Map.Entry<String, Map<String, Integer>>> CateMapSet = CateMap.entrySet();
		for (Iterator<Map.Entry<String, Map<String, Integer>>> it = CateMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, Integer>> me = it.next();
			CateNum[i++] = me.getValue().size();
			Set<Map.Entry<String, Integer>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();
				if(CateCenterMap.containsKey(me.getKey())){
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						if(CateCenterMap.get(me.getKey()).containsKey(me3.getKey())){
							double p = CateCenterMap.get(me.getKey()).get(me3.getKey());
							CateCenterMap.get(me.getKey()).put(me3.getKey(),p+me3.getValue());
						}else{
							CateCenterMap.get(me.getKey()).put(me3.getKey(), me3.getValue());
						}
					}
				}
				else{
					Map<Integer, Double> tempMap = new TreeMap<Integer, Double>();
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						tempMap.put(me3.getKey(), me3.getValue());
					}
					if(Ksize<tempMap.size()){
						Ksize = tempMap.size();
					}
					CateCenterMap.put(me.getKey(), tempMap);
				}			
			}
		}
        //printSIDMap(strDir, "CateCenterMap.txt", CateCenterMap);

		i = 0;
		String[] CateName = new String[CateCenterMap.size()];
		Set<Map.Entry<String, Map<Integer, Double>>> CateCenterMapSet = CateCenterMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CateCenterMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			CateName[i] = me.getKey();
			Set<Map.Entry<Integer, Double>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();

				CateCenterMap.get(me.getKey()).put(me2.getKey(),me2.getValue()/CateNum[i]);
			}
			i++;
		}
		
		Map<String, Map<Integer, Double>> CateCenterMapNew  = new TreeMap<String, Map<Integer, Double>>();
		CateCenterMapNew = DataPreProcess.copyMaptoMap(CateCenterMap); 

		//注意Point2计算cos距离，Point3计算欧式距离
		Point2 p = new Point2(CateMap, CateCenterMapNew, CateCenterMap, TrainFileMapVsm);
		p.SetMnum(Mnum);

		double LWI = p.GetDataSetLWradius();

		//System.out.println(" LWI    :"+LWI);
		return LWI;
	}

	public static double CalculatePercentLWradius(String strDir, double u, Map<String, Map<Integer, Double>> TrainFileMapVsm, 
								              Map<String, Map<Integer, Double>> TestFileMapVsm) throws IOException {
		//训练集中相同的类放在一个map中，形成同类样本map
		int  Tsize = TrainFileMapVsm.size();
		Map<String, Map<String, Integer>> CateMap = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<Integer, Double>> CateCenterMap = new TreeMap<String, Map<Integer, Double>>();

		Set<Map.Entry<String, Map<Integer, Double>>> TrainFileMapVsmSet = TrainFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainFileMapVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			
			String Cate = me.getKey().split("_")[0];
			if(CateMap.containsKey(Cate)){
				if(CateMap.get(Cate).containsKey(me.getKey())){
					int count = CateMap.get(Cate).get(me.getKey());
					CateMap.get(Cate).put(me.getKey(), count+1);
				}
				else{
					CateMap.get(Cate).put(me.getKey(),1);
				}
			}
			else{
			    Map<String, Integer> tempMap = new TreeMap<String, Integer>();
				tempMap.put(me.getKey(),1);
				CateMap.put(Cate, tempMap);
			}
		}

        //printSSIMap(strDir, "CateMap.txt", CateMap);
		//寻找每个类别的质心点
		int Ksize = 0;
		int j = 0;
		int i = 0;
		int[] CateNum = new int[CateMap.size()];

		Set<Map.Entry<String, Map<String, Integer>>> CateMapSet = CateMap.entrySet();
		for (Iterator<Map.Entry<String, Map<String, Integer>>> it = CateMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, Integer>> me = it.next();
			CateNum[i++] = me.getValue().size();
			Set<Map.Entry<String, Integer>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();
				if(CateCenterMap.containsKey(me.getKey())){
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						if(CateCenterMap.get(me.getKey()).containsKey(me3.getKey())){
							double p = CateCenterMap.get(me.getKey()).get(me3.getKey());
							CateCenterMap.get(me.getKey()).put(me3.getKey(),p+me3.getValue());
						}else{
							CateCenterMap.get(me.getKey()).put(me3.getKey(), me3.getValue());
						}
					}
				}
				else{
					Map<Integer, Double> tempMap = new TreeMap<Integer, Double>();
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						tempMap.put(me3.getKey(), me3.getValue());
					}
					if(Ksize<tempMap.size()){
						Ksize = tempMap.size();
					}
					CateCenterMap.put(me.getKey(), tempMap);
				}			
			}
		}
        //printSIDMap(strDir, "CateCenterMap.txt", CateCenterMap);

		i = 0;
		String[] CateName = new String[CateCenterMap.size()];
		Set<Map.Entry<String, Map<Integer, Double>>> CateCenterMapSet = CateCenterMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CateCenterMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			CateName[i] = me.getKey();
			Set<Map.Entry<Integer, Double>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();

				CateCenterMap.get(me.getKey()).put(me2.getKey(),me2.getValue()/CateNum[i]);
			}
			i++;
		}
		
		Map<String, Map<Integer, Double>> CateCenterMapNew  = new TreeMap<String, Map<Integer, Double>>();
		CateCenterMapNew = DataPreProcess.copyMaptoMap(CateCenterMap); 

		//注意Point2计算cos距离，Point3计算欧式距离
		Point2 p = new Point2(CateMap, CateCenterMapNew, CateCenterMap, TrainFileMapVsm);

		//double LWI = p.GetDataSetPercentLWindex(u);
		double LWI = p.GetDataSetPercentLWradius(u);

		//System.out.println(" LWI    :"+LWI);
		return LWI;
	}
	
//===========================================================================
public static double CalculateDunnIndex(String strDir, int InterIndex, int IntraIndex, int Mnum, Map<String, Map<Integer, Double>> TrainFileMapVsm, 
								              Map<String, Map<Integer, Double>> TestFileMapVsm) throws IOException {
		//训练集中相同的类放在一个map中，形成同类样本map
		int  Tsize = TrainFileMapVsm.size();
		Map<String, Map<String, Integer>> CateMap = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<Integer, Double>> CateCenterMap = new TreeMap<String, Map<Integer, Double>>();

		Set<Map.Entry<String, Map<Integer, Double>>> TrainFileMapVsmSet = TrainFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainFileMapVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			
			String Cate = me.getKey().split("_")[0];
			if(CateMap.containsKey(Cate)){
				if(CateMap.get(Cate).containsKey(me.getKey())){
					int count = CateMap.get(Cate).get(me.getKey());
					CateMap.get(Cate).put(me.getKey(), count+1);
				}
				else{
					CateMap.get(Cate).put(me.getKey(),1);
				}
			}
			else{
			    Map<String, Integer> tempMap = new TreeMap<String, Integer>();
				tempMap.put(me.getKey(),1);
				CateMap.put(Cate, tempMap);
			}
		}

        //printSSIMap(strDir, "CateMap.txt", CateMap);
		//寻找每个类别的质心点
		int Ksize = 0;
		int j = 0;
		int i = 0;
		int[] CateNum = new int[CateMap.size()];

		Set<Map.Entry<String, Map<String, Integer>>> CateMapSet = CateMap.entrySet();
		for (Iterator<Map.Entry<String, Map<String, Integer>>> it = CateMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, Integer>> me = it.next();
			CateNum[i++] = me.getValue().size();
			Set<Map.Entry<String, Integer>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();
				if(CateCenterMap.containsKey(me.getKey())){
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						if(CateCenterMap.get(me.getKey()).containsKey(me3.getKey())){
							double p = CateCenterMap.get(me.getKey()).get(me3.getKey());
							CateCenterMap.get(me.getKey()).put(me3.getKey(),p+me3.getValue());
						}else{
							CateCenterMap.get(me.getKey()).put(me3.getKey(), me3.getValue());
						}
					}
				}
				else{
					Map<Integer, Double> tempMap = new TreeMap<Integer, Double>();
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						tempMap.put(me3.getKey(), me3.getValue());
					}
					if(Ksize<tempMap.size()){
						Ksize = tempMap.size();
					}
					CateCenterMap.put(me.getKey(), tempMap);
				}			
			}
		}
        //printSIDMap(strDir, "CateCenterMap.txt", CateCenterMap);

		i = 0;
		String[] CateName = new String[CateCenterMap.size()];
		Set<Map.Entry<String, Map<Integer, Double>>> CateCenterMapSet = CateCenterMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CateCenterMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			CateName[i] = me.getKey();
			Set<Map.Entry<Integer, Double>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();

				CateCenterMap.get(me.getKey()).put(me2.getKey(),me2.getValue()/CateNum[i]);
			}
			i++;
		}
		
		Map<String, Map<Integer, Double>> CateCenterMapNew  = new TreeMap<String, Map<Integer, Double>>();
		CateCenterMapNew = DataPreProcess.copyMaptoMap(CateCenterMap); 

		//注意Point2计算cos距离，Point3计算欧式距离
		Point2 p = new Point2(CateMap, CateCenterMapNew, CateCenterMap, TrainFileMapVsm);
		p.SetMnum(Mnum);

		return p.DunnIndex(InterIndex, IntraIndex);
	}

	public static double CalculateDaviesBouldinIndex(String strDir, int Mnum, Map<String, Map<Integer, Double>> TrainFileMapVsm, 
								              Map<String, Map<Integer, Double>> TestFileMapVsm) throws IOException {
		//训练集中相同的类放在一个map中，形成同类样本map
		int  Tsize = TrainFileMapVsm.size();
		Map<String, Map<String, Integer>> CateMap = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<Integer, Double>> CateCenterMap = new TreeMap<String, Map<Integer, Double>>();

		Set<Map.Entry<String, Map<Integer, Double>>> TrainFileMapVsmSet = TrainFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainFileMapVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			
			String Cate = me.getKey().split("_")[0];
			if(CateMap.containsKey(Cate)){
				if(CateMap.get(Cate).containsKey(me.getKey())){
					int count = CateMap.get(Cate).get(me.getKey());
					CateMap.get(Cate).put(me.getKey(), count+1);
				}
				else{
					CateMap.get(Cate).put(me.getKey(),1);
				}
			}
			else{
			    Map<String, Integer> tempMap = new TreeMap<String, Integer>();
				tempMap.put(me.getKey(),1);
				CateMap.put(Cate, tempMap);
			}
		}

        //printSSIMap(strDir, "CateMap.txt", CateMap);
		//寻找每个类别的质心点
		int Ksize = 0;
		int j = 0;
		int i = 0;
		int[] CateNum = new int[CateMap.size()];

		Set<Map.Entry<String, Map<String, Integer>>> CateMapSet = CateMap.entrySet();
		for (Iterator<Map.Entry<String, Map<String, Integer>>> it = CateMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, Integer>> me = it.next();
			CateNum[i++] = me.getValue().size();
			Set<Map.Entry<String, Integer>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();
				if(CateCenterMap.containsKey(me.getKey())){
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						if(CateCenterMap.get(me.getKey()).containsKey(me3.getKey())){
							double p = CateCenterMap.get(me.getKey()).get(me3.getKey());
							CateCenterMap.get(me.getKey()).put(me3.getKey(),p+me3.getValue());
						}else{
							CateCenterMap.get(me.getKey()).put(me3.getKey(), me3.getValue());
						}
					}
				}
				else{
					Map<Integer, Double> tempMap = new TreeMap<Integer, Double>();
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						tempMap.put(me3.getKey(), me3.getValue());
					}
					if(Ksize<tempMap.size()){
						Ksize = tempMap.size();
					}
					CateCenterMap.put(me.getKey(), tempMap);
				}			
			}
		}
        //printSIDMap(strDir, "CateCenterMap.txt", CateCenterMap);

		i = 0;
		String[] CateName = new String[CateCenterMap.size()];
		Set<Map.Entry<String, Map<Integer, Double>>> CateCenterMapSet = CateCenterMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CateCenterMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			CateName[i] = me.getKey();
			Set<Map.Entry<Integer, Double>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();

				CateCenterMap.get(me.getKey()).put(me2.getKey(),me2.getValue()/CateNum[i]);
			}
			i++;
		}
		
		Map<String, Map<Integer, Double>> CateCenterMapNew  = new TreeMap<String, Map<Integer, Double>>();
		CateCenterMapNew = DataPreProcess.copyMaptoMap(CateCenterMap); 

		//注意Point2计算cos距离，Point3计算欧式距离
		Point2 p = new Point2(CateMap, CateCenterMapNew, CateCenterMap, TrainFileMapVsm);
		p.SetMnum(Mnum);

		return p.DaviesBouldinIndex();
	}
	
	public static double CalculateSilhouetteIndex(String strDir, int Mnum, Map<String, Map<Integer, Double>> TrainFileMapVsm, 
								              Map<String, Map<Integer, Double>> TestFileMapVsm) throws IOException {
		//训练集中相同的类放在一个map中，形成同类样本map
		int  Tsize = TrainFileMapVsm.size();
		Map<String, Map<String, Integer>> CateMap = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<Integer, Double>> CateCenterMap = new TreeMap<String, Map<Integer, Double>>();

		Set<Map.Entry<String, Map<Integer, Double>>> TrainFileMapVsmSet = TrainFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainFileMapVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			
			String Cate = me.getKey().split("_")[0];
			if(CateMap.containsKey(Cate)){
				if(CateMap.get(Cate).containsKey(me.getKey())){
					int count = CateMap.get(Cate).get(me.getKey());
					CateMap.get(Cate).put(me.getKey(), count+1);
				}
				else{
					CateMap.get(Cate).put(me.getKey(),1);
				}
			}
			else{
			    Map<String, Integer> tempMap = new TreeMap<String, Integer>();
				tempMap.put(me.getKey(),1);
				CateMap.put(Cate, tempMap);
			}
		}

        //printSSIMap(strDir, "CateMap.txt", CateMap);
		//寻找每个类别的质心点
		int Ksize = 0;
		int j = 0;
		int i = 0;
		int[] CateNum = new int[CateMap.size()];

		Set<Map.Entry<String, Map<String, Integer>>> CateMapSet = CateMap.entrySet();
		for (Iterator<Map.Entry<String, Map<String, Integer>>> it = CateMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, Integer>> me = it.next();
			CateNum[i++] = me.getValue().size();
			Set<Map.Entry<String, Integer>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();
				if(CateCenterMap.containsKey(me.getKey())){
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						if(CateCenterMap.get(me.getKey()).containsKey(me3.getKey())){
							double p = CateCenterMap.get(me.getKey()).get(me3.getKey());
							CateCenterMap.get(me.getKey()).put(me3.getKey(),p+me3.getValue());
						}else{
							CateCenterMap.get(me.getKey()).put(me3.getKey(), me3.getValue());
						}
					}
				}
				else{
					Map<Integer, Double> tempMap = new TreeMap<Integer, Double>();
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						tempMap.put(me3.getKey(), me3.getValue());
					}
					if(Ksize<tempMap.size()){
						Ksize = tempMap.size();
					}
					CateCenterMap.put(me.getKey(), tempMap);
				}			
			}
		}
        //printSIDMap(strDir, "CateCenterMap.txt", CateCenterMap);

		i = 0;
		String[] CateName = new String[CateCenterMap.size()];
		Set<Map.Entry<String, Map<Integer, Double>>> CateCenterMapSet = CateCenterMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CateCenterMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			CateName[i] = me.getKey();
			Set<Map.Entry<Integer, Double>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();

				CateCenterMap.get(me.getKey()).put(me2.getKey(),me2.getValue()/CateNum[i]);
			}
			i++;
		}
		
		Map<String, Map<Integer, Double>> CateCenterMapNew  = new TreeMap<String, Map<Integer, Double>>();
		CateCenterMapNew = DataPreProcess.copyMaptoMap(CateCenterMap); 

		//注意Point2计算cos距离，Point3计算欧式距离
		Point2 p = new Point2(CateMap, CateCenterMapNew, CateCenterMap, TrainFileMapVsm);
		p.SetMnum(Mnum);

		return p.SilhouetteIndex();
	}

	
	public static Map<String, Double> CalculateSumofSquaresbasedIndices(String strDir, int Mnum, Map<String, Map<Integer, Double>> TrainFileMapVsm, 
								              Map<String, Map<Integer, Double>> TestFileMapVsm) throws IOException {
		//训练集中相同的类放在一个map中，形成同类样本map
		int  Tsize = TrainFileMapVsm.size();
		Map<String, Map<String, Integer>> CateMap = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<Integer, Double>> CateCenterMap = new TreeMap<String, Map<Integer, Double>>();

		Set<Map.Entry<String, Map<Integer, Double>>> TrainFileMapVsmSet = TrainFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainFileMapVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			
			String Cate = me.getKey().split("_")[0];
			if(CateMap.containsKey(Cate)){
				if(CateMap.get(Cate).containsKey(me.getKey())){
					int count = CateMap.get(Cate).get(me.getKey());
					CateMap.get(Cate).put(me.getKey(), count+1);
				}
				else{
					CateMap.get(Cate).put(me.getKey(),1);
				}
			}
			else{
			    Map<String, Integer> tempMap = new TreeMap<String, Integer>();
				tempMap.put(me.getKey(),1);
				CateMap.put(Cate, tempMap);
			}
		}

        //printSSIMap(strDir, "CateMap.txt", CateMap);
		//寻找每个类别的质心点
		int Ksize = 0;
		int j = 0;
		int i = 0;
		int[] CateNum = new int[CateMap.size()];

		Set<Map.Entry<String, Map<String, Integer>>> CateMapSet = CateMap.entrySet();
		for (Iterator<Map.Entry<String, Map<String, Integer>>> it = CateMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, Integer>> me = it.next();
			CateNum[i++] = me.getValue().size();
			Set<Map.Entry<String, Integer>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();
				if(CateCenterMap.containsKey(me.getKey())){
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						if(CateCenterMap.get(me.getKey()).containsKey(me3.getKey())){
							double p = CateCenterMap.get(me.getKey()).get(me3.getKey());
							CateCenterMap.get(me.getKey()).put(me3.getKey(),p+me3.getValue());
						}else{
							CateCenterMap.get(me.getKey()).put(me3.getKey(), me3.getValue());
						}
					}
				}
				else{
					Map<Integer, Double> tempMap = new TreeMap<Integer, Double>();
					Set<Map.Entry<Integer, Double>> allVsmSet3 = TrainFileMapVsm.get(me2.getKey()).entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it3 = allVsmSet3.iterator(); it3.hasNext();) {
						Map.Entry<Integer, Double> me3 = it3.next();
						tempMap.put(me3.getKey(), me3.getValue());
					}
					if(Ksize<tempMap.size()){
						Ksize = tempMap.size();
					}
					CateCenterMap.put(me.getKey(), tempMap);
				}			
			}
		}
        //printSIDMap(strDir, "CateCenterMap.txt", CateCenterMap);

		i = 0;
		String[] CateName = new String[CateCenterMap.size()];
		Set<Map.Entry<String, Map<Integer, Double>>> CateCenterMapSet = CateCenterMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CateCenterMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			CateName[i] = me.getKey();
			Set<Map.Entry<Integer, Double>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();

				CateCenterMap.get(me.getKey()).put(me2.getKey(),me2.getValue()/CateNum[i]);
			}
			i++;
		}
		
		Map<String, Map<Integer, Double>> CateCenterMapNew  = new TreeMap<String, Map<Integer, Double>>();
		CateCenterMapNew = DataPreProcess.copyMaptoMap(CateCenterMap); 

		//注意Point2计算cos距离，Point3计算欧式距离
		Point2 p = new Point2(CateMap, CateCenterMapNew, CateCenterMap, TrainFileMapVsm);
		p.SetMnum(Mnum);

		return p.SumofSquaresbasedIndices();
	}

}
