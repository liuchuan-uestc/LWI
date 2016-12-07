package PreProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import SVM.libsvm.svm_node;
import SVM.libsvm.svm_parameter;
import SVM.libsvm.svm_print_interface;

/**
 * Newsgroups文档集预处理类
 */
public class DataPreProcess {


	public void CopyDir(String StrSrcDir) throws IOException {

		File FileDataDir = new File(StrSrcDir);
		if (!FileDataDir.exists()) {
			System.out.println("File not exist:" + StrSrcDir);
			return;
		}

		String StrSubDir = StrSrcDir.substring(StrSrcDir.lastIndexOf('/'));
		String StrDesDir = StrSrcDir + "/../../0_StemedSample" + StrSubDir;
		File FileDesDir = new File(StrDesDir);
		if (!FileDesDir.exists()) {
			FileDesDir.mkdirs();
		}

		File[] srcFiles = FileDataDir.listFiles();
		String[] stemFileNames = new String[srcFiles.length];
		if(srcFiles.length > 1){
			for (int i = 0; i < srcFiles.length; i++) {
				String fileFullName = srcFiles[i].getCanonicalPath();
				String fileShortName = srcFiles[i].getName();
				// 确认子文件名不是目录如果是可以再次递归调用s
				if (!new File(fileFullName).isDirectory()) {
					// System.out.println("Begin preprocess:" + fileFullName);
					StringBuilder stringBuilder = new StringBuilder();
					fileShortName = fixedWidthIntegertoString(fileShortName, 8);
					stringBuilder.append(StrDesDir + "/" + fileShortName);
					copyProcessFile(StrSrcDir, fileFullName, stringBuilder.toString());
				} else {
					fileFullName = fileFullName.replace("\\", "/");
					CopyDir(fileFullName);
				}
			}
		}
	}

