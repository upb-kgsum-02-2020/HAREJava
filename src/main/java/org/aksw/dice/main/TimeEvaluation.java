package org.aksw.dice.main;

import java.util.logging.Logger;

import org.aksw.dice.HARE.HARERank;
import org.aksw.dice.PageRank.PageRank;

import org.aksw.dice.parallel.reader.RDFReadWriteHandler;
import org.apache.jena.rdf.model.Model;

public class TimeEvaluation {
	private final Logger LOGGER = Logger.getLogger(TimeEvaluation.class.getName());

	public static void main(String[] args) {
		TimeEvaluation work = new TimeEvaluation();
		String filename = null;
		if (args.length == 0) {
			System.out.println("no arguments were given.");
		} else if (args[0].equals("-f") && args[2].equals("-t")) {
			filename = args[1];
			long tic = System.currentTimeMillis();
			RDFReadWriteHandler reader = new RDFReadWriteHandler();
			Model readmodel = reader.readData(filename);
			long tac = System.currentTimeMillis();
			System.out.println("Reading Data time is " + ((tac - tic) / 1000d) + " seconds");
			switch (args[3]) {
			case "hare":
				work.setupHARE(readmodel);
				break;
			case "pagerank":
				work.setupPageRank(readmodel);
				break;

			default:
				work.LOGGER.info("No valid experiment were provided");
				break;
			}

			System.exit(0);
		} else
			work.LOGGER.info(
					"Arguments not according to the format. \n Execute as follows: \n java Example.java -f <filename> -t <typename>");
	}

	public void setupPageRank(Model model) {
		String filerank = "LastPageRankCalculation.txt";
		long tic = System.currentTimeMillis();
		PageRank prhandler = new PageRank(model);
		prhandler.calculateRank();
		long tac = System.currentTimeMillis();
		System.out.println("Total Execution  of PageRank time is " + ((tac - tic) / 1000d) + " seconds");

	}

	public void setupHARE(Model model) {
		String filerank = "LastRankCalculation.txt";

		long tic = System.currentTimeMillis();
		HARERank harehandler = new HARERank(model);
		harehandler.calculateRank();
		long tac = System.currentTimeMillis();
		System.out.println("Total Execution HARE  time is " + ((tac - tic) / 1000d) + " seconds");

	}

}
