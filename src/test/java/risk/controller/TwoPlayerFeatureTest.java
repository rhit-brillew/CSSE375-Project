package risk.controller;

import java.awt.*;
import java.util.ArrayList;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import risk.model.Card;
import risk.model.SixSidedDie;
import risk.model.TerritoryModel;
import risk.view.GameView;

public class TwoPlayerFeatureTest {
	TerritoryMapController mockedTerritories = createMockedTerritories();
	ArrayList<Card> cards = new ArrayList<>();
	GameView gameView = EasyMock.niceMock(GameView.class);
	SixSidedDie die = EasyMock.strictMock(SixSidedDie.class);
	SetupController setupController;

	public TerritoryMapController createMockedTerritories(){
		TerritoryMapController territories = EasyMock.partialMockBuilder(TerritoryMapController.class)
				.withConstructor().createMock();
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("China", new Point(0, 0), "Asia").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Russia", new Point(0, 0), "Europe").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Madagascar", new Point(0, 0), "Africa").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Brazil", new Point(0, 0), "South America").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Western United States", new Point(0, 0), "North America").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Peru", new Point(0, 0), "South America").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("North Africa", new Point(0, 0), "Africa").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("East Africa", new Point(0, 0), "Africa").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Egypt", new Point(0, 0), "Africa").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Western Europe", new Point(0, 0), "Europe").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Central Africa", new Point(0, 0), "Africa").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("India", new Point(0, 0), "Asia").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Alaska", new Point(0, 0), "North America").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Eastern Canada", new Point(0, 0), "North America").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Siberia", new Point(0, 0), "Asia").createMock());

		return territories;
	}

	public void createCards(){
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("China", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Russia", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Madagascar", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Brazil", "Infantry").createMock());

		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Western United States", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Peru", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("North Africa", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("East Africa", "Infantry").createMock());

		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Egypt", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Western Europe", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Central Africa", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("India", "Infantry").createMock());

		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Alaska", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Eastern Canada", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(String.class, String.class)
				.withArgs("Siberia", "Infantry").createMock());
	}

	public void setupTwoPlayerFeatureTest(){
		EasyMock.expect(gameView.getNumberOfPlayers()).andReturn(2);
		EasyMock.replay(gameView);
		setupController = new SetupController(gameView, die, mockedTerritories);
		createCards();
		setupController.territories.setDeck(cards);
	}

	@Test
	public void  testPlaceNeutralArmies() {
		setupTwoPlayerFeatureTest();
		setupController.twoPlayerSetupPhase();
		Assert.assertEquals(35, setupController.neutralPlayer.getNumberOfUnplacedArmies());
		EasyMock.verify(gameView);
	}

	@Test
	public void  testClaimAlreadyClaimed() {
		setupTwoPlayerFeatureTest();
		setupController.gameState = SetupController.GameState.CLAIMING;
		setupController.twoPlayerSetupPhase();
		Assert.assertEquals(35, setupController.neutralPlayer.getNumberOfUnplacedArmies());

		setupController.territories.getTerritoryByName("China").setOwner(setupController.neutralPlayer.getColor());
		setupController.territoryPressed("China", true);
		Assert.assertEquals(35, setupController.playerModels.get(0).getNumberOfUnplacedArmies());
		EasyMock.verify(gameView);
	}

	@Test
	public void  testClaimUnclaimed() {
		setupTwoPlayerFeatureTest();
		setupController.gameState = SetupController.GameState.CLAIMING;
		setupController.twoPlayerSetupPhase();
		Assert.assertEquals(35, setupController.neutralPlayer.getNumberOfUnplacedArmies());

		setupController.territories.getTerritoryByName("Siberia").setOwner(Color.GRAY);
		setupController.territoryPressed("Siberia", true);
		Assert.assertEquals(34, setupController.playerModels.get(0).getNumberOfUnplacedArmies());
		EasyMock.verify(gameView);
	}

	@Test
	public void  testPlaceTwoArmiesOnClaimedTerritory() {
		setupTwoPlayerFeatureTest();
		setupController.gameState = SetupController.GameState.CLAIMING;
		setupController.twoPlayerSetupPhase();
		Assert.assertEquals(35, setupController.neutralPlayer.getNumberOfUnplacedArmies());

		setupController.territories.getTerritoryByName("Siberia").setOwner(Color.RED);
		setupController.territoryPressed("Siberia", true);
		setupController.territoryPressed("Siberia", true);
		setupController.territories.getTerritoryByName("China").setOwner(Color.GRAY);
		setupController.territoryPressed("China", true);
		Assert.assertEquals(33, setupController.playerModels.get(0).getNumberOfUnplacedArmies());
		EasyMock.verify(gameView);
	}

	@Test
	public void  testPlaceTwoArmiesOnDifferentClaimedTerritories() {
		setupTwoPlayerFeatureTest();
		setupController.gameState = SetupController.GameState.CLAIMING;
		setupController.twoPlayerSetupPhase();
		Assert.assertEquals(35, setupController.neutralPlayer.getNumberOfUnplacedArmies());

		setupController.territories.getTerritoryByName("Siberia").setOwner(Color.RED);
		setupController.territories.getTerritoryByName("India").setOwner(Color.RED);
		setupController.territoryPressed("Siberia", true);
		setupController.territoryPressed("India", true);
		Assert.assertEquals(33, setupController.playerModels.get(0).getNumberOfUnplacedArmies());
		EasyMock.verify(gameView);
	}

	@Test
	public void  testPlacePlayerArmies() {
		setupTwoPlayerFeatureTest();
		setupController.twoPlayerSetupPhase();

		Assert.assertEquals(35, setupController.playerModels.get(0).getNumberOfUnplacedArmies());
		Assert.assertEquals(35, setupController.playerModels.get(1).getNumberOfUnplacedArmies());
		EasyMock.verify(gameView);
	}

	@Test
	public void testDetermineFirstPlayerTwoPlayersNonMatchingRolls(){
		setupTwoPlayerFeatureTest();
		setupController.startingRolls = new int[2];
		setupController.startingRolls[0] = 2;
		setupController.startingRolls[1] = 4;

		setupController.determineFirstPlayer();
		EasyMock.verify(gameView);
	}

	@Test
	public void testDetermineFirstPlayerTwoPlayersMatchingRolls(){
		setupTwoPlayerFeatureTest();
		setupController.startingRolls = new int[2];
		setupController.startingRolls[0] = 4;
		setupController.startingRolls[1] = 4;

		setupController.determineFirstPlayer();
		EasyMock.verify(gameView);
	}
}
