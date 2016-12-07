package SVM.libsvm;
public class svm_problem implements java.io.Serializable
{
	public int l;
	public double[] y;
	public String[] name;
	public svm_node[][] x;
}
