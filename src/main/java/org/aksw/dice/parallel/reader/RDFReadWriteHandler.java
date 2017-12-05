package org.aksw.dice.parallel.reader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

import no.uib.cipr.matrix.DenseMatrix;

public class RDFReadWriteHandler {
	public void writeRDFResults(DenseMatrix S_n_hare, DenseMatrix S_t_hare, DenseMatrix S_n,
			ArrayList<Statement> tripleList, ArrayList<Resource> entityList, String datasetname) {
		Model outputModel = ModelFactory.createDefaultModel();
		Property hare = ResourceFactory.createProperty("http://aksw.org/property/hareRank");
		Property pageRank = ResourceFactory.createProperty("http://aksw.org/property/pageRank");
		int size = tripleList.size();
		System.out.println("Writing model to file: " + datasetname + ".ttl. ");
		for (Statement triple : tripleList) {
			ReifiedStatement rstmt = outputModel.createReifiedStatement(triple);
			rstmt.addLiteral(hare, S_t_hare.get(0, tripleList.indexOf(triple)));
			rstmt.addLiteral(pageRank, S_n.get(0, tripleList.indexOf(triple)));
			outputModel.add(triple);

			if (triple.getObject().isLiteral()) {
				Resource r = ResourceFactory.createResource(triple.getObject().toString());
				outputModel.addLiteral(r, hare, S_n_hare.get(0, entityList.indexOf(r)));
				outputModel.addLiteral(r, pageRank, S_n.get(0, size + entityList.indexOf(r)));
			} else {
				outputModel.addLiteral(triple.getObject().asResource(), hare,
						S_n_hare.get(0, entityList.indexOf(triple.getObject())));
				outputModel.addLiteral(triple.getObject().asResource(), pageRank,
						S_n.get(0, size + entityList.indexOf(triple.getObject())));
			}

			outputModel.addLiteral(triple.getSubject(), hare, S_n_hare.get(0, entityList.indexOf(triple.getSubject())));

			outputModel.addLiteral(triple.getPredicate().asResource(), hare,
					S_n_hare.get(0, entityList.indexOf(triple.getPredicate())));
			outputModel.addLiteral(triple.getSubject(), pageRank,
					S_n.get(0, size + entityList.indexOf(triple.getSubject())));

			outputModel.addLiteral(triple.getPredicate().asResource(), pageRank,
					S_n.get(0, size + entityList.indexOf(triple.getPredicate())));
		}

		outputModel.write(System.out, "Turtle");

		String outputfile = datasetname.concat("_result.ttl");

		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(outputfile);
			outputModel.write(outputStream, "Turtle");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	
	// Stream to constantly read data while processing the data

	public Model readDataUsingThreads(final String filename) {

		PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
		final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Runnable parser = new Runnable() {
			@Override
			public void run() {
				RDFDataMgr.parse(inputStream, filename);
			}
		};

		executor.submit(parser);
		Model model = ModelFactory.createDefaultModel();
		while (iter.hasNext()) {
			Triple next = iter.next();
			if (next.getObject().isLiteral())
				model.add(new ResourceImpl(next.getSubject().toString()),
						new PropertyImpl(next.getPredicate().getURI()),
						model.createTypedLiteral(next.getObject().toString()));
			else
				model.add(new ResourceImpl(next.getSubject().toString()),
						new PropertyImpl(next.getPredicate().getURI()), new ResourceImpl(next.getObject().toString()));

		}

		// model.write(System.out, "TURTLE");
		return model;
	}

	public Model readData(String filename) {
		Model model = ModelFactory.createDefaultModel();

		// Allows us to read data directly from server
		model = RDFDataMgr.loadModel(filename);
		return model;
	}

}
