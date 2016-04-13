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

   

        
        
	public void addRowLast(int num) {
		mat.reshape(mat.numRows + num, mat.numCols,true);
	}

	public void addColLast(int num) {
		int ncols = mat.numCols + num;
		double[] newData;
		newData = new double[(ncols * mat.numRows)];
		for (int j = 0; j < mat.getNumCols(); j++) {
			//data[ y*numCols + x ] = data[y][x]
			for (int i = 0; i < mat.getNumRows(); i++) {
				newData[i * ncols + j] = mat.get(i, j);
			}
		}
		mat.setNumCols(ncols);
		mat.setData(newData);
	}

	public Matrix(int nrow, int ncol) {
		mat = new DenseMatrix64F(nrow, ncol);
	}

	public double[] getData() {
		return mat.data;
	}

	public Matrix(DenseMatrix64F mat) {
		this.mat = mat;
	}

	public int getNumRows() {
		return mat.numRows;
	}

	public int getNumCols() {
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

	public static Matrix resta(Matrix m1, Matrix m2) {
		DenseMatrix64F toret = new DenseMatrix64F(m1.getNumRows(), m1.getNumCols());
		CommonOps.sub(m1.mat, m2.mat, toret);
		return new Matrix(toret);
	}

	public double[] getRow(int row) {
		double[] toret = new double[this.getNumCols()];
		for (int j = 0; j < this.getNumCols(); j++) {
			toret[j] = this.get(row, j);
		}
		return toret;
	}

	public double[] getCol(int col) {
		double[] toret = new double[this.getNumRows()];
		for (int j = 0; j < this.getNumRows(); j++) {
			toret[j] = this.get(j, col);
		}
		return toret;
	}

	public void setCol(double[] data, int col) {
		for (int j = 0; j < this.getNumRows(); j++) {
			this.set(data[j], j, col);
		}
	}

	public void setRow(double[] data, int row) {
		for (int k = 0; k < this.getNumCols(); k++) {
			this.set(data[k], row, k);
		}
	}

	public void normalize() {
		Matrix m_norm = new Matrix(mat);
		double[] rowValues;
		double valueToNorm;
		for (int row = 0; row < this.getNumRows(); row++) {
			rowValues = this.getRow(row);
			valueToNorm = DoubleStream.of(rowValues).parallel().sum();
			if ((valueToNorm == 1) || (valueToNorm == 0)) {
				continue;
			}
			for (int col = 0; col < this.getNumCols(); col++) {
				m_norm.set(mat.get(row, col) / valueToNorm, row, col);
			}
		}
	}

	void teleport(double r) {

		double[] d = new double[mat.numCols * mat.numRows];
		Arrays.fill(d, 1.0 / mat.numCols);
		Matrix m = new Matrix(d, mat.numRows, mat.numCols);
		CommonOps.add(1 - r, mat, r, m.mat, mat);
	}

	public Matrix transpose() {
		Matrix toret = new Matrix(this.mat);
		CommonOps.transpose(toret.mat);
		return toret;
	}

	public Matrix RocchiNormalize(Matrix users) {
		Matrix m_norm = new Matrix(mat);
		double[] rowValues;
		double valueToNorm;
		for (int col = 0; col < this.getNumCols(); col++) {
			rowValues = users.getRow(col);
			valueToNorm = 0;
			for (int i = 0; i < rowValues.length; i++) {
				valueToNorm += (rowValues[i] != 0 ? 1 : 0);
			}
			if (valueToNorm == 0)
				continue;
			for (int rw = 0; rw < this.getNumRows(); rw++) {
                            double val = mat.get( rw,col);
                            val = val/valueToNorm;
                            m_norm.set(val, rw,col);
			}
		}
		return m_norm;
	}

	public void set(int row, int col, double rating) {
		this.set(rating, row, col);
	}

}
