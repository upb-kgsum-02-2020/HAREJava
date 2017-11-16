package org.aksw.dice.parallel.PageRank;

import org.aksw.dice.PageRank.PageRank;
import org.aksw.dice.parallel.HARE.TransitionMatrixUtilParallel;
import org.apache.jena.rdf.model.Model;
import org.ujmp.core.util.UJMPSettings;

public class PageRankParallel {
	public static final String OUTPUT_FILE = "LastParallelPageRankCalculation.txt";

	TransitionMatrixUtilParallel matrxUtil;
	PageRank pageRank;
	public PageRankParallel(Model data) {
		
		UJMPSettings.getInstance().setNumberOfThreads(5);
		this.matrxUtil = new TransitionMatrixUtilParallel(data);
		this.pageRank= new PageRank(data);
		this.pageRank.calculateRank();
	}
}
