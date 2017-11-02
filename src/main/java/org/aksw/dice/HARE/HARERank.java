package org.aksw.dice.HARE;

import org.apache.jena.rdf.model.Model;
import org.ujmp.core.Matrix;
import org.ujmp.core.SparseMatrix;

public class HARERank {
	SparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	SparseMatrix F;

	// P: is the product matrix
	Matrix P_n;
	// Matrix P_t;

	TransitionMatrixUtil matrxUtil;

	public HARERank(Model data) {
		this.matrxUtil = new TransitionMatrixUtil(data);
		this.W = matrxUtil.getW();
		this.F = matrxUtil.getF();

	}

	public void calculateRank() {

		double alpha = this.matrxUtil.getAlpha();
		double beta = this.matrxUtil.getBeta();
		double intitialValue = beta / (alpha * (beta + alpha));
		P_n = this.F.mtimes(this.W);

		Matrix S_n = Matrix.Factory.fill(intitialValue, (long) alpha, (long) 1.0);

		Matrix I = Matrix.Factory.fill(1, (long) alpha, (long) 1.0);
		double damping = 0.85;
		double epsilon = 1e-3;
		double error = 1;
		while (error > epsilon) {
			Matrix S_n_previous = S_n;
			S_n = (P_n.times(damping).transpose().mtimes(S_n_previous)
					.plus(I.times((1 - damping) / S_n_previous.getRowCount())));

			error = S_n.manhattenDistanceTo(S_n_previous, true);
			System.out.println(error);
		}

	}

}
