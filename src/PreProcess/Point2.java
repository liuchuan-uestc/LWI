package PreProcess;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Point2{	
	double w;
	double K;
	double B;
	double a;
	double xMin;
	double xMax;
	int Mnum;
	Map<String, Map<String, Integer>> CatePointName;
	Map<String, Map<Integer, Double>> CenterFixed;
	Map<String, Map<Integer, Double>> CenterPoint;
	Map<String, Map<Integer, Double>> Trainpoint;
	Map<String, Double> ClusterRadius;
	Map<String, String> MinFreeSpacePointToPoint;
	Map<String, Double> CenterPointMinFreeSpace;
	Map<String, String> MinLWindexPointToPoint;
	Map<String, Double> CenterPointMinLWindex;

	Map<String, String> MinLWradiusPointToPoint;
	Map<String, Double> CenterPointMinLWradius;

	Map<String, Integer> TrainFileFlagMapVsm;
	//20160930 add for SSB
	Map<Integer, Double> GlobalCenterpoint;
	//---------------------------------------------------------------
	//	Constructors
	//---------------------------------------------------------------	

	public Point2(Map<String, Map<String, Integer>> CatePointNameMap,
				  Map<String, Map<Integer, Double>> CenterFixedMap,
				  Map<String, Map<Integer, Double>> CateCenterMap, 
				  Map<String, Map<Integer, Double>> TrainPointMap){

		//CenterPointMap = new TreeMap<String, Map<Integer, Double>>();
		//TrainpointMap  = new TreeMap<String, Map<Integer, Double>>();
		w = 1;
		K = 3.0;
		B = 1;
		a = 0.01;
		xMin = 0.0;
		xMax = 0.8;
		Mnum = 3;
		CatePointName  = CatePointNameMap;
		CenterFixed    = CenterFixedMap;
		CenterPoint    = CateCenterMap;
		Trainpoint     = TrainPointMap;
		
		Set<Map.Entry<String, Map<Integer, Double>>> TrainpointSet = Trainpoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainpointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			normalization(me.getValue());
		}
		
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			normalization(me.getValue());
		}
		
		Set<Map.Entry<String, Map<Integer, Double>>> CenterFixedSet = CenterFixed.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterFixedSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			normalization(me.getValue());
		}	
		
		CenterPointMinFreeSpace  = new TreeMap<String, Double>();
		MinFreeSpacePointToPoint = new TreeMap<String, String>();
		CenterPointMinLWindex  = new TreeMap<String, Double>();
		MinLWindexPointToPoint = new TreeMap<String, String>();

		CenterPointMinLWradius  = new TreeMap<String, Double>();
		MinLWradiusPointToPoint	= new TreeMap<String, String>();

		ClusterRadius = new TreeMap<String, Double>();
		TrainFileFlagMapVsm = new TreeMap<String, Integer>();
	}

	public void SetA(double i){
		a = i;
	}
	
	public void SetMnum(int i){
		Mnum = i;
	}
	
	public void SetxMinMax(double iMin, double iMax){
		xMin = iMin;
		xMax = iMax;
	}
	
	public Map<String, Map<Integer, Double>> GetCenterPointMap(){
		return CenterPoint;
	}

	public Map<String, Double> GetCenterPointMinFreeSpace(){
		return CenterPointMinFreeSpace;
	}

	public Map<String, Double> GetClusterRadius(){
		return ClusterRadius;
	}

	public double GetMinFreeSpace(){
		double MinFreeSpace = 10.0;

		Set<Map.Entry<String, Double>> allFreeSpaceSet = CenterPointMinFreeSpace.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allFreeSpaceSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			if(MinFreeSpace > me.getValue())
			{
				MinFreeSpace = me.getValue();
			}
		}
		return MinFreeSpace;
	}

	public static Map<Integer, Double> GetGlobalCenter(Map<String, Map<Integer, Double>> TrainFileMapVsm) throws IOException {
	
		Map<Integer, Double> GlobalCenterpoint = new TreeMap<Integer, Double>();
	
		int TotalNum = TrainFileMapVsm.size();
	
		Set<Map.Entry<String, Map<Integer, Double>>> TrainFileMapVsmSet = TrainFileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = TrainFileMapVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
	
			Set<Map.Entry<Integer, Double>> allVsmSet2 = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();
	
				if(GlobalCenterpoint.containsKey(me2.getKey())){
					double p = GlobalCenterpoint.get(me2.getKey());
					GlobalCenterpoint.put(me2.getKey(),p+me2.getValue());
				}else{
					GlobalCenterpoint.put(me2.getKey(), me2.getValue());
				}
			}
		}
	
	    //printSIDMap(strDir, "CateCenterMap.txt", CateCenterMap);
	
		Set<Map.Entry<Integer, Double>> GlobalCenterpointSet = GlobalCenterpoint.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> it = GlobalCenterpointSet.iterator(); it.hasNext();) {
			Map.Entry<Integer, Double> me = it.next();
	
			GlobalCenterpoint.put(me.getKey(),me.getValue()/TotalNum);
		}
		return GlobalCenterpoint;
	}

	public String GetMinFreeSpaceCluster(){
		double MinFreeSpace = 10.0;
		String MinFreeSpaceName = "";
		Set<Map.Entry<String, Double>> allFreeSpaceSet = CenterPointMinFreeSpace.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allFreeSpaceSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			if(MinFreeSpace > me.getValue())
			{
				MinFreeSpace = me.getValue();
				MinFreeSpaceName = me.getKey();
			}
		}
		return MinFreeSpaceName;
	}

	public String GetMinFreeSpaceClusterSlave(String CenterPoint){
		return MinFreeSpacePointToPoint.get(CenterPoint);
	}
	
	public double GetAverageFreeSpace(){
		double SumFreeSpace = 0.0;

		Set<Map.Entry<String, Double>> allFreeSpaceSet = CenterPointMinFreeSpace.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allFreeSpaceSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			SumFreeSpace += me.getValue();
		}
		SumFreeSpace = SumFreeSpace/CenterPointMinFreeSpace.size();
		return SumFreeSpace;
	}

	public double GetMaxClusterRadius(){
		double MaxClusterRadius = 0.0;

		Set<Map.Entry<String, Double>> allClusterRadiusSet = ClusterRadius.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allClusterRadiusSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			if(MaxClusterRadius < me.getValue())
			{
				MaxClusterRadius = me.getValue();
			}
		}
		return MaxClusterRadius;
	}

	public double GetAverageClusterRadius(){
		double SumClusterRadius = 0.0;

		Set<Map.Entry<String, Double>> allClusterRadiusSet = ClusterRadius.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allClusterRadiusSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			SumClusterRadius += me.getValue();
		}
		SumClusterRadius = SumClusterRadius/ClusterRadius.size();
		return SumClusterRadius;
	}
	
	public double CptTotalFreeSpace(){
		double sum = 0.0;
		double FreeSpace = 0.0;
		double MinFreeSpace = 0.0;
		String MinFreeSpaceName = "";
		CenterPointMinFreeSpace.clear();
		MinFreeSpacePointToPoint.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MinFreeSpace = 10.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					FreeSpace = CptFreeSpace(me.getKey(), me2.getKey());
					sum += FreeSpace;
					if(MinFreeSpace > FreeSpace){
						MinFreeSpace = FreeSpace;
						MinFreeSpaceName = me2.getKey();
					}
				}
			}
			CenterPointMinFreeSpace.put(me.getKey(), MinFreeSpace);
			MinFreeSpacePointToPoint.put(me.getKey(), MinFreeSpaceName);
		}
		return sum;
	}
	
	public double CptFreeSpace2(String CenterPointMain, String CenterPointSlave){
		double sum = 0.0;
		String NearestPointToMain  = FindNearestPoint3(CenterPointMain, CenterPointSlave);
		String NearestPointToSlave = FindNearestPoint3(CenterPointSlave, CenterPointMain);
		double distanceToMain  = 1-CptSimility(NearestPointToMain, CenterPointMain);
		double distanceToSlave = 1-CptSimility(NearestPointToSlave, CenterPointMain);
		sum = distanceToMain - distanceToSlave;
		if(false)
		{
			System.out.println("CptFreeSpace distanceToMain : "+distanceToMain);
			System.out.println("CptFreeSpace distanceToSlave: "+distanceToSlave);
			System.out.println("CptFreeSpace CenterPointMain : "+CenterPointMain);
			System.out.println("CptFreeSpace CenterPointSlave: "+CenterPointSlave);
			System.out.println("CptFreeSpace NearestPointToMain : "+NearestPointToMain);
			System.out.println("CptFreeSpace NearestPointToSlave: "+NearestPointToSlave);
		}
		return sum;
	}
	
	public double CptFreeSpace(String CenterPointMain, String CenterPointSlave){
		double sum = 0.0;
		double distanceSlaveToMain  = FindNearestPoint(CenterPointMain, CenterPointSlave);
		double distanceMainToMain = FindNearestPoint2(CenterPointMain, CenterPointSlave);
		sum = distanceSlaveToMain - distanceMainToMain;
		if(false)
		{
			System.out.println("CptFreeSpace distanceSlaveToMain : "+distanceSlaveToMain);
			System.out.println("CptFreeSpace distanceMainToMain  : "+distanceMainToMain);
		}
		return sum;
	}

	public double CptFreeSpace(String CenterPointMain, String CenterPointSlave, double u){
		double sum = 0.0;
		double distanceSlaveToMain  = FindNearestPoint(CenterPointMain, CenterPointSlave, u);
		double distanceMainToMain = FindNearestPoint2(CenterPointMain, CenterPointSlave, u);
		sum = distanceSlaveToMain - distanceMainToMain;
		if(false)
		{
			System.out.println("CptFreeSpace distanceSlaveToMain : "+distanceSlaveToMain);
			System.out.println("CptFreeSpace distanceMainToMain  : "+distanceMainToMain);
		}
		return sum;
	}
	
	public String FindNearestPoint3(String CenterPointMain, String CenterPointSlave){
		double sum = 0.0;
		double MaxSimility      = -10.0;
		double SimilityWithMain = 0.0;
		String NearestPoint = "";
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointSlave).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();
			SimilityWithMain = CptSimility(me.getKey(), CenterPointMain);
			if(MaxSimility < SimilityWithMain){
				MaxSimility  = SimilityWithMain;
				NearestPoint = me.getKey();
			}
		}
		return NearestPoint;
	}
	public double FindNearestPoint(String CenterPointMain, String CenterPointSlave){
		double sum = 0.0;
		double Mindistance      = 0.0;
		double DistanceWithMain = 0.0;
		int MnumBak = Mnum;

		String NearestPoint = "";
		SortedMap<Double, Integer> MindistanceMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointSlave).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();
			DistanceWithMain = 1-CptSimility(me.getKey(), CenterPointMain);
            MindistanceMap.put(DistanceWithMain, 1);
		}
		
		Set<Map.Entry<Double, Integer>> MindistanceMapSet2 = MindistanceMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MindistanceMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(MnumBak>0){
				Mindistance += me2.getKey();
				MnumBak--;
			}else{
				break;
			}
		}
		
		if(false)
		{
			System.out.println("1 Mindistance/Mnum : "+Mindistance/Mnum);
		}
		return Mindistance/Mnum;
	}
	
	public double FindNearestPoint2(String CenterPointMain, String CenterPointSlave){
		double sum = 0.0;
		double Mindistance      = 0.0;
		double DistanceWithMain = 0.0;
		int MnumBak = Mnum;

		String NearestPoint = "";
		SortedMap<Double, String> MindistanceMap = new TreeMap<Double, String>();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();
			DistanceWithMain = 1-CptSimility(me.getKey(), CenterPointSlave);
            MindistanceMap.put(DistanceWithMain, me.getKey());
		}
		
		Set<Map.Entry<Double, String>> MindistanceMapSet2 = MindistanceMap.entrySet();
		for (Iterator<Map.Entry<Double, String>> it2 = MindistanceMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, String> me2 = it2.next();
			if(MnumBak>0){
				Mindistance += CptSimility(me2.getValue(), CenterPointMain);
				MnumBak--;
			}else{
				break;
			}
		}
		if(false)
		{
			System.out.println("2 Mindistance/Mnum : "+Mindistance/Mnum);
		}
		return Mindistance/Mnum;
	}

	public double FindNearestPoint(String CenterPointMain, String CenterPointSlave, double u){
		double sum = 0.0;
		double Mindistance      = 0.0;
		double DistanceWithMain = 0.0;
		double uMnum = u*CatePointName.get(CenterPointSlave).size();
		uMnum = uMnum>=1?uMnum:1;
		double uMnumBak = uMnum;

		String NearestPoint = "";
		SortedMap<Double, Integer> MindistanceMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointSlave).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();
			DistanceWithMain = 1-CptSimility(me.getKey(), CenterPointMain);
            MindistanceMap.put(DistanceWithMain, 1);
		}
		
		Set<Map.Entry<Double, Integer>> MindistanceMapSet2 = MindistanceMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MindistanceMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(uMnumBak>0){
				Mindistance += me2.getKey();
				uMnumBak--;
			}else{
				break;
			}
		}
		
		if(false)
		{
			System.out.println("1 Mindistance/Mnum : "+Mindistance/Mnum);
		}
		return Mindistance/uMnum;
	}
	
	public double FindNearestPoint2(String CenterPointMain, String CenterPointSlave, double u){
		double sum = 0.0;
		double Mindistance      = 0.0;
		double DistanceWithMain = 0.0;
		double uMnum = u*CatePointName.get(CenterPointSlave).size();
		uMnum = uMnum>=1?uMnum:1;
		double uMnumBak = uMnum;

		String NearestPoint = "";
		SortedMap<Double, String> MindistanceMap = new TreeMap<Double, String>();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();
			DistanceWithMain = 1-CptSimility(me.getKey(), CenterPointSlave);
            MindistanceMap.put(DistanceWithMain, me.getKey());
		}
		
		Set<Map.Entry<Double, String>> MindistanceMapSet2 = MindistanceMap.entrySet();
		for (Iterator<Map.Entry<Double, String>> it2 = MindistanceMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, String> me2 = it2.next();
			if(uMnumBak>0){
				Mindistance += CptSimility(me2.getValue(), CenterPointMain);
				uMnumBak--;
			}else{
				break;
			}
		}
		if(false)
		{
			System.out.println("2 Mindistance/Mnum : "+Mindistance/Mnum);
		}
		return Mindistance/uMnum;
	}


	public double CptClusterRadius(){
		double sum = 0.0;
		double Radius = 0.0;
		ClusterRadius.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			Radius = CptOneClusterRadius(me.getKey());
			sum += Radius;
			//System.out.println("CptClusterRadius Radius("+me.getKey()+")    : "+Radius);
			ClusterRadius.put(me.getKey(), Radius);
		}
		return sum;
	}

	public double CptOneClusterRadius(String CenterPoint){
		int MnumBak = Mnum;
		int index = 0;
		double sum = 0.0;
		double Radius = 0.0;
		double MaxRadius = 0.0;
		SortedMap<Double, Integer> MaxRadiusMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPoint).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
			Map.Entry<String, Integer> me2 = it2.next();
			Radius = CptSimility(me2.getKey(), CenterPoint);
			sum += 1-Radius;
			index++;
            MaxRadiusMap.put(Radius, 1);
		}
		sum = sum/index;

		Set<Map.Entry<Double, Integer>> MaxRadiusMapSet2 = MaxRadiusMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MaxRadiusMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(MnumBak>0){
				MaxRadius += 1-me2.getKey();
				MnumBak--;
			}else{
				break;
			}
		}
		
		return MaxRadius/Mnum;
	}

	public double CptOneClusterRadius2(String CenterPoint){
		int MnumBak = Mnum;
		int index = 0;
		double sum = 0.0;
		double Radius = 0.0;
		double MaxRadius = 0.0;
		Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPoint).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
			Map.Entry<String, Integer> me2 = it2.next();
			Radius = CptSimility(me2.getKey(), CenterPoint);
			sum += 1-Radius;
			index++;
		}
		sum = sum/index;
		
		return sum;
	}

	public double CptOneClusterSSW(String CenterPoint){
		int MnumBak = Mnum;
		//int index = 0;
		double sum = 0.0;
		double Radius = 0.0;
		double MaxRadius = 0.0;
		Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPoint).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
			Map.Entry<String, Integer> me2 = it2.next();
			Radius = CptSimility(me2.getKey(), CenterPoint);
			sum += 1-Radius;
			//index++;
		}
	
		return sum;
	}
	
	public double CptOneClusterRadius(String CenterPoint, double u){
		double  uMnum = u*CatePointName.get(CenterPoint).size();
		uMnum = uMnum>=1?uMnum:1;
		double  uMnumBak = uMnum;
		int index = 0;
		double sum = 0.0;
		double Radius = 0.0;
		double MaxRadius = 0.0;
		SortedMap<Double, Integer> MaxRadiusMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPoint).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
			Map.Entry<String, Integer> me2 = it2.next();
			Radius = CptSimility(me2.getKey(), CenterPoint);
			sum += 1-Radius;
			index++;
            MaxRadiusMap.put(Radius, 1);
		}
		sum = sum/index;

		Set<Map.Entry<Double, Integer>> MaxRadiusMapSet2 = MaxRadiusMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MaxRadiusMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(uMnumBak>0){
				MaxRadius += 1-me2.getKey();
				uMnumBak--;
			}else{
				break;
			}
		}
		
		return MaxRadius/uMnum;
	}
