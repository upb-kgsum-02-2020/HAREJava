import org.aksw.dice.HARE.HARERank;
import org.aksw.dice.HARE.TransitionMatrixUtil;
import org.aksw.dice.PageRank.PageRank;
import org.aksw.dice.RDFhandler.RDFReadWriteHandler;
import org.aksw.dice.parallel.reader.RDFReadWriteParallelHandler;

@SuppressWarnings("unused")
public class Example {

	public static void main(String[] args) {
		final String filename = "data.ttl";
		RDFReadWriteParallelHandler reader = new RDFReadWriteParallelHandler();

		HARERank matrxUtil = new HARERank(reader.readData(filename));
		matrxUtil.calculateRank();
	}
}
