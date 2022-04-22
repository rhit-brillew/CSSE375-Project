package risk.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import risk.model.Card;
import risk.model.PlayerModel;
import risk.model.SixSidedDie;
import risk.model.StaticResourceBundle;
import risk.model.TerritoryModel;
import risk.view.GameView;

/** BVA: territoryPressed
 * CASE
 * 		| Expected
 * gamePhase is PLACING but player has no unplaced armies                                                   
 * 		| error label updated
 * gamePhase is PLACING and player has unplaced armies                                                      
 * 		| army added to territory and displays updated
 * gamePhase is PLACING and player doesn't own territory                                                    
 * 		| error label updated
 * gamePhase is PLACING and territory doesn't exist                                                         
 * 		| error label updated
 * gamePhase is PLACING and territory name is empty                                                         
 * 		| error label updated
 * gamePhase is ATTACKING currentAttacker and currentDefender are not null                                  
 * 		| error label updated
 * gamePhase is ATTACKING and currentAttacker is null and territoryName does not exist                      
 * 		| IllegalArgumentException
 * gamePhase is ATTACKING and currentDefender is null and territories are not adjacent                      
 * 		| error label updated
 * gamePhase is ATTACKING and currentDefender is null and territoryName is owned by currentPlayer           
 * 		| error label updated
 * gamePhase is ATTACKING and currentAttacker is null territoryName is owned by opponent                    
 * 		| error label updated
 * gamePhase is ATTACKING and currentAttacker is null and territoryName has one army                        
 * 		| error label updated
 * gamePhase is ATTACKING and currentAttacker is null and territoryName has more than one army              
 * 		| territoryName is highlighted and currentAttacker is now territoryName
 * gamePhase is ATTACKING and currentAttacker is not null with 2 armies and territoryName is owned by enemy 
 * 		| territoryName is highlighted and currentDefender is now territoryName there is 1 attacking army
 * gamePhase is ATTACKING and currentAttacker is not null with 3 armies and territoryName is owned by enemy 
 * 		| territoryName is highlighted and currentDefender is now territoryName there is 2 attacking army
 * gamePhase is ATTACKING and currentAttacker is not null with 4 armies and territoryName is owned by enemy 
 * 		| territoryName is highlighted and currentDefender is now territoryName there is 3 attacking army
 * gamePhase is TRADING                                                                                     
 * 		| error label is updated saying it is the trading phase
 * gamePhase is REINFORCING and removing a troop territory only has one troop                               
 * 		| error label updated to show territory must have at least 1 troop left
 * gamePhase is REINFORCING and removing a troop territory not owned by player                              
 * 		| error label updated to show territory must be owned by current player
 * gamePhase is REINFORCING and removing a troop no unplaced troops and territoryRemovedFrom is null        
 * 		| territory has one less troop, player has one troop to place. territoryRemovedFrom is territory
 * gamePhase is REINFORCING and removing a troop from not territoryRemovedFrom unplaced troops is 0         
 * 		| territory has one less troop, player has one more troop to place. territoryRemovedFrom is territory
 * gamePhase is REINFORCING and removing a troop from territoryRemovedFrom                                  
 * 		| territory has one less troop, player has one more troop to place
 * gamePhase is REINFORCING and removing a troop from not territoryRemovedFrom unplaced troops is not 0     
 * 		| error label is updated saying player must place territories before removing from a different territory
 * gamePhase is REINFORCING and placing a troop territory not owned by player                               
 * 		| error label is updated saying territory must be owned by player
 * gamePhase is REINFORCING and placing a troop current no unplaced troops                                  
 * 		| error label is updated saying player must have unplaced troops to place army
 * gamePhase is REINFORCING and placing a troop noncontinuous path from territoryRemovedFrom                
 * 		| error label is updated saying troops must only be moved to territories connected by owned territories
 * gamePhase is REINFORCING and placing a troop continuous path from territoryRemovedFrom                   
 * 		| territory has one more troop, player has one less troop to place
 *
 * BVA: nextPhase
 * CASE                                                                 
 * 		| EXPECTED
 * gamePhase is TRADING and current player has 5 cards                  
 * 		| error label updated
 * gamePhase is TRADING and current player has 0 cards                  
 * 		| error label updated
 * gamePhase is TRADING and current player has 1 cards                  
 * 		| error label updated
 * gamePhase is PLACING and current player has unplaced armies          
 * 		| error label updated
 * gamePhase is PLACING and current player has no unplaced armies       
 * 		| gamePhase is ATTACKING
 * gamePhase is ATTACKING and getsCard false                            
 * 		| gamePhase is REINFORCING
 * gamePhase is ATTACKING and gets card true, deck size is 0            
 * 		| deck size is still 0 and player has same amount of cards
 * gamePhase is ATTACKING and gets card true, deck size is 1            
 * 		| deck size is 0 and player has one more card than they started with
 * gamePhase is ATTACKING and gets card true, deck size is 42           
 * 		| deck size is one less and player has one more card than they started with
 * gamePhase is REINFORCING and current player has unplaced armies      
 * 		| error label updated
 * gamePhase is REINFORCING and current player has no unplaced armies   
 * 		| currentPlayer incremented and gamePhase is TRADING
 * gamePhase is REINFORCING, two player game player 0's turn            
 * 		| currentPlayer is switched to the other real player
 * gamePhase is REINFORCING, two player game player 1's turn            
 * 		| currentPlayer is switched to the other real player
 *
 * BVA: tradeInCards
 * CASE                                                             
 * 		| EXPECTED
 * current player has 0 cards                                       
 * 		| Error label is updated saying they can't trade due to not having enough cards.
 * current player has 2 cards                                       
 * 		| Error label is updated saying they can't trade due to not having enough cards.
 * current player has 3 cards: 2 of one type 1 of another           
 * 		| Error label is updated saying they can't trade due to not meeting the trade condition.
 * current player has 3 cards: all infantry                         
 * 		| The three cards are traded in and the players unplaced armies increases
 * current player has 3 cards: all cavalry                          
 * 		| The three cards are traded in and the players unplaced armies increases
 * current player has 3 cards: all artillery                        
 * 		| The three cards are traded in and the players unplaced armies increases
 * current player has 3 cards: all different type                   
 * 		| The three cards are traded in and the players unplaced armies increases
 * current player has the whole deck of cards                       
 * 		| The three cards are traded in and the players unplaced armies increases
 * current player trades in the second sets of cards                
 * 		| The three cards are traded in and the players unplaced armies increases
 * current player trades in the fifth sets of cards                 
 * 		| The three cards are traded in and the players unplaced armies increases
 * current player trades in the sixth sets of cards                 
 * 		| The three cards are traded in and the players unplaced armies increases
 * current player trades in a set of cards with an owned territory  
 * 		| The three cards are traded in and the players unplaced armies increases. Owned territories armies increases by 2
 *
 * BVA: determineBattleWinner
 * CASE                                         
 * 		| EXPECTED
 * Attacker wins it was not the last territory  
 * 		| defending territory is now owned by attacker
 * Attacker wins it was the last territory      
 * 		| defending territory is now owned by attacker and the win message is displayed
 * Defender wins                                
 * 		| defending territory is still owned by defender
 *
 * BVA: moveTroops
 * CASE                                             
 * 		| EXPECTED
 * move no troops                                   
 * 		| starting and ending territory have unchanged army count
 * move 1 troop                                     
 * 		| starting territory has one more army; ending has one less
 * move one less troop than on starting territory   
 * 		| starting territory has one troop; ending has original + starting territory original - 1
 * move same number of troop on starting territory  
 * 		| error label is updated telling player one troop must remain
 *
 * BVA: playerRolls
 *  CASE                                                    
 *  	| EXPECTED
 *  Attacker has 3 rolls left                               
 *  	| attacker has one less roll left, roll added to attacker roll list
 *  Attacker has 1 roll left                                
 *  	| attacker has 0 rolls left, roll added to attacker roll list
 *  Attacker has 0 rolls left, defender has 2 rolls left    
 *  	| defender has one less roll left, roll added to defender roll list
 *  Attacker has 0 rolls left, defender has 1 roll left     
 *  	| defender has 0 rolls left, roll added to defender roll list
 *  Attacker and defender have 0 rolls left                 
 *  	| attacking display updated and battle winner determined
 *
 * BVA: determineNumberOfRolls
 *  CASE                            | EXPECTED
 *  1 army on defender's territory  | defender rolls 1 die
 *  attacker selects 1 die          | defender rolls 1 die
 *  attacker selects 3 dice         | defender rolls 2 dice
 */

