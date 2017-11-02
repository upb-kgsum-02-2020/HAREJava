import org.aksw.dice.HARE.HARERank;
import org.aksw.dice.HARE.TransitionMatrixUtil;
import org.aksw.dice.reader.RDFReader;

@SuppressWarnings("unused")
public class Example {

	public static void main(String[] args) {
		final String filename = "data.ttl";

		RDFReader reader = new RDFReader();
		HARERank hr = new HARERank(reader.readData(filename));
		hr.calculateRank();
	}
}
