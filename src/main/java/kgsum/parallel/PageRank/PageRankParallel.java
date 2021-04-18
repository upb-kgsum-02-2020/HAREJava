package kgsum.parallel.PageRank;

import kgsum.PageRank.PageRank;
import kgsum.parallel.HARE.TransitionMatrixUtilParallel;
import org.apache.jena.rdf.model.Model;
import org.ujmp.core.util.UJMPSettings;

public class PageRankParallel {


	TransitionMatrixUtilParallel matrxUtil;
	private PageRank pageRank;

	public PageRankParallel(Model data) {
		UJMPSettings.getInstance().setNumberOfThreads(2);
		this.matrxUtil = new TransitionMatrixUtilParallel(data);
		this.setPageRank(new PageRank(data));
	}

	public PageRank getPageRank() {
		return pageRank;
	}

	public void setPageRank(PageRank pageRank) {
		this.pageRank = pageRank;
	}

	
}
