package org.aksw.dice.HARE;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.ujmp.core.Matrix;
import org.ujmp.core.SparseMatrix;
import org.ujmp.core.util.UJMPSettings;
import org.ujmp.core.util.io.IntelligentFileWriter;

public class HARERank {
	private static final Logger LOGGER = Logger.getLogger(HARERank.class.getName());

	public SparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	public SparseMatrix F;

	// P: is the product matrix
	Matrix P_n;
	Matrix P_t;
	Matrix S;
	Matrix S_n_Final;
	Matrix S_t_Final;
	TransitionMatrixUtil matrxUtil;

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
		this.W = matrxUtil.getW();
		this.F = matrxUtil.getF();
		this.P_n = this.F.mtimes(this.W);
		this.P_t = this.W.mtimes(this.F);
		LOGGER.info("HARE Rank Constructor Initialisation Complete ");

	}

	public void calculateRank() {
		LOGGER.info("HARE Rank calculation started");
		double alpha = this.matrxUtil.getAlpha();
		double beta = this.matrxUtil.getBeta();

		double intitialValue = 1 / alpha;

		Matrix S_n = Matrix.Factory.fill(intitialValue, (long) alpha, (long) 1.0);
		Matrix I = Matrix.Factory.fill(1, (long) alpha, (long) 1.0);
		double damping = 0.85;
		double epsilon = 1e-3;
		double error = 1;
		int iteration = 1;
		LOGGER.info("All setup complete ");
		// Iteration over Equation 9
		while (error > epsilon) {
			LOGGER.info("Going for iteration " + iteration);
			Matrix S_n_previous = S_n;
			
			S_n = (P_n.times(damping).transpose().mtimes(S_n_previous)
					.plus(I.times((1 - damping) / S_n_previous.getRowCount())));
			
			error = S_n.manhattenDistanceTo(S_n_previous, true);
			LOGGER.info("Iteration " + iteration + "Complete");
			iteration++;
		}
		LOGGER.info("Rank Calculation Completed!!");
		// Multiply with Equation 8
		double factorSn = alpha / (beta + alpha);
		double factorSt = beta / (beta + alpha);
		S_t_Final = this.F.transpose().mtimes(S_n);
		S_t_Final = S_t_Final.times(factorSt).transpose();
		S_n_Final = S_n.times(factorSn).transpose();

		this.S = SparseMatrix.Factory.horCat(S_t_Final, S_n_Final);
		LOGGER.info("Obtained Final S matrix!!");
		System.out.println(S.toString());

	}

	/**
	 * @return the s
	 */
	public Matrix getS() {
		return S;
	}

	/**
	 * @param s
	 *            the s to set
	 */
	public void setS(Matrix s) {
		S = s;
	}

	/**
	 * @return the s_n_Final
	 */
	public Matrix getS_n_Final() {
		return S_n_Final;
	}

	/**
	 * @param s_n_Final
	 *            the s_n_Final to set
	 */
	public void setS_n_Final(Matrix s_n_Final) {
		S_n_Final = s_n_Final;
	}

	/**
	 * @return the s_t_Final
	 */
	public Matrix getS_t_Final() {
		return S_t_Final;
	}

	/**
	 * @param s_t_Final
	 *            the s_t_Final to set
	 */
	public void setS_t_Final(Matrix s_t_Final) {
		S_t_Final = s_t_Final;
	}

	public void writeRankToFile(String filename) {
		try {
			@SuppressWarnings("resource")
			IntelligentFileWriter writer = new IntelligentFileWriter(filename);
			writer.write("S_N \n");
			writer.write(this.S_n_Final.toString());
			writer.write(" S_T \n");
			writer.write(this.S_t_Final.toString());
			writer.write(" S \n");
			writer.write(this.S.toString());

			writer.flush();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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