package org.aksw.dice.reader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

// Stream to constantly read data while processing the data

public class RDFReader {
	final String filename = "data.ttl";

	public Model readData() {
		Model model = ModelFactory.createDefaultModel();
		// Allows us to read data directly from server
		model = RDFDataMgr.loadModel(filename);
		return model;
	}

	public void readDataUsingStream() {
		PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
		final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Runnable parser = new Runnable() {
			public void run() {
				RDFParser.source(filename).parse(inputStream);
			}
		};
		executor.submit(parser);
	}

}
