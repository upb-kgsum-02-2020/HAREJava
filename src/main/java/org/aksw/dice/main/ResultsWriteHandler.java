package org.aksw.dice.main;

import org.aksw.dice.HARE.HARERank;
import org.aksw.dice.PageRank.PageRank;

import org.aksw.dice.parallel.reader.RDFReadWriteHandler;
import org.apache.jena.rdf.model.Model;

public class ResultsWriteHandler {
	public static void main(String[] args) {
		String filename = null;
		if (args.length == 0) {
			System.out.println("no arguments were given.");
		} else if (args[0].equals("-f")) {
			filename = args[1];
			RDFReadWriteHandler reader = new RDFReadWriteHandler();
			Model readmodel = reader.readDataUsingThreads(filename);
			RDFReadWriteHandler write = new RDFReadWriteHandler();
			HARERank hrTester = new HARERank(readmodel);
			hrTester.calculateRank();
			PageRank pr = new PageRank(readmodel);
			pr.calculateRank();

			/*
			 * PageRankParallel prhandler = new PageRankParallel(readmodel);
			 * prhandler.getPageRank().calculateRank(); HARERankParallel harehandler = new
			 * HARERankParallel(readmodel); harehandler.getrank().calculateRank();
			 */

			write.writeRDFResults(hrTester.getS_n_Final(), hrTester.getS_t_Final(), pr.getS_n_Final(),
					hrTester.getMatrxUtil().getTripleList(), hrTester.getMatrxUtil().getEntityList(), "test");
		

		}
	}
}
