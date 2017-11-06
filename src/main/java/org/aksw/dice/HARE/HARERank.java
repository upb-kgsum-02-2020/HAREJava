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
	Matrix P_t;

	Matrix S_n_Final;
	Matrix S_t_Final;
	TransitionMatrixUtil matrxUtil;

	public HARERank(Model data) {
		this.matrxUtil = new TransitionMatrixUtil(data);
		this.W = matrxUtil.getW();
		this.F = matrxUtil.getF();
		this.P_n = this.F.mtimes(this.W);
		this.P_t = this.W.mtimes(this.F);

	}

	public void calculateRank() {
		double alpha = this.matrxUtil.getAlpha();
		double beta = this.matrxUtil.getBeta();
		double factor = beta / (alpha * (beta + alpha));
		double intitialValue = 1 / alpha;
		Matrix S_n = Matrix.Factory.fill(intitialValue, (long) alpha, (long) 1.0);
		Matrix I = Matrix.Factory.fill(1, (long) alpha, (long) 1.0);

		double damping = 0.85;
		double epsilon = 1e-3;
		double error = 1;

		// Iteration over Equation 9
		while (error > epsilon) {
			Matrix S_n_previous = S_n;
			S_n = (P_n.times(damping).transpose().mtimes(S_n_previous)
					.plus(I.times((1 - damping) / S_n_previous.getRowCount())));
			error = S_n.manhattenDistanceTo(S_n_previous, true);

		}

		// Multiply with Equation 8
		this.S_n_Final = S_n.times(factor);

		this.calculateScoreTriples();

	}

	// Equation 6
	public void calculateScoreTriples() {
		S_t_Final = this.F.transpose().mtimes(S_n_Final);
		S_t_Final = S_t_Final.transpose();
		S_n_Final = S_n_Final.transpose();
		S_n_Final.showGUI();

	}

	public Matrix getP_n() {
		return P_n;
	}

	public Matrix getP_t() {
		return P_t;
	}

}
