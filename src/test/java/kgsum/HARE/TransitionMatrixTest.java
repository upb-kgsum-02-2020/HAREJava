package kgsum.HARE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.junit.Before;
import org.junit.Test;
import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;
import org.junit.Assert;

public class TransitionMatrixTest {
	static Resource r1 = ResourceFactory.createResource("http://aksw.org/resource/BarackObama");
	static Property p1 = ResourceFactory.createProperty("http://aksw.org/property/spouse");
	static Property p2 = ResourceFactory.createProperty("http://aksw.org/property/party");
	static Resource r2 = ResourceFactory.createResource("http://aksw.org/resource/Democrats");
	static Resource r3 = ResourceFactory.createResource("http://aksw.org/resource/MichelleObama");

	public static List<Statement> actualTriples = new ArrayList<Statement>(
			Arrays.asList(ResourceFactory.createStatement(r1, p2, r2), ResourceFactory.createStatement(r1, p1, r3)));

	public List<Resource> actualEntity = new ArrayList<Resource>(Arrays.asList(r1, p1, r3, p2, r2));
	TransitionMatrixUtil matrixUtil;

	@Before
	public void data() {
		Model testModel = ModelFactory.createDefaultModel();
		testModel.add(actualTriples);
		matrixUtil = new TransitionMatrixUtil(testModel);

	}

	@Test
	public void setupMatrixTest() {
		Matrix F_actual = DenseMatrix.Factory.zeros(5, 2);
		F_actual.setAsDouble(0.5, 0, 0);
		F_actual.setAsDouble(0.5, 0, 1);
		F_actual.setAsDouble(1, 1, 0);
		F_actual.setAsDouble(0, 1, 1);
		F_actual.setAsDouble(1, 2, 0);
		F_actual.setAsDouble(0, 2, 1);
		F_actual.setAsDouble(0, 3, 0);
		F_actual.setAsDouble(1, 3, 1);
		F_actual.setAsDouble(0, 4, 0);
		F_actual.setAsDouble(1, 4, 1);

		Matrix W_actual = DenseMatrix.Factory.fill(0, 2, 5);
		double a = 1.0 / 3.0;
		W_actual.setAsDouble(a, 0, 0);
		W_actual.setAsDouble(a, 0, 1);
		W_actual.setAsDouble(a, 0, 2);
		W_actual.setAsDouble(0, 0, 3);
		W_actual.setAsDouble(0, 0, 4);
		W_actual.setAsDouble(a, 1, 0);
		W_actual.setAsDouble(0, 1, 1);
		W_actual.setAsDouble(0, 1, 2);
		W_actual.setAsDouble(a, 1, 3);
		W_actual.setAsDouble(a, 1, 4);

		org.junit.Assert.assertEquals(W_actual, matrixUtil.getW());
		org.junit.Assert.assertEquals(F_actual, matrixUtil.getF());
	}

	@Test
	public void getDimensionValuesTest() {
		Assert.assertEquals(matrixUtil.getAlpha(), actualEntity.size());
		Assert.assertEquals(matrixUtil.getBeta(), actualTriples.size());

	}

}
