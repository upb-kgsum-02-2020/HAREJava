import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

public class Tester {
	public static void main(String[] args) throws IOException {
		Resource r1 = ResourceFactory.createResource("http://aksw.org/resource/BarackObama");
		Property p1 = ResourceFactory.createProperty("http://aksw.org/property/spouse");
		Property p2 = ResourceFactory.createProperty("http://aksw.org/property/party");
		Resource r2 = ResourceFactory.createResource("http://aksw.org/resource/Democrats");
		Resource r3 = ResourceFactory.createResource("http://aksw.org/resource/MichelleObama");

		List<Statement> actualTriples = new ArrayList<Statement>(Arrays
				.asList(ResourceFactory.createStatement(r1, p2, r2), ResourceFactory.createStatement(r1, p1, r3)));

		List<Resource> actualEntity = new ArrayList<Resource>(Arrays.asList(r1, p1, r3, p2, r2));

		Model model = ModelFactory.createDefaultModel();
		model.add(actualTriples);
		String fileName = "/Users/Kunal/workspace/HARE/Data/KnowledgeBases/example.nt";
		FileWriter out = new FileWriter(fileName);
		try {
			model.write(out, "N-TRIPLE");
			out.flush();
		} finally {
			try {
				out.close();
			} catch (IOException closeException) {
				// ignore
			}

		}
	}
}
