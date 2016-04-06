package es.uam.eps.bmi.search.ranking.graph;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public class Matrix {

	private DenseMatrix64F mat;

	public Matrix(double[] data, int nrow, int ncol) {
		mat = new DenseMatrix64F(nrow, ncol);
		mat.setData(data);
	}
	
	public Matrix(int nrow,int ncol){
		mat = new DenseMatrix64F(nrow, ncol);
	}

	public double[] getData(){
		return mat.data;
	}
	public Matrix(DenseMatrix64F mat) {
		this.mat = mat;
	}

	public int getNumRows(){
		return mat.numRows;
	}
	
	public int getNumCols(){
		return mat.numCols;
	}
	
	public static Matrix producto(Matrix m1, Matrix m2) {
		DenseMatrix64F ret = new DenseMatrix64F(m1.getNumRows(), m2.getNumCols());
		CommonOps.mult(m1.mat, m2.mat, ret);
		return new Matrix(ret);
	}

	void set(double value, int row, int col) {
		mat.set(row, col, value);
	}

	@Override
	public String toString() {
		return mat.toString();
	}

	void setRow(int row, int val) {
		for (int col = 0; col < mat.numCols; col++) {
			mat.set(row, col, val);
		}
	}

	double get(int row, int col) {
		return mat.get(row, col);
	}

	public static Matrix power(Matrix m1, int exp) {
		Matrix a;
		if (exp <= 1) {
			return m1;
		} else {
			a = Matrix.producto(m1, m1);
			a.normalize();
			return power(a, exp - 1);
		}

	}
	
	public static Matrix resta(Matrix m1,Matrix m2){
		 DenseMatrix64F toret = new  DenseMatrix64F(m1.getNumRows(),m1.getNumCols());
		 CommonOps.sub(m1.mat, m2.mat, toret);
		 return new Matrix(toret);
	}

	double[] getRow(int row) {
		return Arrays.copyOfRange(mat.data, row * mat.numRows, mat.numRows* (row + 1));
	}

	void normalize() {
		Matrix m_norm = new Matrix(mat);
		double [] rowValues;
		double valueToNorm;
		for (int row = 0; row < this.getNumRows();row++){
			rowValues = this.getRow(row);
			valueToNorm = DoubleStream.of(rowValues).parallel().sum();
			if ((valueToNorm==1) || (valueToNorm == 0))
				continue;
			for(int col = 0; col < this.getNumCols(); col ++){
				m_norm.set(mat.get(row,col)/valueToNorm, row, col);
			}
		}
	}

	void teleport(double r) {
		
		double[] d = new double[mat.numCols*mat.numRows];
		Arrays.fill(d, 1.0/mat.numCols);
		Matrix m = new Matrix(d,mat.numRows,mat.numCols);
		CommonOps.add(1-r, mat, r, m.mat, mat);
	}
	
	

}