/*
 * calculateNumberOfArmies:
 *
 * own less than nine territories
 * own more than nine territories(divisible by 3)
 * own more than nine territories(not divisible by 3)
 */

/*
 * hasWon Cases:
 *
 * player has won the game
 * player has not won the game
 */

public class TurnControllerTest {
	TerritoryMapController mockedTerritories = createTerritories();
	ArrayList<PlayerModel> playerModels = createPlayerModels();
	GameView mockedGameView = EasyMock.niceMock(GameView.class);
	SixSidedDie mockedDie = EasyMock.strictMock(SixSidedDie.class);

	// Mock setup methods
	public TerritoryMapController createTerritories(){
		StaticResourceBundle.createResourceBundle(new Locale("en", "US"));
		TerritoryMapController territories = EasyMock.partialMockBuilder(TerritoryMapController.class)
				.withConstructor().createMock();
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class).withArgs("China", new Point(0, 0), "Asia").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class).withArgs("Russia", new Point(0, 0), "Europe").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class).withArgs("Madagascar", new Point(0, 0), "Africa").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class).withArgs("Brazil", new Point(0, 0), "South America").createMock());

		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Western United States", new Point(0, 0), "North America").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class).withArgs("Peru", new Point(0, 0), "South America").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class).withArgs("North Africa", new Point(0, 0), "Africa").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class).withArgs("East Africa", new Point(0, 0), "Africa").createMock());

		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class).withArgs("Egypt", new Point(0, 0), "Africa").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class)
				.withArgs("Western Europe", new Point(0, 0), "Europe").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class).withArgs("Central", new Point(0, 0), "Africa").createMock());
		territories.addTerritory(EasyMock.partialMockBuilder(TerritoryModel.class).withConstructor(
				String.class, Point.class, String.class).withArgs("India", new Point(0, 0), "Asia").createMock());

		return territories;
	}

	public ArrayList<PlayerModel> createPlayerModels(){
		ArrayList<PlayerModel> playerModels = new ArrayList<>();
		playerModels.add(EasyMock.partialMockBuilder(PlayerModel.class)
				.withConstructor(Integer.class, Color.class).withArgs(35, Color.RED).createMock());
		playerModels.add(EasyMock.partialMockBuilder(PlayerModel.class)
				.withConstructor(Integer.class, Color.class).withArgs(35, Color.GREEN).createMock());
		playerModels.add(EasyMock.partialMockBuilder(PlayerModel.class)
				.withConstructor(Integer.class, Color.class).withArgs(35, Color.BLUE).createMock());
		playerModels.add(EasyMock.partialMockBuilder(PlayerModel.class)
				.withConstructor(Integer.class, Color.class).withArgs(35, Color.MAGENTA).createMock());
		playerModels.add(EasyMock.partialMockBuilder(PlayerModel.class)
				.withConstructor(Integer.class, Color.class).withArgs(35, Color.ORANGE).createMock());
		return playerModels;
	}
	
	public ArrayList<Card> createCards(){
		ArrayList<Card> cards = new ArrayList<>();
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("China", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("Russia", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("Madagascar", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("Brazil", "Cavalry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class)
				.withArgs("Western United States", "Cavalry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("Peru", "Cavalry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("North Africa", "Artillery").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("East Africa", "Artillery").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("Egypt", "Artillery").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("Western Europe", "Infantry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("Central", "Cavalry").createMock());
		cards.add(EasyMock.partialMockBuilder(Card.class).withConstructor(
				String.class, String.class).withArgs("India", "Artillery").createMock());

		return cards;
	}

	public void createContinentMap(){
		HashMap<String, Integer> map = new HashMap<>();
		map.put("North America", 5);
		map.put("South America", 2);
		map.put("Asia", 7);
		map.put("Africa", 3);
		map.put("Europe", 5);
		map.put("Australia",2);

		this.mockedTerritories.continents = map;
	}

	@Test
	public void testTerritoryPressedPlacingOwnedWithUnplacedArmies() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.gamePhase = TurnController.GamePhase.PLACING;
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("China").setOwner(Color.RED);
		turnController.territoryPressed("China", true);

		assertEquals(2, mockedTerritories.getTerritoryByName("China").getNumberOfArmies());
		assertEquals(34, playerModels.get(0).getNumberOfUnplacedArmies());
	}

	@Test
	public void testTerritoryPressedPlacingUnownedTerritory(){
		mockedGameView.updateErrorLabel("You do not own this territory");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.gamePhase = TurnController.GamePhase.PLACING;
		turnController.territories.getTerritoryByName("Madagascar").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("Madagascar").setOwner(Color.RED);
		turnController.currentPlayer = 1;
		turnController.territoryPressed("Madagascar", true);

		assertEquals(35, playerModels.get(1).getNumberOfUnplacedArmies());
		assertEquals(1,  mockedTerritories.getTerritoryByName("Madagascar").getNumberOfArmies());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedPlacingEmptyString(){
		mockedGameView.updateErrorLabel("Territory name must be a valid string");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.gamePhase = TurnController.GamePhase.PLACING;
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("China").setOwner(Color.RED);
		turnController.territoryPressed("", true);

		assertEquals(35, playerModels.get(0).getNumberOfUnplacedArmies());
		assertEquals(1,  mockedTerritories.getTerritoryByName("China").getNumberOfArmies());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedPlacingTerritoryDoesntExist() {
		mockedGameView.updateErrorLabel("Territory name must be a valid string");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.gamePhase = TurnController.GamePhase.PLACING;
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("China").setOwner(Color.RED);
		turnController.territoryPressed("A territory that doesn't exist", true);

		assertEquals(35, playerModels.get(0).getNumberOfUnplacedArmies());
		assertEquals(1,  mockedTerritories.getTerritoryByName("China").getNumberOfArmies());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedPlacingTerritoryWithNoAvailableTroops(){
		mockedGameView.updateErrorLabel("You don't have enough armies");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.gamePhase = TurnController.GamePhase.PLACING;
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(1);
		turnController.playerModels.get(0).setNumberOfUnplacedArmies(0);
		turnController.territories.getTerritoryByName("China").setOwner(Color.RED);
		turnController.territoryPressed("China", true);

		assertEquals(0, playerModels.get(0).getNumberOfUnplacedArmies());
		assertEquals(1,  mockedTerritories.getTerritoryByName("China").getNumberOfArmies());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedAttackingCurrentAttackerAndCurrentDefenderAreNotNull() {
		mockedGameView.updateErrorLabel("Another battle is currently in progress");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.currentAttacker = "China";
		turnController.currentDefender = "Madagascar";
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.territoryPressed("China", true);

		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedAttackingCurrentAttackerIsNullAndTerritoryNameDoesNotExist() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.currentAttacker = "China";
		turnController.currentDefender = null;
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		try {
			turnController.territoryPressed("FakeTerritory", true);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Territory name must be a valid string", e.getMessage());
		}
	}

	@Test
	public void testTerritoryPressedAttackingCurrentDefenderNullTerritoriesNotAdjacent() {
		mockedGameView.updateErrorLabel("<html>Owned territory must be "
				+ "selected and adjacent to opponent territory</html>");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.currentAttacker = "China";
		turnController.currentDefender = null;
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.territoryPressed("Madagascar", true);

		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedAttackingCurrentDefenderNullTerritoryNameOwnedByCurrentPlayer() {
		mockedGameView.updateErrorLabel("Can't Attack your own territory");
		mockedTerritories.addEdge("China", "Russia");
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("China")
			.setOwner(turnController.playerModels.get(turnController.currentPlayer).getColor());
		turnController.territories.getTerritoryByName("Russia").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("Russia")
			.setOwner(turnController.playerModels.get(turnController.currentPlayer).getColor());
		turnController.currentAttacker = "China";
		turnController.currentDefender = null;
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.territoryPressed("Russia", true);

		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedAttackingCurrentAttackerNullTerritoryNameOwnedByOpponent() {
		mockedGameView.updateErrorLabel("You do not own this territory");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("China").setOwner(Color.BLACK);
		turnController.currentAttacker = null;
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.territoryPressed("China", true);

		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedAttackingCurrentAttackerNullTerritoryHasOneArmy() {
		mockedGameView.updateErrorLabel("Must have at least 2 troops to attack");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("China")
			.setOwner(turnController.playerModels.get(turnController.currentPlayer).getColor());
		turnController.currentAttacker = null;
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.territoryPressed("China", true);

		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedAttackingCurrentAttackerNullTerritoryHasMoreThanOneArmy() {
		mockedGameView.highlightTerritory("China");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(2);
		turnController.territories.getTerritoryByName("China")
			.setOwner(turnController.playerModels.get(turnController.currentPlayer).getColor());
		turnController.currentAttacker = null;
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.territoryPressed("China", true);

		assertEquals("China", turnController.currentAttacker);
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedAttackingCurrentAttackerNotNullWith2ArmiesTerritoryNameOwnedByOpponent() {
		mockedGameView.showAttackCount(1);
		mockedTerritories.addEdge("China", "Russia");
		mockedGameView.highlightTerritory("Russia");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(2);
		turnController.territories.getTerritoryByName("China")
			.setOwner(turnController.playerModels.get(turnController.currentPlayer).getColor());
		turnController.territories.getTerritoryByName("Russia").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("Russia").setOwner(Color.BLACK);
		turnController.currentAttacker = "China";
		turnController.currentDefender = null;
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.territoryPressed("Russia", true);

		assertEquals("Russia", turnController.currentDefender);
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedAttackingCurrentAttackerNotNullWith3ArmiesTerritoryNameOwnedByOpponent() {
		mockedGameView.showAttackCount(2);
		mockedTerritories.addEdge("China", "Russia");
		mockedGameView.highlightTerritory("Russia");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(3);
		turnController.territories.getTerritoryByName("China")
			.setOwner(turnController.playerModels.get(turnController.currentPlayer).getColor());
		turnController.territories.getTerritoryByName("Russia").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("Russia").setOwner(turnController.playerModels.get(2).getColor());
		turnController.currentAttacker = "China";
		turnController.currentDefender = null;
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.territoryPressed("Russia", true);

		assertEquals("Russia", turnController.currentDefender);
		assertEquals(turnController.playerModels.get(2).getColor(), 
				mockedTerritories.getTerritoryByName(turnController.currentDefender).getOwner());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedAttackingCurrentAttackerNotNullWith4ArmiesTerritoryNameOwnedByOpponent() {
		mockedGameView.showAttackCount(3);
		mockedTerritories.addEdge("China", "Russia");
		mockedGameView.highlightTerritory("Russia");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(4);
		turnController.territories.getTerritoryByName("China")
			.setOwner(turnController.playerModels.get(turnController.currentPlayer).getColor());
		turnController.territories.getTerritoryByName("Russia").changeArmyAmountBy(1);
		turnController.territories.getTerritoryByName("Russia").setOwner(Color.BLACK);
		turnController.currentAttacker = "China";
		turnController.currentDefender = null;
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.territoryPressed("Russia", true);

		assertEquals("Russia", turnController.currentDefender);
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedTrading() {
		mockedGameView.updateErrorLabel("Click next phase to skip Trading phase");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territoryPressed("China", true);

		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedReinforcingRemovingTerritoryHasOneTroop() {
		mockedGameView.updateErrorLabel("You must leave at least one troop on territories you own");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		TerritoryModel clickedTerritory = turnController.territories.getTerritoryByName("China");
		clickedTerritory.changeArmyAmountBy(1);
		clickedTerritory.setOwner(turnController.playerModels.get(turnController.currentPlayer).getColor());
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		int numberOfUnplacedArmiesBeforeTerritoryPressed
			= playerModels.get(turnController.currentPlayer).getNumberOfUnplacedArmies();
		turnController.territoryPressed("China", false);

		assertEquals(1, clickedTerritory.getNumberOfArmies());
		assertEquals(numberOfUnplacedArmiesBeforeTerritoryPressed,
				playerModels.get(turnController.currentPlayer).getNumberOfUnplacedArmies());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedReinforcingRemovingTerritoryNotOwnedByPlayer() {
		mockedGameView.updateErrorLabel("You do not own this territory");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		TerritoryModel clickedTerritory = turnController.territories.getTerritoryByName("China");
		clickedTerritory.changeArmyAmountBy(5);
		turnController.currentPlayer = 1;
		clickedTerritory.setOwner(turnController.playerModels.get(0).getColor());
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		int numberOfUnplacedArmiesBeforeTerritoryPressed
			= playerModels.get(turnController.currentPlayer).getNumberOfUnplacedArmies();
		turnController.territoryPressed("China", false);

		assertEquals(5, clickedTerritory.getNumberOfArmies());
		assertEquals(numberOfUnplacedArmiesBeforeTerritoryPressed,
				playerModels.get(turnController.currentPlayer).getNumberOfUnplacedArmies());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedReinforcingRemovingFirstTerritoryNoUnplacedTroops() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		TerritoryModel clickedTerritory = turnController.territories.getTerritoryByName("China");
		PlayerModel currentPlayer = playerModels.get(turnController.currentPlayer);
		clickedTerritory.changeArmyAmountBy(5);
		clickedTerritory.setOwner(currentPlayer.getColor());
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		int territoryArmyCountBeforeTerritoryPressed = clickedTerritory.getNumberOfArmies();
		int numberOfUnplacedArmiesBeforeTerritoryPressed = currentPlayer.getNumberOfUnplacedArmies();
		turnController.territoryPressed("China", false);

		assertEquals(territoryArmyCountBeforeTerritoryPressed - 1, clickedTerritory.getNumberOfArmies());
		assertEquals(numberOfUnplacedArmiesBeforeTerritoryPressed + 1, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals("China", turnController.territoryRemovedFrom);
	}

	@Test
	public void testTerritoryPressedReinforcingRemovingNotTerritoryRemovedFromNoUnplacedTroops() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		TerritoryModel clickedTerritory = turnController.territories.getTerritoryByName("China");
		PlayerModel currentPlayer = playerModels.get(turnController.currentPlayer);
		currentPlayer.setNumberOfUnplacedArmies(0);
		clickedTerritory.changeArmyAmountBy(5);
		clickedTerritory.setOwner(currentPlayer.getColor());
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		turnController.territoryRemovedFrom = "Russia";
		int territoryArmyCountBeforeTerritoryPressed = clickedTerritory.getNumberOfArmies();
		int numberOfUnplacedArmiesBeforeTerritoryPressed = currentPlayer.getNumberOfUnplacedArmies();
		turnController.territoryPressed("China", false);

		assertEquals(territoryArmyCountBeforeTerritoryPressed - 1, clickedTerritory.getNumberOfArmies());
		assertEquals(numberOfUnplacedArmiesBeforeTerritoryPressed + 1, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals("China", turnController.territoryRemovedFrom);
	}

	@Test
	public void testTerritoryPressedReinforcingRemovingTerritoryRemovedFromUnplacedTroops() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		TerritoryModel clickedTerritory = turnController.territories.getTerritoryByName("China");
		PlayerModel currentPlayer = playerModels.get(turnController.currentPlayer);
		currentPlayer.setNumberOfUnplacedArmies(5);
		clickedTerritory.changeArmyAmountBy(5);
		clickedTerritory.setOwner(currentPlayer.getColor());
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		turnController.territoryRemovedFrom = "China";
		int territoryArmyCountBeforeTerritoryPressed = clickedTerritory.getNumberOfArmies();
		int numberOfUnplacedArmiesBeforeTerritoryPressed = currentPlayer.getNumberOfUnplacedArmies();
		turnController.territoryPressed("China", false);

		assertEquals(territoryArmyCountBeforeTerritoryPressed - 1, clickedTerritory.getNumberOfArmies());
		assertEquals(numberOfUnplacedArmiesBeforeTerritoryPressed + 1, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals("China", turnController.territoryRemovedFrom);
	}

	@Test
	public void testTerritoryPressedReinforcingRemovingNotTerritoryRemovedFromUnplacedTroops() {
		mockedGameView.updateErrorLabel("You must place troops from one territory before"
				+ " removing from a different territory");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		TerritoryModel clickedTerritory = turnController.territories.getTerritoryByName("China");
		PlayerModel currentPlayer = playerModels.get(turnController.currentPlayer);
		currentPlayer.setNumberOfUnplacedArmies(5);
		clickedTerritory.changeArmyAmountBy(5);
		clickedTerritory.setOwner(currentPlayer.getColor());
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		turnController.territoryRemovedFrom = "Russia";
		int territoryArmyCountBeforeTerritoryPressed = clickedTerritory.getNumberOfArmies();
		int numberOfUnplacedArmiesBeforeTerritoryPressed = currentPlayer.getNumberOfUnplacedArmies();
		turnController.territoryPressed("China", false);

		assertEquals(territoryArmyCountBeforeTerritoryPressed, clickedTerritory.getNumberOfArmies());
		assertEquals(numberOfUnplacedArmiesBeforeTerritoryPressed, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals("Russia", turnController.territoryRemovedFrom);
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedReinforcingPlacingTerritoryNotOwnedByPlayer() {
		mockedGameView.updateErrorLabel("You do not own this territory");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territoryRemovedFrom = "China";
		TerritoryModel clickedTerritory = turnController.territories.getTerritoryByName("China");
		clickedTerritory.changeArmyAmountBy(5);
		turnController.currentPlayer = 1;
		clickedTerritory.setOwner(turnController.playerModels.get(0).getColor());
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		int numberOfUnplacedArmiesBeforeTerritoryPressed
			= playerModels.get(turnController.currentPlayer).getNumberOfUnplacedArmies();
		turnController.territoryPressed("China", true);

		assertEquals(5, clickedTerritory.getNumberOfArmies());
		assertEquals(numberOfUnplacedArmiesBeforeTerritoryPressed,
				playerModels.get(turnController.currentPlayer).getNumberOfUnplacedArmies());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedReinforcingPlacingNoUnplacedArmies() {
		mockedGameView.updateErrorLabel("You don't have enough armies");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territoryRemovedFrom = "China";
		PlayerModel currentPlayer = playerModels.get(turnController.currentPlayer);
		currentPlayer.setNumberOfUnplacedArmies(0);
		TerritoryModel clickedTerritory = turnController.territories.getTerritoryByName("China");
		clickedTerritory.changeArmyAmountBy(5);
		clickedTerritory.setOwner(currentPlayer.getColor());
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		int numberOfUnplacedArmiesBeforeTerritoryPressed = currentPlayer.getNumberOfUnplacedArmies();
		int territoryArmyCountBeforeTerritoryPressed = clickedTerritory.getNumberOfArmies();
		turnController.territoryPressed("China", true);

		assertEquals(territoryArmyCountBeforeTerritoryPressed, clickedTerritory.getNumberOfArmies());
		assertEquals(numberOfUnplacedArmiesBeforeTerritoryPressed, currentPlayer.getNumberOfUnplacedArmies());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedReinforcingPlacingNoncontinuousPathFromTerritoryRemovedFrom() {
		mockedGameView.updateErrorLabel("You must place armies on a territory which can be "
				+ "reached from the territory you took them from");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = playerModels.get(turnController.currentPlayer);
		currentPlayer.setNumberOfUnplacedArmies(5);

		TerritoryModel clickedTerritory = turnController.territories.getTerritoryByName("China");
		clickedTerritory.changeArmyAmountBy(5);
		clickedTerritory.setOwner(currentPlayer.getColor());

		TerritoryModel startingTerritory = turnController.territories.getTerritoryByName("Russia");
		startingTerritory.setOwner(currentPlayer.getColor());
		startingTerritory.changeArmyAmountBy(5);
		turnController.territoryRemovedFrom = "Russia";

		TerritoryModel betweenTerritory = turnController.territories.getTerritoryByName("Madagascar");
		betweenTerritory.setOwner(Color.PINK);

		turnController.territories.addEdge("Russia", "Madagascar");
		turnController.territories.addEdge("China", "Madagascar");

		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		int numberOfUnplacedArmiesBeforeTerritoryPressed = currentPlayer.getNumberOfUnplacedArmies();
		int territoryArmyCountBeforeTerritoryPressed = clickedTerritory.getNumberOfArmies();
		turnController.territoryPressed("China", true);

		assertEquals(territoryArmyCountBeforeTerritoryPressed, clickedTerritory.getNumberOfArmies());
		assertEquals(numberOfUnplacedArmiesBeforeTerritoryPressed, currentPlayer.getNumberOfUnplacedArmies());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTerritoryPressedReinforcingPlacingContinuousPathFromTerritoryRemovedFrom() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = playerModels.get(turnController.currentPlayer);
		currentPlayer.setNumberOfUnplacedArmies(5);

		TerritoryModel clickedTerritory = turnController.territories.getTerritoryByName("China");
		clickedTerritory.changeArmyAmountBy(5);
		clickedTerritory.setOwner(currentPlayer.getColor());

		TerritoryModel startingTerritory = turnController.territories.getTerritoryByName("Russia");
		startingTerritory.setOwner(currentPlayer.getColor());
		startingTerritory.changeArmyAmountBy(5);
		turnController.territoryRemovedFrom = "Russia";

		TerritoryModel betweenTerritory = turnController.territories.getTerritoryByName("Madagascar");
		betweenTerritory.setOwner(currentPlayer.getColor());

		turnController.territories.addEdge("Russia", "Madagascar");
		turnController.territories.addEdge("China", "Madagascar");

		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		int numberOfUnplacedArmiesBeforeTerritoryPressed = currentPlayer.getNumberOfUnplacedArmies();
		int territoryArmyCountBeforeTerritoryPressed = clickedTerritory.getNumberOfArmies();
		turnController.territoryPressed("China", true);

		assertEquals(territoryArmyCountBeforeTerritoryPressed + 1, clickedTerritory.getNumberOfArmies());
		assertEquals(numberOfUnplacedArmiesBeforeTerritoryPressed - 1, currentPlayer.getNumberOfUnplacedArmies());
	}

	@Test
	public void testNextPhaseTrading5Cards() {
		mockedGameView.updateErrorLabel("You must trade at least 1 more set in");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.gamePhase = TurnController.GamePhase.TRADING;
		giveCurrentPlayerNCards(5, turnController);
		turnController.nextPhase();

		assertEquals(TurnController.GamePhase.TRADING, turnController.gamePhase);
		EasyMock.verify(mockedGameView);
	}

	private void giveCurrentPlayerNCards(int n, TurnController turnController) {
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		for(int i = 0; i < n; i++) {
			currentPlayer.addCard(new Card("Fake territory", "Fake troop type"));
		}
	}

	@Test
	public void testNextPhaseTrading0Cards() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.gamePhase = TurnController.GamePhase.TRADING;
		giveCurrentPlayerNCards(0, turnController);
		turnController.nextPhase();

		assertEquals(TurnController.GamePhase.PLACING, turnController.gamePhase);
	}

	@Test
	public void testNextPhaseTrading1Cards() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.gamePhase = TurnController.GamePhase.TRADING;
		giveCurrentPlayerNCards(1, turnController);
		turnController.nextPhase();

		assertEquals(TurnController.GamePhase.PLACING, turnController.gamePhase);
	}

	@Test
	public void testNextPhasePlacingUnplacedArmiesNotZero() {
		mockedGameView.updateErrorLabel("You must place all troops");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.playerModels.get(turnController.currentPlayer).setNumberOfUnplacedArmies(3);
		turnController.gamePhase = TurnController.GamePhase.PLACING;
		turnController.nextPhase();

		assertEquals(TurnController.GamePhase.PLACING, turnController.gamePhase);
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testNextPhasePlacingUnplacedArmiesZero() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.playerModels.get(turnController.currentPlayer).setNumberOfUnplacedArmies(0);
		turnController.gamePhase = TurnController.GamePhase.PLACING;
		turnController.nextPhase();

		assertEquals(TurnController.GamePhase.ATTACKING, turnController.gamePhase);
	}

	@Test
	public void testNextPhaseAttackingGetsCardFalse() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.nextPhase();

		assertEquals(TurnController.GamePhase.REINFORCING, turnController.gamePhase);
	}

	@Test
	public void testNextPhaseAttackingGetsCardTrueDeckSize0() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);

		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.getsCard = true;
		int currentPlayerStartingCards = currentPlayer.getCardCount();
		turnController.territories.setDeck(new ArrayList<>());
		int startingDeckSize = turnController.territories.deck.size();
		turnController.nextPhase();
		assertEquals(currentPlayerStartingCards, currentPlayer.getCardCount());
		assertEquals(startingDeckSize, turnController.territories.deck.size());
	}

	@Test
	public void testNextPhaseAttackingGetsCardTrueDeckSize1() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		ArrayList<Card> deck = new ArrayList<>();
		deck.add(new Card("Alberta", "Artillery"));
		turnController.territories.setDeck(deck);
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.getsCard = true;
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		int currentPlayerStartingCards = currentPlayer.getCardCount();
		int startingDeckSize = turnController.territories.deck.size();
		turnController.nextPhase();
		
		assertEquals(currentPlayerStartingCards + 1, currentPlayer.getCardCount());
		assertEquals(startingDeckSize - 1, turnController.territories.deck.size());
	}

	@Test
	public void testNextPhaseAttackingGetsCardTrueDeckSize42() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		ArrayList<Card> deck = new ArrayList<>();
		for(int i = 0; i < 42; i++) {
			deck.add(new Card(Integer.toString(i), "Artillery"));
		}
		turnController.territories.setDeck(deck);
		turnController.gamePhase = TurnController.GamePhase.ATTACKING;
		turnController.getsCard = true;
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		int currentPlayerStartingCards = currentPlayer.getCardCount();
		int startingDeckSize = turnController.territories.deck.size();
		turnController.nextPhase();

		assertEquals(currentPlayerStartingCards + 1, currentPlayer.getCardCount());
		assertEquals(startingDeckSize - 1, turnController.territories.deck.size());
	}

	@Test
	public void testNextPhaseReinforcingUnplacedArmiesNotZero() {
		mockedGameView.updateErrorLabel("You must place all troops");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.playerModels.get(turnController.currentPlayer).setNumberOfUnplacedArmies(3);
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		turnController.nextPhase();

		assertEquals(TurnController.GamePhase.REINFORCING, turnController.gamePhase);
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testNextPhaseReinforcingUnplacedArmiesZero() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territories.allTerritories.get(5).setOwner(turnController.playerModels.get(1).getColor());
		turnController.playerModels.get(turnController.currentPlayer).setNumberOfUnplacedArmies(0);
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		turnController.nextPhase();

		assertEquals(TurnController.GamePhase.TRADING, turnController.gamePhase);
		assertEquals(1, turnController.currentPlayer);
	}

	@Test
	public void testNextPhaseReinforcingTwoPlayerGamePlay0Turn() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.currentPlayer = 0;
		turnController.playerModels.get(turnController.currentPlayer).setNumberOfUnplacedArmies(0);
		turnController.territories.allTerritories.get(5).setOwner(turnController.playerModels.get(1).getColor());
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		turnController.playerModels.set(2, new PlayerModel(0, Color.DARK_GRAY));
		turnController.nextPhase();

		assertEquals(TurnController.GamePhase.TRADING, turnController.gamePhase);
		assertEquals(1, turnController.currentPlayer);
	}

	@Test
	public void testNextPhaseReinforcingTwoPlayerGamePlay1Turn() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.currentPlayer = 1;
		turnController.territories.allTerritories.get(5).setOwner(turnController.playerModels.get(0).getColor());
		turnController.playerModels.get(turnController.currentPlayer).setNumberOfUnplacedArmies(0);
		turnController.gamePhase = TurnController.GamePhase.REINFORCING;
		turnController.playerModels.set(2, new PlayerModel(0, Color.DARK_GRAY));
		turnController.nextPhase();

		assertEquals(TurnController.GamePhase.TRADING, turnController.gamePhase);
		assertEquals(0, turnController.currentPlayer);
	}

	@Test
	public void testNextPhasePlayerWithoutArmies() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.currentPlayer = 0;
		turnController.territories.allTerritories.get(5).setOwner(turnController.playerModels.get(2).getColor());
		turnController.incrementCurrentPlayer();
		Assert.assertEquals(2, turnController.currentPlayer);
	}

	@Test
	public void testIsOutAllPlayersHaveTerritories() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.currentPlayer = 0;
		turnController.territories.allTerritories.get(0).setOwner(turnController.playerModels.get(0).getColor());
		turnController.territories.allTerritories.get(1).setOwner(turnController.playerModels.get(1).getColor());
		turnController.territories.allTerritories.get(2).setOwner(turnController.playerModels.get(2).getColor());
		turnController.territories.allTerritories.get(3).setOwner(turnController.playerModels.get(3).getColor());
		turnController.territories.allTerritories.get(4).setOwner(turnController.playerModels.get(4).getColor());
		turnController.incrementCurrentPlayer();
		Assert.assertEquals(1, turnController.currentPlayer);
	}

	@Test
	public void testIsOutOnePlayerHasTerritories() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.currentPlayer = 0;
		turnController.territories.allTerritories.get(4).setOwner(turnController.playerModels.get(4).getColor());
		turnController.incrementCurrentPlayer();
		Assert.assertEquals(4, turnController.currentPlayer);
	}

	@Test
	public void testTradeInCardsZeroCards() {
		mockedGameView.updateErrorLabel("You do not have enough cards to trade");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		int numberOfUnplacedArmiesBeforeTrading = turnController.playerModels
				.get(turnController.currentPlayer).getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading, turnController.playerModels
				.get(turnController.currentPlayer).getNumberOfUnplacedArmies());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTradeInCardsTwoCards() {
		mockedGameView.updateErrorLabel("You do not have enough cards to trade");
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		currentPlayer.addCard(new Card("China", "Artillery"));
		currentPlayer.addCard(new Card("Russia", "Artillery"));
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(2, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTradeInCardsThreeCardsTwoOneSplit() {
		mockedGameView.updateErrorLabel("Your cards do not match the criteria to trade");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		currentPlayer.addCard(new Card("China", "Artillery"));
		currentPlayer.addCard(new Card("Russia", "Artillery"));
		currentPlayer.addCard(new Card("Greenland", "Infantry"));
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(3, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTradeInCardsThreeCardsInfantry() {
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		currentPlayer.addCard(new Card("China", "Infantry"));
		currentPlayer.addCard(new Card("Russia", "Infantry"));
		currentPlayer.addCard(new Card("Egypt", "Infantry"));
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading + 4, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(0, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTradeInCardsThreeCardsCavalry() {
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		currentPlayer.addCard(new Card("China", "Cavalry"));
		currentPlayer.addCard(new Card("Russia", "Cavalry"));
		currentPlayer.addCard(new Card("Egypt", "Cavalry"));
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading + 4, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(0, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTradeInCardsThreeCardsArtillery() {
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		currentPlayer.addCard(new Card("China", "Artillery"));
		currentPlayer.addCard(new Card("Russia", "Artillery"));
		currentPlayer.addCard(new Card("Egypt", "Artillery"));
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading + 4, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(0, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTradeInCardsThreeCardsDifferentType() {
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		currentPlayer.addCard(new Card("China", "Artillery"));
		currentPlayer.addCard(new Card("Russia", "Infantry"));
		currentPlayer.addCard(new Card("Egypt", "Cavalry"));
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading + 4, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(0, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTradeInCardsWholeDeckOfCards() {
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		ArrayList<Card> cards= createCards();
		for(Card card : cards) {
			currentPlayer.addCard(card);
		}
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading + 4, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(cards.size() - 3, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTradeInCardsCurrentPlayerTurnsInSecondSetOfCards() {
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		turnController.setsTurnedIn = 1;
		currentPlayer.addCard(new Card("China", "Artillery"));
		currentPlayer.addCard(new Card("Russia", "Artillery"));
		currentPlayer.addCard(new Card("Egypt", "Artillery"));
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading + 6, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(0, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTradeInCardsCurrentPlayerTurnsInFifthSetOfCards() {
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		turnController.setsTurnedIn = 4;
		currentPlayer.addCard(new Card("China", "Artillery"));
		currentPlayer.addCard(new Card("Russia", "Artillery"));
		currentPlayer.addCard(new Card("Egypt", "Artillery"));
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading + 12, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(0, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}
	
	@Test
	public void testTradeInCardsCurrentPlayerTurnsInSixthSetOfCards() {
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		turnController.setsTurnedIn = 5;
		currentPlayer.addCard(new Card("China", "Artillery"));
		currentPlayer.addCard(new Card("Russia", "Artillery"));
		currentPlayer.addCard(new Card("Egypt", "Artillery"));
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading + 15, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(0, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testTradeInCardsThreeCardsOwnsATerritory() {
		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		PlayerModel currentPlayer = turnController.playerModels.get(turnController.currentPlayer);
		currentPlayer.addCard(new Card("China", "Artillery"));
		currentPlayer.addCard(new Card("Russia", "Artillery"));
		currentPlayer.addCard(new Card("Egypt", "Artillery"));
		mockedTerritories.getTerritoryByName("China").setOwner(currentPlayer.getColor());
		int numberOfArmiesOnTerritoryBeforeTrading = mockedTerritories.getTerritoryByName("China").getNumberOfArmies();
		int numberOfUnplacedArmiesBeforeTrading = currentPlayer.getNumberOfUnplacedArmies();
		turnController.tradeInCards();

		assertEquals(numberOfUnplacedArmiesBeforeTrading + 4, currentPlayer.getNumberOfUnplacedArmies());
		assertEquals(numberOfArmiesOnTerritoryBeforeTrading + 2,
				mockedTerritories.getTerritoryByName("China").getNumberOfArmies());
		assertEquals(0, currentPlayer.getCardCount());
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testCalculateNumberOfArmiesExactly9Territories(){
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.playerModels.get(0).setNumberOfUnplacedArmies(7);
		int actual = turnController.calculateNumberOfArmies();
		assertEquals(3, actual);
	}

	@Test
	public void testCalculateNumberOfArmiesLessThan9Territories(){
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		createContinentMap();
		turnController.playerModels.get(0).setNumberOfUnplacedArmies(7);
		int actual = turnController.calculateNumberOfArmies();
		assertEquals(3, actual);
	}

	@Test
	public void testCalculateNumberOfArmiesMoreThan9DivisibleBy3(){
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		createContinentMap();
		for(int i = 0; i < 12; i++){
			mockedTerritories.allTerritories.get(i).setOwner(Color.RED);
		}
		int actual = turnController.calculateNumberOfArmies();
		assertEquals(4, actual - 24);
	}

	@Test
	public void testCalculateNumberOfArmiesMoreThan9NotDivisibleBy3(){
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		//turnController.playerModels.get(0).setNumberOfUnplacedArmies(10);
		createContinentMap();
		for(int i = 0; i < 10; i++){
			mockedTerritories.allTerritories.get(i).setOwner(Color.RED);
		}
		int actual = turnController.calculateNumberOfArmies();
		assertEquals(3, actual - 14);
	}

	@Test
	public void testDetermineWinnerAttackerWinsNotLastTerritory() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.attackerRolls = new ArrayList<>();
		turnController.defenderRolls = new ArrayList<>();
		turnController.attackerRolls.add(5);
		turnController.defenderRolls.add(3);
		turnController.attackerRolls.add(4);
		turnController.defenderRolls.add(3);
		turnController.attackerRolls.add(6);
		turnController.currentDefender = "India";
		turnController.currentAttacker = "China";

		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(2);
		turnController.territories.getTerritoryByName("China").setOwner(Color.RED);
		turnController.territories.getTerritoryByName("India").changeArmyAmountBy(2);
		turnController.territories.getTerritoryByName("India").setOwner(Color.GREEN);

		turnController.determineBattleWinner();

		assertEquals(turnController.territories.getTerritoryByName("India").getOwner(), Color.RED);
	}

	@Test
	public void testDetermineWinnerAttackerWinsLastTerritory() {
		mockedGameView.showWinMessage(1);

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.attackerRolls = new ArrayList<>();
		turnController.defenderRolls = new ArrayList<>();
		turnController.attackerRolls.add(5);
		turnController.defenderRolls.add(3);
		turnController.attackerRolls.add(4);
		turnController.defenderRolls.add(3);
		turnController.attackerRolls.add(6);
		turnController.currentDefender = "India";
		turnController.currentAttacker = "China";
		for(TerritoryModel territory : turnController.territories.allTerritories) {
			territory.setOwner(Color.RED);
		}

		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(2);
		turnController.territories.getTerritoryByName("China").setOwner(Color.RED);
		turnController.territories.getTerritoryByName("India").changeArmyAmountBy(2);
		turnController.territories.getTerritoryByName("India").setOwner(Color.GREEN);

		turnController.determineBattleWinner();

		assertEquals(turnController.territories.getTerritoryByName("India").getOwner(), Color.RED);
		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testDetermineWinnerAttackerLoses() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.attackerRolls = new ArrayList<>();
		turnController.defenderRolls = new ArrayList<>();
		turnController.attackerRolls.add(1);
		turnController.defenderRolls.add(3);
		turnController.attackerRolls.add(3);
		turnController.defenderRolls.add(6);
		turnController.attackerRolls.add(6);
		turnController.currentDefender = "India";
		turnController.currentAttacker = "China";

		turnController.territories.getTerritoryByName("China").changeArmyAmountBy(2);
		turnController.territories.getTerritoryByName("China").setOwner(Color.RED);
		turnController.territories.getTerritoryByName("India").changeArmyAmountBy(2);
		turnController.territories.getTerritoryByName("India").setOwner(Color.GREEN);

		turnController.determineBattleWinner();

		assertEquals(turnController.territories.getTerritoryByName("India").getOwner(), Color.GREEN);
	}

	@Test
	public void testMoveTroops0Troops() {
		testMoveTroopsNTroops(0, 50);
	}

	@Test
	public void testMoveTroops1Troop() {
		testMoveTroopsNTroops(1, 50);
	}

	@Test
	public void testMoveTroops1LessTroopThanOnStartingTerritory() {
		testMoveTroopsNTroops(50, 51);
	}

	private void testMoveTroopsNTroops(int n, int startingTerritoryStartingArmyAmount) {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		TerritoryModel startingTerritory = turnController.territories.getTerritoryByName("China");
		TerritoryModel endingTerritory = turnController.territories.getTerritoryByName("Russia");
		startingTerritory.changeArmyAmountBy(startingTerritoryStartingArmyAmount - startingTerritory.getNumberOfArmies());
		endingTerritory.changeArmyAmountBy(50);
		turnController.currentAttacker = startingTerritory.getName();
		turnController.currentDefender = endingTerritory.getName();
		int endingTerritoryStartingArmyAmount = endingTerritory.getNumberOfArmies();
		turnController.moveTroops(n);

		assertEquals(startingTerritoryStartingArmyAmount - n, startingTerritory.getNumberOfArmies());
		assertEquals(endingTerritoryStartingArmyAmount + n, endingTerritory.getNumberOfArmies());
	}

	@Test
	public void testMoveTroopsSameNumberOfTroopsAsStartingTerritory() {
		mockedGameView.updateErrorLabel("You must leave at least one troop on territories you own");

		EasyMock.replay(mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		TerritoryModel startingTerritory = turnController.territories.getTerritoryByName("China");
		TerritoryModel endingTerritory = turnController.territories.getTerritoryByName("Russia");
		int startingTerritoryStartingArmyAmount = 50;
		startingTerritory.changeArmyAmountBy(startingTerritoryStartingArmyAmount - startingTerritory.getNumberOfArmies());
		endingTerritory.changeArmyAmountBy(50);
		turnController.currentAttacker = startingTerritory.getName();
		turnController.currentDefender = endingTerritory.getName();
		int endingTerritoryStartingArmyAmount = endingTerritory.getNumberOfArmies();
		turnController.moveTroops(50);

		assertEquals(startingTerritoryStartingArmyAmount, startingTerritory.getNumberOfArmies());
		assertEquals(endingTerritoryStartingArmyAmount, endingTerritory.getNumberOfArmies());

		EasyMock.verify(mockedGameView);
	}

	@Test
	public void testHasWonFalse(){
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		for(int i = 0; i < 12; i++){
			mockedTerritories.allTerritories.get(i).setOwner(Color.RED);
		}
		boolean result = turnController.hasWon();
		Assert.assertTrue(result);
	}

	@Test
	public void testHasWonTrue(){
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		for(int i = 0; i < 9; i++){
			mockedTerritories.allTerritories.get(i).setOwner(Color.RED);
		}
		boolean result = turnController.hasWon();
		Assert.assertFalse(result);
	}

	@Test
	public void testPlayerRollsAttacker3RollsLeft() {
		EasyMock.expect(mockedDie.roll()).andReturn(5);

		EasyMock.replay(mockedDie);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.attackerRolls = new ArrayList<Integer>();
		turnController.attackerRollCount = 3;
		turnController.playerRolls();

		assertEquals(2, turnController.attackerRollCount);
		assertEquals(5, (int) turnController.attackerRolls.get(0));
		EasyMock.verify(mockedDie);
	}

	@Test
	public void testPlayerRollsAttacker1RollLeft() {
		EasyMock.expect(mockedDie.roll()).andReturn(5);
		mockedGameView.updateStateToDefenderRoll();

		EasyMock.replay(mockedDie, mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.attackerRolls = new ArrayList<Integer>();
		turnController.attackerRolls.add(3);
		turnController.attackerRolls.add(1);
		turnController.attackerRollCount = 1;
		turnController.playerRolls();

		assertEquals(0, turnController.attackerRollCount);
		assertEquals(5, (int) turnController.attackerRolls.get(2));
		EasyMock.verify(mockedDie, mockedGameView);
	}

	@Test
	public void testPlayerRollsDefender2RollsLeft() {
		EasyMock.expect(mockedDie.roll()).andReturn(5);

		EasyMock.replay(mockedDie);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.attackerRollCount = 0;
		turnController.defenderRollCount = 2;
		turnController.defenderRolls = new ArrayList<>();
		turnController.playerRolls();

		assertEquals(1, turnController.defenderRollCount);
		assertEquals(5, (int) turnController.defenderRolls.get(0));
		EasyMock.verify(mockedDie);
	}

	@Test
	public void testPlayerRollsDefender1RollsLeft() {
		EasyMock.expect(mockedDie.roll()).andReturn(5);
		mockedGameView.updateCurrentAttackingDisplay(0);

		EasyMock.replay(mockedDie, mockedGameView);
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.attackerRollCount = 0;
		turnController.defenderRollCount = 1;
		turnController.defenderRolls = new ArrayList<>();
		turnController.attackerRolls = new ArrayList<>();
		turnController.attackerRolls.add(3);
		turnController.attackerRolls.add(6);
		turnController.currentDefender = "India";
		turnController.currentAttacker = "China";
		turnController.defenderRolls.add(3);
		turnController.playerRolls();

		assertEquals(0, turnController.defenderRollCount);
		assertEquals(2, turnController.defenderRolls.size());
		EasyMock.verify(mockedDie, mockedGameView);
	}

	@Test
	public void testDetermineNumberOfRollsOneDefendingArmy() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.territories.getTerritoryByName("India").changeArmyAmountBy(1);
		turnController.currentDefender = "India";

		turnController.determineNumberOfRolls(3);

		assertEquals(1, turnController.defenderRollCount);
	}

	@Test
	public void testDetermineNumberOfRollsAttackerChooses1() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.currentDefender = "India";

		turnController.determineNumberOfRolls(1);

		assertEquals(1, turnController.defenderRollCount);
	}

	@Test
	public void testDetermineNumberOfRollsAttackerChooses3() {
		TurnController turnController = new TurnController(mockedGameView, mockedTerritories, playerModels, 0, mockedDie);
		turnController.currentDefender = "India";

		turnController.determineNumberOfRolls(3);

		assertEquals(2, turnController.defenderRollCount);
	}

}