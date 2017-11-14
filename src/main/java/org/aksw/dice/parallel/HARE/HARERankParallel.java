package org.aksw.dice.parallel.HARE;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;

import org.ujmp.core.SparseMatrix;

import org.ujmp.core.util.io.IntelligentFileWriter;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
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
	Matrix P_t;
	Matrix S_n_Final;
	Matrix S_t_Final;
	TransitionMatrixUtilParallel matrxUtil;

	public HARERankParallel(Model data) {
		this.matrxUtil = new TransitionMatrixUtilParallel(data);
		this.W = matrxUtil.getW();
		this.F = matrxUtil.getF();
		this.F.mult(this.W, this.P_n);

	}

	public void calculateRank() {
		int alpha = this.matrxUtil.getAlpha();
		int beta = this.matrxUtil.getBeta();
		double intitialValue = 1 / alpha;
		Matrix S_n = new DenseMatrix(alpha, 1);
		for (int i = 0; i < S_n.numRows(); ++i) {
			S_n.set(i,0, intitialValue);
		}
		
		Matrix I = new DenseMatrix(alpha, 1);
		
		for (int i = 0; i < I.numRows(); ++i) {
			I.set(i,0, 1);
		}

		double damping = 0.85;
		double epsilon = 1e-3;
		double error = 1;

		// Iteration over Equation 9
		while (error > epsilon) {
			Matrix S_n_previous = S_n;
			Matrix term1 = new DenseMatrix()
		P_n.scale(damping).mult(S_n_previous, );
			S_n = (
					.plus(I.scale(alpha)((1 - damping) / S_n_previous.getRowCount())));
			
			
			error = S_n.manhattenDistanceTo(S_n_previous, true);

		}

		// Multiply with Equation 8
		double factorSn = alpha / (beta + alpha);
		double factorSt = beta / (beta + alpha);

		
		this.F.transpose().mult(S_n, S_t_Final);
		S_t_Final =  S_t_Final.mult(factorSt, S_t_Final).transpose();
		S_n_Final = S_n.times(factorSn).transpose();
		Matrix S = SparseMatrix.Factory.horCat(S_t_Final, S_n_Final);
		System.out.println(S.toString());
		// this.writeRankToFile();
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

	public Matrix getP_t() {
		return P_t;
	}

}
