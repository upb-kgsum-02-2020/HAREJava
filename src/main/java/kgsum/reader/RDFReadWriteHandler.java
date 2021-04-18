package kgsum.reader;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.ujmp.core.Matrix;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RDFReadWriteHandler {
	public void writeRDFResults(Matrix S_n_hare, Matrix S_t_hare, ArrayList<Statement> tripleList,
			ArrayList<Resource> entityList, String datasetname) {
		Model outputModel = ModelFactory.createDefaultModel();
		Property hare = ResourceFactory.createProperty("http://aksw.org/property/hareRank");
		int size = tripleList.size();
		System.out.println("Writing model to file: " + datasetname + ".ttl. ");
		for (Statement triple : tripleList) {
			ReifiedStatement rstmt = outputModel.createReifiedStatement(triple);
			rstmt.addLiteral(hare, S_t_hare.getAsDouble(0, tripleList.indexOf(triple)));
			// rstmt.addLiteral(pageRank, S_n.getAsDouble(0, tripleList.indexOf(triple)));
			outputModel.add(triple);
			if (triple.getObject().isLiteral()) {
				String name = triple.getObject().toString();
				String psudoName = name.substring(0, name.indexOf("^"));
				String res = null;
				if (psudoName.contains(" ")) {
					res = psudoName.replaceAll(" ", "_");
				} else
					res = psudoName;
				Resource r = ResourceFactory.createResource(res);
				outputModel.addLiteral(r, hare, S_n_hare.getAsDouble(0, entityList.indexOf(r)));
			} else {
				outputModel.addLiteral(triple.getObject().asResource(), hare,
						S_n_hare.getAsDouble(0, entityList.indexOf(triple.getObject())));
			}
			outputModel.addLiteral(triple.getSubject(), hare,
					S_n_hare.getAsDouble(0, entityList.indexOf(triple.getSubject())));

			outputModel.addLiteral(triple.getPredicate().asResource(), hare,
					S_n_hare.getAsDouble(0, entityList.indexOf(triple.getPredicate())));
		}

		String outputfile = datasetname.concat("_result.ttl");

		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(outputfile);
			outputModel.write(outputStream, "Turtle");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void writePageRankResults(Matrix S_n, ArrayList<Statement> tripleList, ArrayList<Resource> entityList,
			String datasetname) {
		Model outputModel = ModelFactory.createDefaultModel();

		Property pageRank = ResourceFactory.createProperty("http://aksw.org/property/pageRank");
		System.out.println("Writing model to file: " + datasetname + ".ttl. ");
		int size = tripleList.size();
		for (Statement triple : tripleList) {
			ReifiedStatement rstmt = outputModel.createReifiedStatement(triple);
			rstmt.addLiteral(pageRank, S_n.getAsDouble(0, tripleList.indexOf(triple)));
			outputModel.add(triple);
			outputModel.addLiteral(triple.getSubject(), pageRank,
					S_n.getAsDouble(0, size + entityList.indexOf(triple.getSubject())));
			outputModel.addLiteral(triple.getObject().asResource(), pageRank,
					S_n.getAsDouble(0, size + entityList.indexOf(triple.getObject())));
			outputModel.addLiteral(triple.getPredicate().asResource(), pageRank,
					S_n.getAsDouble(0, size + entityList.indexOf(triple.getPredicate())));
		}
		outputModel.write(System.out, "Turtle");

		String outputfile = datasetname.concat("_PRresult.ttl");

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
