package org.aksw.dice.parallel.HARE;

import org.aksw.dice.HARE.HARERank;
import org.apache.jena.rdf.model.Model;
import org.ujmp.core.util.UJMPSettings;

public class HARERankParallel {



	TransitionMatrixUtilParallel matrxUtil;
	HARERank rank;

	public HARERankParallel(Model data) {
		UJMPSettings.getInstance().setNumberOfThreads(8);
		this.matrxUtil = new TransitionMatrixUtilParallel(data);
		this.rank = new HARERank(data);

	}

	

	/**
	 * @return the hr
	 */
	public HARERank getrank() {
		return rank;
	}

}