//===========================================================
	public double CptTotalPercentLWindex(double u){
		double sum = 0.0;
		double LWindex = 0.0;
		double MinLWindex = 0.0;
		String MinLWindexName = "";
		CenterPointMinLWindex.clear();
		MinLWindexPointToPoint.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MinLWindex = 10.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					LWindex = CptLWindex(me.getKey(), me2.getKey(), u);
					sum += LWindex;
					if(MinLWindex > LWindex){
						MinLWindex = LWindex;
						MinLWindexName = me2.getKey();
					}
				}
			}
			CenterPointMinLWindex.put(me.getKey(), MinLWindex);
			MinLWindexPointToPoint.put(me.getKey(), MinLWindexName);
		}
		return sum;
	}

	public double CptTotalPercentLWradius(double u){
		double sum = 0.0;
		double LWindex = 0.0;
		double MinLWindex = 0.0;
		String MinLWindexName = "";
		CenterPointMinLWindex.clear();
		MinLWindexPointToPoint.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MinLWindex = 10.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					LWindex = CptLWradius(me.getKey(), me2.getKey(), u);
					sum += LWindex;
					if(MinLWindex > LWindex){
						MinLWindex = LWindex;
						MinLWindexName = me2.getKey();
					}
				}
			}
			CenterPointMinLWradius.put(me.getKey(), MinLWindex);
			MinLWradiusPointToPoint.put(me.getKey(), MinLWindexName);
		}
		return sum;
	}

	public double CptTotalPercentLWindexradius(double u){
		double sum = 0.0;
		double LWindex = 0.0;
		double MinLWindex = 0.0;
		String MinLWindexName = "";
		CenterPointMinLWindex.clear();
		MinLWindexPointToPoint.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MinLWindex = 10.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					LWindex = CptLWindexradius(me.getKey(), me2.getKey(), u);
					sum += LWindex;
					if(MinLWindex > LWindex){
						MinLWindex = LWindex;
						MinLWindexName = me2.getKey();
					}
				}
			}
			CenterPointMinLWindex.put(me.getKey(), MinLWindex);
			MinLWindexPointToPoint.put(me.getKey(), MinLWindexName);
		}
		return sum;
	}

	public double CptLWindex(String CenterPointMain, String CenterPointSlave, double u){
        int Separable = 0;
		double LWindex = 0.0;
		double Radius_i = 0.0;
		double Radius_j = 0.0;
		double distance_ij = 0.0;
        double MinRadius = 0.0;

		//distance_ij = 1-CptSimilityCTC(CenterPointMain, CenterPointSlave);
		//Radius_i = CptOneClusterRadius(CenterPointMain, u);
		//Radius_j = CptOneClusterRadius(CenterPointSlave, u);
		if((distance_ij<Radius_i)||(distance_ij<Radius_j))
		{
			//LWindex = distance_ij-(Radius_i+Radius_j);
		}else{
			//LWindex = CptFreeSpace(CenterPointMain, CenterPointSlave, u);
			//LWindex_ji = CptFreeSpace(CenterPointSlave, CenterPointMain);
		}

		LWindex = CptFreeSpace(CenterPointMain, CenterPointSlave, u);
		if(Radius_i<Radius_j)
		{
			MinRadius = Radius_i;
		}else{
			MinRadius = Radius_j;
		}

		if(LWindex<-2*MinRadius){
			Separable = 1;
		}else{
			if(LWindex<0){
				Separable = 2;
			}else{
				Separable = 3;
			}
		}

		if(false)
		{
			System.out.println("CptLWindex distance("+CenterPointMain+","+CenterPointSlave+"): "+distance_ij);
			System.out.println("CptLWindex Radius("+CenterPointMain+")    : "+Radius_i);
			System.out.println("CptLWindex Radius("+CenterPointSlave+")    : "+Radius_j);
			System.out.println("CptLWindex LWindex     : "+LWindex);
			switch(Separable)
			{
				case 1:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly non-separable");
					break;
				case 2:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  approximate linearly separable");
					break;
				case 3:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly separable");
					break;
				default:
					System.out.println("CptLWindex Separable    : "+Separable);
			}
		    System.out.println("-----------------------------------------");
		}
		return LWindex;
	}

	public double CptLWradius(String CenterPointMain, String CenterPointSlave, double u){
        int Separable = 0;
		double LWradius = 0.0;
		double distance_ij = 1-CptSimilityCTC(CenterPointMain, CenterPointSlave);
        double MinRadius = 0.0;
		double Radius_i = CptOneClusterRadius(CenterPointMain, u);
		double Radius_j = CptOneClusterRadius(CenterPointSlave, u);

		LWradius = distance_ij-(Radius_i+Radius_j);


		if(Radius_i<Radius_j)
		{
			MinRadius = Radius_i;
		}else{
			MinRadius = Radius_j;
		}

		if(LWradius<-2*MinRadius){
			Separable = 1;
		}else{
			if(LWradius<0){
				Separable = 2;
			}else{
				Separable = 3;
			}
		}

		if(false)
		{
			System.out.println("CptLWindex distance("+CenterPointMain+","+CenterPointSlave+"): "+distance_ij);
			System.out.println("CptLWindex Radius("+CenterPointMain+")    : "+Radius_i);
			System.out.println("CptLWindex Radius("+CenterPointSlave+")    : "+Radius_j);
			System.out.println("CptLWindex LWindex     : "+LWradius);
			switch(Separable)
			{
				case 1:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly non-separable");
					break;
				case 2:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  approximate linearly separable");
					break;
				case 3:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly separable");
					break;
				default:
					System.out.println("CptLWindex Separable    : "+Separable);
			}
		    System.out.println("-----------------------------------------");
		}
		return LWradius;
	}

	public double CptLWindexradius(String CenterPointMain, String CenterPointSlave, double u){
        int Separable = 0;
		double LWindex = 0.0;
		double Radius_i = 0.0;
		double Radius_j = 0.0;
		double distance_ij = 0.0;
        double MinRadius = 0.0;

		distance_ij = 1-CptSimilityCTC(CenterPointMain, CenterPointSlave);
		Radius_i = CptOneClusterRadius(CenterPointMain, u);
		Radius_j = CptOneClusterRadius(CenterPointSlave, u);
		if((distance_ij<Radius_i)||(distance_ij<Radius_j))
		{
			LWindex = distance_ij-(Radius_i+Radius_j);
		}else{
			LWindex = CptFreeSpace(CenterPointMain, CenterPointSlave, u);
		}

		if(Radius_i<Radius_j)
		{
			MinRadius = Radius_i;
		}else{
			MinRadius = Radius_j;
		}

		if(LWindex<-2*MinRadius){
			Separable = 1;
		}else{
			if(LWindex<0){
				Separable = 2;
			}else{
				Separable = 3;
			}
		}

		if(false)
		{
			System.out.println("CptLWindex distance("+CenterPointMain+","+CenterPointSlave+"): "+distance_ij);
			System.out.println("CptLWindex Radius("+CenterPointMain+")    : "+Radius_i);
			System.out.println("CptLWindex Radius("+CenterPointSlave+")    : "+Radius_j);
			System.out.println("CptLWindex LWindex     : "+LWindex);
			switch(Separable)
			{
				case 1:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly non-separable");
					break;
				case 2:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  approximate linearly separable");
					break;
				case 3:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly separable");
					break;
				default:
					System.out.println("CptLWindex Separable    : "+Separable);
			}
		    System.out.println("-----------------------------------------");
		}
		return LWindex;
	}

