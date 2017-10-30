package org.aksw.dice.HARE;

import java.util.ArrayList;

import java.util.logging.Logger;

import org.aksw.dice.reader.RDFReader;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.ujmp.core.SparseMatrix;

public class TransitionMatrixUtil {
	private static final Logger LOGGER = Logger.getLogger(TransitionMatrixUtil.class.getName());
	RDFReader reader;
	// W:the transition matrix from triples to entities.
	SparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	SparseMatrix F;
	// model to store the read data
	Model m;
	// alpha - number of entities
	long alpha;
	// beta - number of triples
	long beta;

	private ArrayList<Resource> entityList;
	private ArrayList<Statement> tripleList;

	public TransitionMatrixUtil() {
		this.reader = new RDFReader();
		this.m = this.reader.readData();
		this.entityList = new ArrayList<Resource>();
		this.tripleList = new ArrayList<Statement>();
		this.alpha = 0;
		this.beta = 0;
		this.getDimensionValues();

	}

	public void getDimensionValues() {

		StmtIterator iter = this.m.listStatements();
		while (iter.hasNext()) {
			Statement t = iter.next();
			tripleList.add(t);
			if (t.getSubject() != null) {
				this.entityList.add(t.getSubject());
			}
			if (t.getPredicate() != null) {
				this.entityList.add(t.getPredicate());
			}
			if (t.getObject() != null) {
				// convert literal to resource
				if (t.getObject().isResource())
					this.entityList.add(t.getObject().asResource());
				else
					this.entityList.add(ResourceFactory.createResource(t.getObject().toString()));
			}
		}

		this.beta = tripleList.size();
		this.alpha = entityList.size();
	}

	public void setupMatrix() {
		if ((this.alpha != 0) && (this.beta != 0)) {
			this.W = SparseMatrix.Factory.zeros(this.beta, this.alpha);
			this.F = SparseMatrix.Factory.zeros(this.alpha, this.beta);

			if ((!entityList.isEmpty()) && (!tripleList.isEmpty())) {
				for (Resource res : entityList) {
					for (Statement trip : tripleList) {

						if (trip.getObject().isLiteral()) {
							Resource r = ResourceFactory.createResource(trip.getObject().toString());
							if (r.equals(res)) {
								this.W.setAsDouble(0.33, tripleList.indexOf(trip), entityList.indexOf(res));
							}
						}

						else if ((trip.getSubject().equals(res)) || (trip.getPredicate().equals(res))
								|| (trip.getObject().equals(res))) {

							this.W.setAsDouble(0.33, tripleList.indexOf(trip), entityList.indexOf(res));
						}

					}
				}
			}

			W.showGUI();
		} else
			LOGGER.warning("Matrix not made!!");

	}

}
