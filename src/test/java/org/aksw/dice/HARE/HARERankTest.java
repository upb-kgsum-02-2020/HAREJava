package org.aksw.dice.HARE;

import static org.junit.Assert.*;

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

import junit.framework.Assert;

public class HARERankTest {

	static Resource r1 = ResourceFactory.createResource("http://aksw.org/resource/BarackObama");
	static Property p1 = ResourceFactory.createProperty("http://aksw.org/property/spouse");
	static Property p2 = ResourceFactory.createProperty("http://aksw.org/property/party");
	static Resource r2 = ResourceFactory.createResource("http://aksw.org/resource/Democrats");
	static Resource r3 = ResourceFactory.createResource("http://aksw.org/resource/MichelleObama");

	public static List<Statement> actualTriples = new ArrayList<Statement>(
			Arrays.asList(ResourceFactory.createStatement(r1, p2, r2), ResourceFactory.createStatement(r1, p1, r3)));

	public List<Resource> actualEntity = new ArrayList<Resource>(Arrays.asList(r1, p1, r3, p2, r2));
	HARERank hrTester;

	@Before
	public void data() {
		Model testModel = ModelFactory.createDefaultModel();
		testModel.add(actualTriples);
		hrTester = new HARERank(testModel);
	}

	@Test
	public void SMatrixTest() {
		/*
		 * Matrix P_N_actual = DenseMatrix.Factory.fill(0, 5, 5);
		 * 
		 * 
		 * P_N_actual.setAsDouble((double)0.3333, 0, 0);
		 * P_N_actual.setAsDouble((double)0.1667, 0, 1);
		 * P_N_actual.setAsDouble((double)0.1667, 0, 2);
		 * P_N_actual.setAsDouble((double)0.1667, 0, 3);
		 * P_N_actual.setAsDouble((double)0.1667, 0, 4);
		 * 
		 * P_N_actual.setAsDouble((double)0.3333, 1, 0);
		 * P_N_actual.setAsDouble((double)0.3333, 1, 1);
		 * P_N_actual.setAsDouble((double)0.3333, 1, 2);
		 * P_N_actual.setAsDouble((double)0, 1, 3); P_N_actual.setAsDouble((double)0, 1,
		 * 4);
		 * 
		 * P_N_actual.setAsDouble((double)0.3333, 2, 0);
		 * P_N_actual.setAsDouble((double)0.3333, 2, 1);
		 * P_N_actual.setAsDouble((double)0.3333, 2, 2);
		 * P_N_actual.setAsDouble((double)0, 2, 3); P_N_actual.setAsDouble((double)0, 2,
		 * 4);
		 * 
		 * P_N_actual.setAsDouble((double)0.3333, 3, 0);
		 * P_N_actual.setAsDouble((double)0, 3, 1); P_N_actual.setAsDouble((double)0, 3,
		 * 2); P_N_actual.setAsDouble((double)0.3333, 3, 3);
		 * P_N_actual.setAsDouble((double)0.3333, 3, 4);
		 * 
		 * P_N_actual.setAsDouble((double)0.3333, 4, 0);
		 * P_N_actual.setAsDouble((double)0, 4, 1); P_N_actual.setAsDouble((double)0, 4,
		 * 2); P_N_actual.setAsDouble((double)0.3333, 4, 3);
		 * P_N_actual.setAsDouble((double)0.3333, 4, 4);
		 * 
		 * Matrix P_T_actual = DenseMatrix.Factory.fill(0, 2, 2);
		 * 
		 * P_T_actual.setAsDouble((double)0.8333, 0, 0);
		 * P_T_actual.setAsDouble((double)0.1667, 0, 1);
		 * 
		 * P_T_actual.setAsDouble((double)0.1667, 1, 0);
		 * P_T_actual.setAsDouble((double)0.8333, 1, 1);
		 */

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

		Matrix P_N_actual = F_actual.mtimes(W_actual);
		Matrix P_T_actual = W_actual.mtimes(F_actual);

		
		Assert.assertEquals(P_T_actual, hrTester.getP_t());
		Assert.assertEquals(P_N_actual, hrTester.getP_n());

	}

	public static void main(String[] args) {
		HARERankTest test = new HARERankTest();
		test.data();
		test.SMatrixTest();
		test.hrTester.calculateRank();
	}
}
