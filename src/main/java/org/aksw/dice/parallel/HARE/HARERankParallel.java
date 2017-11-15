package org.aksw.dice.parallel.HARE;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.pfunction.library.concat;
import org.ujmp.core.SparseMatrix;

import org.ujmp.core.util.io.IntelligentFileWriter;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector.Norm;
import no.uib.cipr.matrix.io.MatrixInfo.MatrixField;
import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

public class HARERankParallel {
	public static final String OUTPUT_FILE = "LastParallelRankCalculation.txt";
	LinkedSparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	LinkedSparseMatrix F;

	// P: is the product matrix
	Matrix P_n;
	//Matrix P_t;
	Matrix S_n_Final;
	Matrix S_t_Final;
	TransitionMatrixUtilParallel matrxUtil;

	public HARERankParallel(Model data) {
		this.matrxUtil = new TransitionMatrixUtilParallel(data);
		this.W = new LinkedSparseMatrix(this.matrxUtil.beta, this.matrxUtil.alpha);
		this.F = new LinkedSparseMatrix(this.matrxUtil.alpha, this.matrxUtil.beta);
		this.P_n = new LinkedSparseMatrix(this.matrxUtil.alpha, this.matrxUtil.alpha);
		this.S_t_Final= new LinkedSparseMatrix(this.matrxUtil.beta, 1);;
		//this.P_t = new LinkedSparseMatrix(this.matrxUtil.beta, this.matrxUtil.beta);

	}

	public void calculateRank() {
		this.W = matrxUtil.getW();
		this.F = matrxUtil.getF();
		this.F.mult(this.W, this.P_n);
		
		System.out.println( "P_n = "+P_n.toString());
		int alpha = this.matrxUtil.getAlpha();
		int beta = this.matrxUtil.getBeta();
		double intitialValue = 1 / alpha;
		Matrix S_n = new DenseMatrix(alpha, 1);
		for (int i = 0; i < S_n.numRows(); ++i) {
			S_n.set(i, 0, intitialValue);
		}

		Matrix I = new DenseMatrix(alpha, 1);

		for (int i = 0; i < I.numRows(); ++i) {
			I.set(i, 0, 1);
		}

		double damping = 0.85;
		double epsilon = 1e-3;
		double error = 1;
		int iteration = 1;
		// Iteration over Equation 9
		while (iteration < 10) {
			Matrix S_n_previous = S_n;
			Matrix term1 = new DenseMatrix(alpha, 1);
			P_n.scale(damping).mult(S_n_previous, term1);
			System.out.println(term1.toString());
			S_n = term1.add(I.scale((1 - damping) / S_n_previous.numRows()));
			System.out.println("+++++++++++++++++++++++++++++++");
			System.out.println(S_n.toString());
			iteration++;
			
			// Difference
			//error = S_n.norm(Matrix.Norm.One) - S_n_previous.norm(Matrix.Norm.One);

		}

		// Multiply with Equation 8
		double factorSn = alpha / (beta + alpha);
		double factorSt = beta / (beta + alpha);

		this.F.transAmult(S_n, S_t_Final);
		S_t_Final = S_t_Final.scale(factorSt);
		S_n_Final = S_n.scale(factorSn);
	//	Matrix S = this.concat(S_n_Final, S_t_Final);
		//System.out.println(S_n.toString());
		// this.writeRankToFile();
	}

	public Matrix concat(Matrix Sn, Matrix St) {
		int size = Sn.numColumns() + St.numColumns();
		int size1 = Sn.numColumns();
		int size2 = St.numColumns();
		Matrix S = new DenseMatrix(size, 1);
		for (int i = 0; i < size2; i++) {
			S.set(1, i, St.get(1, i));
		}
		for (int i = 0; i < size1; i++) {
			S.set(1, size2 + i, Sn.get(1, i));
		}

		return S;
	}

	public void writeRankToFile() {
		try {
			@SuppressWarnings("resource")
			IntelligentFileWriter writer = new IntelligentFileWriter(OUTPUT_FILE);
			writer.write("S_N \n");
			writer.write(this.S_n_Final.toString());
			writer.write(" S_T \n");
			writer.write(this.S_t_Final.toString());
			writer.flush();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Matrix getP_n() {
		return P_n;
	}



}
