package org.aksw.dice.parallel.HARE;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.aksw.dice.RDFhandler.RDFReadWriteHandler;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.ujmp.core.SparseMatrix;
import org.ujmp.core.util.UJMPSettings;

import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

public class TransitionMatrixUtilParallel {
	private static final Logger LOGGER = Logger.getLogger(TransitionMatrixUtilParallel.class.getName());
	RDFReadWriteHandler reader;
	// W:the transition matrix from triples to entities.
	LinkedSparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	LinkedSparseMatrix F;
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

	public TransitionMatrixUtilParallel(Model data) {
		this.entitySet = new LinkedHashSet<Resource>();
		this.tripleSet = new LinkedHashSet<Statement>();
		this.alpha = 0;
		this.beta = 0;
		this.setupMatrix(data);
	}

	public ArrayList<Resource> getEntityList() {
		return entityList;
	}

	public ArrayList<Statement> getTripleList() {
		return tripleList;
	}

	public Set<Resource> entitySet;
	public Set<Statement> tripleSet;

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
		this.beta = this.tripleList.size();
		this.alpha = this.entityList.size();
	}

	public LinkedSparseMatrix getW() {
		return W;
	}

	public LinkedSparseMatrix getF() {
		return F;
	}

	public void setupMatrix(Model data) {
		this.getDimensionValues(data);
		LOGGER.info("alpha and beta " + this.alpha + " " + this.beta);
		double a = 1.0 / 3.0;
		if ((this.alpha != 0) && (this.beta != 0)) {
			this.W = new LinkedSparseMatrix(this.beta, this.alpha);
			this.W.zero();
			this.F = new LinkedSparseMatrix(this.alpha, this.beta);
			this.F.zero();
			double tripleCountforResource = 0;
			if ((!entityList.isEmpty()) && (!tripleList.isEmpty())) {
				for (Resource res : entityList) {
					tripleCountforResource = 0;
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
					// populating F
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

		} else
			LOGGER.warning("Matrix not made!!");

		LOGGER.info("Size of F " + this.F.numRows() + "   " + this.F.numColumns());
		LOGGER.info("Size of W " + this.W.numRows() + "   " + this.W.numColumns());

	}
}
