package org.aksw.dice.reader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

public class RDFReader {
	public static void main(String... argv) {
		final String filename = "data.ttl";
		// String ext = filename.substring(filename.lastIndexOf("."));

		PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
		final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

		ExecutorService executor = Executors.newSingleThreadExecutor();

		Runnable parser = new Runnable() {

			public void run() {
				RDFParser.source(filename).parse(inputStream);
			}
		};

		executor.submit(parser);

		while (iter.hasNext()) {
			Triple next = iter.next();
			System.out.println(next);
		}
	}

}
