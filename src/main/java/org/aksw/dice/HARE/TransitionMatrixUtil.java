package org.aksw.dice.HARE;

import java.util.ArrayList;
import java.util.List;

import org.aksw.dice.reader.RDFReader;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.ujmp.core.SparseMatrix;

public class TransitionMatrixUtil {

	RDFReader reader;
	// W:the transition matrix from triples to entities.
	SparseMatrix W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	SparseMatrix F;
	// model to store the read data
	Model m;
	// alpha - number of entities
	double alpha;
	// beta - number of triples
	double beta;

	public TransitionMatrixUtil() {
		this.reader = new RDFReader();
		this.m = this.reader.readData();
		this.getDimensionValues();

	}

	public void getDimensionValues() {
		this.beta = m.size();
		StmtIterator iter = this.m.listStatements();
		while (iter.hasNext()) {
			Statement t = iter.next();
			if (t.getSubject() != null)
				this.alpha++;
			if (t.getPredicate() != null)
				this.alpha++;
			if (t.getObject() != null)
				this.alpha++;
		}
	}

	public void setupMatrix() {

	}
}
