package SVM;
import SVM.libsvm.*;
import java.io.*;
import java.util.*;


public class svm_predict {
	private static svm_print_interface svm_print_null = new svm_print_interface()
	{
		public void print(String s) {}
	};

	private static svm_print_interface svm_print_stdout = new svm_print_interface()
	{
		public void print(String s)
		{
			System.out.print(s);
		}
	};

	private static svm_print_interface svm_print_string = svm_print_stdout;

	static void info(String s) 
	{
		svm_print_string.print(s);
	}

	private static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}

	private static void predict(BufferedReader input, DataOutputStream output, svm_model model, int predict_probability) throws IOException
	{
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;

		if(predict_probability == 1)
		{
			if(svm_type == svm_parameter.EPSILON_SVR ||
			   svm_type == svm_parameter.NU_SVR)
			{
				svm_predict.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="+svm.svm_get_svr_probability(model)+"\n");
			}
			else
			{
				int[] labels=new int[nr_class];
				svm.svm_get_labels(model,labels);
				prob_estimates = new double[nr_class];
				output.writeBytes("labels");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(" "+labels[j]);
				output.writeBytes("\n");
			}
		}
		while(true)
		{
			String line = input.readLine();
			if(line == null) break;

			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

			double target = atof(st.nextToken());
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}

			double v;
			if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC))
			{
				v = svm.svm_predict_probability(model,x,prob_estimates);
				output.writeBytes(v+" ");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(prob_estimates[j]+" ");
				output.writeBytes("\n");
			}
			else
			{
				v = svm.svm_predict(model,x);
				output.writeBytes(v+"\n");
			}

			if(v == target)
				++correct;
			error += (v-target)*(v-target);
			sumv += v;
			sumy += target;
			sumvv += v*v;
			sumyy += target*target;
			sumvy += v*target;
			++total;
		}
		if(svm_type == svm_parameter.EPSILON_SVR ||
		   svm_type == svm_parameter.NU_SVR)
		{
			svm_predict.info("Mean squared error = "+error/total+" (regression)\n");
			svm_predict.info("Squared correlation coefficient = "+
				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
				 " (regression)\n");
		}
		else
			svm_predict.info("Accuracy = "+(double)correct/total*100+
				 "% ("+correct+"/"+total+") (SVM classification)\n");
	}

	private static void exit_with_help()
	{
		System.err.print("usage: svm_predict [options] test_file model_file output_file\n"
		+"options:\n"
		+"-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n"
		+"-q : quiet mode (no outputs)\n");
		System.exit(1);
	}

	public static void main(String argv[]) throws IOException
	{
		int i, predict_probability=0;
        	svm_print_string = svm_print_stdout;

		// parse options
		for(i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			++i;
			switch(argv[i-1].charAt(1))
			{
				case 'b':
					predict_probability = atoi(argv[i]);
					break;
				case 'q':
					svm_print_string = svm_print_null;
					i--;
					break;
				default:
					System.err.print("Unknown option: " + argv[i-1] + "\n");
					exit_with_help();
			}
		}
		if(i>=argv.length-2)
			exit_with_help();
		try 
		{
			BufferedReader input = new BufferedReader(new FileReader(argv[i]));
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(argv[i+2])));
			svm_model model = svm.svm_load_model(argv[i+1]);
			if (model == null)
			{
				System.err.print("can't open model file "+argv[i+1]+"\n");
				System.exit(1);
			}
			if(predict_probability == 1)
			{
				if(svm.svm_check_probability_model(model)==0)
				{
					System.err.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			}
			else
			{
				if(svm.svm_check_probability_model(model)!=0)
				{
					svm_predict.info("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
			predict(input,output,model,predict_probability);
			input.close();
			output.close();
		} 
		catch(FileNotFoundException e) 
		{
			exit_with_help();
		}
		catch(ArrayIndexOutOfBoundsException e) 
		{
			exit_with_help();
		}
	}

	public double run(String argv[], svm_model memery_model, Map<String, Map<Integer, Double>> FileMapVsm) throws IOException
	{
		double Accuracy = 0.0;
		int i, predict_probability=0;
		int Use_memery_model = 0;
        svm_print_string = svm_print_stdout;

		// parse options
		for(i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			++i;
			switch(argv[i-1].charAt(1))
			{
				case 'b':
					predict_probability = atoi(argv[i]);
					break;
				case 'q':
					svm_print_string = svm_print_null;
					i--;
					break;
				case 'm':
					Use_memery_model = atoi(argv[i]);
					break;
				default:
					System.err.print("Unknown option: " + argv[i-1] + "\n");
					exit_with_help();
			}
		}
		if(i>=argv.length-2)
			exit_with_help();
		try 
		{
			//BufferedReader input = new BufferedReader(new FileReader(argv[i]));
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(argv[i+2])));
			svm_model model;
			if(0 == Use_memery_model){
				System.out.println("Svm_predict load model... ");
				model = svm.svm_load_model(argv[i+1]);
			}
			else{
				System.out.println("Svm_predict use memery model... ");
				model = memery_model;
			}
			if (model == null)
			{
				System.err.print("can't open model file "+argv[i+1]+"\n");
				System.exit(1);
			}
			if(predict_probability == 1)
			{
				if(svm.svm_check_probability_model(model)==0)
				{
					System.err.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			}
			else
			{
				if(svm.svm_check_probability_model(model)!=0)
				{
					svm_predict.info("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
			//System.out.println("Svm_predict start predict...");
			Accuracy = predict(FileMapVsm,output,model,predict_probability);
			//input.close();
			output.close();
		} 
		catch(FileNotFoundException e) 
		{
			exit_with_help();
		}
		catch(ArrayIndexOutOfBoundsException e) 
		{
			exit_with_help();
		}
		return Accuracy;
	}

	public double run(String argv[], svm_model[] memery_model, 
					   Map<String, Map<Integer, Double>> FileMapVsm,
					   Map<String,String> actual, Map<String,String> pred) throws IOException
	{
		double Accuracy = 0.0;
		int i, predict_probability=0;
		int Use_memery_model = 0;
        svm_print_string = svm_print_stdout;

		// parse options
		for(i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			++i;
			switch(argv[i-1].charAt(1))
			{
				case 'b':
					predict_probability = atoi(argv[i]);
					break;
				case 'q':
					svm_print_string = svm_print_null;
					i--;
					break;
				case 'm':
					Use_memery_model = atoi(argv[i]);
					break;
				default:
					System.err.print("Unknown option: " + argv[i-1] + "\n");
					exit_with_help();
			}
		}
		if(i>=argv.length-2)
			exit_with_help();
		try 
		{
			//BufferedReader input = new BufferedReader(new FileReader(argv[i]));
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(argv[i+2])));

			//System.out.println("Svm_predict start predict...");
			Accuracy = predict(FileMapVsm,output,memery_model,predict_probability,actual,pred);
			//input.close();
			output.close();
		} 
		catch(FileNotFoundException e) 
		{
			exit_with_help();
		}
		catch(ArrayIndexOutOfBoundsException e) 
		{
			exit_with_help();
		}
		return Accuracy;
	}

	public double run(String argv[], svm_model memery_model, Map<String, Map<Integer, Double>> FileMapVsm,
					   Map<String,String> actual, Map<String,String> pred) throws IOException
	{
		double Accuracy = 0.0;
		int i, predict_probability=0;
		int Use_memery_model = 0;
        svm_print_string = svm_print_stdout;

		// parse options
		for(i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			++i;
			switch(argv[i-1].charAt(1))
			{
				case 'b':
					predict_probability = atoi(argv[i]);
					break;
				case 'q':
					svm_print_string = svm_print_null;
					i--;
					break;
				case 'm':
					Use_memery_model = atoi(argv[i]);
					break;
				default:
					System.err.print("Unknown option: " + argv[i-1] + "\n");
					exit_with_help();
			}
		}
		if(i>=argv.length-2)
			exit_with_help();
		try 
		{
			//BufferedReader input = new BufferedReader(new FileReader(argv[i]));
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(argv[i+2])));
			svm_model model;
			if(0 == Use_memery_model){
				System.out.println("Svm_predict load model... ");
				model = svm.svm_load_model(argv[i+1]);
			}
			else{
				System.out.println("Svm_predict use memery model... ");
				model = memery_model;
			}
			if (model == null)
			{
				System.err.print("can't open model file "+argv[i+1]+"\n");
				System.exit(1);
			}
			if(predict_probability == 1)
			{
				if(svm.svm_check_probability_model(model)==0)
				{
					System.err.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			}
			else
			{
				if(svm.svm_check_probability_model(model)!=0)
				{
					svm_predict.info("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
			//System.out.println("Svm_predict start predict...");
			Accuracy = predict(FileMapVsm,output,model,predict_probability,actual,pred);
			//input.close();
			output.close();
		} 
		catch(FileNotFoundException e) 
		{
			exit_with_help();
		}
		catch(ArrayIndexOutOfBoundsException e) 
		{
			exit_with_help();
		}
		return Accuracy;
	}

	private static double predict(Map<String, Map<Integer, Double>> FileMapVsm, DataOutputStream output, svm_model model, int predict_probability) throws IOException
	{
		int i = 0;
		int correct = 0;
		int total = 0;
		int indextemp = 0;
		double error = 0;
		double Accuracy = 0.0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;

		if(predict_probability == 1)
		{
			if(svm_type == svm_parameter.EPSILON_SVR ||
			   svm_type == svm_parameter.NU_SVR)
			{
				svm_predict.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="+svm.svm_get_svr_probability(model)+"\n");
			}
			else
			{
				int[] labels=new int[nr_class];
				svm.svm_get_labels(model,labels);
				prob_estimates = new double[nr_class];
				output.writeBytes("labels");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(" "+labels[j]);
				output.writeBytes("\n");
			}
		}
		
		Set<Map.Entry<String, Map<Integer, Double>>> FileMapVsmSet = FileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = FileMapVsmSet
				.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			
			String categoryName = me.getKey().split("_")[0];
			double target = atof(categoryName);

			int m = me.getValue().size();
			svm_node[] x = new svm_node[m];
			i = 0;
			indextemp = -1;
			Set<Map.Entry<Integer, Double>> allWords = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allWords.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();

				x[i] = new svm_node();
				x[i].index = me2.getKey();
				x[i].value = me2.getValue();
				//check map index, must be accend
				if(indextemp < x[i].index)
					indextemp = x[i].index;
				else{
					System.err.print("check map index, must be accend");
					System.exit(1);
				}
				i++;
			}
			
			double v;
			if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC))
			{
				v = svm.svm_predict_probability(model,x,prob_estimates);
				output.writeBytes(v+" ");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(prob_estimates[j]+" ");
				output.writeBytes("\n");
			}
			else
			{
				v = svm.svm_predict(model,x);
				output.writeBytes(v+"\n");
			}

			if(v == target)
				++correct;
			error += (v-target)*(v-target);
			sumv += v;
			sumy += target;
			sumvv += v*v;
			sumyy += target*target;
			sumvy += v*target;
			++total;
		}
			
		if(svm_type == svm_parameter.EPSILON_SVR ||
		   svm_type == svm_parameter.NU_SVR)
		{
			Accuracy = error/total;
			svm_predict.info("Mean squared error = "+error/total+" (regression)\n");
			svm_predict.info("Squared correlation coefficient = "+
				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
				 " (regression)\n");
		}
		else
		{
			Accuracy = (double)correct/total;
			svm_predict.info("Accuracy = "+(double)correct/total*100+
					 "% ("+correct+"/"+total+") (SVM classification)\n");
		}
		return Accuracy;
	}

	private static double predict(Map<String, Map<Integer, Double>> FileMapVsm, DataOutputStream output, svm_model model, int predict_probability, 
								   Map<String,String> actual, Map<String,String> pred) throws IOException
	{
		int i = 0;
		int z = 0;
		int correct = 0;
		int total = 0;
		int indextemp = 0;
		double error = 0;
		double Accuracy = 0.0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;

		if(predict_probability == 1)
		{
			if(svm_type == svm_parameter.EPSILON_SVR ||
			   svm_type == svm_parameter.NU_SVR)
			{
				svm_predict.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="+svm.svm_get_svr_probability(model)+"\n");
			}
			else
			{
				int[] labels=new int[nr_class];
				svm.svm_get_labels(model,labels);
				prob_estimates = new double[nr_class];
				output.writeBytes("labels");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(" "+labels[j]);
				output.writeBytes("\n");
			}
		}
		
		Set<Map.Entry<String, Map<Integer, Double>>> FileMapVsmSet = FileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = FileMapVsmSet
				.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			
			String categoryName = me.getKey().split("_")[0];
			double target = atof(categoryName);

			int m = me.getValue().size();
			svm_node[] x = new svm_node[m];
			i = 0;
			indextemp = -1;
			Set<Map.Entry<Integer, Double>> allWords = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allWords.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();

				x[i] = new svm_node();
				x[i].index = me2.getKey();
				x[i].value = me2.getValue();
				//check map index, must be accend
				if(indextemp < x[i].index)
					indextemp = x[i].index;
				else{
					System.err.print("check map index, must be accend");
					System.exit(1);
				}
				i++;
			}
			
			double v;
			if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC))
			{
				v = svm.svm_predict_probability(model,x,prob_estimates);
				output.writeBytes(v+" ");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(prob_estimates[j]+" ");
				output.writeBytes("\n");
			}
			else
			{
				v = svm.svm_predict(model,x);
				output.writeBytes(v+"\n");
			}
			
			z++;
			actual.put(""+z, ""+target);
			pred.put(""+z, ""+v);

			if(v == target)
				++correct;
			error += (v-target)*(v-target);
			sumv += v;
			sumy += target;
			sumvv += v*v;
			sumyy += target*target;
			sumvy += v*target;
			++total;
		}
			
		if(svm_type == svm_parameter.EPSILON_SVR ||
		   svm_type == svm_parameter.NU_SVR)
		{
			Accuracy = error/total;
			svm_predict.info("Mean squared error = "+error/total+" (regression)\n");
			svm_predict.info("Squared correlation coefficient = "+
				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
				 " (regression)\n");
		}
		else
		{
			Accuracy = (double)correct/total;
			svm_predict.info("Accuracy = "+(double)correct/total*100+
					 "% ("+correct+"/"+total+") (SVM classification)\n");
		}
		return Accuracy;
	}

	//modifid by liuchuan 20151214 for one class SVM
	private static double predict(Map<String, Map<Integer, Double>> FileMapVsm, DataOutputStream output, svm_model[] model, int predict_probability, 
								   Map<String,String> actual, Map<String,String> pred) throws IOException
	{
		int i = 0;
		int z = 0;
		int correct = 0;
		int total = 0;
		int indextemp = 0;
		double error = 0;
		double Accuracy = 0.0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type=svm.svm_get_svm_type(model[0]);
		int nr_class=svm.svm_get_nr_class(model[0]);
		double[] prob_estimates=null;

		
		Set<Map.Entry<String, Map<Integer, Double>>> FileMapVsmSet = FileMapVsm.entrySet();
		for (Iterator<Map.Entry<String, Map<Integer, Double>>> it = FileMapVsmSet
				.iterator(); it.hasNext();) {
			Map.Entry<String, Map<Integer, Double>> me = it.next();
			
			String categoryName = me.getKey().split("_")[0];
			double target = atof(categoryName);

			int m = me.getValue().size();
			svm_node[] x = new svm_node[m];
			i = 0;
			indextemp = -1;
			Set<Map.Entry<Integer, Double>> allWords = me.getValue().entrySet();
			for (Iterator<Map.Entry<Integer, Double>> it2 = allWords.iterator(); it2.hasNext();) {
				Map.Entry<Integer, Double> me2 = it2.next();

				x[i] = new svm_node();
				x[i].index = me2.getKey();
				x[i].value = me2.getValue();
				//check map index, must be accend
				if(indextemp < x[i].index)
					indextemp = x[i].index;
				else{
					System.err.print("check map index, must be accend");
					System.exit(1);
				}
				i++;
			}
			
			double v;

				v = svm.svm_predict(model,x);
				System.out.println("Predict : "+v+" , actual : "+target);
				
				output.writeBytes(v+"\n");
			
			z++;
			actual.put(""+z, ""+target);
			pred.put(""+z, ""+v);

			if(v == target)
				++correct;
			error += (v-target)*(v-target);
			sumv += v;
			sumy += target;
			sumvv += v*v;
			sumyy += target*target;
			sumvy += v*target;
			++total;
		}
			
		if(svm_type == svm_parameter.EPSILON_SVR ||
		   svm_type == svm_parameter.NU_SVR)
		{
			Accuracy = error/total;
			svm_predict.info("Mean squared error = "+error/total+" (regression)\n");
			svm_predict.info("Squared correlation coefficient = "+
				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
				 " (regression)\n");
		}
		else
		{
			Accuracy = (double)correct/total;
			svm_predict.info("Accuracy = "+(double)correct/total*100+
					 "% ("+correct+"/"+total+") (SVM classification)\n");
		}
		return Accuracy;
	}


}