//===========================================================
	public double CptTotalLWindex(){
		double sum = 0.0;
		double LWindex = 0.0;
		double MinLWindex = 0.0;
		String MinLWindexName = "";
		CenterPointMinLWindex.clear();
		MinLWindexPointToPoint.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MinLWindex = 10.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					LWindex = CptLWindex(me.getKey(), me2.getKey());
					sum += LWindex;
					if(MinLWindex > LWindex){
						MinLWindex = LWindex;
						MinLWindexName = me2.getKey();
					}
				}
			}
			CenterPointMinLWindex.put(me.getKey(), MinLWindex);
			MinLWindexPointToPoint.put(me.getKey(), MinLWindexName);
		}
		return sum;
	}

	public double CptTotalLWradius(){
		double sum = 0.0;
		double LWindex = 0.0;
		double MinLWindex = 0.0;
		String MinLWindexName = "";
		CenterPointMinLWindex.clear();
		MinLWindexPointToPoint.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MinLWindex = 10.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					LWindex = CptLWradius(me.getKey(), me2.getKey());
					sum += LWindex;
					if(MinLWindex > LWindex){
						MinLWindex = LWindex;
						MinLWindexName = me2.getKey();
					}
				}
			}
			CenterPointMinLWradius.put(me.getKey(), MinLWindex);
			MinLWradiusPointToPoint.put(me.getKey(), MinLWindexName);
		}
		return sum;
	}

	public double CptTotalLWindexradius(){
		double sum = 0.0;
		double LWindex = 0.0;
		double MinLWindex = 0.0;
		String MinLWindexName = "";
		CenterPointMinLWindex.clear();
		MinLWindexPointToPoint.clear();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MinLWindex = 10.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					LWindex = CptLWindexradius(me.getKey(), me2.getKey());
					sum += LWindex;
					if(MinLWindex > LWindex){
						MinLWindex = LWindex;
						MinLWindexName = me2.getKey();
					}
				}
			}
			CenterPointMinLWindex.put(me.getKey(), MinLWindex);
			MinLWindexPointToPoint.put(me.getKey(), MinLWindexName);
		}
		return sum;
	}

	public double CptLWindex(String CenterPointMain, String CenterPointSlave){
        int Separable = 0;
		double LWindex = 0.0;
		double Radius_i = 0.0;
		double Radius_j = 0.0;
		double distance_ij = 0.0;
        double MinRadius = 0.0;

		//distance_ij = 1-CptSimilityCTC(CenterPointMain, CenterPointSlave);
		//double Radius_i = CptOneClusterRadius(CenterPointMain);
		//double Radius_j = CptOneClusterRadius(CenterPointSlave);

		//Radius_i = CptOneClusterRadius2(CenterPointMain);
		//Radius_j = CptOneClusterRadius2(CenterPointSlave);
		if((distance_ij<Radius_i)||(distance_ij<Radius_j))
		{
			//LWindex = distance_ij-(Radius_i+Radius_j);
		}else{
			//LWindex = CptFreeSpace(CenterPointMain, CenterPointSlave);
			//LWindex_ji = CptFreeSpace(CenterPointSlave, CenterPointMain);
		}
		
		LWindex = CptFreeSpace(CenterPointMain, CenterPointSlave);

		if(Radius_i<Radius_j)
		{
			MinRadius = Radius_i;
		}else{
			MinRadius = Radius_j;
		}

		if(LWindex<-2*MinRadius){
			Separable = 1;
		}else{
			if(LWindex<0){
				Separable = 2;
			}else{
				Separable = 3;
			}
		}

		if(false)
		{
			System.out.println("CptLWindex distance("+CenterPointMain+","+CenterPointSlave+"): "+distance_ij);
			System.out.println("CptLWindex Radius("+CenterPointMain+")    : "+Radius_i);
			System.out.println("CptLWindex Radius("+CenterPointSlave+")    : "+Radius_j);
			System.out.println("CptLWindex LWindex     : "+LWindex);
			switch(Separable)
			{
				case 1:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly non-separable");
					break;
				case 2:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  approximate linearly separable");
					break;
				case 3:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly separable");
					break;
				default:
					System.out.println("CptLWindex Separable    : "+Separable);
			}
		    System.out.println("-----------------------------------------");
		}
		return LWindex;
	}

	public double CptLWradius(String CenterPointMain, String CenterPointSlave){
        int Separable = 0;
		double LWradius = 0.0;
		double distance_ij = 1-CptSimilityCTC(CenterPointMain, CenterPointSlave);
        double MinRadius = 0.0;

		double Radius_i = CptOneClusterRadius(CenterPointMain);
		double Radius_j = CptOneClusterRadius(CenterPointSlave);

		//double Radius_i = CptOneClusterRadius2(CenterPointMain);
		//double Radius_j = CptOneClusterRadius2(CenterPointSlave);

		LWradius = distance_ij-(Radius_i+Radius_j);


		if(Radius_i<Radius_j)
		{
			MinRadius = Radius_i;
		}else{
			MinRadius = Radius_j;
		}

		if(LWradius<-2*MinRadius){
			Separable = 1;
		}else{
			if(LWradius<0){
				Separable = 2;
			}else{
				Separable = 3;
			}
		}

		if(false)
		{
			System.out.println("CptLWindex distance("+CenterPointMain+","+CenterPointSlave+"): "+distance_ij);
			System.out.println("CptLWindex Radius("+CenterPointMain+")    : "+Radius_i);
			System.out.println("CptLWindex Radius("+CenterPointSlave+")    : "+Radius_j);
			System.out.println("CptLWindex LWindex     : "+LWradius);
			switch(Separable)
			{
				case 1:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly non-separable");
					break;
				case 2:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  approximate linearly separable");
					break;
				case 3:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly separable");
					break;
				default:
					System.out.println("CptLWindex Separable    : "+Separable);
			}
		    System.out.println("-----------------------------------------");
		}
		return LWradius;
	}
	
	public double CptLWindexradius(String CenterPointMain, String CenterPointSlave){
        int Separable = 0;
		double LWindex = 0.0;
		double Radius_i = 0.0;
		double Radius_j = 0.0;
		double distance_ij = 0.0;
        double MinRadius = 0.0;

		distance_ij = 1-CptSimilityCTC(CenterPointMain, CenterPointSlave);
		Radius_i = CptOneClusterRadius(CenterPointMain);
		Radius_j = CptOneClusterRadius(CenterPointSlave);

		if((distance_ij<Radius_i)||(distance_ij<Radius_j))
		{
			LWindex = distance_ij-(Radius_i+Radius_j);
		}else{
			LWindex = CptFreeSpace(CenterPointMain, CenterPointSlave);
		}
		
		if(Radius_i<Radius_j)
		{
			MinRadius = Radius_i;
		}else{
			MinRadius = Radius_j;
		}

		if(LWindex<-2*MinRadius){
			Separable = 1;
		}else{
			if(LWindex<0){
				Separable = 2;
			}else{
				Separable = 3;
			}
		}

		if(false)
		{
			System.out.println("CptLWindex distance("+CenterPointMain+","+CenterPointSlave+"): "+distance_ij);
			System.out.println("CptLWindex Radius("+CenterPointMain+")    : "+Radius_i);
			System.out.println("CptLWindex Radius("+CenterPointSlave+")    : "+Radius_j);
			System.out.println("CptLWindex LWindex     : "+LWindex);
			switch(Separable)
			{
				case 1:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly non-separable");
					break;
				case 2:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  approximate linearly separable");
					break;
				case 3:
					System.out.println("Cluster "+CenterPointMain+","+CenterPointSlave+":  linearly separable");
					break;
				default:
					System.out.println("CptLWindex Separable    : "+Separable);
			}
		    System.out.println("-----------------------------------------");
		}
		return LWindex;
	}
