package org.aksw.dice.PageRank;

import java.io.IOException;

import org.aksw.dice.HARE.TransitionMatrixUtil;
import org.apache.jena.rdf.model.Model;
import org.ujmp.core.Matrix;
import org.ujmp.core.SparseMatrix;
import org.ujmp.core.util.io.IntelligentFileWriter;

public class PageRank {

	public SparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	public SparseMatrix F;

	// P: is the product matrix
	Matrix P;
	Matrix P_t;

	Matrix S_n_Final;
	Matrix S_t_Final;
	TransitionMatrixUtil matrxUtil;

	public PageRank(Model data) {
		this.matrxUtil = new TransitionMatrixUtil(data);
		this.W = matrxUtil.getW();
		this.F = matrxUtil.getF();

	}

	public void calculateRank() {

		double beta = this.matrxUtil.getBeta();
		double intitialValue = 1 / beta;

		SparseMatrix blk1 = SparseMatrix.Factory.zeros(W.getRowCount(), F.getColumnCount());
		SparseMatrix blk2 = SparseMatrix.Factory.zeros(F.getRowCount(), W.getColumnCount());

		this.P = SparseMatrix.Factory.vertCat(SparseMatrix.Factory.horCat(blk1, W),
				SparseMatrix.Factory.horCat(F, blk2));
		Matrix PRval = Matrix.Factory.fill(intitialValue, P.getRowCount(), (long) 1.0);
		Matrix I = Matrix.Factory.fill(1, P.getRowCount(), (long) 1.0);
		double damping = 0.85;
		double epsilon = 1e-3;
		double error = 1;
		while (error > epsilon) {
			Matrix previousPRval = PRval;
			PRval = (P.times(damping).transpose().mtimes(previousPRval)
					.plus(I.times((1 - damping) / previousPRval.getRowCount())));
			error = PRval.manhattenDistanceTo(previousPRval, true);

		}
		this.S_n_Final = PRval;
		S_n_Final = S_n_Final.transpose();
		System.out.println(S_n_Final.toString());

	}

	public void writeRankToFile(String filename) {
		try {
			@SuppressWarnings("resource")
			IntelligentFileWriter writer = new IntelligentFileWriter(filename);
			writer.write("S_N \n");
			writer.write(this.S_n_Final.toString());
			writer.flush();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Matrix getP_n() {
		return P;
	}

	public Matrix getP_t() {
		return P_t;
	}
}