	public static Map<String, Map<Integer, Double>> copyMaptoMap(Map<String, Map<Integer, Double>> NameMap){

		Map<String, Map<Integer, Double>> NameMapNew = new TreeMap<String, Map<Integer, Double>>();
		Set<Map.Entry<String, Map<Integer, Double>>> NameMapSet = NameMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = NameMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			    TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
				Set<Map.Entry<Integer, Double>> cSet = me.getValue().entrySet();
				for (Iterator<Map.Entry<Integer, Double>> cit = cSet.iterator(); cit.hasNext();) {
					Map.Entry<Integer, Double> cme = cit.next();
					tempMap.put(cme.getKey(), cme.getValue());
				}
				NameMapNew.put(me.getKey(), tempMap);
		}
		return NameMapNew;
	}

	public static void printSISMapForLW(String strDir, String fileName,
			Map<String, Map<Integer, String>> wordMap) throws IOException {
		System.out.println("printSISMapForLW:" + strDir + fileName);
		int countLine = 0;
		File FileDir = new File(strDir);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
		
		File outPutFile = new File(strDir + fileName);
		FileWriter outPutFileWriter = new FileWriter(outPutFile);

		Set<Map.Entry<String, Map<Integer, String>>> wordMapSet = wordMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, String>>> it = wordMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, String>> me = it.next();
			outPutFileWriter.write(me.getKey() + "       ");
		
			Set<Map.Entry<Integer, String>> allWords = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, String>> it2 = allWords.iterator(); it2.hasNext();) {
				Map.Entry<Integer, String> me2 = it2.next();
				outPutFileWriter.write(me2.getValue() + "       ");
				
			}
			outPutFileWriter.write("\n");
			countLine++;
		}
		outPutFileWriter.flush();
		outPutFileWriter.close();
		System.out.println(fileName + " size is " + countLine);
	}

	/**
	 * 对每行字符串进行处理，主要是词法分析、去停用词和stemming
	 * 
	 * @param line
	 *            待处理的一行字符串
	 * @param ArrayList
	 *            <String> 停用词数组
	 * @return String 处理好的一行字符串，是由处理好的单词重新生成，以空格为分隔符
	 * @throws IOException
	 */
	private static String lineProcess(String line,
			ArrayList<String> stopWordsArray) throws IOException {
		// TODO Auto-generated method stub
		// step1 英文词法分析，去除数字、连字符、标点符号、特殊字符，所有大写字母转换成小写，可以考虑用正则表达式
		String res[] = line.split("[^a-zA-Z]");
		String resString = new String();
		// step2去停用词
		// step3stemming,返回后一起做
		for (int i = 0; i < res.length; i++) {
			if (!res[i].isEmpty()
					&& !stopWordsArray.contains(res[i].toLowerCase())) {
				resString += " " + res[i].toLowerCase() + " ";
			}
		}
		return resString;
	}

	public static void printIDMap(String strDir, String fileName,
			Map<Integer, Double> wordMap) throws IOException {
		System.out.println("printIDMap:" + strDir + fileName);
		int countLine = 0;
		File FileDir = new File(strDir);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
		
		File outPutFile = new File(strDir + fileName);
		FileWriter outPutFileWriter = new FileWriter(outPutFile);
		Set<Map.Entry<Integer, Double>> allWords = wordMap.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> it = allWords.iterator(); it
				.hasNext();) {
			Map.Entry<Integer, Double> me = it.next();
			outPutFileWriter.write(me.getKey() + " " + me.getValue() + "\n");
			countLine++;
		}

		outPutFileWriter.flush();
		outPutFileWriter.close();
		System.out.println(fileName + " size is " + countLine);
	}

	public static void printSIMap(String strDir, String fileName,
			Map<String, Integer> wordMap) throws IOException {
		System.out.println("printSIMap:" + strDir + fileName);
		int countLine = 0;
		File FileDir = new File(strDir);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
		
		File outPutFile = new File(strDir + fileName);
		FileWriter outPutFileWriter = new FileWriter(outPutFile);
		Set<Map.Entry<String, Integer>> allWords = wordMap.entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allWords.iterator(); it
				.hasNext();) {
			Map.Entry<String, Integer> me = it.next();
			outPutFileWriter.write(me.getKey() + " " + me.getValue() + "\n");
			countLine++;
		}
		outPutFileWriter.flush();
		outPutFileWriter.close();

		System.out.println(fileName + " size is " + countLine);
	}

	public static void printISMap(String strDir, String fileName,
			Map<Integer, String> wordMap) throws IOException {
		System.out.println("printISMap:" + strDir + fileName);
		int countLine = 0;
		File FileDir = new File(strDir);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
		
		File outPutFile = new File(strDir + fileName);
		FileWriter outPutFileWriter = new FileWriter(outPutFile);
		Set<Map.Entry<Integer, String>> allWords = wordMap.entrySet();
		for (Iterator<Map.Entry<Integer, String>> it = allWords.iterator(); it
				.hasNext();) {
			Map.Entry<Integer, String> me = it.next();
			outPutFileWriter.write(me.getKey() + " " + me.getValue() + "\n");
			countLine++;
		}
		outPutFileWriter.flush();
		outPutFileWriter.close();
		System.out.println(fileName + " size is " + countLine);
	}

	/***********************************************************************/
	/*
	 * IGDR /* 作用： /* 卡方统计，计算每个单词与类的相关度 /* 参数： /* /* 返回： /* /*
	 * /**********************************************************************
	 */
	public Map<Integer, Double> DRByIGandTFIDF(String srcDir,
			double SlectPecent, int tfHold) throws IOException {

		System.out.println("DRByIGandTFIDF srcDir:" + srcDir);
		int PrintDebug = 0;
		int[][] DocClassTerm;
		int[] DocNumPerClass;
		int MaxTerm = 0;
		int MaxDoc = 0;
		int MaxLabel = 0;

		String word;
		int countLine = 0;
		double wordWeight;
		DimensionReduction DR = new DimensionReduction();
		SortedMap<Integer, Double> indexTDIDFMap = new TreeMap<Integer, Double>();
		SortedMap<String, Integer> WordindexMap = new TreeMap<String, Integer>();
		SortedMap<Integer, String> indexWordMap = new TreeMap<Integer, String>();
		SortedMap<String, Double> NewwordMap = new TreeMap<String, Double>();
		SortedMap<String, Double> IDFPerWordMap = new TreeMap<String, Double>();
		SortedMap<String, Double> TFPerDocMap = new TreeMap<String, Double>();
		SortedMap<String, Integer> TermPerDocMap = new TreeMap<String, Integer>();
		// wordMap中保存2_ReducTFSample文件夹中出现次数大于tfHold的词条
		NewwordMap = DR.countWords(srcDir, tfHold, NewwordMap);

		IDFPerWordMap = DR.computeIDF(srcDir, NewwordMap);

		Set<Map.Entry<String, Double>> allWords = NewwordMap.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allWords.iterator(); it
				.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			WordindexMap.put(me.getKey(), ++countLine);
			indexWordMap.put(countLine, me.getKey());
		}
		// 词条编号从1开始计数
		MaxTerm = countLine + 1;

		File[] sampleDir = new File(srcDir).listFiles();
		// 类别编号从1开始计数
		MaxLabel = sampleDir.length + 1;
		DocNumPerClass = new int[MaxLabel];
		DocClassTerm = new int[MaxTerm][MaxLabel];
		for (int i = 0; i < sampleDir.length; i++) {
			File[] sample = sampleDir[i].listFiles();
			DocNumPerClass[i + 1] = sample.length;
			MaxDoc += sample.length;

			String targetDir = srcDir + "/../3_DRByIGandTFIDF/"
					+ sampleDir[i].getName();
			File targetDirFile = new File(targetDir);
			if (!targetDirFile.exists()) {
				targetDirFile.mkdirs();
			}
			for (int j = 0; j < sample.length; j++) {
				TFPerDocMap.clear();
				indexTDIDFMap.clear();
				TFPerDocMap = DR.computeTFPerDoc(sample[j], NewwordMap);
				TermPerDocMap = DR.computeTermPerDoc(sample[j], NewwordMap);
				String fileShortName = sample[j].getName();

				if (fileShortName.contains("stemed")) {
					targetDir = srcDir + "/../3_DRByIGandTFIDF/"
							+ sampleDir[i].getName() + "/"
							+ fileShortName.substring(0, 8);

					FileWriter tgWriter = new FileWriter(targetDir + "stemed");
					FileReader samReader = new FileReader(sample[j]);
					BufferedReader samBR = new BufferedReader(samReader);
					// wordMap为降维后的特征，把wordMap中保存的词条过滤到新文件夹2_ReducTFSample中
					// 2_ReducTFSample文件夹中保存降维后的词条
					while ((word = samBR.readLine()) != null) {
						if (NewwordMap.containsKey(word)) {
							wordWeight = TFPerDocMap.get(word)
									* IDFPerWordMap.get(word);

							if ((PrintDebug & 0x01) == 0x01) {
								tgWriter.append(WordindexMap.get(word) + " "
										+ word + " " + wordWeight + "\n");
							}
							indexTDIDFMap.put(WordindexMap.get(word),
									wordWeight);
							DocClassTerm[WordindexMap.get(word)][i + 1]++;

						}
					}
					if ((PrintDebug & 0x01) == 0x01) {
						tgWriter.flush();
						tgWriter.close();

						FileWriter tgWriterVSM = new FileWriter(targetDir
								+ "vsm");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterVSM.append(me.getKey() + " "
									+ me.getValue() + "\n");
						}

						tgWriterVSM.flush();
						tgWriterVSM.close();

						FileWriter tgWriterTF = new FileWriter(targetDir + "TF");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet2 = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet2
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterTF.append(me.getKey()
									+ " "
									+ TermPerDocMap.get(indexWordMap.get(me
											.getKey())) + "\n");
						}

						tgWriterTF.flush();
						tgWriterTF.close();
					}
				}
			}
		}
		// 输出词典编号文件
		if ((PrintDebug & 0x02) == 0x02) {
			printSIMap(srcDir + "/../0_outputfile/", "WordindexMapIG.txt",
					WordindexMap);
			printISMap(srcDir + "/../0_outputfile/", "indexWordMapIG.txt",
					indexWordMap);
		}
		Map<Integer, Double> indexIGMap = new TreeMap<Integer, Double>();
		indexIGMap = DR.IGDR(DocClassTerm, DocNumPerClass, MaxTerm, MaxDoc,
				MaxLabel);
		if ((PrintDebug & 0x03) == 0x03) {
			printIDMap(srcDir + "/../0_outputfile/", "indexIGMapOld.txt",
					indexIGMap);
		}
		indexIGMap = DR.SlectWordByValue(indexIGMap, SlectPecent);
		if ((PrintDebug & 0x03) == 0x03) {
			printIDMap(srcDir + "/../0_outputfile/", "indexIGMapNew.txt",
					indexIGMap);
		}

		if ((PrintDebug & 0x08) == 0x08) {
			File[] sampleDir1 = new File(srcDir).listFiles();
			for (int i = 0; i < sampleDir1.length; i++) {
				File[] sample = sampleDir1[i].listFiles();
				String targetDir = srcDir + "/../3_DRByIGandTFIDF/"
						+ sampleDir1[i].getName();
				File targetDirFile = new File(targetDir);
				if (!targetDirFile.exists()) {
					targetDirFile.mkdirs();
				}
				for (int j = 0; j < sample.length; j++) {
					TFPerDocMap.clear();
					indexTDIDFMap.clear();
					TFPerDocMap = DR.computeTFPerDoc(sample[j], NewwordMap);
					TermPerDocMap = DR.computeTermPerDoc(sample[j], NewwordMap);
					String fileShortName = sample[j].getName();

					if (fileShortName.contains("stemed")) {
						targetDir = srcDir + "/../3_DRByIGandTFIDF/"
								+ sampleDir1[i].getName() + "/"
								+ fileShortName.substring(0, 8);
						FileWriter tgWriter = new FileWriter(targetDir
								+ "stemedIG");
						FileReader samReader = new FileReader(sample[j]);
						BufferedReader samBR = new BufferedReader(samReader);
						// wordMap为降维后的特征，把wordMap中保存的词条过滤到新文件夹2_ReducTFSample中
						// 2_ReducTFSample文件夹中保存降维后的词条
						while ((word = samBR.readLine()) != null) {
							if ((NewwordMap.containsKey(word))
									&& (indexIGMap.containsKey(WordindexMap
											.get(word)))) {
								wordWeight = TFPerDocMap.get(word)
										* IDFPerWordMap.get(word);
								tgWriter.append(WordindexMap.get(word) + " "
										+ word + " " + wordWeight + "\n");
								indexTDIDFMap.put(WordindexMap.get(word),
										wordWeight);
							}
						}
						tgWriter.flush();
						tgWriter.close();

						FileWriter tgWriterVSM = new FileWriter(targetDir
								+ "vsmIG");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterVSM.append(me.getKey() + " "
									+ me.getValue() + "\n");
						}

						tgWriterVSM.flush();
						tgWriterVSM.close();

						FileWriter tgWriterTF = new FileWriter(targetDir
								+ "TFIG");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet2 = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet2
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterTF.append(me.getKey()
									+ " "
									+ TermPerDocMap.get(indexWordMap.get(me
											.getKey())) + "\n");
						}

						tgWriterTF.flush();
						tgWriterTF.close();
					}
				}
			}
		}

		return indexIGMap;
	}

	/***********************************************************************/
	/*
	 * CSDR /* 作用： /* 卡方统计，计算每个单词与类的相关度 /* 参数： /* /* 返回： /* /*
	 * /**********************************************************************
	 */
	public Map<Integer, Double> DRByCSandTFIDF(String srcDir,
			double SlectPecent, int tfHold) throws IOException {

		System.out.println("DRByCSandTFIDF srcDir:" + srcDir);
		int PrintDebug = 0;
		int[][] DocClassTerm;
		int[] DocNumPerClass;
		int MaxTerm = 0;
		int MaxDoc = 0;
		int MaxLabel = 0;

		String word;
		int countLine = 0;
		double wordWeight;
		DimensionReduction DR = new DimensionReduction();
		SortedMap<Integer, Double> indexTDIDFMap = new TreeMap<Integer, Double>();
		SortedMap<String, Integer> WordindexMap = new TreeMap<String, Integer>();
		SortedMap<Integer, String> indexWordMap = new TreeMap<Integer, String>();
		SortedMap<String, Double> NewwordMap = new TreeMap<String, Double>();
		SortedMap<String, Double> IDFPerWordMap = new TreeMap<String, Double>();
		SortedMap<String, Double> TFPerDocMap = new TreeMap<String, Double>();
		SortedMap<String, Integer> TermPerDocMap = new TreeMap<String, Integer>();
		// wordMap中保存2_ReducTFSample文件夹中出现次数大于tfHold的词条
		NewwordMap = DR.countWords(srcDir, tfHold, NewwordMap);

		IDFPerWordMap = DR.computeIDF(srcDir, NewwordMap);

		Set<Map.Entry<String, Double>> allWords = NewwordMap.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allWords.iterator(); it
				.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			WordindexMap.put(me.getKey(), ++countLine);
			indexWordMap.put(countLine, me.getKey());
		}
		// 词条编号从1开始计数
		MaxTerm = countLine + 1;

		File[] sampleDir = new File(srcDir).listFiles();
		// 类别编号从1开始计数
		MaxLabel = sampleDir.length + 1;
		DocNumPerClass = new int[MaxLabel];
		DocClassTerm = new int[MaxTerm][MaxLabel];
		for (int i = 0; i < sampleDir.length; i++) {
			File[] sample = sampleDir[i].listFiles();
			DocNumPerClass[i + 1] = sample.length;
			MaxDoc += sample.length;

			String targetDir = srcDir + "/../3_DRByCSandTFIDF/"
					+ sampleDir[i].getName();
			File targetDirFile = new File(targetDir);
			if (!targetDirFile.exists()) {
				targetDirFile.mkdirs();
			}
			for (int j = 0; j < sample.length; j++) {
				TFPerDocMap.clear();
				indexTDIDFMap.clear();
				TFPerDocMap = DR.computeTFPerDoc(sample[j], NewwordMap);
				TermPerDocMap = DR.computeTermPerDoc(sample[j], NewwordMap);
				String fileShortName = sample[j].getName();

				if (fileShortName.contains("stemed")) {
					targetDir = srcDir + "/../3_DRByCSandTFIDF/"
							+ sampleDir[i].getName() + "/"
							+ fileShortName.substring(0, 8);

					FileWriter tgWriter = new FileWriter(targetDir + "stemed");
					FileReader samReader = new FileReader(sample[j]);
					BufferedReader samBR = new BufferedReader(samReader);
					// wordMap为降维后的特征，把wordMap中保存的词条过滤到新文件夹2_ReducTFSample中
					// 2_ReducTFSample文件夹中保存降维后的词条
					while ((word = samBR.readLine()) != null) {
						if (NewwordMap.containsKey(word)) {
							wordWeight = TFPerDocMap.get(word)
									* IDFPerWordMap.get(word);

							if ((PrintDebug & 0x01) == 0x01) {
								tgWriter.append(WordindexMap.get(word) + " "
										+ word + " " + wordWeight + "\n");
							}
							indexTDIDFMap.put(WordindexMap.get(word),
									wordWeight);
							DocClassTerm[WordindexMap.get(word)][i + 1]++;

						}
					}
					if ((PrintDebug & 0x01) == 0x01) {
						tgWriter.flush();
						tgWriter.close();

						FileWriter tgWriterVSM = new FileWriter(targetDir
								+ "vsm");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterVSM.append(me.getKey() + " "
									+ me.getValue() + "\n");
						}

						tgWriterVSM.flush();
						tgWriterVSM.close();

						FileWriter tgWriterTF = new FileWriter(targetDir + "TF");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet2 = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet2
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterTF.append(me.getKey()
									+ " "
									+ TermPerDocMap.get(indexWordMap.get(me
											.getKey())) + "\n");
						}

						tgWriterTF.flush();
						tgWriterTF.close();
					}
				}
			}
		}
		// 输出词典编号文件
		if ((PrintDebug & 0x02) == 0x02) {
			printSIMap(srcDir + "/../0_outputfile/", "WordindexMapCS.txt",
					WordindexMap);
			printISMap(srcDir + "/../0_outputfile/", "indexWordMapCS.txt",
					indexWordMap);
		}
		Map<Integer, Double> indexCSMap = new TreeMap<Integer, Double>();
		indexCSMap = DR.CSDR(DocClassTerm, DocNumPerClass, MaxTerm, MaxDoc,
				MaxLabel);
		if ((PrintDebug & 0x03) == 0x03) {
			printIDMap(srcDir + "/../0_outputfile/", "indexCSMapOld.txt",
					indexCSMap);
		}
		indexCSMap = DR.SlectWordByValue(indexCSMap, SlectPecent);
		if ((PrintDebug & 0x03) == 0x03) {
			printIDMap(srcDir + "/../0_outputfile/", "indexCSMapNew.txt",
					indexCSMap);
		}

		if ((PrintDebug & 0x08) == 0x08) {
			File[] sampleDir1 = new File(srcDir).listFiles();
			for (int i = 0; i < sampleDir1.length; i++) {
				File[] sample = sampleDir1[i].listFiles();
				String targetDir = srcDir + "/../3_DRByCSandTFIDF/"
						+ sampleDir1[i].getName();
				File targetDirFile = new File(targetDir);
				if (!targetDirFile.exists()) {
					targetDirFile.mkdirs();
				}
				for (int j = 0; j < sample.length; j++) {
					TFPerDocMap.clear();
					indexTDIDFMap.clear();
					TFPerDocMap = DR.computeTFPerDoc(sample[j], NewwordMap);
					TermPerDocMap = DR.computeTermPerDoc(sample[j], NewwordMap);
					String fileShortName = sample[j].getName();

					if (fileShortName.contains("stemed")) {
						targetDir = srcDir + "/../3_DRByCSandTFIDF/"
								+ sampleDir1[i].getName() + "/"
								+ fileShortName.substring(0, 8);
						FileWriter tgWriter = new FileWriter(targetDir
								+ "stemedCS");
						FileReader samReader = new FileReader(sample[j]);
						BufferedReader samBR = new BufferedReader(samReader);
						// wordMap为降维后的特征，把wordMap中保存的词条过滤到新文件夹2_ReducTFSample中
						// 2_ReducTFSample文件夹中保存降维后的词条
						while ((word = samBR.readLine()) != null) {
							if ((NewwordMap.containsKey(word))
									&& (indexCSMap.containsKey(WordindexMap
											.get(word)))) {
								wordWeight = TFPerDocMap.get(word)
										* IDFPerWordMap.get(word);
								tgWriter.append(WordindexMap.get(word) + " "
										+ word + " " + wordWeight + "\n");
								indexTDIDFMap.put(WordindexMap.get(word),
										wordWeight);
							}
						}
						tgWriter.flush();
						tgWriter.close();

						FileWriter tgWriterVSM = new FileWriter(targetDir
								+ "vsmCS");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterVSM.append(me.getKey() + " "
									+ me.getValue() + "\n");
						}

						tgWriterVSM.flush();
						tgWriterVSM.close();

						FileWriter tgWriterTF = new FileWriter(targetDir
								+ "TFCS");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet2 = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet2
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterTF.append(me.getKey()
									+ " "
									+ TermPerDocMap.get(indexWordMap.get(me
											.getKey())) + "\n");
						}

						tgWriterTF.flush();
						tgWriterTF.close();
					}
				}
			}
		}

		return indexCSMap;
	}

	/***********************************************************************/
	/*
	 * MIDR /* 作用： /* 互信息，计算每个单词与类的依赖程度 /* 参数： /* /* 返回： /* /*
	 * /**********************************************************************
	 */
	public Map<Integer, Double> DRByMIandTFIDF(String srcDir,
			double SlectPecent, int tfHold) throws IOException {

		System.out.println("DRByMIandTFIDF srcDir:" + srcDir);
		int PrintDebug = 0;
		int[][] DocClassTerm;
		int[] DocNumPerClass;
		int MaxTerm = 0;
		int MaxDoc = 0;
		int MaxLabel = 0;

		String word;
		int countLine = 0;
		double wordWeight;
		DimensionReduction DR = new DimensionReduction();
		SortedMap<Integer, Double> indexTDIDFMap = new TreeMap<Integer, Double>();
		SortedMap<String, Integer> WordindexMap = new TreeMap<String, Integer>();
		SortedMap<Integer, String> indexWordMap = new TreeMap<Integer, String>();
		SortedMap<String, Double> NewwordMap = new TreeMap<String, Double>();
		SortedMap<String, Double> IDFPerWordMap = new TreeMap<String, Double>();
		SortedMap<String, Double> TFPerDocMap = new TreeMap<String, Double>();
		SortedMap<String, Integer> TermPerDocMap = new TreeMap<String, Integer>();
		// wordMap中保存2_ReducTFSample文件夹中出现次数大于tfHold的词条
		NewwordMap = DR.countWords(srcDir, tfHold, NewwordMap);

		IDFPerWordMap = DR.computeIDF(srcDir, NewwordMap);

		Set<Map.Entry<String, Double>> allWords = NewwordMap.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allWords.iterator(); it
				.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			WordindexMap.put(me.getKey(), ++countLine);
			indexWordMap.put(countLine, me.getKey());
		}
		// 词条编号从1开始计数
		MaxTerm = countLine + 1;

		File[] sampleDir = new File(srcDir).listFiles();
		// 类别编号从1开始计数
		MaxLabel = sampleDir.length + 1;
		DocNumPerClass = new int[MaxLabel];
		DocClassTerm = new int[MaxTerm][MaxLabel];
		for (int i = 0; i < sampleDir.length; i++) {
			File[] sample = sampleDir[i].listFiles();
			DocNumPerClass[i + 1] = sample.length;
			MaxDoc += sample.length;

			String targetDir = srcDir + "/../3_DRByMIandTFIDF/"
					+ sampleDir[i].getName();
			File targetDirFile = new File(targetDir);
			if (!targetDirFile.exists()) {
				targetDirFile.mkdirs();
			}
			for (int j = 0; j < sample.length; j++) {
				TFPerDocMap.clear();
				indexTDIDFMap.clear();
				TFPerDocMap = DR.computeTFPerDoc(sample[j], NewwordMap);
				TermPerDocMap = DR.computeTermPerDoc(sample[j], NewwordMap);
				String fileShortName = sample[j].getName();

				if (fileShortName.contains("stemed")) {
					targetDir = srcDir + "/../3_DRByMIandTFIDF/"
							+ sampleDir[i].getName() + "/"
							+ fileShortName.substring(0, 8);

					FileWriter tgWriter = new FileWriter(targetDir + "stemed");
					FileReader samReader = new FileReader(sample[j]);
					BufferedReader samBR = new BufferedReader(samReader);
					// wordMap为降维后的特征，把wordMap中保存的词条过滤到新文件夹2_ReducTFSample中
					// 2_ReducTFSample文件夹中保存降维后的词条
					while ((word = samBR.readLine()) != null) {
						if (NewwordMap.containsKey(word)) {
							wordWeight = TFPerDocMap.get(word)
									* IDFPerWordMap.get(word);

							if ((PrintDebug & 0x01) == 0x01) {
								tgWriter.append(WordindexMap.get(word) + " "
										+ word + " " + wordWeight + "\n");
							}
							indexTDIDFMap.put(WordindexMap.get(word),
									wordWeight);
							DocClassTerm[WordindexMap.get(word)][i + 1]++;

						}
					}
					if ((PrintDebug & 0x01) == 0x01) {
						tgWriter.flush();
						tgWriter.close();

						FileWriter tgWriterVSM = new FileWriter(targetDir
								+ "vsm");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterVSM.append(me.getKey() + " "
									+ me.getValue() + "\n");
						}

						tgWriterVSM.flush();
						tgWriterVSM.close();

						FileWriter tgWriterTF = new FileWriter(targetDir + "TF");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet2 = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet2
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterTF.append(me.getKey()
									+ " "
									+ TermPerDocMap.get(indexWordMap.get(me
											.getKey())) + "\n");
						}

						tgWriterTF.flush();
						tgWriterTF.close();
					}
				}
			}
		}
		// 输出词典编号文件
		if ((PrintDebug & 0x02) == 0x02) {
			printSIMap(srcDir + "/../0_outputfile/", "WordindexMapMI.txt",
					WordindexMap);
			printISMap(srcDir + "/../0_outputfile/", "indexWordMapMI.txt",
					indexWordMap);
		}
		Map<Integer, Double> indexMIMap = new TreeMap<Integer, Double>();
		indexMIMap = DR.MIDR(DocClassTerm, DocNumPerClass, MaxTerm, MaxDoc,
				MaxLabel);
		if ((PrintDebug & 0x03) == 0x03) {
			printIDMap(srcDir + "/../0_outputfile/", "indexMIMapOld.txt",
					indexMIMap);
		}
		indexMIMap = DR.SlectWordByValue(indexMIMap, SlectPecent);
		if ((PrintDebug & 0x03) == 0x03) {
			printIDMap(srcDir + "/../0_outputfile/", "indexMIMapNew.txt",
					indexMIMap);
		}

		if ((PrintDebug & 0x08) == 0x08) {
			File[] sampleDir1 = new File(srcDir).listFiles();
			for (int i = 0; i < sampleDir1.length; i++) {
				File[] sample = sampleDir1[i].listFiles();
				String targetDir = srcDir + "/../3_DRByMIandTFIDF/"
						+ sampleDir1[i].getName();
				File targetDirFile = new File(targetDir);
				if (!targetDirFile.exists()) {
					targetDirFile.mkdirs();
				}
				for (int j = 0; j < sample.length; j++) {
					TFPerDocMap.clear();
					indexTDIDFMap.clear();
					TFPerDocMap = DR.computeTFPerDoc(sample[j], NewwordMap);
					TermPerDocMap = DR.computeTermPerDoc(sample[j], NewwordMap);
					String fileShortName = sample[j].getName();

					if (fileShortName.contains("stemed")) {
						targetDir = srcDir + "/../3_DRByMIandTFIDF/"
								+ sampleDir1[i].getName() + "/"
								+ fileShortName.substring(0, 8);
						FileWriter tgWriter = new FileWriter(targetDir
								+ "stemedMI");
						FileReader samReader = new FileReader(sample[j]);
						BufferedReader samBR = new BufferedReader(samReader);
						// wordMap为降维后的特征，把wordMap中保存的词条过滤到新文件夹2_ReducTFSample中
						// 2_ReducTFSample文件夹中保存降维后的词条
						while ((word = samBR.readLine()) != null) {
							if ((NewwordMap.containsKey(word))
									&& (indexMIMap.containsKey(WordindexMap
											.get(word)))) {
								wordWeight = TFPerDocMap.get(word)
										* IDFPerWordMap.get(word);
								tgWriter.append(WordindexMap.get(word) + " "
										+ word + " " + wordWeight + "\n");
								indexTDIDFMap.put(WordindexMap.get(word),
										wordWeight);
							}
						}
						tgWriter.flush();
						tgWriter.close();

						FileWriter tgWriterVSM = new FileWriter(targetDir
								+ "vsmMI");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterVSM.append(me.getKey() + " "
									+ me.getValue() + "\n");
						}

						tgWriterVSM.flush();
						tgWriterVSM.close();

						FileWriter tgWriterTF = new FileWriter(targetDir
								+ "TFMI");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet2 = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet2
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterTF.append(me.getKey()
									+ " "
									+ TermPerDocMap.get(indexWordMap.get(me
											.getKey())) + "\n");
						}

						tgWriterTF.flush();
						tgWriterTF.close();
					}
				}
			}
		}

		return indexMIMap;
	}

