package org.aksw.dice.parallel.reader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

public class RDFReader {

	public Model readData(final String filename) {

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

}