//======================================================================
	
	public double GetMinLWindex(){
		double MinLWindex = 0xffffff;

		Set<Map.Entry<String, Double>> allLWindexSet = CenterPointMinLWindex.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allLWindexSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			if(MinLWindex > me.getValue())
			{
				MinLWindex = me.getValue();
			}
		}
		return MinLWindex;
	}

	public double GetAverageLWindex(){
		double SumLWindex = 0.0;

		Set<Map.Entry<String, Double>> allFreeSpaceSet = CenterPointMinLWindex.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allFreeSpaceSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			SumLWindex += me.getValue();
		}
		SumLWindex = SumLWindex/CenterPointMinLWindex.size();
		return SumLWindex;
	}

	public double GetMinLWradius(){
		double MinLWindex = 0xffffff;

		Set<Map.Entry<String, Double>> allLWindexSet = CenterPointMinLWradius.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allLWindexSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			if(MinLWindex > me.getValue())
			{
				MinLWindex = me.getValue();
			}
		}
		return MinLWindex;
	}
	
	public double GetAverageLWradius(){
		double SumLWindex = 0.0;

		Set<Map.Entry<String, Double>> allFreeSpaceSet = CenterPointMinLWradius.entrySet();
		for (Iterator<Map.Entry<String, Double>> it = allFreeSpaceSet.iterator(); it.hasNext();) {
			Map.Entry<String, Double> me = it.next();
			SumLWindex += me.getValue();
		}
		SumLWindex = SumLWindex/CenterPointMinLWradius.size();
		return SumLWindex;
	}

	public void DataAnalyse()
	{
		double TotalLWindex = CptTotalLWindex();
		double MinLWindex   = GetMinLWindex();
		double AverageLWindex = GetAverageLWindex();
		System.out.println(" TotalLWindex  : "+TotalLWindex);
		System.out.println(" MinLWindex    : "+MinLWindex);
		System.out.println(" AverageLWindex: "+AverageLWindex);

		CptClusterRadius();
		double MaxClusterRadius     = GetMaxClusterRadius();
		double AverageClusterRadius = GetAverageClusterRadius();
		System.out.println(" MaxClusterRadius     : "+MaxClusterRadius);
		System.out.println(" AverageClusterRadius : "+AverageClusterRadius);

		double DI     = DunnIndex();
		double DBI    = DaviesBouldinIndex();
		System.out.println(" DunnIndex            : "+DI);
		System.out.println(" DaviesBouldinIndex   : "+DBI);
	    System.out.println("-----------------------------------------");
	}
