package org.aksw.dice.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.dice.HARE.HARERank;
import org.aksw.dice.parallel.reader.RDFReadWriteHandler;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

public class WriteExample {
	public static void main(String[] args) {

		Resource r1 = ResourceFactory.createResource("http://aksw.org/resource/BarackObama");
		Property p1 = ResourceFactory.createProperty("http://aksw.org/property/spouse");
		Property p2 = ResourceFactory.createProperty("http://aksw.org/property/party");
		Resource r2 = ResourceFactory.createResource("http://aksw.org/resource/Democrats");
		Resource r3 = ResourceFactory.createResource("http://aksw.org/resource/MichelleObama");

		List<Statement> actualTriples = new ArrayList<Statement>(Arrays
				.asList(ResourceFactory.createStatement(r1, p2, r2), ResourceFactory.createStatement(r1, p1, r3)));

		Model testModel = ModelFactory.createDefaultModel();
		RDFReadWriteHandler write = new RDFReadWriteHandler();
		testModel.add(actualTriples);
		HARERank hrTester = new HARERank(testModel);
		hrTester.calculateRank();
		write.writeRDFResults(hrTester.getS_n_Final(), hrTester.getS_t_Final(), hrTester.getMatrxUtil().getTripleList(),
				hrTester.getMatrxUtil().getEntityList(), "test");

	}
}
