package org.aksw.dice.matrixUtil;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

public class MatrixUtilities {

	public void printSparseMat(String name, Matrix m) {
		System.out.println("---------------------");
		System.out.println(name);
		System.out.println("---------------------");
		int rows = m.numRows();
		int cols = m.numColumns();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				System.out.printf("%.9f" + " \t", m.get(i, j));
			}
			System.out.println();
		}
		System.out.println("---------------------");
		System.out.println("---------------------");

	}

	public void fill(Matrix A, double value) {
		int n = A.numRows(), m = A.numColumns();
		if (m == 0 || n == 0)
			throw new RuntimeException("Size not acurate");

		for (int j = 0; j < n; ++j) {
			for (int i = 0; i < m; ++i) {
				A.set(j, i, value);
			}
		}
	}

	public Matrix add(Matrix A, Matrix B) {
		DenseMatrix C = new DenseMatrix(A.add(B));
		return C;
	}

	public void fill(Vector A, double value) {
		int n = A.size();
		if (n == 0)
			throw new RuntimeException("Size not acurate");

		for (int j = 0; j < n; ++j) {

			A.set(j, value);

		}
	}

	public DenseMatrix concat(DenseMatrix Sn, DenseMatrix St) {
		int size = Sn.numRows() + St.numRows();
		int size1 = Sn.numRows();
		int size2 = St.numRows();
		DenseMatrix S = new DenseMatrix(size, 1);
		for (int i = 0; i < size2; i++) {
			S.set(i, 0, St.get(i, 0));
		}
		for (int i = 0; i < size1; i++) {
			S.set(size2 + i, 0, Sn.get(i, 0));
		}

		return S;
	}

	public DenseMatrix horConcat(LinkedSparseMatrix Sn, LinkedSparseMatrix St) {
		if (Sn.numRows() != St.numRows()) {
			throw new RuntimeException("Number of rows not equal");
		}
		int row = Sn.numRows();
		int col = Sn.numColumns() + St.numColumns();
		int size1 = Sn.numColumns();
		int size2 = St.numColumns();
		DenseMatrix S = new DenseMatrix(row, col);
		for (int j = 0; j < row; ++j) {
			for (int i = 0; i < size1; ++i) {
				S.set(j, i, Sn.get(j, i));
			}

		}
		for (int j = 0; j < row; ++j) {
			for (int i = 0; i < size2; ++i) {

				S.set(j, size1 + i, St.get(j, i));
			}
		}
		return S;

	}

	public DenseMatrix verConcat(DenseMatrix Sn, DenseMatrix St) {
		if (Sn.numColumns() != St.numColumns()) {
			throw new RuntimeException("Number of columns not equal");
		}
		int row = Sn.numRows() + St.numRows();
		int col = Sn.numColumns();
		int size1 = Sn.numRows();
		int size2 = St.numRows();
		DenseMatrix S = new DenseMatrix(row, col);
		for (int j = 0; j < col; ++j) {
			for (int i = 0; i < size1; ++i) {
				S.set(i, j, Sn.get(i, j));

			}

		}
		for (int j = 0; j < col; ++j) {
			for (int i = 0; i < size2; ++i) {
				S.set(size1 + i, j, St.get(i, j));
			}
		}
		return S;

	}
}