//===========================================================	
	public double GetDataSetLWindex()
	{
		double TotalLWindex = CptTotalLWindex();
		double MinLWindex   = GetMinLWindex();
		double AverageLWindex = GetAverageLWindex();
		System.out.println(" TotalLWindex  : "+TotalLWindex);
		System.out.println(" MinLWindex    : "+MinLWindex);
		System.out.println(" AverageLWindex: "+AverageLWindex);
	    System.out.println("-----------------------------------------");
	    return AverageLWindex;
	}

	public double GetDataSetLWradius()
	{
		double TotalLWradius = CptTotalLWradius();
		double MinLWradius   = GetMinLWradius();
		double AverageLWradius = GetAverageLWradius();
		System.out.println(" TotalLWradius  : "+TotalLWradius);
		System.out.println(" MinLWradius    : "+MinLWradius);
		System.out.println(" AverageLWradius: "+AverageLWradius);
	    System.out.println("-----------------------------------------");
	    return AverageLWradius;
	}

	public double GetDataSetLWindexradius()
	{
		double TotalLWindexradius = CptTotalLWindexradius();
		double MinLWindex   = GetMinLWindex();
		double AverageLWindex = GetAverageLWindex();
		System.out.println(" TotalLWindexradius  : "+TotalLWindexradius);
		System.out.println(" MinLWindex          : "+MinLWindex);
		System.out.println(" AverageLWindex      : "+AverageLWindex);
	    System.out.println("-----------------------------------------");
	    return AverageLWindex;
	}	

	public double GetDataSetPercentLWindex(double u)
	{
		double TotalLWindex = CptTotalPercentLWindex(u);
		double MinLWindex   = GetMinLWindex();
		double AverageLWindex = GetAverageLWindex();
		System.out.println("      Percent  : "+u);
		System.out.println(" TotalLWindex  : "+TotalLWindex);
		System.out.println(" MinLWindex    : "+MinLWindex);
		System.out.println(" AverageLWindex: "+AverageLWindex);
	    System.out.println("-----------------------------------------");
	    return AverageLWindex;
	}

	public double GetDataSetPercentLWradius(double u)
	{
		double TotalLWradius = CptTotalPercentLWradius(u);
		double MinLWradius   = GetMinLWradius();
		double AverageLWradius = GetAverageLWradius();
		System.out.println("       Percent  : "+u);
		System.out.println(" TotalLWradius  : "+TotalLWradius);
		System.out.println(" MinLWradius    : "+MinLWradius);
		System.out.println(" AverageLWradius: "+AverageLWradius);
	    System.out.println("-----------------------------------------");
	    return AverageLWradius;
	}

	public double GetDataSetPercentLWindexradius(double u)
	{
		double TotalLWindexradius = CptTotalPercentLWindexradius(u);
		double MinLWindex   = GetMinLWindex();
		double AverageLWindex = GetAverageLWindex();
		System.out.println("            Percent  : "+u);
		System.out.println(" TotalLWindexradius  : "+TotalLWindexradius);
		System.out.println(" MinLWindex          : "+MinLWindex);
		System.out.println(" AverageLWindex      : "+AverageLWindex);
	    System.out.println("-----------------------------------------");
	    return AverageLWindex;
	}

