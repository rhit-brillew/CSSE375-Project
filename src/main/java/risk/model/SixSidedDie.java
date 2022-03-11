package risk.model;

import java.util.Random;

public class SixSidedDie {
	Random random = new Random();

	public int roll(){
		return random.nextInt(5) + 1;
	}
}
