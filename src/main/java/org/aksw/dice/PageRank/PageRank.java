package org.aksw.dice.PageRank;

import java.io.IOException;
import java.util.logging.Logger;

import org.aksw.dice.HARE.HARERank;
import org.aksw.dice.matrixUtil.MatrixUtilities;
import org.aksw.dice.matrixUtil.TransitionMatrixUtil;
import org.apache.jena.rdf.model.Model;
import no.uib.cipr.matrix.Matrix;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

public class PageRank {
	private static final Logger LOGGER = Logger.getLogger(HARERank.class.getName());

	public LinkedSparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	public LinkedSparseMatrix F;

	// P: is the product matrix

	DenseMatrix P;
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

	public PageRank(Model data) {
		this.matrxUtil = new TransitionMatrixUtil(data);
		this.matUtil = new MatrixUtilities();
		this.W = matrxUtil.getW();
		this.F = matrxUtil.getF();

		LOGGER.info("Page Rank Constructor Initialisation Complete ");

	}

	public void calculateRank() {
		LOGGER.info("Page Rank Calculation started ");
		double beta = this.matrxUtil.getBeta();
		double intitialValue = 1 / beta;
		LinkedSparseMatrix blk1 = new LinkedSparseMatrix(W.numRows(), F.numColumns());
		LinkedSparseMatrix blk2 = new LinkedSparseMatrix(F.numRows(), W.numColumns());
		blk1.zero();
		blk2.zero();

		int iteration = 1;

		this.P = this.matUtil.verConcat(this.matUtil.horConcat(blk1, W), this.matUtil.horConcat(F, blk2));

		DenseMatrix PRval = new DenseMatrix(P.numRows(), 1);
		this.matUtil.fill(PRval, intitialValue);
		DenseMatrix I = new DenseMatrix(P.numRows(), 1);
		this.matUtil.fill(I, 1);

		double damping = 0.85;
		double epsilon = 1e-3;
		double error = 1;
		DenseMatrix PRval_used = new DenseMatrix(PRval);

		LOGGER.info("All setup complete ");
		while (error > epsilon) {
			LOGGER.info("Going for iteration " + iteration);
			DenseMatrix previousPRval = new DenseMatrix(PRval_used);
			DenseMatrix I_used = new DenseMatrix(I);
			DenseMatrix P_1 = new DenseMatrix(P.numRows(), 1);
			P.transAmult(previousPRval, P_1);
			P_1.scale(damping);
			I_used.scale((1 - damping) / (double) previousPRval.numRows());
			PRval_used = new DenseMatrix(P_1.add(I_used));
			LOGGER.info("Iteration " + iteration + "Complete");
			error = PRval_used.norm(Matrix.Norm.One) - previousPRval.norm(Matrix.Norm.One);
			iteration++;

		}
		LOGGER.info("Rank Calculation Completed!!");
		this.S_n_Final = PRval_used;
		this.matUtil.printSparseMat("P", this.S_n_Final);
		LOGGER.info("Obtained Final S matrix!!");

	}

	/**
	 * @return the s_n_Final
	 */
	public DenseMatrix getS_n_Final() {
		return S_n_Final;
	}

	/**
	 * @param s_n_Final the s_n_Final to set
	 */
	public void setS_n_Final(DenseMatrix s_n_Final) {
		S_n_Final = s_n_Final;
	}

}