//===========================================================	
	public double DaviesBouldinIndex()
	{
		double sum  = 0.0;
		double R_ij = 0.0;
		double MaxR_ij = 0.0;

		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			MaxR_ij = 0.0;
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					R_ij = R_fun(me.getKey(), me2.getKey());
					if(MaxR_ij < R_ij){
						MaxR_ij = R_ij;
					}
				}
			}
			sum += MaxR_ij;
		}

		double DBI = sum/CenterPoint.size();
		System.out.println("    Davies Index: "+DBI);
	    System.out.println("-----------------------------------------");

		return DBI;
	}

	public double R_fun(String CenterPointMain, String CenterPointSlave)
	{
		double Radius_i = CptOneClusterRadius(CenterPointMain);
		double Radius_j = CptOneClusterRadius(CenterPointSlave);
		double distance_ij = 1-CptSimilityCTC(CenterPointMain, CenterPointSlave);
		return (Radius_i+Radius_j)/distance_ij;
	}
//===========================================================
//20150714 Silhouette index
	public double SilhouetteIndex()
	{
		int TotalSample = 0;
		double Simility = 0.0;
		double minDistance = 0.0;
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			TotalSample += CatePointName.get(me.getKey()).size();

			Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(me.getKey()).entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();

				Simility = Silhouette_S(me2.getKey(), me.getKey());
	            minDistance += Simility;
			}
		}
		 
		double SIL = minDistance/TotalSample;
		System.out.println(" SilhouetteIndex: "+SIL);
		System.out.println("-----------------------------------------");
		return SIL;
	}

	public double Silhouette_S(String SampleName, String CenterName)
	{
		double Silb = Silhouette_b(SampleName, CenterName);
		double Sila = Silhouette_a(SampleName, CenterName);
		double Maxab = Sila>Silb?Sila:Silb;
		return (Silb-Sila)/Maxab;
	}

	public double Silhouette_a(String SampleName, String CenterName)
	{
		double Simility    = 0.0;
		double minDistance = 0.0;

		int Clustersize = CatePointName.get(CenterName).size();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterName).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();

			Simility = CptSimilitySTS(SampleName, me.getKey());
			minDistance += 1-Simility;
		}
		return minDistance/Clustersize;
	}	

	public double Silhouette_b(String SampleName, String CenterName)
	{
		int MnumNew = 1;
		int MnumBak = MnumNew;
		double Simility    = 0.0;
		double minDistance = 0.0;
		SortedMap<Double, Integer> MaxRadiusMap = new TreeMap<Double, Integer>();

		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			if(me.getKey() != CenterName){
				Simility = Silhouette_a(SampleName, me.getKey());
	            MaxRadiusMap.put(Simility, 1);
			}
		}

		Set<Map.Entry<Double, Integer>> MaxRadiusMapSet2 = MaxRadiusMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MaxRadiusMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(MnumBak>0){
				minDistance += me2.getKey();
				MnumBak--;
			}else{
				break;
			}
		}

		return minDistance/MnumNew;
	}	

//===========================================================
//20160930 Sum of based indices
	public Map<String, Double> SumofSquaresbasedIndices()
	{
		double SSW = 0.0;
		double SSB = 0.0;
		double Radius = 0.0;
		double ClassSample = 0.0;
		double TotalSample = 0.0;
		Map<String, Double> ReturnMap = new TreeMap<String, Double>();
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			TotalSample += CatePointName.get(me.getKey()).size();
			Radius = CptOneClusterSSW(me.getKey());
			SSW += Radius;
		}
		SSW = SSW/TotalSample;
		
		try {
			GlobalCenterpoint = GetGlobalCenter(Trainpoint);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		normalization(GlobalCenterpoint);
		
		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
			ClassSample = CatePointName.get(me2.getKey()).size();
			Radius = 1-computeSim(GlobalCenterpoint,me2.getValue());
			SSB += ClassSample*Radius;
		}
		SSB = SSB/TotalSample;
		
		double m = CenterPoint.size();
		double d = GlobalCenterpoint.size();
		
		double BallHallIndex = SSW/m;
		double CHIndex = (SSB/(m-1))/(SSW/(TotalSample-m));
		double HIndex  = Math.log(SSB/SSW);
		double XUIndex = d*Math.log(Math.sqrt(SSW/(d*TotalSample*TotalSample)))+Math.log(m);
		double WBIndex = m*SSW/SSB;
		
		ReturnMap.put("BallHallIndex",BallHallIndex);
		ReturnMap.put("CHIndex",CHIndex);
		ReturnMap.put("HIndex",HIndex);
		ReturnMap.put("XUIndex",XUIndex);
		ReturnMap.put("WBIndex",WBIndex);
		
		System.out.println("             SSW: "+SSW);
		System.out.println("             SSB: "+SSB);
		System.out.println("   BallHallIndex: "+BallHallIndex);
		System.out.println("         CHIndex: "+CHIndex);
		System.out.println("          HIndex: "+HIndex);
		System.out.println("         XUIndex: "+XUIndex);
		System.out.println("         WBIndex: "+WBIndex);
		System.out.println("-----------------------------------------");
		return ReturnMap;
	}

