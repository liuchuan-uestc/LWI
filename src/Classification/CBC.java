package Classification;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;


public class CBC{
	static double TrainCenterPoint = 0;

	public static double CBCMain(String strDir, Map<String, Map<Integer, Double>> TrainFileMapVsm, 
								           Map<String, Map<Integer, Double>> TestFileMapVsm,
								           Map<String,String> actual, Map<String,String> pred) throws IOException {
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

		Set<Map.Entry<String, Map<Integer, Double>>> CateCenterMapSet2 = CateCenterMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CateCenterMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
			//DataPreProcess.printIDMap(strDir, me2.getKey()+".txt", me2.getValue());
		}
		
		i = 0;
		j = 0;
		int MaxIndex = 0;
		Set<Map.Entry<String, Map<Integer, Double>>> CateCenterMapSet5 = CateCenterMap.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CateCenterMapSet5.iterator(); it2.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
			MaxIndex = Integer.parseInt(me2.getKey())>MaxIndex?Integer.parseInt(me2.getKey()):MaxIndex;	
		}
		System.out.println("MaxIndex: "+MaxIndex);
		MaxIndex = CateCenterMap.size()>MaxIndex?CateCenterMap.size():MaxIndex;	
		
		double[][] distance = new double[TestFileMapVsm.size()+1][MaxIndex+1];
		
		//System.out.println("MultlinearMap.size(): "+MultlinearMap.size());
		for(i=0; i<TestFileMapVsm.size(); i++){
			for(j=0; j<MaxIndex+1; j++){
				distance[i][j] = 10000.0;
			}
		}
		
		i = 0;
		j = 0;
		Set<Map.Entry<String, Map<Integer, Double>>> TestFileMapVsmSet = TestFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TestFileMapVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			j = 0;
			Set<Map.Entry<String, Map<Integer, Double>>> CateCenterMapSet1 = CateCenterMap.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CateCenterMapSet1.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				j = Integer.parseInt(me2.getKey());	
				//System.out.println("i: "+i+",  j: "+j);
				distance[i][j] = getDistance(me.getValue(),me2.getValue());
			}
		    i++;
		}

		int[] nearestMeans = new int[TestFileMapVsm.size()];
		for (i = 0; i < TestFileMapVsm.size(); i++) {
			nearestMeans[i] = findNearestMeans(distance, i);
		}

		i = 0;
		int correct = 0;
		int total   = TestFileMapVsm.size();
		
		Set<Map.Entry<String, Map<Integer, Double>>> TestFileMapVsmSet2 = TestFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TestFileMapVsmSet2.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			String Cate = me.getKey().split("_")[0];

			//System.out.println("i: "+i+", Cate: "+Integer.parseInt(Cate)+", nearestMeans: "+nearestMeans[i]);
			if(Integer.parseInt(Cate) == nearestMeans[i])
			{
				correct += 1.0;
			}
			actual.put(""+i, Cate);
			pred.put(""+i, ""+nearestMeans[i]);
			i++;
		}

		System.out.println("Accuracy = "+(double)correct/total*100+"% ("+correct+"/"+total+") (Rocchio Classifier)\n");
		return (double)correct/total;
	}

	public static double getDistance(Map<Integer, Double> map1,
			Map<Integer, Double> map2) {
		// TODO Auto-generated method stub
		return 1 - computeSim(map1, map2);
	}
	
	public static int findNearestMeans(double[][] distance, int m) {
		// TODO Auto-generated method stub
		double minDist = distance[m][0];
		int j = 0;
		for (int i = 0; i < distance[m].length; i++) {
			if (distance[m][i] < minDist) {
				minDist = distance[m][i];
				j = i;
			}
		}
		return j;
	}
	public static double computeSim(Map<Integer, Double> testWordTFMap,
			Map<Integer, Double> trainWordTFMap) {
		// TODO Auto-generated method stub
		double mul = 0, testAbs = 0, trainAbs = 0;
		Set<Map.Entry<Integer, Double>> testWordTFMapSet = testWordTFMap
				.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> it = testWordTFMapSet
				.iterator(); it.hasNext();) {
			Map.Entry<Integer, Double> me = it.next();
			if (trainWordTFMap.containsKey(me.getKey())) {
				mul += me.getValue() * trainWordTFMap.get(me.getKey());
			}
			testAbs += me.getValue() * me.getValue();
		}
		testAbs = Math.sqrt(testAbs);

		Set<Map.Entry<Integer, Double>> trainWordTFMapSet = trainWordTFMap
				.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> it = trainWordTFMapSet
				.iterator(); it.hasNext();) {
			Map.Entry<Integer, Double> me = it.next();
			trainAbs += me.getValue() * me.getValue();
		}
		trainAbs = Math.sqrt(trainAbs);
		return mul / (testAbs * trainAbs);
	}

}
