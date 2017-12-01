package org.aksw.dice.matrixUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.ResourceFactory;
import org.aksw.dice.parallel.reader.RDFReadWriteHandler;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

public class TransitionMatrixUtil {
	private static final Logger LOGGER = Logger.getLogger(TransitionMatrixUtil.class.getName());

	public ArrayList<Resource> getEntityList() {
		return entityList;
	}

	public void setEntityList(ArrayList<Resource> entityList) {
		this.entityList = entityList;
	}

	public ArrayList<Statement> getTripleList() {
		return tripleList;
	}

	public void setTripleList(ArrayList<Statement> tripleList) {
		this.tripleList = tripleList;
	}

	RDFReadWriteHandler reader;
	// W:the transition matrix from triples to entities.
	LinkedSparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	LinkedSparseMatrix F;
	MatrixUtilities util;

	/**
	 * @return the w
	 */
	public LinkedSparseMatrix getW() {
		return W;
	}

	/**
	 * @param w
	 *            the w to set
	 */
	public void setW(LinkedSparseMatrix w) {
		W = w;
	}

	/**
	 * @return the f
	 */
	public LinkedSparseMatrix getF() {
		return F;
	}

	/**
	 * @param f
	 *            the f to set
	 */
	public void setF(LinkedSparseMatrix f) {
		F = f;
	}

	/**
	 * @param alpha
	 *            the alpha to set
	 */
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	/**
	 * @param beta
	 *            the beta to set
	 */
	public void setBeta(int beta) {
		this.beta = beta;
	}

	// alpha - number of entities
	int alpha;
	// beta - number of triples
	int beta;

	public int getAlpha() {
		return alpha;
	}

	public int getBeta() {
		return beta;
	}

	ArrayList<Resource> entityList;
	ArrayList<Statement> tripleList;
	private Set<Resource> entitySet;
	private Set<Statement> tripleSet;

	public TransitionMatrixUtil(Model data) {

		this.entitySet = new LinkedHashSet<Resource>();
		this.tripleSet = new LinkedHashSet<Statement>();
		this.alpha = 0;
		this.beta = 0;
		this.util = new MatrixUtilities();
		this.setupMatrix(data);
	}

	public void getDimensionValues(Model data) {

		StmtIterator iter = data.listStatements();
		while (iter.hasNext()) {
			Statement t = iter.next();
			tripleSet.add(t);
			if (t.getSubject() != null) {
				this.entitySet.add(t.getSubject());
			}
			if (t.getPredicate() != null) {
				this.entitySet.add(t.getPredicate());
			}
			if (t.getObject() != null) {
				// convert literal to resource
				if (t.getObject().isResource())
					this.entitySet.add(t.getObject().asResource());
				else
					this.entitySet.add(ResourceFactory.createResource(t.getObject().toString()));
			}
		}

		this.entityList = new ArrayList<Resource>(this.entitySet);
		this.tripleList = new ArrayList<Statement>(this.tripleSet);

		LOGGER.info("Obtained Entity and Triple List!!");
		this.beta = tripleList.size();
		this.alpha = entityList.size();
		LOGGER.info("Obtained alpha and beta!!" + "entities count is  " + this.alpha + " triple count is " + this.beta);
	}

	// since the resources are available seperately the order is defined my triple
	// insertion.
	public void setupMatrix(Model data) {
		LOGGER.info("Setting up Matrices !!");
		this.getDimensionValues(data);
		double a = 1.0 / 3.0;
		try {
			if ((this.alpha != 0) && (this.beta != 0)) {
				this.W = new LinkedSparseMatrix(this.beta, this.alpha);
				this.F = new LinkedSparseMatrix(this.alpha, this.beta);
				LOGGER.info(alpha + "   " + beta);
				this.W.zero();
				this.F.zero();
			
				double tripleCountforResource = 0;
				if ((!entityList.isEmpty()) && (!tripleList.isEmpty())) {
					for (Resource res : entityList) {
						tripleCountforResource = 0;
						LOGGER.info("Populationg W!!");
						// populating W
						for (Statement trip : tripleList) {
							if (trip.getObject().isLiteral()) {
								Resource r = ResourceFactory.createResource(trip.getObject().toString());
								if (r.equals(res)) {
									this.W.set(tripleList.indexOf(trip), entityList.indexOf(res), a);
									tripleCountforResource++;
								}
							} else if ((trip.getSubject().equals(res)) || (trip.getPredicate().equals(res))
									|| (trip.getObject().equals(res))) {

								this.W.set(tripleList.indexOf(trip), entityList.indexOf(res), a);
								tripleCountforResource++;
							}
						}
						LOGGER.info("Populationg W Complete!");

						LOGGER.info("Populating F!!");
						if (tripleCountforResource != 0) {
							double b = 1.0 / tripleCountforResource;
							for (Statement trip : tripleList) {
								if (trip.getObject().isLiteral()) {
									Resource r = ResourceFactory.createResource(trip.getObject().toString());
									if (r.equals(res)) {

										this.F.set(entityList.indexOf(res), tripleList.indexOf(trip), b);
									}
								} else if ((trip.getSubject().equals(res)) || (trip.getPredicate().equals(res))
										|| (trip.getObject().equals(res))) {

									this.F.set(entityList.indexOf(res), tripleList.indexOf(trip), b);
								}
							}
						}
					}
				}
				 
				LOGGER.info("Populationg W & F Complete!");
			}

		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warning("Matrix not made!!");
		}

	}

}