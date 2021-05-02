package kgsum.main;

import kgsum.HARE.HARERank;

import kgsum.reader.RDFReadWriteHandler;
import org.apache.jena.rdf.model.Model;

public class ResultsWriteHandler {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("no arguments were given.");
			return;
		}

		String filename = args[0];
		RDFReadWriteHandler reader = new RDFReadWriteHandler();
		Model readModel = reader.readData(filename);
		RDFReadWriteHandler write = new RDFReadWriteHandler();
		HARERank hrTester = new HARERank(readModel);
		hrTester.calculateRank();
		write.writeFilteredTriples(
				hrTester.getS_t_Final(),
				hrTester.getMatrxUtil().getTripleList(),
				filename.substring(0, filename.indexOf('.')));
//			prTester.calculateRank();
//			write.writePageRankResults(
//					prTester.getS_n_Final(),
//					prTester.getMatrxUtil().getTripleList(),
//					prTester.getMatrxUtil().getEntityList(),
//					"test1");


//		}
	}
}
