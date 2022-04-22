package risk.controller;

import static org.junit.Assert.*;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import risk.model.Edge;
import risk.model.TerritoryModel;


/**
 * BVA: areThereUnclaimedTerritories
 *  CASE                     | EXPECTED
 *  No unclaimed territories | False
 *  1 unclaimed territory    | True
 *  41 unclaimed territories | True
 *  42 unclaimed territories | True
 *
 * BVA: getNameLocationHashSet
 *  CASE    | EXPECTED
 *  Empty   | Empty HashMap
 *  Size 1  | HashMap of the one territory
 *  Size 41 | HashMap of the 41 territories
 *  Size 42 | HashMap of the 42 territories
 *
 * BVA: getTerritoryByName
 *  CASE                                | EXPECTED
 *  The named territory does not exist  | IllegalArgumentException
 *  The named territory exists size 1   | the territory is returned
 *  The named territory exists size 41  | the territory is returned
 *  The named territory exists size 42  | the territory is returned
 *
 * BVA: areTerritoriesAdjacent
 *  CASE                                                    | EXPECTED
 *  The two territories are not adjacent                    | false
 *  The two territories are adjacent in same order as edge  | true
 *  The two territories are adjacent in different order as edge  | true
 *
 * BVA: areTerritoriesConnectedByOwnedTerritories
 *  CASE                                                                        | EXPECTED
 *  The two territories are not owned by the same person                        | false
 *  The two territories are not owned                                           | false
 *  The first territory is not owned                                            | false
 *  The second territory is not owned                                           | false
 *  The two territories are not connected by owned territories                  | false
 *  The two territories are adjacent                                            | true
 *  The two territories are not adjacent and are connected by owned territories | true
 *
 * BVA: readTerritoryXMLData
 *  CASE                     | EXPECTED
 *  the function is called   | a territory map controller of all correct territory information.
 *
 * BVA: EdgeEquals
 *  CASE                    | EXPECTED
 *  Same object             | True
 *  Null object             | False
 *  Not an edge             | False
 *  Same variables          | True
 *  Traversed is different  | False
 *  T1 is different         | False
 *  T2 is different         | False
 *
 * BVA: EdgeHashCode
 *  CASE            | EXPECTED
 *  executed        | UnsupportedOperationException
 */

