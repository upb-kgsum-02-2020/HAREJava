package org.aksw.dice.PageRank;

import java.io.IOException;
import java.util.logging.Logger;

import org.aksw.dice.HARE.HARERank;
import org.aksw.dice.HARE.TransitionMatrixUtil;
import org.apache.jena.rdf.model.Model;
import org.ujmp.core.Matrix;
import org.ujmp.core.SparseMatrix;
import org.ujmp.core.util.io.IntelligentFileWriter;

public class PageRank {
	private static final Logger LOGGER = Logger.getLogger(HARERank.class.getName());
	
	public SparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	public SparseMatrix F;

	// P: is the product matrix
	Matrix P;
	Matrix P_t;

	Matrix S_n_Final;
	TransitionMatrixUtil matrxUtil;

	public PageRank(Model data) {
		this.matrxUtil = new TransitionMatrixUtil(data);
		this.W = matrxUtil.getW();
		this.F = matrxUtil.getF();
		LOGGER.info("Page Rank Constructor Initialisation Complete ");

	}

	public void calculateRank() {

		double beta = this.matrxUtil.getBeta();
		double intitialValue = 1 / beta;

		SparseMatrix blk1 = SparseMatrix.Factory.zeros(W.getRowCount(), F.getColumnCount());
		SparseMatrix blk2 = SparseMatrix.Factory.zeros(F.getRowCount(), W.getColumnCount());
		int iteration = 1;
		this.P = SparseMatrix.Factory.vertCat(SparseMatrix.Factory.horCat(blk1, W),
				SparseMatrix.Factory.horCat(F, blk2));
		Matrix PRval = Matrix.Factory.fill(intitialValue, P.getRowCount(), (long) 1.0);
		Matrix I = Matrix.Factory.fill(1, P.getRowCount(), (long) 1.0);
		double damping = 0.85;
		double epsilon = 1e-3;
		double error = 1;
		LOGGER.info("All setup complete ");
		while (error > epsilon) {
			LOGGER.info("Going for iteration " + iteration);
			Matrix previousPRval = PRval;
			PRval = (P.times(damping).transpose().mtimes(previousPRval)
					.plus(I.times((1 - damping) / previousPRval.getRowCount())));
			error = PRval.manhattenDistanceTo(previousPRval, true);
			LOGGER.info("Iteration " + iteration + "Complete");
			iteration++;

		}
		LOGGER.info("Rank Calculation Completed!!");
		this.S_n_Final = PRval;
		S_n_Final = S_n_Final.transpose();
		LOGGER.info("Obtained Final S matrix!!");

	}


	/**
	 * @return the s_n_Final
	 */
	public Matrix getS_n_Final() {
		return S_n_Final;
	}

	/**
	 * @param s_n_Final the s_n_Final to set
	 */
	public void setS_n_Final(Matrix s_n_Final) {
		S_n_Final = s_n_Final;
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