/***********************************************************************/
/* * CEDR 
/* 作用： 
/* 互信息，计算每个单词与类的依赖程度
/* 参数：
/*
/* 返回： 
/* 
/* * 
/***********************************************************************/
	public Map<Integer, Double> DRByCEandTFIDF(String srcDir,
			double SlectPecent, int tfHold) throws IOException {

		System.out.println("DRByCEandTFIDF srcDir:" + srcDir);
		int PrintDebug = 0;
		int[][] DocClassTerm;
		int[] DocNumPerClass;
		int MaxTerm = 0;
		int MaxDoc = 0;
		int MaxLabel = 0;

		String word;
		int countLine = 0;
		double wordWeight;
		DimensionReduction DR = new DimensionReduction();
		SortedMap<Integer, Double> indexTDIDFMap = new TreeMap<Integer, Double>();
		SortedMap<String, Integer> WordindexMap = new TreeMap<String, Integer>();
		SortedMap<Integer, String> indexWordMap = new TreeMap<Integer, String>();
		SortedMap<String, Double> NewwordMap = new TreeMap<String, Double>();
		SortedMap<String, Double> IDFPerWordMap = new TreeMap<String, Double>();
		SortedMap<String, Double> TFPerDocMap = new TreeMap<String, Double>();
		SortedMap<String, Integer> TermPerDocMap = new TreeMap<String, Integer>();
		// wordMap中保存2_ReducTFSample文件夹中出现次数大于tfHold的词条
		NewwordMap = DR.countWords(srcDir, tfHold, NewwordMap);

		IDFPerWordMap = DR.computeIDF(srcDir, NewwordMap);

		Set<Map.Entry<String, Double>> allWords = NewwordMap.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allWords.iterator(); it
				.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			WordindexMap.put(me.getKey(), ++countLine);
			indexWordMap.put(countLine, me.getKey());
		}
		// 词条编号从1开始计数
		MaxTerm = countLine + 1;

		File[] sampleDir = new File(srcDir).listFiles();
		// 类别编号从1开始计数
		MaxLabel = sampleDir.length + 1;
		DocNumPerClass = new int[MaxLabel];
		DocClassTerm = new int[MaxTerm][MaxLabel];
		for (int i = 0; i < sampleDir.length; i++) {
			File[] sample = sampleDir[i].listFiles();
			DocNumPerClass[i + 1] = sample.length;
			MaxDoc += sample.length;

			String targetDir = srcDir + "/../3_DRByCEandTFIDF/"
					+ sampleDir[i].getName();
			File targetDirFile = new File(targetDir);
			if (!targetDirFile.exists()) {
				targetDirFile.mkdirs();
			}
			for (int j = 0; j < sample.length; j++) {
				TFPerDocMap.clear();
				indexTDIDFMap.clear();
				TFPerDocMap = DR.computeTFPerDoc(sample[j], NewwordMap);
				TermPerDocMap = DR.computeTermPerDoc(sample[j], NewwordMap);
				String fileShortName = sample[j].getName();

				if (fileShortName.contains("stemed")) {
					targetDir = srcDir + "/../3_DRByCEandTFIDF/"
							+ sampleDir[i].getName() + "/"
							+ fileShortName.substring(0, 8);

					FileWriter tgWriter = new FileWriter(targetDir + "stemed");
					FileReader samReader = new FileReader(sample[j]);
					BufferedReader samBR = new BufferedReader(samReader);
					// wordMap为降维后的特征，把wordMap中保存的词条过滤到新文件夹2_ReducTFSample中
					// 2_ReducTFSample文件夹中保存降维后的词条
					while ((word = samBR.readLine()) != null) {
						if (NewwordMap.containsKey(word)) {
							wordWeight = TFPerDocMap.get(word)
									* IDFPerWordMap.get(word);

							if ((PrintDebug & 0x01) == 0x01) {
								tgWriter.append(WordindexMap.get(word) + " "
										+ word + " " + wordWeight + "\n");
							}
							indexTDIDFMap.put(WordindexMap.get(word),
									wordWeight);
							DocClassTerm[WordindexMap.get(word)][i + 1]++;

						}
					}
					if ((PrintDebug & 0x01) == 0x01) {
						tgWriter.flush();
						tgWriter.close();

						FileWriter tgWriterVSM = new FileWriter(targetDir
								+ "vsm");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterVSM.append(me.getKey() + " "
									+ me.getValue() + "\n");
						}

						tgWriterVSM.flush();
						tgWriterVSM.close();

						FileWriter tgWriterTF = new FileWriter(targetDir + "TF");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet2 = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet2
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterTF.append(me.getKey()
									+ " "
									+ TermPerDocMap.get(indexWordMap.get(me
											.getKey())) + "\n");
						}

						tgWriterTF.flush();
						tgWriterTF.close();
					}
				}
			}
		}
		// 输出词典编号文件
		if ((PrintDebug & 0x02) == 0x02) {
			printSIMap(srcDir + "/../0_outputfile/", "WordindexMapCE.txt",
					WordindexMap);
			printISMap(srcDir + "/../0_outputfile/", "indexWordMapCE.txt",
					indexWordMap);
		}
		Map<Integer, Double> indexCEMap = new TreeMap<Integer, Double>();
		indexCEMap = DR.CEDR(DocClassTerm, DocNumPerClass, MaxTerm, MaxDoc,
				MaxLabel);

		if ((PrintDebug & 0x04) == 0x04) {
			printIDMap(srcDir + "/../0_outputfile/", "indexCEMapOld.txt",
					indexCEMap);
		}
		indexCEMap = DR.SlectWordByValue(indexCEMap, SlectPecent);
		if ((PrintDebug & 0x04) == 0x04) {
			printIDMap(srcDir + "/../0_outputfile/", "indexCEMapNew.txt",
					indexCEMap);
		}

		if ((PrintDebug & 0x08) == 0x08) {
			File[] sampleDir1 = new File(srcDir).listFiles();
			for (int i = 0; i < sampleDir1.length; i++) {
				File[] sample = sampleDir1[i].listFiles();
				String targetDir = srcDir + "/../3_DRByCEandTFIDF/"
						+ sampleDir1[i].getName();
				File targetDirFile = new File(targetDir);
				if (!targetDirFile.exists()) {
					targetDirFile.mkdirs();
				}
				for (int j = 0; j < sample.length; j++) {
					TFPerDocMap.clear();
					indexTDIDFMap.clear();
					TFPerDocMap = DR.computeTFPerDoc(sample[j], NewwordMap);
					TermPerDocMap = DR.computeTermPerDoc(sample[j], NewwordMap);
					String fileShortName = sample[j].getName();

					if (fileShortName.contains("stemed")) {
						targetDir = srcDir + "/../3_DRByCEandTFIDF/"
								+ sampleDir1[i].getName() + "/"
								+ fileShortName.substring(0, 8);
						FileWriter tgWriter = new FileWriter(targetDir
								+ "stemedCE");
						FileReader samReader = new FileReader(sample[j]);
						BufferedReader samBR = new BufferedReader(samReader);
						// wordMap为降维后的特征，把wordMap中保存的词条过滤到新文件夹2_ReducTFSample中
						// 2_ReducTFSample文件夹中保存降维后的词条
						while ((word = samBR.readLine()) != null) {
							if ((NewwordMap.containsKey(word))
									&& (indexCEMap.containsKey(WordindexMap
											.get(word)))) {
								wordWeight = TFPerDocMap.get(word)
										* IDFPerWordMap.get(word);
								tgWriter.append(WordindexMap.get(word) + " "
										+ word + " " + wordWeight + "\n");
								indexTDIDFMap.put(WordindexMap.get(word),
										wordWeight);
							}
						}
						tgWriter.flush();
						tgWriter.close();

						FileWriter tgWriterVSM = new FileWriter(targetDir
								+ "vsmCE");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterVSM.append(me.getKey() + " "
									+ me.getValue() + "\n");
						}

						tgWriterVSM.flush();
						tgWriterVSM.close();

						FileWriter tgWriterTF = new FileWriter(targetDir
								+ "TFCE");
						Set<Map.Entry<Integer, Double>> indexTDIDFMapSet2 = indexTDIDFMap
								.entrySet();
						for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet2
								.iterator(); it.hasNext();) {
							Map.Entry<Integer, Double> me = it.next();
							tgWriterTF.append(me.getKey()
									+ " "
									+ TermPerDocMap.get(indexWordMap.get(me
											.getKey())) + "\n");
						}

						tgWriterTF.flush();
						tgWriterTF.close();
					}
				}
			}
		}
		return indexCEMap;
	}
	
	public static Map<String, Double> compute_accuracy_F_RetMap(Map<String,String> actual,
												 Map<String,String> pred,
												 Map<Integer,String> classes,
												 String DesFile, int flagP, int flagF) throws IOException {
		//int numclasses = 0.0;
		double numcorrect = 0.0;
		double accuracy   = 0.0;
		double ClusterEntropy = 0.0;
		if(0==classes.size())
		{
			Map<String,String> classtemp = new TreeMap<String, String>();
			Set<Map.Entry<String, String>> allText0 = actual.entrySet();
			for (Iterator<Map.Entry<String, String>> it = allText0.iterator(); it.hasNext();) {
				Map.Entry<String, String> me = it.next();
				if(classtemp.containsKey(me.getValue())){
					//do nothing
				}else{
					classtemp.put(me.getValue(),me.getValue());
				}
			}
			int z = 0;
			Set<Map.Entry<String, String>> classtempSet = classtemp.entrySet();
			for (Iterator<Map.Entry<String, String>> it = classtempSet.iterator(); it.hasNext();) {
				Map.Entry<String, String> me = it.next();
				z++;
				classes.put(z,me.getKey());
			}
			System.out.println("compute_accuracy_F: Class_size = "+classes.size());
		}
		double[] precision = new double[classes.size()+1];
		double[] recall= new double[classes.size()+1];
		double[] F= new double[classes.size()+1];
		double[][]confus = new double[classes.size()+1][classes.size()+1];
		double[] everyClusterEntropy = new double[classes.size()+1];
		for(int i = 0; i <= classes.size(); i++) {
			for(int j = 0; j <= classes.size(); j++) {
				confus[i][j] = 0.0;
			}
		}
		if(actual.size()!=pred.size())
		{
			if(1==flagP)
				System.out.println("Erro: actual.size = " + actual.size()+",  pred.size = "+pred.size());
			return null;
		}

		Set<Map.Entry<String, String>> allText = actual.entrySet();
		for (Iterator<Map.Entry<String, String>> it = allText.iterator(); it.hasNext();) {
			Map.Entry<String, String> me = it.next();
			if(me.getValue().equals(pred.get(me.getKey())))
			{
				numcorrect++;
			}
		}
		accuracy = numcorrect/actual.size();

		for(int i = 1; i <= classes.size(); i++) {
			String a = classes.get(i);
			for(int j = 1; j <= classes.size(); j++) {
				String b = classes.get(j);
				Set<Map.Entry<String, String>> allText2 = actual.entrySet();
				for (Iterator<Map.Entry<String, String>> it = allText2.iterator(); it.hasNext();) {
					Map.Entry<String, String> me = it.next();
					if(me.getValue().equals(a))
					{
						if(pred.get(me.getKey()).equals(b))
						{
							confus[i][0]++;
							confus[i][j]++;
						}
					}
				}
			}
		}

		for(int j = 1; j <= classes.size(); j++) {
			for(int i = 1; i <= classes.size(); i++) {
				confus[0][j] += confus[i][j];
			}
		}
		
	    F[0] = 0;
		recall[0] = 0;
		precision[0] = 0;
		for(int i = 1; i <= classes.size(); i++) {
			if(confus[i][0]>0)
			{
				recall[i] = confus[i][i] / confus[i][0];
			}
			else
			{
				recall[i] = 0.0;
			}
			
			if(confus[0][i]>0)
			{
				precision[i] = confus[i][i] / confus[0][i];
			}
			else
			{
				precision[i] = 0.0;
			}

			if((precision[i]+recall[i])>0)
			{
				F[i] = 2 * (precision[i]*recall[i]) / (precision[i]+recall[i]);
			}
			else
			{
				F[i] = 0.0;
			}
			
		    F[0] += F[i];
			recall[0] += recall[i];
			precision[0] += precision[i];
		}

		double macro_p  = precision[0]/classes.size();
		double macro_r  = recall[0]/classes.size();
		double macro_F1 = 2*macro_p*macro_r/(macro_p+macro_r);

		double T_TP = 0.0;
		double T_TP_FP = 0.0;
		double T_TP_FN = 0.0;
		double micro_p  = 0.0;
		double micro_r  = 0.0;
		double micro_F1 = 0.0;

		for(int i = 1; i <= classes.size(); i++) {
			T_TP += confus[i][i];
			T_TP_FP += confus[0][i];
			T_TP_FN += confus[i][0];
		}

		if(T_TP_FP>0)
		{
			micro_p = T_TP / T_TP_FP;
		}
		else
		{
			micro_p = 0.0;
		}
		if(T_TP_FN>0)
		{
			micro_r = T_TP / T_TP_FN;
		}
		else
		{
			micro_r = 0.0;
		}
		micro_F1 = 2*micro_p*micro_r/(micro_p+micro_r);


		for (int j = 1; j <= classes.size(); j++) {
			if (confus[0][j] != 0) {
				for (int i = 1; i <= classes.size(); i++) {
					double p = (double) confus[i][j] / confus[0][j];
					if (p != 0) {
						everyClusterEntropy[j] += -p * Math.log(p);
					}
				}
				ClusterEntropy += confus[0][j] / (double) pred.size()
								  * everyClusterEntropy[j];
			}
		}


		if(1==flagP){
			System.out.println();
			for(int i = 1; i <= classes.size(); i++) {
				System.out.print("  ");
				for(int j = 1; j <= classes.size(); j++) {
					System.out.print(fixedWidthIntegertoString(""+confus[i][j],5) + "    ");
				}
				System.out.println();
			}

			System.out.println();
			System.out.println("numcorrect:");
			System.out.println(numcorrect);
			System.out.println();
			System.out.println("accuracy:");
			System.out.println(accuracy);	
			
			System.out.println();
			System.out.println("precision:");
			for (int i = 1; i <= classes.size(); i++) {
				System.out.print(precision[i]+"    ");
			}
			System.out.println();
			
			System.out.println("recall:");
			for (int i = 1; i <= classes.size(); i++) {
				System.out.print(recall[i]+"    ");
			}
			System.out.println();
			
			System.out.println("F1:");
			for (int i = 1; i <= classes.size(); i++) {
				System.out.print(F[i]+"    ");
			}
			System.out.println();
			System.out.println();
			System.out.println("macro_p :    "+macro_p);
			System.out.println();
			System.out.println("macro_r :    "+macro_r);
			System.out.println();
			System.out.println("macro_F1:    "+macro_F1);
			System.out.println();
			System.out.println("micro_p :    "+micro_p);
			System.out.println();
			System.out.println("micro_r :    "+micro_r);
			System.out.println();
			System.out.println("micro_F1:    "+micro_F1);
			System.out.println();
			System.out.println("Entropy :    "+ClusterEntropy);
		}else if(2==flagP){
			System.out.println();
			System.out.println("macro_p :    "+macro_p);
			System.out.println();
			System.out.println("macro_r :    "+macro_r);
			System.out.println();
			System.out.println("macro_F1:    "+macro_F1);
			System.out.println();
			System.out.println("micro_p :    "+micro_p);
			System.out.println();
			System.out.println("micro_r :    "+micro_r);
			System.out.println();
			System.out.println("micro_F1:    "+micro_F1);
			System.out.println();
			System.out.println("Entropy :    "+ClusterEntropy);
		}
		
		if(1==flagF){
			FileWriter Writer = new FileWriter(DesFile);
			Writer.append("\n");
			for(int i = 1; i <= classes.size(); i++) {
				Writer.append("\n");;
				for(int j = 1; j <= classes.size(); j++) {
					Writer.append(fixedWidthIntegertoString(""+confus[i][j],5) + "    ");
				}
				Writer.append("\n");
			}

			Writer.append("\n");
			Writer.append("numcorrect: ");
			Writer.append(""+numcorrect);
			Writer.append("\n");
			Writer.append("accuracy  : ");
			Writer.append(""+accuracy);	
			
			Writer.append("\n");
			Writer.append("precision : \n");
			for (int i = 1; i <= classes.size(); i++) {
				Writer.append(precision[i]+"    ");
			}
			Writer.append("\n");
			
			Writer.append("recall : \n");
			for (int i = 1; i <= classes.size(); i++) {
				Writer.append(recall[i]+"    ");
			}
			Writer.append("\n");
			
			Writer.append("F1 : \n");
			for (int i = 1; i <= classes.size(); i++) {
				Writer.append(F[i]+"    ");
			}
			Writer.append("\n");
			Writer.append("\n");
			Writer.append("macro_p :    "+macro_p);
			Writer.append("\n");
			Writer.append("macro_r :    "+macro_r);
			Writer.append("\n");
			Writer.append("macro_F1:    "+macro_F1);
			Writer.append("\n");
			Writer.append("micro_p :    "+micro_p);
			Writer.append("\n");
			Writer.append("micro_r :    "+micro_r);
			Writer.append("\n");
			Writer.append("micro_F1:    "+micro_F1);
			Writer.append("\n");
			Writer.append("Entropy :    "+ClusterEntropy);

			Writer.flush();
			Writer.close();
		}
		Map<String, Double> ResultMap = new TreeMap<String, Double>();
		ResultMap.put("macro_p",macro_p);
		ResultMap.put("macro_r",macro_r);
		ResultMap.put("macro_F1",macro_F1);
		ResultMap.put("micro_p",micro_p);
		ResultMap.put("micro_r",micro_r);
		ResultMap.put("micro_F1",micro_F1);
		ResultMap.put("Entropy",ClusterEntropy);

		return ResultMap;
	}
 
	public static Map<String, Map<Integer, Double>> MergeMaptoMap(Map<String, Map<Integer, Double>> NameMap1,
																  Map<String, Map<Integer, Double>> NameMap2){

		Map<String, Map<Integer, Double>> NameMapNew = new TreeMap<String, Map<Integer, Double>>();
		Set<Map.Entry<String, Map<Integer, Double>>> NameMapSet1 = NameMap1.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = NameMapSet1.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			    TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
				Set<Map.Entry<Integer, Double>> cSet = me.getValue().entrySet();
				for (Iterator<Map.Entry<Integer, Double>> cit = cSet.iterator(); cit.hasNext();) {
					Map.Entry<Integer, Double> cme = cit.next();
					tempMap.put(cme.getKey(), cme.getValue());
				}
				NameMapNew.put(me.getKey(), tempMap);
		}

		Set<Map.Entry<String, Map<Integer, Double>>> NameMapSet2 = NameMap2.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = NameMapSet2.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			    TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
				Set<Map.Entry<Integer, Double>> cSet = me.getValue().entrySet();
				for (Iterator<Map.Entry<Integer, Double>> cit = cSet.iterator(); cit.hasNext();) {
					Map.Entry<Integer, Double> cme = cit.next();
					tempMap.put(cme.getKey(), cme.getValue());
				}
				NameMapNew.put(me.getKey(), tempMap);
		}
		
		return NameMapNew;
	}

	public Map<String, Map<Integer, Double>> FilterMapByMapSID(String srcDir, Map<String, Map<Integer, Double>> FileWordMap, 
		           Map<Integer, Double> IndexValueMap){

		Map<String, Map<Integer, Double>> FileWordMapNew = new TreeMap<String, Map<Integer, Double>>();
		TreeMap<Integer, Double> WordMap = new TreeMap<Integer, Double>();

		Set<Map.Entry<String, Map<Integer, Double>>> FileWordMapSet = FileWordMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = FileWordMapSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			WordMap.clear();
			
			Set<Map.Entry<Integer, Double>> allWordSet = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allWordSet.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();
				if (IndexValueMap.containsKey(me2.getKey())) {
					WordMap.put(me2.getKey(),me2.getValue());
				}	
			}
			if(WordMap.size()>0){
				TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
				tempMap.putAll(WordMap);
				FileWordMapNew.put(me.getKey(), tempMap);
			}
			else{
				System.out.println("FilterMapByMapSID delet empty point...");
			}
		}
		return FileWordMapNew;
	}
	
	public Map<String, Map<Integer, Double>> ReadTrainFileVsm(String srcFile) throws IOException {

		long linecount = 0;
		String line;
		String[] lineSplitBlock;
		String[] BlockIndexTfidf;
		File trainSamples = new File(srcFile);
		BufferedReader trainSamplesBR = new BufferedReader(new FileReader(trainSamples));
		Map<String, Map<Integer, Double>> trainFileNameWordTFMap = new TreeMap<String, Map<Integer, Double>>();
		TreeMap<Integer, Double> trainWordTFMap = new TreeMap<Integer, Double>();
		while ((line = trainSamplesBR.readLine()) != null) {
			linecount++;
			lineSplitBlock = line.split(" ");
			trainWordTFMap.clear();
			for (int i = 2; i < lineSplitBlock.length; i = i + 1) {
				BlockIndexTfidf = lineSplitBlock[i].split(":");
				trainWordTFMap.put(Integer.valueOf(BlockIndexTfidf[0]), 
					              Double.valueOf(BlockIndexTfidf[1]));
			}
			TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
			tempMap.putAll(trainWordTFMap);
			trainFileNameWordTFMap.put(lineSplitBlock[0]+"_"+lineSplitBlock[1], tempMap);
		}
		trainSamplesBR.close();

		return trainFileNameWordTFMap;
	}	

	public Map<String, Map<Integer, Double>> ReadTestFileVsm(String srcFile) throws IOException {

		int linecount = 0;
		String line;
		String[] lineSplitBlock;
		String[] BlockIndexTfidf;
		File testSamples = new File(srcFile);
		BufferedReader testSamplesBR = new BufferedReader(new FileReader(testSamples));
		Map<String, Map<Integer, Double>> testFileNameWordTFMap = new TreeMap<String, Map<Integer, Double>>();
		Map<Integer, Double> testWordTFMap = new TreeMap<Integer, Double>();
		while ((line = testSamplesBR.readLine()) != null) {
			linecount++;
			lineSplitBlock = line.split(" ");
			testWordTFMap.clear();
			for (int i = 2; i < lineSplitBlock.length; i = i + 1) {
				BlockIndexTfidf = lineSplitBlock[i].split(":");
				testWordTFMap.put(Integer.valueOf(BlockIndexTfidf[0]), 
					              Double.valueOf(BlockIndexTfidf[1]));
			}
			TreeMap<Integer, Double> tempMap = new TreeMap<Integer, Double>();
			tempMap.putAll(testWordTFMap);
			testFileNameWordTFMap.put(lineSplitBlock[0]+"_"+lineSplitBlock[1], tempMap);
		}
		testSamplesBR.close();

		return testFileNameWordTFMap;
	}

	public static void CreatDir(String srcDir) throws IOException {
		String DirName = srcDir.replace("\\", "/");
		File FileDir = new File(DirName);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
	}
	
	public Map<Integer, Double> SlectedDRMethod(String srcDir,
			double SlectPecent, int tfHold, int index) throws IOException {
		DataPreProcess    DataPP = new DataPreProcess();
		Map<Integer, Double> IndexValueMap = new TreeMap<Integer, Double>();

		switch(index)
		{
			case 1:
				System.out.println("Slected DRByIGandTFIDF");
				IndexValueMap = DataPP.DRByIGandTFIDF(srcDir, SlectPecent, tfHold);
				break;
			case 2:
				System.out.println("Slected DRByCSandTFIDF");
				IndexValueMap = DataPP.DRByCSandTFIDF(srcDir, SlectPecent, tfHold);
				break;
			case 3:
				System.out.println("Slected DRByMIandTFIDF");
				IndexValueMap = DataPP.DRByMIandTFIDF(srcDir, SlectPecent, tfHold);
				break;
			case 4: 
				System.out.println("Slected DRByCEandTFIDF");
				IndexValueMap = DataPP.DRByCEandTFIDF(srcDir, SlectPecent, tfHold);
				break;
			case 5: 
				System.out.println("Slected DRByCEandTFIDF");
				IndexValueMap = DataPP.DRByCEandTFIDF(srcDir, SlectPecent, tfHold);
				break;
			default:
				System.out.println("Slected default");
				break;
		}
		return IndexValueMap;
	}

	public static Map<String, Integer> ReadSIDictMap(String strDir, String fileName) throws IOException {
		//System.out.println("ReadSIDictMap:" + strDir + fileName);
		int countLine = 0;
		String word;
		String[] lineSplitBlock;

		Map<String, Integer> DictMap = new TreeMap<String, Integer>();
		File ReadFile = new File(strDir + fileName);

					FileReader samReader = new FileReader(ReadFile);
					BufferedReader samBR = new BufferedReader(samReader);
					
					while ((word = samBR.readLine()) != null) {
						countLine++;
						lineSplitBlock = word.split(" ");
						DictMap.put(lineSplitBlock[0],Integer.valueOf(lineSplitBlock[1]));
					}
		//System.out.println(fileName + " size is " + countLine);
		return DictMap;
	}

	public void CreatTrainAndTestDir(String srcDir, int TrainTestNum) throws IOException {
		System.out.println("Start the CreatTrainAndTestDir");
		for (int i = 0; i < TrainTestNum; i++) {
			CreatTrainAndTestFileVsm(srcDir, 1.0/TrainTestNum, i);
			//CreatTrainAndTestFileWord2(srcDir, 1.0/TrainTestNum, i);
			//CreatTrainAndTestFileSVM(srcDir, 1.0/TrainTestNum, i);
		}
	}
	
	public static void CreatTrainAndTestFileVsm(String srcDir, double trainSamplePercent, int indexOfSample) throws IOException {
		System.out.println("Start the CreatTrainAndTestFile: " + indexOfSample);
		String word;
		int TotalDocCout = 0;
		String[] lineSplitBlock;
		Map<String, Integer> WordindexMapDict = new TreeMap<String, Integer>();
		Map<String, Integer> CateindexMapDict = new TreeMap<String, Integer>();

		WordindexMapDict = 	ReadSIDictMap(srcDir + "/../stopword/", "WordindexMapDict.txt");
		CateindexMapDict = 	ReadSIDictMap(srcDir + "/../stopword/", "CateindexMapDict.txt");

		String trainFileDir = srcDir+"/../4_DocVector";
		String testFileDir  = srcDir+"/../4_DocVector";
		File FileDir = new File(trainFileDir);
		if (!FileDir.exists()) {
			FileDir.mkdirs();
		}
		
		trainFileDir = srcDir+"/../4_DocVector/VsmTFIDFMapTrainSample"+indexOfSample+".txt";
		testFileDir  = srcDir+"/../4_DocVector/VsmTFIDFMapTestSample" +indexOfSample+".txt";
		FileWriter tsTrainWriter = new FileWriter(new File(trainFileDir));
		FileWriter tsTestWrtier  = new FileWriter(new File(testFileDir));
		FileWriter tsWriter = tsTrainWriter;

		File[] sampleDir = new File(srcDir).listFiles();
		for (int i = 0; i < sampleDir.length; i++) {
			String cateShortName = sampleDir[i].getName();
			File[] sample = sampleDir[i].listFiles();

			double testBeginIndex = indexOfSample
					* (sample.length * trainSamplePercent);// 测试样例的起始文件序号
			double testEndIndex = (indexOfSample + 1)
					* (sample.length * trainSamplePercent);// 测试样例集的结束文件序号

			for (int j = 0; j < sample.length; j++) {
				if (j >= testBeginIndex && j <= testEndIndex) {
					tsWriter = tsTestWrtier;
				} else {
					tsWriter = tsTrainWriter;
				}
				
				String fileShortName = sample[j].getName();
				if (fileShortName.contains("vsm")) {
					TotalDocCout++;
					FileReader samReader = new FileReader(sample[j]);
					BufferedReader samBR = new BufferedReader(samReader);
					//int Cateindex = CateindexMapDict.get(cateShortName);
					tsWriter.append(CateindexMapDict.get(cateShortName) + " ");
					String keyWord = fileShortName.substring(0, 8);
					tsWriter.append(keyWord + " ");
					
					while ((word = samBR.readLine()) != null) {
						lineSplitBlock = word.split(" ");
						int index = WordindexMapDict.get(lineSplitBlock[1]);
						if(Integer.valueOf(lineSplitBlock[0]) == index)
						tsWriter.append(index+":"+lineSplitBlock[2]+" ");
						else
						System.out.println("CreatTrainAndTestFileVsm:find IOException------------ ");
					}
					tsWriter.append("\n");
					tsWriter.flush();				
				}
			}
		}
		tsTrainWriter.close();
		tsTestWrtier.close();
		tsWriter.close();
		System.out.println("CreatTrainAndTestFileVsm: Total Document Count = " + TotalDocCout);
	}

	public void ComputeTFIDF(String srcDir, int tfHold) throws IOException {

		System.out.println("Compute----------TFIDF");
		System.out.println("ComputeTFIDF srcDir:" + srcDir);
		String word;
		int countLine = 0;
		int cateShortNum = 0;
		double wordWeight;
		DimensionReduction DR = new DimensionReduction();
		SortedMap<Integer, Double> indexTDIDFMap = new TreeMap<Integer, Double>();
		SortedMap<String, Integer> WordindexMap = new TreeMap<String, Integer>();
		SortedMap<Integer, String> indexWordMap = new TreeMap<Integer, String>();

		SortedMap<String, Integer> CateindexMap = new TreeMap<String, Integer>();
		SortedMap<Integer, String> indexCateMap = new TreeMap<Integer, String>();
		
		SortedMap<String, Double> NewwordMap = new TreeMap<String, Double>();
		SortedMap<String, Double> IDFPerWordMap = new TreeMap<String, Double>();
		SortedMap<String, Double> TFPerDocMap = new TreeMap<String, Double>();
		SortedMap<String, Integer> TermPerDocMap = new TreeMap<String, Integer>();
		// wordMap中保存2_ReducTFSample文件夹中出现次数大于tfHold的词条
		NewwordMap = DR.countWords(srcDir, tfHold, NewwordMap);

		IDFPerWordMap = DR.computeIDF(srcDir, NewwordMap);

		Set<Map.Entry<String, Double>> allWords = NewwordMap.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allWords.iterator(); it
				.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			WordindexMap.put(me.getKey(), ++countLine);
			indexWordMap.put(countLine, me.getKey());
		}

		File[] sampleDir = new File(srcDir).listFiles();
		for (int i = 0; i < sampleDir.length; i++) {
			File[] sample = sampleDir[i].listFiles();
			String cateShortName = sampleDir[i].getName();
			
			CateindexMap.put(cateShortName, ++cateShortNum);
			indexCateMap.put(cateShortNum, cateShortName);
			
			String targetDir = srcDir + "/../2_IDFTFSample/"
					+ sampleDir[i].getName();
			File targetDirFile = new File(targetDir);
			if (!targetDirFile.exists()) {
				targetDirFile.mkdirs();
			}
			for (int j = 0; j < sample.length; j++) {
				TFPerDocMap.clear();
				indexTDIDFMap.clear();
				TFPerDocMap = DR.computeTFPerDoc(sample[j], NewwordMap);
				TermPerDocMap = DR.computeTermPerDoc(sample[j], NewwordMap);
				String fileShortName = sample[j].getName();

				if (fileShortName.contains("stemed")) {
					targetDir = srcDir + "/../2_IDFTFSample/"
							+ sampleDir[i].getName() + "/"
							+ fileShortName.substring(0, 8);
					FileWriter tgWriter = new FileWriter(targetDir + "stemed");
					FileReader samReader = new FileReader(sample[j]);
					BufferedReader samBR = new BufferedReader(samReader);
					// wordMap为降维后的特征，把wordMap中保存的词条过滤到新文件夹2_ReducTFSample中
					// 2_ReducTFSample文件夹中保存降维后的词条
					while ((word = samBR.readLine()) != null) {
						if (NewwordMap.containsKey(word)) {
							wordWeight = TFPerDocMap.get(word)
									* IDFPerWordMap.get(word);
							tgWriter.append(WordindexMap.get(word) + " " + word
									+ " " + wordWeight + "\n");
							indexTDIDFMap.put(WordindexMap.get(word),
									wordWeight);
						}
					}
					tgWriter.flush();
					tgWriter.close();

					FileWriter tgWriterVSM = new FileWriter(targetDir + "vsm");
					Set<Map.Entry<Integer, Double>> indexTDIDFMapSet = indexTDIDFMap
							.entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet
							.iterator(); it.hasNext();) {
						Map.Entry<Integer, Double> me = it.next();
						tgWriterVSM.append(me.getKey()+" "+indexWordMap.get(me.getKey())
							               +" "+ me.getValue()+ "\n");
					}

					tgWriterVSM.flush();
					tgWriterVSM.close();

					FileWriter tgWriterTF = new FileWriter(targetDir + "TF");
					Set<Map.Entry<Integer, Double>> indexTDIDFMapSet2 = indexTDIDFMap
							.entrySet();
					for (Iterator<Map.Entry<Integer, Double>> it = indexTDIDFMapSet2
							.iterator(); it.hasNext();) {
						Map.Entry<Integer, Double> me = it.next();
						tgWriterTF.append(me.getKey()
								+ " "
								+ TermPerDocMap.get(indexWordMap.get(me
										.getKey())) + "\n");
					}

					tgWriterTF.flush();
					tgWriterTF.close();
				}
			}
		}
		printSIMap(srcDir + "/../stopword/", "WordindexMapDict.txt",
				WordindexMap);
		printISMap(srcDir + "/../stopword/", "indexWordMapDict.txt",
				indexWordMap);
		printSIMap(srcDir + "/../stopword/", "CateindexMapDict.txt",
				CateindexMap);
		printISMap(srcDir + "/../stopword/", "indexCateMapDict.txt",
				indexCateMap);

	}

	public void CopyDirVsm(String StrSrcDir) throws IOException {

		File FileDataDir = new File(StrSrcDir);
		if (!FileDataDir.exists()) {
			System.out.println("File not exist:" + StrSrcDir);
			return;
		}
		String StrSubDir = StrSrcDir.substring(StrSrcDir.lastIndexOf('/'));
		String StrDesDir = StrSrcDir + "/../../1_StemedSample" + StrSubDir;
		File FileDesDir = new File(StrDesDir);
		if (!FileDesDir.exists()) {
			FileDesDir.mkdirs();
		}

		File[] srcFiles = FileDataDir.listFiles();
		String[] stemFileNames = new String[srcFiles.length];
		if(srcFiles.length > 1){
			for (int i = 0; i < srcFiles.length; i++) {
				String fileFullName = srcFiles[i].getCanonicalPath();
				String fileShortName = srcFiles[i].getName();
				if (!new File(fileFullName).isDirectory()) {
					StringBuilder stringBuilder = new StringBuilder();
					fileShortName = fixedWidthIntegertoString(fileShortName, 8);
					stringBuilder.append(StrDesDir + "/" + fileShortName);
					if(fileShortName.contains("stemed")){
						copyProcessFile(StrSrcDir, fileFullName, stringBuilder.toString());
					}
				} else {
					fileFullName = fileFullName.replace("\\", "/");
					CopyDirVsm(fileFullName);
				}
			}
		}
	}
	
   	public static String fixedWidthIntegertoString (String s, int w) {
      	//String s = Integer.toString(n);
      	while (s.length() < w) {
      	   s = "0" + s;
     	 }
     	return s;
  	}

	private static void createProcessFile(String srcDirStopword, String srcDir, String targetDir)
			throws IOException {
		// TODO Auto-generated method stub
		String STRSTOPWORDS = srcDirStopword+"/../../stopword/stopwords.txt";
		FileReader srcFileReader = new FileReader(srcDir);
		FileReader stopWordsReader = new FileReader(STRSTOPWORDS);
		FileWriter targetFileWriter = new FileWriter(targetDir);
		BufferedReader srcFileBR = new BufferedReader(srcFileReader);// 装饰模式
		BufferedReader stopWordsBR = new BufferedReader(stopWordsReader);
		String line, resLine, stopWordsLine;
		// 用stopWordsBR够着停用词的ArrayList容器
		ArrayList<String> stopWordsArray = new ArrayList<String>();
		while ((stopWordsLine = stopWordsBR.readLine()) != null) {
			if (!stopWordsLine.isEmpty()) {
				stopWordsArray.add(stopWordsLine);
			}
		}
		while ((line = srcFileBR.readLine()) != null) {
			resLine = lineProcess(line, stopWordsArray);
			if (!resLine.isEmpty()) {
				// 按行写，一行写一个单词
				String[] tempStr = resLine.split(" ");// \s
				for (int i = 0; i < tempStr.length; i++) {
					if (!tempStr[i].isEmpty()) {
						targetFileWriter.append(tempStr[i] + "\n");
					}
				}
			}
		}
		targetFileWriter.flush();
		targetFileWriter.close();
		srcFileReader.close();
		stopWordsReader.close();
		srcFileBR.close();
		stopWordsBR.close();
	}

	private static void copyProcessFile(String srcDirStopword, String srcDir, String targetDir)
			throws IOException {
		FileReader srcFileReader = new FileReader(srcDir);
		FileWriter targetFileWriter = new FileWriter(targetDir);
		BufferedReader srcFileBR = new BufferedReader(srcFileReader);// 装饰模式
		String line, resLine, stopWordsLine;
		while ((line = srcFileBR.readLine()) != null) {
			targetFileWriter.append(line + "\n");
		}
		targetFileWriter.flush();
		targetFileWriter.close();
		srcFileReader.close();
		srcFileBR.close();
	}

	public void StemData(String StrSrcDir) throws IOException {

		File FileDataDir = new File(StrSrcDir);
		if (!FileDataDir.exists()) {
			System.out.println("File not exist:" + StrSrcDir);
			return;
		}

		String StrSubDir = StrSrcDir.substring(StrSrcDir.lastIndexOf('/'));
		String StrDesDir = StrSrcDir + "/../../0_StemedSample" + StrSubDir;
		File FileDesDir = new File(StrDesDir);
		if (!FileDesDir.exists()) {
			FileDesDir.mkdirs();
		}

		File[] srcFiles = FileDataDir.listFiles();
		String[] stemFileNames = new String[srcFiles.length];
		if(srcFiles.length > 1){
			for (int i = 0; i < srcFiles.length; i++) {
				String fileFullName = srcFiles[i].getCanonicalPath();
				String fileShortName = srcFiles[i].getName();
				if (!new File(fileFullName).isDirectory()) {
					StringBuilder stringBuilder = new StringBuilder();
					fileShortName = fixedWidthIntegertoString(fileShortName, 8);
					stringBuilder.append(StrDesDir + "/" + fileShortName);
					createProcessFile(StrSrcDir, fileFullName, stringBuilder.toString());
					stemFileNames[i] = stringBuilder.toString();
				} else {
					fileFullName = fileFullName.replace("\\", "/");
					StemData(fileFullName);
				}
			}
			if (stemFileNames.length > 0 && stemFileNames[0] != null) {
				Stemmer.porterMain(stemFileNames);
			}
		}
	}

	public static void SetPrintOut(String Dir) throws FileNotFoundException
	{
		String DesFile = Dir+"/PrintFile.dat";
		PrintStream ps=new PrintStream(new FileOutputStream(DesFile));  
		System.setOut(ps); 
	}
	
   	public static String fixedWidthIntegertoSpace (String s, int w) {
      	//String s = Integer.toString(n);
      	while (s.length() < w) {
      	   s = " " + s;
     	 }
     	return s;
  	}	
	
//=======================================================================	

}