public class TestTerritoryMapController {
	@Test
	public void testAreThereUnclaimedTerritoriesNo() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		setNTerritoriesToBeClaimed(42, territoryMapController);
		assertFalse(territoryMapController.areThereUnclaimedTerritories());
	}

	@Test
	public void testAreThereUnclaimedTerritoriesOne() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		setNTerritoriesToBeClaimed(41, territoryMapController);
		assertTrue(territoryMapController.areThereUnclaimedTerritories());
	}

	@Test
	public void testAreThereUnclaimedTerritoriesFortyOne() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		setNTerritoriesToBeClaimed(1, territoryMapController);
		assertTrue(territoryMapController.areThereUnclaimedTerritories());
	}

	@Test
	public void testAreThereUnclaimedTerritoriesFortyTwo() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		setNTerritoriesToBeClaimed(0, territoryMapController);
		assertTrue(territoryMapController.areThereUnclaimedTerritories());
	}

	private TerritoryMapController createAllTerritoriesFromXML() {
		NodeList nodeList = null;
		TerritoryMapController territoryMapController = new TerritoryMapController();
		try {
			File file = new File("src/main/resources/board1/territoryLocations.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			nodeList = doc.getElementsByTagName("territory");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					String name = element.getAttribute("name");
					int x = Integer.parseInt(element.getAttribute("x"));
					int y = Integer.parseInt(element.getAttribute("y"));
					String continent = element.getAttribute("continent");
					Point location = new Point(x, y);
					territoryMapController.addTerritory(new TerritoryModel(name, location, continent));
				}
			}
		}
		return territoryMapController;
	}

	private void setNTerritoriesToBeClaimed(int n, TerritoryMapController territoryMapController) {
		for(int i = 0; i < n; i++) {
			territoryMapController.allTerritories.get(i).setOwner(ColorFactory.createColor("Player1"));
		}
	}

	@Test
	public void testGetNameLocationHashSetEmpty() {
		testGetNameLocationHashSetSizeN(0);
	}

	private void testGetNameLocationHashSetSizeN(int n) {
		TerritoryMapController territoryMapController = new TerritoryMapController();
		HashMap<String, Point> expectedHashMap = new HashMap<>();

		for(int i = 0; i < n; i++) {
			String territoryName = "Territory" + i;
			Point territoryLocation = new Point(i, i);
			territoryMapController.addTerritory(new TerritoryModel(territoryName, territoryLocation, "Asia"));
			expectedHashMap.put(territoryName, territoryLocation);
		}

		assertEquals(expectedHashMap, territoryMapController.getNameLocationHashMap());
	}

	@Test
	public void testGetNameLocationHashSetSizeOne() {
		testGetNameLocationHashSetSizeN(1);
	}

	@Test
	public void testGetNameLocationHashSetSize41() {
		testGetNameLocationHashSetSizeN(41);
	}

	@Test
	public void testGetNameLocationHashSetSize42() {
		testGetNameLocationHashSetSizeN(42);
	}

	@Test
	public void testGetTerritoryByNameTheNamedTerritoryDoesNotExist() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		try {
			territoryMapController.getTerritoryByName("NoneExistentTerritory");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Territory name must be a valid string", e.getMessage());
		}
	}

	@Test
	public void testSetTerritoryOwnerByName(){
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		territoryMapController.setTerritoryOwnerByName("China", Color.RED);
		assertEquals(Color.RED, territoryMapController.getTerritoryByName("China").getOwner());
	}

	@Test
	public void testChangeTerritoryArmyAmountBy(){
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		int armyAmount = territoryMapController.getTerritoryByName("China").getNumberOfArmies();
		territoryMapController.changeTerritoryArmyAmountBy("China", 1);
		assertEquals(armyAmount+1, territoryMapController.getTerritoryByName("China").getNumberOfArmies());
	}

	@Test
	public void testGetTerritoryByNameSize1() {
		testGetTerritoryByNameWithNTerritories(1);
	}

	private void testGetTerritoryByNameWithNTerritories(int n) {
		ArrayList<TerritoryModel> territoryModels = createNTerritoryModelsThatExpectName(n - 1);

		TerritoryModel lookedForTerritory = createChinaThatExpectsName();
		territoryModels.add(lookedForTerritory);
		EasyMock.replay(territoryModels.toArray());

		TerritoryMapController territoryMapController = new TerritoryMapController();
		for(TerritoryModel territoryModel : territoryModels) {
			territoryMapController.addTerritory(territoryModel);
		}
		territoryMapController.addTerritory(lookedForTerritory);

		assertEquals(lookedForTerritory, territoryMapController.getTerritoryByName("China"));
		EasyMock.verify(territoryModels.toArray());
	}

	private TerritoryModel createChinaThatExpectsName() {
		TerritoryModel lookedForTerritory = EasyMock.strictMock(TerritoryModel.class);
		EasyMock.expect(lookedForTerritory.getName()).andReturn("China");
		return lookedForTerritory;
	}

	private ArrayList<TerritoryModel> createNTerritoryModelsThatExpectName(int n) {
		ArrayList<TerritoryModel> territoryModels = new ArrayList<>();
		for(int i = 0; i < n; i++) {
			TerritoryModel notLookedForTerritory = EasyMock.strictMock(TerritoryModel.class);
			EasyMock.expect(notLookedForTerritory.getName()).andReturn("Territory" + i);
			territoryModels.add(notLookedForTerritory);
		}
		return territoryModels;
	}

	@Test
	public void testGetTerritoryByNameSize41() {
		testGetTerritoryByNameWithNTerritories(41);
	}

	@Test
	public void testGetTerritoryByNameSize42() {
		testGetTerritoryByNameWithNTerritories(42);
	}

	@Test
	public void testAreTerritoriesAdjacentNo() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		assertFalse(territoryMapController.areTerritoriesAdjacent("Russia", "Alaska"));
	}

	private void addEdgeMapFromXML(TerritoryMapController territoryMapController) {
		NodeList nodeList = null;

		try {
			File file = new File("src/main/resources/board1/territoryEdges.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			nodeList = doc.getElementsByTagName("edge");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					String t1 = element.getAttribute("t1");
					String t2 = element.getAttribute("t2");
					territoryMapController.addEdge(t1, t2);
				}
			}
		}
	}

	@Test
	public void testAreTerritoriesAdjacentYesInSameOrder() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		assertTrue(territoryMapController.areTerritoriesAdjacent("Yakutsk", "Kamchatka"));
	}

	@Test
	public void testAreTerritoriesAdjacentYesInDifferentOrder() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		assertTrue(territoryMapController.areTerritoriesAdjacent("Kamchatka", "Yakutsk"));
	}

	@Test
	public void testAreTerritoriesConnectedByOwnedTerritoriesTerritoriesNotOwned() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		territoryMapController.getTerritoryByName("China");
		territoryMapController.getTerritoryByName("Mongolia");
		assertFalse(territoryMapController.areTerritoriesConnectedByOwnedTerritories("China", "Mongolia"));
	}

	@Test
	public void testAreTerritoriesConnectedByOwnedTerritoriesTerritoriesOnlyFirstNotOwned() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		territoryMapController.getTerritoryByName("China");
		territoryMapController.getTerritoryByName("Mongolia").setOwner(ColorFactory.createColor("Player2"));
		assertFalse(territoryMapController.areTerritoriesConnectedByOwnedTerritories("China", "Mongolia"));
	}

	@Test
	public void testAreTerritoriesConnectedByOwnedTerritoriesTerritoriesOnlySecondNotOwned() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		territoryMapController.getTerritoryByName("China").setOwner(ColorFactory.createColor("Player2"));
		territoryMapController.getTerritoryByName("Mongolia");
		assertFalse(territoryMapController.areTerritoriesConnectedByOwnedTerritories("China", "Mongolia"));
	}

	@Test
	public void testAreTerritoriesConnectedByOwnedTerritoriesTerritoriesNotOwnedBySamePlayer() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		territoryMapController.getTerritoryByName("China").setOwner(ColorFactory.createColor("Player1"));
		territoryMapController.getTerritoryByName("Mongolia").setOwner(ColorFactory.createColor("Player2"));
		assertFalse(territoryMapController.areTerritoriesConnectedByOwnedTerritories("China", "Mongolia"));
	}

	@Test
	public void testAreTerritoriesConnectedByOwnedTerritoriesNo() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		territoryMapController.getTerritoryByName("China").setOwner(ColorFactory.createColor("Player1"));
		territoryMapController.getTerritoryByName("Greenland").setOwner(ColorFactory.createColor("Player1"));
		assertFalse(territoryMapController.areTerritoriesConnectedByOwnedTerritories("China", "Greenland"));
	}

	@Test
	public void testAreTerritoriesConnectedByOwnedTerritoriesTwoTerritoriesAreAdjacent() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		territoryMapController.getTerritoryByName("China").setOwner(ColorFactory.createColor("Player1"));
		territoryMapController.getTerritoryByName("Mongolia").setOwner(ColorFactory.createColor("Player1"));
		assertTrue(territoryMapController.areTerritoriesConnectedByOwnedTerritories("China", "Mongolia"));
	}

	@Test
	public void testAreTerritoriesConnectedByOwnedTerritoriesTwoTerritoriesAreNotAdjacent() {
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		Color player1Color = ColorFactory.createColor("Player1");
		territoryMapController.getTerritoryByName("China").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Afghanistan").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Russia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Scandinavia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Iceland").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Greenland").setOwner(ColorFactory.createColor("Player1"));
		assertTrue(territoryMapController.areTerritoriesConnectedByOwnedTerritories("China", "Greenland"));
	}

	@Test
	public void testOwnsContinentAsia(){
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		Color player1Color = ColorFactory.createColor("Player1");
		territoryMapController.getTerritoryByName("China").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Middle East").setOwner(player1Color);
		territoryMapController.getTerritoryByName("India").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Southeast Asia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Afghanistan").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Ural").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Mongolia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Japan").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Siberia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Irkutsk").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Yakutsk").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Kamchatka").setOwner(player1Color);

		Assert.assertTrue(territoryMapController.checkOwnsContinent(player1Color, "Asia"));
	}

	@Test
	public void testOwnsContinentEurope(){
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		Color player1Color = ColorFactory.createColor("Player1");
		territoryMapController.getTerritoryByName("Russia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Scandinavia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Northern Europe").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Southern Europe").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Western Europe").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Great Britain").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Iceland").setOwner(player1Color);

		Assert.assertTrue(territoryMapController.checkOwnsContinent(player1Color, "Europe"));
	}

	@Test
	public void testOwnsContinentNorthAmerica(){
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		Color player1Color = ColorFactory.createColor("Player1");
		territoryMapController.getTerritoryByName("Greenland").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Northwest Territory").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Alaska").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Alberta").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Ontario").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Eastern Canada").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Western United States").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Eastern United States").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Central America").setOwner(player1Color);

		Assert.assertTrue(territoryMapController.checkOwnsContinent(player1Color, "North America"));
	}

	@Test
	public void testOwnsContinentSouthAmerica(){
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		Color player1Color = ColorFactory.createColor("Player1");
		territoryMapController.getTerritoryByName("Brazil").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Peru").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Argentina").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Venezuela").setOwner(player1Color);

		Assert.assertTrue(territoryMapController.checkOwnsContinent(player1Color, "South America"));
	}

	@Test
	public void testOwnsContinentAfrica(){
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		Color player1Color = ColorFactory.createColor("Player1");
		territoryMapController.getTerritoryByName("North Africa").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Egypt").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Central Africa").setOwner(player1Color);
		territoryMapController.getTerritoryByName("East Africa").setOwner(player1Color);
		territoryMapController.getTerritoryByName("South Africa").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Madagascar").setOwner(player1Color);

		Assert.assertTrue(territoryMapController.checkOwnsContinent(player1Color, "Africa"));
	}

	@Test
	public void testOwnsContinentAustralia(){
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		Color player1Color = ColorFactory.createColor("Player1");
		territoryMapController.getTerritoryByName("Indonesia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("New Guinea").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Western Australia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Eastern Australia").setOwner(player1Color);

		Assert.assertTrue(territoryMapController.checkOwnsContinent(player1Color, "Australia"));
	}

	@Test
	public void testDoesntOwnsContinentAustralia(){
		TerritoryMapController territoryMapController = createAllTerritoriesFromXML();
		addEdgeMapFromXML(territoryMapController);
		Color player1Color = ColorFactory.createColor("Player1");
		territoryMapController.getTerritoryByName("Indonesia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("New Guinea").setOwner(ColorFactory.createColor("Player2"));
		territoryMapController.getTerritoryByName("Western Australia").setOwner(player1Color);
		territoryMapController.getTerritoryByName("Eastern Australia").setOwner(player1Color);

		Assert.assertFalse(territoryMapController.checkOwnsContinent(player1Color, "Australia"));
	}

	@Test
	public void testReadTerritoryXMLData() {
		TerritoryMapController expectedTerritoryMapController = createTerritoryMapControllerFullTerritoriesAndEdges();
		TerritoryMapController actualTerritoryMapController = TerritoryMapController.loadTerritoryXMLData(1);
		for(int i = 0; i < expectedTerritoryMapController.allTerritories.size(); i++) {
			TerritoryModel expectedTerritory = expectedTerritoryMapController.allTerritories.get(i);
			assertEquals(expectedTerritory, actualTerritoryMapController.allTerritories.get(i));
		}
		assertEquals(expectedTerritoryMapController.allTerritories.size(),
				actualTerritoryMapController.allTerritories.size());
		for(int i = 0; i < expectedTerritoryMapController.edges.size(); i++) {
			Edge expectedEdge = expectedTerritoryMapController.edges.get(i);
			assertEquals(expectedEdge, actualTerritoryMapController.edges.get(i));
		}
		assertEquals(expectedTerritoryMapController.edges.size(), actualTerritoryMapController.edges.size());
	}

	private TerritoryMapController createTerritoryMapControllerFullTerritoriesAndEdges() {
		TerritoryMapController territoryMapController = createTerritoryMapControllerFullTerritories();
		return addEdgeMap(territoryMapController);
	}

	private TerritoryMapController createTerritoryMapControllerFullTerritories() {
		TerritoryMapController territoryMapController = new TerritoryMapController();
		territoryMapController.addTerritory(new TerritoryModel("Alaska", new Point(120, 190), "North America"));
		territoryMapController.addTerritory(new TerritoryModel("Northwest Territory", new Point(290, 200), "North America"));
		territoryMapController.addTerritory(new TerritoryModel("Alberta", new Point(340, 320),  "North America"));
		territoryMapController.addTerritory(new TerritoryModel("Ontario", new Point(390, 290),  "North America"));
		territoryMapController.addTerritory(new TerritoryModel("Eastern Canada", new Point(540, 295),  "North America"));
		territoryMapController.addTerritory(new TerritoryModel("Greenland", new Point(660, 190), "North America"));
		territoryMapController.addTerritory(new TerritoryModel("Western United States", new Point(365, 375),
				"North America"));
		territoryMapController.addTerritory(new TerritoryModel("Eastern United States", new Point(510, 395),
				"North America"));
		territoryMapController.addTerritory(new TerritoryModel("Central America", new Point(365, 540),  "North America"));

		territoryMapController.addTerritory(new TerritoryModel("Venezuela", new Point(515, 585), "South America"));
		territoryMapController.addTerritory(new TerritoryModel("Brazil", new Point(630, 710), "South America"));
		territoryMapController.addTerritory(new TerritoryModel("Peru", new Point(535, 730), "South America"));
		territoryMapController.addTerritory(new TerritoryModel("Argentina", new Point(490, 835), "South America"));

		territoryMapController.addTerritory(new TerritoryModel("Iceland", new Point(845, 255), "Europe"));
		territoryMapController.addTerritory(new TerritoryModel("Scandinavia", new Point(960, 280), "Europe"));
		territoryMapController.addTerritory(new TerritoryModel("Great Britain", new Point(785, 330), "Europe"));
		territoryMapController.addTerritory(new TerritoryModel("Russia", new Point(1130, 370), "Europe"));
		territoryMapController.addTerritory(new TerritoryModel("Northern Europe", new Point(915, 415), "Europe"));
		territoryMapController.addTerritory(new TerritoryModel("Southern Europe", new Point(1010, 495), "Europe"));
		territoryMapController.addTerritory(new TerritoryModel("Western Europe", new Point(860, 470), "Europe"));

		territoryMapController.addTerritory(new TerritoryModel("North Africa", new Point(910, 590), "Africa"));
		territoryMapController.addTerritory(new TerritoryModel("Egypt", new Point(990, 605), "Africa"));
		territoryMapController.addTerritory(new TerritoryModel("East Africa", new Point(1095, 665), "Africa"));
		territoryMapController.addTerritory(new TerritoryModel("Central Africa", new Point(1030, 730), "Africa"));
		territoryMapController.addTerritory(new TerritoryModel("South Africa", new Point(1010, 835), "Africa"));
		territoryMapController.addTerritory(new TerritoryModel("Madagascar", new Point(1198, 863), "Africa"));

		territoryMapController.addTerritory(new TerritoryModel("Ural", new Point(1340, 340), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("Siberia", new Point(1430, 270), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("Yakutsk", new Point(1550, 215), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("Kamchatka", new Point(1680, 230), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("Irkutsk", new Point(1550, 285), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("China", new Point(1460, 470), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("Mongolia", new Point(1510, 380), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("Japan", new Point(1768, 377), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("Afghanistan", new Point(1280, 395), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("Middle East", new Point(1180, 580), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("India", new Point(1350, 520), "Asia"));
		territoryMapController.addTerritory(new TerritoryModel("Southeast Asia", new Point(1585, 625), "Asia"));

		territoryMapController.addTerritory(new TerritoryModel("Indonesia", new Point(1585, 725), "Australia"));
		territoryMapController.addTerritory(new TerritoryModel("New Guinea", new Point(1725, 720), "Australia"));
		territoryMapController.addTerritory(new TerritoryModel("Western Australia", new Point(1645, 845), "Australia"));
		territoryMapController.addTerritory(new TerritoryModel("Eastern Australia", new Point(1720, 810), "Australia"));

		return territoryMapController;
	}

	private TerritoryMapController addEdgeMap(TerritoryMapController territoryMapController) {
		territoryMapController.addEdge("Alaska", "Northwest Territory");
		territoryMapController.addEdge("Alaska", "Alberta");
		territoryMapController.addEdge("Northwest Territory", "Greenland");
		territoryMapController.addEdge("Greenland", "Ontario");
		territoryMapController.addEdge("Greenland", "Eastern Canada");
		territoryMapController.addEdge("Greenland", "Iceland");
		territoryMapController.addEdge("Alberta", "Northwest Territory");
		territoryMapController.addEdge("Alberta", "Ontario");
		territoryMapController.addEdge("Alberta", "Western United States");
		territoryMapController.addEdge("Ontario", "Western United States");
		territoryMapController.addEdge("Ontario", "Eastern United States");
		territoryMapController.addEdge("Ontario", "Eastern Canada");
		territoryMapController.addEdge("Ontario", "Northwest Territory");
		territoryMapController.addEdge("Eastern Canada", "Eastern United States");
		territoryMapController.addEdge("Western United States", "Eastern United States");
		territoryMapController.addEdge("Western United States", "Central America");
		territoryMapController.addEdge("Eastern United States", "Central America");
		territoryMapController.addEdge("Central America", "Venezuela");
		territoryMapController.addEdge("Venezuela", "Peru");
		territoryMapController.addEdge("Venezuela", "Brazil");
		territoryMapController.addEdge("Peru", "Brazil");
		territoryMapController.addEdge("Peru", "Argentina");
		territoryMapController.addEdge("Brazil", "North Africa");
		territoryMapController.addEdge("Brazil", "Argentina");
		territoryMapController.addEdge("Iceland", "Scandinavia");
		territoryMapController.addEdge("Iceland", "Great Britain");
		territoryMapController.addEdge("Iceland", "Northern Europe");
		territoryMapController.addEdge("Scandinavia", "Russia");
		territoryMapController.addEdge("Russia", "Ural");
		territoryMapController.addEdge("Russia", "Afghanistan");
		territoryMapController.addEdge("Russia", "Middle East");
		territoryMapController.addEdge("Great Britain", "Scandinavia");
		territoryMapController.addEdge("Great Britain", "Northern Europe");
		territoryMapController.addEdge("Great Britain", "Western Europe");
		territoryMapController.addEdge("Western Europe", "Northern Europe");
		territoryMapController.addEdge("Western Europe", "Southern Europe");
		territoryMapController.addEdge("Western Europe", "North Africa");
		territoryMapController.addEdge("Northern Europe", "Russia");
		territoryMapController.addEdge("Northern Europe", "Southern Europe");
		territoryMapController.addEdge("Northern Europe", "Scandinavia");
		territoryMapController.addEdge("Southern Europe", "Russia");
		territoryMapController.addEdge("Southern Europe", "North Africa");
		territoryMapController.addEdge("Southern Europe", "Egypt");
		territoryMapController.addEdge("Southern Europe", "Middle East");
		territoryMapController.addEdge("North Africa", "Egypt");
		territoryMapController.addEdge("North Africa", "East Africa");
		territoryMapController.addEdge("North Africa", "Central Africa");
		territoryMapController.addEdge("Egypt", "Middle East");
		territoryMapController.addEdge("Egypt", "East Africa");
		territoryMapController.addEdge("East Africa", "Middle East");
		territoryMapController.addEdge("East Africa", "Madagascar");
		territoryMapController.addEdge("South Africa","Madagascar");
		territoryMapController.addEdge("Central Africa", "East Africa");
		territoryMapController.addEdge("Central Africa", "South Africa");
		territoryMapController.addEdge("Ural", "Siberia");
		territoryMapController.addEdge("Ural", "Afghanistan");
		territoryMapController.addEdge("Ural", "China");
		territoryMapController.addEdge("Siberia", "Yakutsk");
		territoryMapController.addEdge("Siberia", "Irkutsk");
		territoryMapController.addEdge("Siberia", "China");
		territoryMapController.addEdge("Siberia", "Mongolia");
		territoryMapController.addEdge("Yakutsk", "Kamchatka");
		territoryMapController.addEdge("Yakutsk", "Irkutsk");
		territoryMapController.addEdge("Kamchatka", "Alaska");
		territoryMapController.addEdge("Kamchatka", "Japan");
		territoryMapController.addEdge("Irkutsk", "Kamchatka");
		territoryMapController.addEdge("Irkutsk", "Mongolia");
		territoryMapController.addEdge("Afghanistan", "China");
		territoryMapController.addEdge("Afghanistan", "Middle East");
		territoryMapController.addEdge("Afghanistan", "India");
		territoryMapController.addEdge("China", "Mongolia");
		territoryMapController.addEdge("China", "Southeast Asia");
		territoryMapController.addEdge("Mongolia", "Japan");
		territoryMapController.addEdge("Middle East", "India");
		territoryMapController.addEdge("India", "China");
		territoryMapController.addEdge("India", "Southeast Asia");
		territoryMapController.addEdge("Southeast Asia", "Indonesia");
		territoryMapController.addEdge("Indonesia", "New Guinea");
		territoryMapController.addEdge("Indonesia", "Western Australia");
		territoryMapController.addEdge("New Guinea", "Eastern Australia");
		territoryMapController.addEdge("Western Australia", "Eastern Australia");
		return territoryMapController;
	}

	@Test
	public void testEdgeEqualsSameObject() {
		Edge edge = new Edge("T1", "T2");
		assertTrue(edge.equals(edge));
	}

	@Test
	public void testEdgeEqualsNullObject() {
		Edge edge = new Edge("T1", "T2");
		assertFalse(edge.equals(null));
	}

	@Test
	public void testEdgeEqualsNotAnEdge() {
		Edge edge = new Edge("T1", "T2");
		assertFalse(edge.equals(Integer.valueOf(5)));
	}

	@Test
	public void testEdgeEqualsSameVariables() {
		Edge edge1 = new Edge("T1", "T2");
		edge1.traversed = true;
		Edge edge2 = new Edge("T1", "T2");
		edge2.traversed = true;
		assertEquals(edge1, edge2);
	}

	@Test
	public void testEdgeEqualsTraversedDifferent() {
		Edge edge1 = new Edge("T1", "T2");
		edge1.traversed = true;
		Edge edge2 = new Edge("T1", "T2");
		edge2.traversed = false;
		assertFalse(edge1.equals(edge2));
	}

	@Test
	public void testEdgeEqualsT1Different() {
		Edge edge1 = new Edge("T1One", "T2");
		edge1.traversed = true;
		Edge edge2 = new Edge("T1Two", "T2");
		edge2.traversed = true;
		assertFalse(edge1.equals(edge2));
	}

	@Test
	public void testEdgeEqualsT2Different() {
		Edge edge1 = new Edge("T1", "T2One");
		edge1.traversed = true;
		Edge edge2 = new Edge("T1", "T2Two");
		edge2.traversed = true;
		assertFalse(edge1.equals(edge2));
	}

	@Test
	public void testEdgeHashCode() {
		try {
			new Edge("T1", "T2").hashCode();
			fail();
		} catch (UnsupportedOperationException e) {
			assertEquals("Hashcode not supported", e.getMessage());
		}
	}
}
