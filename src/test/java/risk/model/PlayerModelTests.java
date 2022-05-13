package risk.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.*;
import org.junit.Test;

/**
 * BVA: Constructor
 * CASE                 | EXPECTED
 * unplacedArmies = 41  | IllegalArgumentException
 * unplacedArmies = -1   | IllegalArgumentException
 * unplacedArmies = 0   | getNumberOfUnplacedArmies = 0
 * unplacedArmies = 40  | getNumberOfUnplacedArmies = 40
 */

public class PlayerModelTests {

	private PlayerModel arrangePlayerModel(int armies) {
		return new PlayerModel(armies, Color.BLACK);
	}

	@Test
	public void testConstructor41UnplacedArmies() {
		try {
			arrangePlayerModel(41);
			fail();
		} catch (Exception e) {
			assertEquals("Player must be created with less than 40 unplaced armies", e.getMessage());
		}
	}

	@Test
	public void testConstructorNegative1UnplacedArmies() {
		try {
			arrangePlayerModel(-1);
			fail();
		} catch (Exception e) {
			assertEquals("Player must be created with a positive number of armies", e.getMessage());
		}
	}

	@Test
	public void testConstructor0UnplacedArmies() {
		PlayerModel playerModel = arrangePlayerModel(0);
		assertEquals(0, playerModel.getNumberOfUnplacedArmies());
	}

	@Test
	public void testConstructor40UnplacedArmies() {
		PlayerModel playerModel = arrangePlayerModel(40);
		assertEquals(40, playerModel.getNumberOfUnplacedArmies());
	}
}
