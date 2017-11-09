package org.aksw.dice.parallel.HARE;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.aksw.dice.HARE.TransitionMatrixUtil;
import org.aksw.dice.RDFhandler.RDFReadWriteHandler;
import org.aksw.dice.parallel.reader.RDFReadWriteParallelHandler;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;

public class TransitionMatrixUtilParallel {
	private static final Logger LOGGER = Logger.getLogger(TransitionMatrixUtilParallel.class.getName());
	RDFReadWriteHandler reader;
	// W:the transition matrix from triples to entities.
	SparseDoubleMatrix2D W;
	// F:the matrix of which the entries are the transition probabilities from
	// entities to triples,
	SparseDoubleMatrix2D F;

	// alpha - number of entities
	long alpha;
	// beta - number of triples
	long beta;

	public long getAlpha() {
		return alpha;
	}

	public long getBeta() {
		return beta;
	}

	ArrayList<Resource> entityList;
	ArrayList<Statement> tripleList;

	public TransitionMatrixUtilParallel(Model data) {
		TripleListReader t = new TripleListReader(data);
		t.start();
		EntityListReader e = new EntityListReader(data);
		e.start();
		try {
			TimeUnit.MILLISECONDS.sleep(5000);
			t.t.interrupt();
			e.t.interrupt();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.entityList = new ArrayList<Resource>(e.getEntitySet());
		this.tripleList = new ArrayList<Statement>(t.getTripleSet());
		this.alpha = 0;
		this.beta = 0;

	}

	public ArrayList<Resource> getEntityList() {
		return entityList;
	}

	public ArrayList<Statement> getTripleList() {
		return tripleList;
	}

	public static void main(String[] args) {
		final String filename = "data.ttl";
		RDFReadWriteParallelHandler reader = new RDFReadWriteParallelHandler();

	}
}

class TripleListReader implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(TripleListReader.class.getName());
	Thread t;
	String threadName;
	public Model model;
	Set<Statement> tripleSet;

	public Set<Statement> getTripleSet() {
		return tripleSet;
	}

	TripleListReader(Model m) {
		this.threadName = "tripleReader";
		this.tripleSet = new LinkedHashSet<Statement>();
		this.model = m;
		LOGGER.info(" Initiating the Triple Reader!!");

	}

	@Override
	public void run() {
		while (!t.isInterrupted()) {
			StmtIterator iter = model.listStatements();
			while (iter.hasNext()) {
				Statement t = iter.next();
				tripleSet.add(t);
			}
		}
		System.out.println("Thread " + threadName + " exiting.");
	}

	public void start() {
		System.out.println("Starting " + threadName);

		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

}

class EntityListReader implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(TripleListReader.class.getName());
	Thread t;
	String threadName;
	public Model model;
	Set<Resource> entitySet;

	public Set<Resource> getEntitySet() {
		return entitySet;
	}

	EntityListReader(Model m) {
		this.threadName = "entityReader";
		this.entitySet = new LinkedHashSet<Resource>();
		this.model = m;
		LOGGER.info(" Initiating the Entity Reader!!");

	}

	@Override
	public void run() {
		while (!t.isInterrupted()) {
			StmtIterator iter = model.listStatements();
			while (iter.hasNext()) {
				Statement t = iter.next();
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
		}
		System.out.println("Thread " + threadName + " exiting.");
	}

	public void start() {
		System.out.println("Starting " + threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

}