//===========================================================
	public double DunnIndex()
	{
		double sum  = 0.0;
		double Radius_i = 0.0;
		double MaxRadius_i = 0.0;
		double Distance_ij = 0.0;
		double Mindistance_ij = 1.0;

		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			Radius_i = CptOneClusterRadius(me.getKey());
			if(MaxRadius_i < Radius_i){
				MaxRadius_i = Radius_i;
			}
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					Distance_ij = 1-CptSimilityCTC(me.getKey(), me2.getKey());
					if(Mindistance_ij > Distance_ij){
						Mindistance_ij = Distance_ij;
					}
				}
			}
		}
		return Mindistance_ij/MaxRadius_i;
	}

	public double DunnIndex(int InterClusterIndex, int IntraClusterIndex)
	{
		double sum  = 0.0;
		double Radius_i = 0.0;
		double MaxRadius_i = 0.0;
		double Distance_ij = 0.0;
		double Mindistance_ij = 0xFFFF;

		Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet = CenterPoint.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = CenterPointSet.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();

			switch(IntraClusterIndex)
			{
				case 1:
					Radius_i = DunnIntraClusterDistance1(me.getKey());
					break;
				case 2:
					Radius_i = DunnIntraClusterDistance2(me.getKey());
					break;
				case 3:
					Radius_i = DunnIntraClusterDistance3(me.getKey());
					break;
				default:
					System.err.println("DunnIndex Slected default");
					Radius_i = 0;
					break;
			}
			if(MaxRadius_i < Radius_i){
				MaxRadius_i = Radius_i;
			}

			
			Set<Map.Entry<String, Map<Integer, Double>>> CenterPointSet2 = CenterPoint.entrySet();
			for (Iterator<Map.Entry<String, Map<Integer, Double>>> it2 = CenterPointSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Map<Integer, Double>> me2 = it2.next();
				if(me2.getKey()!=me.getKey()){
					switch(InterClusterIndex){
						case 1:
							Distance_ij = DunnInterClusterDistance1(me.getKey(), me2.getKey());
							break;
						case 2:
							Distance_ij = DunnInterClusterDistance2(me.getKey(), me2.getKey());
							break;
						case 3:
							Distance_ij = DunnInterClusterDistance3(me.getKey(), me2.getKey());
							break;
						case 4:
							Distance_ij = DunnInterClusterDistance4(me.getKey(), me2.getKey());
							break;
						case 5:
							Distance_ij = DunnInterClusterDistance5(me.getKey(), me2.getKey());
							break;
						case 6:
							Distance_ij = DunnInterClusterDistance6(me.getKey(), me2.getKey());
							break;
						default:
							System.err.println("DunnIndex Slected default");
							Distance_ij = 0;
							break;
					}
					if(Mindistance_ij > Distance_ij){
						Mindistance_ij = Distance_ij;
					}
				}
			}
		}
		double DunnI = Mindistance_ij/MaxRadius_i;
		System.out.println("     Inter Index: "+InterClusterIndex);
		System.out.println("     Intra Index: "+IntraClusterIndex);
		System.out.println("  Mindistance_ij: "+Mindistance_ij);
		System.out.println("     MaxRadius_i: "+MaxRadius_i);
		System.out.println("       DunnIndex: "+DunnI);
		System.out.println("-----------------------------------------");
		return DunnI;
	}	
//===========================================================
//20150714 generalizations of Dunn's index

	//Two nearest points from each cluster 
	public double DunnInterClusterDistance1(String CenterPointMain, String CenterPointSlave)
	{

		int index   = 0;
		int MnumNew = Mnum;
		int MnumBak = MnumNew;
		double Simility = 0.0;
		double minDistance = 0.0;
		SortedMap<Double, Integer> MaxRadiusMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();

			Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPointSlave).entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();

				Simility = CptSimilitySTS(me.getKey(), me2.getKey());
				index++;
	            MaxRadiusMap.put(1-Simility, 1);
			}
		}

		Set<Map.Entry<Double, Integer>> MaxRadiusMapSet2 = MaxRadiusMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MaxRadiusMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(MnumBak>0){
				minDistance += me2.getKey();
				MnumBak--;
			}else{
				break;
			}
		}
		
		return minDistance/MnumNew;
	}

	//Two farest points from each cluster 
	public double DunnInterClusterDistance2(String CenterPointMain, String CenterPointSlave)
	{

		int index   = 0;
		int MnumNew = Mnum;
		int MnumBak = MnumNew;
		double Simility = 0.0;
		double minDistance = 0.0;
		SortedMap<Double, Integer> MaxRadiusMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();

			Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPointSlave).entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();

				Simility = CptSimilitySTS(me.getKey(), me2.getKey());
				index++;
	            MaxRadiusMap.put(Simility, 1);
			}
		}

		Set<Map.Entry<Double, Integer>> MaxRadiusMapSet2 = MaxRadiusMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MaxRadiusMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(MnumBak>0){
				minDistance += 1-me2.getKey();
				MnumBak--;
			}else{
				break;
			}
		}
		
		return minDistance/MnumNew;
	}

	//Two farest points from each cluster 
	public double DunnInterClusterDistance3(String CenterPointMain, String CenterPointSlave)
	{

		int index   = 0;
		int MnumNew = Mnum;
		int MnumBak = MnumNew;
		double Simility = 0.0;
		double minDistance = 0.0;
		int ClusterMainSize = CatePointName.get(CenterPointMain).size();
		int ClusterSlavSize = CatePointName.get(CenterPointSlave).size();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();

			Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPointSlave).entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();

				Simility = CptSimilitySTS(me.getKey(), me2.getKey());
				index++;
				minDistance += 1-Simility;
			}
		}
		minDistance = minDistance/(ClusterMainSize*ClusterSlavSize);
		return minDistance;
	}

	//Two  cluster center
	public double DunnInterClusterDistance4(String CenterPointMain, String CenterPointSlave)
	{
		double Simility = 0.0;
		Simility = CptSimilityCTC(CenterPointMain, CenterPointSlave);
		return 1-Simility;
	}

	//Two farest points from each cluster 
	public double DunnInterClusterDistance5(String CenterPointMain, String CenterPointSlave)
	{

		int index   = 0;
		int MnumNew = Mnum;
		int MnumBak = MnumNew;
		double Simility = 0.0;
		double minDistance = 0.0;
		int ClusterMainSize = CatePointName.get(CenterPointMain).size();
		int ClusterSlavSize = CatePointName.get(CenterPointSlave).size();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();

			Simility = CptSimility(me.getKey(), CenterPointMain);
			minDistance += 1-Simility;
		}
		
		Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPointSlave).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
			Map.Entry<String, Integer> me2 = it2.next();

			Simility = CptSimility(me2.getKey(), CenterPointSlave);
			minDistance += 1-Simility;
		}
		
		minDistance = minDistance/(ClusterMainSize+ClusterSlavSize);
		return minDistance;
	}	

	//Hausdorff 
	public double DunnInterClusterDistance6(String CenterPointMain, String CenterPointSlave)
	{
		double Distance1 = DunnInterClusterHausdorff(CenterPointMain,CenterPointSlave);
		double Distance2 = DunnInterClusterHausdorff(CenterPointSlave,CenterPointMain);
		double Distance3 = Distance1>Distance2?Distance1:Distance2;
		return Distance3;
	}
	//Hausdorff 
	public double DunnInterClusterHausdorff(String CenterPointMain, String CenterPointSlave)
	{

		int index   = 0;
		int MnumNew = Mnum;
		int MnumBak = MnumNew;
		double Simility = 0.0;
		double minDistance = 0.0;
		SortedMap<Double, Integer> MaxDistanceMap = new TreeMap<Double, Integer>();
		SortedMap<Double, Integer> MinDistanceMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();

			MinDistanceMap.clear();

			Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPointSlave).entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();

				Simility = CptSimilitySTS(me.getKey(), me2.getKey());
				index++;
	            MinDistanceMap.put(1-Simility, 1);
			}
			
			MnumBak = MnumNew;
			minDistance = 0.0;
			Set<Map.Entry<Double, Integer>> MinDistanceMapSet = MinDistanceMap.entrySet();
			for (Iterator<Map.Entry<Double, Integer>> it2 = MinDistanceMapSet.iterator(); it2.hasNext();) {
				Map.Entry<Double, Integer> me2 = it2.next();
				if(MnumBak>0){
					minDistance += me2.getKey();
					MnumBak--;
				}else{
					break;
				}
			}
			minDistance = minDistance/MnumNew;
			MaxDistanceMap.put(1-minDistance,1);
		}

		MnumBak = MnumNew;
		minDistance = 0.0;
		Set<Map.Entry<Double, Integer>> MaxDistanceMapSet = MaxDistanceMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MaxDistanceMapSet.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(MnumBak>0){
				minDistance += 1-me2.getKey();
				MnumBak--;
			}else{
				break;
			}
		}

		return minDistance/MnumNew;
	}
