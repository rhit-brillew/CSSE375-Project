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
	@Test
	public void testConstructor41UnplacedArmies() {
		try {
			new PlayerModel(41, Color.BLACK);
			fail();
		} catch (Exception e) {
			assertEquals("Player must be created with less than 40 unplaced armies", e.getMessage());
		}
	}

	@Test
	public void testConstructorNegative1UnplacedArmies() {
		try {
			new PlayerModel(-1, Color.BLACK);
			fail();
		} catch (Exception e) {
			assertEquals("Player must be created with a positive number of armies", e.getMessage());
		}
	}

	@Test
	public void testConstructor0UnplacedArmies() {
		PlayerModel playerModel = new PlayerModel(0, Color.BLACK);
		assertEquals(0, playerModel.getNumberOfUnplacedArmies());
	}

	@Test
	public void testConstructor40UnplacedArmies() {
		PlayerModel playerModel = new PlayerModel(40, Color.BLACK);
		assertEquals(40, playerModel.getNumberOfUnplacedArmies());
	}
}
