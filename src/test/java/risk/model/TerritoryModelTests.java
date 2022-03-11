package risk.model;

import static org.junit.Assert.*;

import java.awt.*;
import org.junit.Test;

/**
 * BVA:
 * getNumberOfArmies() is just a getter so no testing needed
 *
 * changeArmyAmountBy(amount):
 * Interval - [MIN_INT, MAX_INT]
 * testing needed:
 * 		1. MIN_INT
 * 		2. MIN_INT-1
 * 		3. MAX_INT
 * 		4. MAX_INT+1
 * MAX_INT+1 and MIN_INT-1 can not be set
 * Numbers used in arithmetic operations already covered by interval
 *
 * BVA: equals
 *  CASE                        | EXPECTED
 *  Same object                 | True
 *  Null object                 | False
 *  Not a TerritoryModel        | False
 *  Same variables              | True
 *  numberOfArmies is different | False
 *  location is different       | False
 *  name is different           | False
 *  owner is different          | False
 *  continent is different      | False
 */

public class TerritoryModelTests {
	@Test
	public void testArmyChangeByMinInt() {
		TerritoryModel territory = new TerritoryModel("Test Territory", new Point(100, 100), "Test continent");
		int startingArmyValue = territory.getNumberOfArmies();
		territory.changeArmyAmountBy(Integer.MIN_VALUE);
		assertEquals(startingArmyValue+Integer.MIN_VALUE, territory.getNumberOfArmies());
	}
	
	@Test
	public void testArmyChangeByMaxInt() {
		TerritoryModel territory = new TerritoryModel("Test Territory", new Point(100, 100), "Test continent");
		int startingArmyValue = territory.getNumberOfArmies();
		territory.changeArmyAmountBy(Integer.MAX_VALUE);
		assertEquals(startingArmyValue+Integer.MAX_VALUE, territory.getNumberOfArmies());
	}

	@Test
	public void testEqualsSameObject() {
		TerritoryModel territory = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		assertEquals(territory, territory);
	}

	@Test
	public void testEqualsNullObject() {
		TerritoryModel territory = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		assertNotEquals(territory, null);
	}

	@Test
	public void testEqualsNotATerritoryModel() {
		TerritoryModel territory = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		assertNotEquals(territory, Integer.valueOf(5));
	}

	@Test
	public void testEqualsSameVariables() {
		TerritoryModel territory1 = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		territory1.changeArmyAmountBy(5);
		territory1.setOwner(Color.BLACK);
		TerritoryModel territory2 = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		territory2.changeArmyAmountBy(5);
		territory2.setOwner(Color.BLACK);
		assertEquals(territory1, territory2);
	}

	@Test
	public void testEqualsNumberOfArmiesDifferent() {
		TerritoryModel territory1 = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		territory1.changeArmyAmountBy(4);
		territory1.setOwner(Color.BLACK);
		TerritoryModel territory2 = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		territory2.changeArmyAmountBy(5);
		territory2.setOwner(Color.BLACK);
		assertNotEquals(territory1, territory2);
	}

	@Test
	public void testEqualsLocationDifferent() {
		TerritoryModel territory1 = new TerritoryModel("Test Territory One", new Point(200, 100), "Test continent");
		territory1.changeArmyAmountBy(5);
		territory1.setOwner(Color.BLACK);
		TerritoryModel territory2 = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		territory2.changeArmyAmountBy(5);
		territory2.setOwner(Color.BLACK);
		assertNotEquals(territory1, territory2);
	}

	@Test
	public void testEqualsNameDifferent() {
		TerritoryModel territory1 = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		territory1.changeArmyAmountBy(5);
		territory1.setOwner(Color.BLACK);
		TerritoryModel territory2 = new TerritoryModel("Test Territory Two", new Point(100, 100), "Test continent");
		territory2.changeArmyAmountBy(5);
		territory2.setOwner(Color.BLACK);
		assertNotEquals(territory1, territory2);
	}

	@Test
	public void testEqualsOwnerDifferent() {
		TerritoryModel territory1 = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		territory1.changeArmyAmountBy(5);
		territory1.setOwner(Color.BLACK);
		TerritoryModel territory2 = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent");
		territory2.changeArmyAmountBy(5);
		territory2.setOwner(Color.BLUE);
		assertNotEquals(territory1, territory2);
	}

	@Test
	public void testEqualsContinentDifferent() {
		TerritoryModel territory1 = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent One");
		territory1.changeArmyAmountBy(5);
		territory1.setOwner(Color.BLACK);
		TerritoryModel territory2 = new TerritoryModel("Test Territory One", new Point(100, 100), "Test continent Two");
		territory2.changeArmyAmountBy(5);
		territory2.setOwner(Color.BLACK);
		assertNotEquals(territory1, territory2);
	}

	@Test
	public void testHashCode() {
		try {
			new TerritoryModel("test name", new Point(100, 100), "test continent").hashCode();
			fail();
		} catch (UnsupportedOperationException e) {
			assertEquals("Hashcode not supported", e.getMessage());
		}
	}
}
