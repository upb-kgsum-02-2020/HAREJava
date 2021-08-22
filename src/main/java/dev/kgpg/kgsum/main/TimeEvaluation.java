package dev.kgpg.kgsum.main;

import java.util.logging.Logger;

import dev.kgpg.kgsum.HARE.HARERank;

import dev.kgpg.kgsum.reader.RDFReadWriteHandler;
import org.apache.jena.rdf.model.Model;

public class TimeEvaluation {
	@SuppressWarnings("unused")
	private final Logger LOGGER = Logger.getLogger(TimeEvaluation.class.getName());

	public static void main(String[] args) {
		TimeEvaluation work = new TimeEvaluation();
//		String filename = null;
//		if (args[0].equals("-f") && args[2].equals("-t")) {
//			filename = args[1];
		long tic = System.currentTimeMillis();
		RDFReadWriteHandler reader = new RDFReadWriteHandler();
		Model readmodel = reader.readData("m.ttl");
		long tac = System.currentTimeMillis();
		System.out.println("Reading Data time is " + ((tac - tic) / 1000d) + " seconds");
		work.setupHARE(readmodel);
		System.exit(0);
	}

	public void setupHARE(Model model) {
		String filerank = "LastRankCalculation.txt";

		long tic = System.currentTimeMillis();
		HARERank harehandler = new HARERank(model);
		harehandler.calculateRank();
		long tac = System.currentTimeMillis();
		System.out.println("Total Execution HARE  time is " + ((tac - tic) / 1000d) + " seconds");
		harehandler.writeRankToFile(filerank);
	}


}
