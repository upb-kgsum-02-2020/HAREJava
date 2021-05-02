package kgsum.main;

import kgsum.HARE.HARERank;

import kgsum.reader.RDFReadWriteHandler;
import org.apache.jena.rdf.model.Model;

public class ResultsWriteHandler {
	public static void main(String[] args) {
		String filename = null;
//		if (args.length == 0) {
//			System.out.println("no arguments were given.");
//		} else if (args[0].equals("-f")) {
//			filename = args[1];
		RDFReadWriteHandler reader = new RDFReadWriteHandler();
		Model readmodel = reader.readData("m.ttl");
		RDFReadWriteHandler write = new RDFReadWriteHandler();
		HARERank hrTester = new HARERank(readmodel);
		hrTester.calculateRank();
		write.writeRDFResults(
				hrTester.getS_n_Final(),
				hrTester.getS_t_Final(),
				hrTester.getMatrxUtil().getTripleList(),
				hrTester.getMatrxUtil().getEntityList(),
				"test");
//			prTester.calculateRank();
//			write.writePageRankResults(
//					prTester.getS_n_Final(),
//					prTester.getMatrxUtil().getTripleList(),
//					prTester.getMatrxUtil().getEntityList(),
//					"test1");


//		}
	}
}