//===========================================================
	public double DunnIntraClusterDistance1(String CenterPointMain)
	{

		int index   = 0;
		int MnumNew = Mnum;
		int MnumBak = MnumNew;
		double Simility = 0.0;
		double minDistance = 0.0;
		SortedMap<Double, Integer> MaxRadiusMap = new TreeMap<Double, Integer>();
		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();

			Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPointMain).entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();

				Simility = CptSimilitySTS(me.getKey(), me2.getKey());
				index++;
	            MaxRadiusMap.put(Simility, 1);
			}
		}

		Set<Map.Entry<Double, Integer>> MaxRadiusMapSet2 = MaxRadiusMap.entrySet();
		for (Iterator<Map.Entry<Double, Integer>> it2 = MaxRadiusMapSet2.iterator(); it2.hasNext();) {
			Map.Entry<Double, Integer> me2 = it2.next();
			if(MnumBak>0){
				minDistance += 1-me2.getKey();
				MnumBak--;
			}else{
				break;
			}
		}
		
		return minDistance/MnumNew;
	}

	public double DunnIntraClusterDistance2(String CenterPointMain)
	{

		int index   = 0;
		int MnumNew = Mnum;
		int MnumBak = MnumNew;
		double Simility = 0.0;
		double minDistance = 0.0;
		int ClusterMainSize = CatePointName.get(CenterPointMain).size();

		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();

			Set<Map.Entry<String, Integer>> allVsmSet2 = CatePointName.get(CenterPointMain).entrySet();
			for (Iterator<Map.Entry<String, Integer>> it2 = allVsmSet2.iterator(); it2.hasNext();) {
				Map.Entry<String, Integer> me2 = it2.next();

				Simility = CptSimilitySTS(me.getKey(), me2.getKey());
				index++;
				minDistance += 1-Simility;
			}
		}
		minDistance = minDistance/(ClusterMainSize*(ClusterMainSize-1));
		return minDistance;
	}

	public double DunnIntraClusterDistance3(String CenterPointMain)
	{

		int index   = 0;
		int MnumNew = Mnum;
		int MnumBak = MnumNew;
		double Simility = 0.0;
		double minDistance = 0.0;
		int ClusterMainSize = CatePointName.get(CenterPointMain).size();

		Set<Map.Entry<String, Integer>> allVsmSet = CatePointName.get(CenterPointMain).entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = allVsmSet.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> me = it.next();

			Simility = CptSimility(me.getKey(), CenterPointMain);
			minDistance += 1-Simility;
		}
		
		minDistance = 2*minDistance/ClusterMainSize;
		return minDistance;
	}
//===========================================================
	private static void normalization(Map<Integer, Double> c)
	{
		int j = 0;
		double sum = 0.0;
		Set<Map.Entry<Integer, Double>> xSet = c.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> xit = xSet.iterator(); xit.hasNext();) {
			Map.Entry<Integer, Double> xme = xit.next();

			sum += xme.getValue()*xme.getValue();
		}
		sum = Math.sqrt(sum);
		Set<Map.Entry<Integer, Double>> xSet1 = c.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> xit1 = xSet1.iterator(); xit1.hasNext();) {
			Map.Entry<Integer, Double> xme1 = xit1.next();
			c.put(xme1.getKey(), xme1.getValue()/sum);
		}
	}

	static double dot(Map<Integer, Double> x, Map<Integer, Double> c)
	{
		double sum = 0;
		Set<Map.Entry<Integer, Double>> xSet = x.entrySet();
		for (Iterator<Map.Entry<Integer, Double>> xit = xSet.iterator(); xit.hasNext();) {
			Map.Entry<Integer, Double> xme = xit.next();

			if(c.containsKey(xme.getKey())){
				sum += xme.getValue()*c.get(xme.getKey());
			}
		}
		return sum;
	}

	private static double computeSim(Map<Integer, Double> testWordTFMap,
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
		mul = mul/(testAbs * trainAbs);
		if (Double.isNaN(mul)) {
			System.out.println("computeSim:Find a NaN ");
			//DataPreProcess.printIDMap("c:/", "testWordTFMap.txt",testWordTFMap);
			//DataPreProcess.printIDMap("c:/", "trainWordTFMap.txt",trainWordTFMap);
		}
		if(mul == 1){
			//mul = 0.0;
		}
		return mul;
	}

	public double CptSimility(String xname, String cname){
		double sum = 0.0;
		sum += computeSim(Trainpoint.get(xname),CenterPoint.get(cname));
		return sum;
	}

	public double CptSimility2(String xname, String cname){
		double sum = 0.0;
		sum += computeSim(Trainpoint.get(xname),CenterFixed.get(cname));
		return sum;
	}

	public double CptSimilityCTC(String cname1, String cname2){
		double sum = 0.0;
		sum += computeSim(CenterPoint.get(cname1),CenterPoint.get(cname2));
		return sum;
	}

	public double CptSimilitySTS(String xname1, String xname2){
		double sum = 0.0;
		sum += computeSim(Trainpoint.get(xname1),Trainpoint.get(xname2));
		return sum;
	}

}
