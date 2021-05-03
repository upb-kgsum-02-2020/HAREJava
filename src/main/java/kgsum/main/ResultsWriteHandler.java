package kgsum.main;

import kgsum.HARE.HARERank;

import kgsum.reader.RDFReadWriteHandler;
import org.apache.jena.rdf.model.Model;

public class ResultsWriteHandler {
	public static void main(String[] args) {
		String filename = null;
		if (args.length == 0) {
			System.out.println("no arguments were given.");
			return;
		}
		else if  (args[0] != null){
			
		
		filename = args[0];
		RDFReadWriteHandler reader = new RDFReadWriteHandler();
		long read_tic = System.currentTimeMillis();//narase added for time
		Model readModel = reader.readData(filename);
		long read_tac = System.currentTimeMillis();//narase added for time
		RDFReadWriteHandler write = new RDFReadWriteHandler();
		long exe_tic = System.currentTimeMillis();//added time calculation Narase
		HARERank hrTester = new HARERank(readModel);
		hrTester.calculateRank();
		//added start for time calculation Narase
		long exe_tac = System.currentTimeMillis();
		System.out.println("Reading Data time is " + ((read_tac - read_tic) / 1000d) + " seconds");
		System.out.println("Total Execution HARE  time is " + ((exe_tac - exe_tic) / 1000d) + " seconds");
		//added finish for time calculation Narase
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


		}
	}
}
