package org.aksw.dice.HARE;

import java.util.logging.Logger;

import org.aksw.dice.matrixUtil.MatrixUtilities;
import org.aksw.dice.matrixUtil.TransitionMatrixUtil;
import org.apache.jena.rdf.model.Model;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

public class HARERank {
	private static final Logger LOGGER = Logger.getLogger(HARERank.class.getName());

	public LinkedSparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	public LinkedSparseMatrix F;

	// P: is the product matrix
	DenseMatrix P_n;
	// DenseMatrix P_t;
	DenseMatrix S;
	DenseMatrix S_n_Final;
	DenseMatrix S_t_Final;
	TransitionMatrixUtil matrxUtil;
	MatrixUtilities matUtil;

	/**
	 * @return the matrxUtil
	 */
	public TransitionMatrixUtil getMatrxUtil() {
		return matrxUtil;
	}

	/**
	 * @param matrxUtil
	 *            the matrxUtil to set
	 */
	public void setMatrxUtil(TransitionMatrixUtil matrxUtil) {
		this.matrxUtil = matrxUtil;
	}

	public HARERank(Model data) {

		this.matrxUtil = new TransitionMatrixUtil(data);
		this.matUtil = new MatrixUtilities();
		this.W = matrxUtil.getW();
		this.F = matrxUtil.getF();
		this.P_n = new DenseMatrix(this.F.numRows(), this.W.numColumns());
		this.F.mult(this.W, this.P_n);
		LOGGER.info("HARE Rank Constructor Initialisation Complete ");

	}

	public void calculateRank() {
		LOGGER.info("HARE Rank calculation started");
		int alpha = this.matrxUtil.getAlpha();
		int beta = this.matrxUtil.getBeta();

		double intitialValue = 1 / (double) alpha;

		DenseMatrix S_n = new DenseMatrix(alpha, 1);
		this.matUtil.fill(S_n, intitialValue);
		DenseMatrix I = new DenseMatrix(alpha, 1);
		this.matUtil.fill(I, 1);
		double damping = 0.85;
		double epsilon = 1e-3;
		double error = 1;
		int iteration = 1;

		DenseMatrix S_n_used = new DenseMatrix(S_n);
	
		LOGGER.info("All setup complete ");
		// Iteration over Equation 9
		while (error > epsilon) {
			LOGGER.info("Going for iteration " + iteration);
			Matrix S_n_previous = new DenseMatrix(S_n_used);
			DenseMatrix I_used = new DenseMatrix(I);
			Matrix S_1 = new DenseMatrix(alpha, 1);
			P_n.transAmult(S_n_previous, S_1);
			S_1.scale(damping);
			I_used.scale((1 - damping) / (double) S_n_previous.numRows());
			S_n_used = new DenseMatrix(S_1.add(I_used));
			LOGGER.info("Iteration " + iteration + "Complete");
			error = S_n_used.norm(Matrix.Norm.One) - S_n_previous.norm(Matrix.Norm.One);
			iteration++;

		}
		LOGGER.info("Rank Calculation Completed!!");
		// Multiply with Equation 8
		double factorSn = (double) alpha / (beta + alpha);
		double factorSt = (double) beta / (beta + alpha);
		S_n = new DenseMatrix(S_n_used);
		DenseMatrix S_t = new DenseMatrix(beta, 1);
		this.F.transAmult(S_n, S_t);
		S_t.scale(factorSt);
		S_n.scale(factorSn);
		this.S = this.matUtil.concat(S_n, S_t);
		this.matUtil.printSparseMat("S", this.S);
		LOGGER.info("Obtained Final S matrix!!");
		// this.matUtil.printSparseMat("S", S_n);

	}

	/**
	 * @return the p_n
	 */
	public DenseMatrix getP_n() {
		return P_n;
	}

	/**
	 * @param p_n
	 *            the p_n to set
	 */
	public void setP_n(DenseMatrix p_n) {
		P_n = p_n;
	}

	/**
	 * @return the s
	 */
	public DenseMatrix getS() {
		return S;
	}

	/**
	 * @param s
	 *            the s to set
	 */
	public void setS(DenseMatrix s) {
		S = s;
	}

	/**
	 * @return the s_n_Final
	 */
	public DenseMatrix getS_n_Final() {
		return S_n_Final;
	}

	/**
	 * @param s_n_Final
	 *            the s_n_Final to set
	 */
	public void setS_n_Final(DenseMatrix s_n_Final) {
		S_n_Final = s_n_Final;
	}

	/**
	 * @return the s_t_Final
	 */
	public DenseMatrix getS_t_Final() {
		return S_t_Final;
	}

	/**
	 * @param s_t_Final
	 *            the s_t_Final to set
	 */
	public void setS_t_Final(DenseMatrix s_t_Final) {
		S_t_Final = s_t_Final;
	}

}