package risk.controller;

import static org.junit.Assert.assertEquals;

import java.awt.*;
import java.util.ArrayList;
import org.easymock.*;
import org.junit.*;
import risk.model.PlayerModel;
import risk.model.SixSidedDie;
import risk.model.TerritoryModel;
import risk.view.GameView;

/**
 * BVA: initializePlayers
 * CASE         | Expected
 * TwoPlayers   | playerModel has three players with 40 armies to place colors: Player1, Player2
 * ThreePlayers | playerModel has three players with 35 armies to place colors: Player1, Player2, Player3
 * FourPlayers  | playerModel has four players with 30 armies to place colors: Player1, Player2, Player3, Player4
 * FivePlayers  | playerModel has five players with 25 armies to place colors: Player1, Player2, Player3, Player4, Player5
 *
 * BVA: determineFirstPlayer
 * CASE                         | EXPECTED
 * TwoPlayersNonMatchingRolls   | PlayerWithHighestRoll
 * TwoPlayersMatchingRolls      | PlayerWhoRolledHighestFirst
 * FivePlayersNonMatchingRolls  | PlayerWithHighestRoll
 * FivePlayersMatchingRolls     | PlayerWhoRolledHighestFirst
 *
 * BVA: doPlayersHaveUnplacedArmies
 * CASE                         | EXPECTED
 * TwoPlayersUnplacedArmies     | True
 * TwoPlayersNoUnplacedArmies   | False
 * FivePlayersUnplacedArmies    | True
 * FivePlayersNoUnplacedArmies  | False
 *
 * BVA: territoryPressed
 * CASE
 * 		| EXPECTED
 * Unclaimed territories; pressed unclaimed
 * 		| Pressed becomes claimed by current player. One army is placed.
 * Unclaimed territories; pressed claimed
 * 		| Pressed still owned by original owner. No armies are placed.
 * No unclaimed territories; pressed owned by current player
 * 		| On army placed on pressed.
 * No unclaimed territories; pressed owned by different player
 * 		| No armies are placed.
 * No unclaimed territories; no armies to place
 * 		| gameState set to PLAYING.
 *
 * BVA: incrementPlayer
 * CASE                         | EXPECTED
 * current player 0             | current player 1
 * current player last player   | current player 0
 */

public class SetupControllerTest {
	@Test
	public void testInitializePlayersTwoPlayers() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(2);
		EasyMock.replay(gameViewMock);

		ArrayList<PlayerModel> expectedPlayerModels = new ArrayList<>();
		expectedPlayerModels.add(new PlayerModel(40, ColorFactory.createColor("Player1")));
		expectedPlayerModels.add(new PlayerModel(40, ColorFactory.createColor("Player2")));

		SetupController controller = new SetupController(gameViewMock, null, null);
		assetPlayerModelArraysEqual(controller.playerModels, expectedPlayerModels);
	}

	private void assetPlayerModelArraysEqual(ArrayList<PlayerModel> playerModels,
												ArrayList<PlayerModel> expectedPlayerModels) {
		Assert.assertEquals(playerModels.size(), expectedPlayerModels.size());
		for(int i = 0; i < playerModels.size(); i++) {
			assertEquals(playerModels.get(i).getNumberOfUnplacedArmies(),
								expectedPlayerModels.get(i).getNumberOfUnplacedArmies());
			assertEquals(playerModels.get(i).getColor(), expectedPlayerModels.get(i).getColor());
		}
	}

	@Test
	public void testInitializePlayersThreePlayers() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);
		EasyMock.replay(gameViewMock);
		ArrayList<PlayerModel> expectedPlayerModels = new ArrayList<>();
		expectedPlayerModels.add(new PlayerModel(35, ColorFactory.createColor("Player1")));
		expectedPlayerModels.add(new PlayerModel(35, ColorFactory.createColor("Player2")));
		expectedPlayerModels.add(new PlayerModel(35, ColorFactory.createColor("Player3")));

		SetupController controller = new SetupController(gameViewMock, null, null);
		assetPlayerModelArraysEqual(controller.playerModels, expectedPlayerModels);
	}

	@Test
	public void testInitializePlayersFourPlayers() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(4);
		EasyMock.replay(gameViewMock);

		ArrayList<PlayerModel> expectedPlayerModels = new ArrayList<>();
		expectedPlayerModels.add(new PlayerModel(30, ColorFactory.createColor("Player1")));
		expectedPlayerModels.add(new PlayerModel(30, ColorFactory.createColor("Player2")));
		expectedPlayerModels.add(new PlayerModel(30, ColorFactory.createColor("Player3")));
		expectedPlayerModels.add(new PlayerModel(30, ColorFactory.createColor("Player4")));

		SetupController controller = new SetupController(gameViewMock, null, null);
		assetPlayerModelArraysEqual(controller.playerModels, expectedPlayerModels);
	}

	@Test
	public void testInitializePlayersFivePlayers() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(5);
		EasyMock.replay(gameViewMock);

		ArrayList<PlayerModel> expectedPlayerModels = new ArrayList<>();
		expectedPlayerModels.add(new PlayerModel(25, ColorFactory.createColor("Player1")));
		expectedPlayerModels.add(new PlayerModel(25, ColorFactory.createColor("Player2")));
		expectedPlayerModels.add(new PlayerModel(25, ColorFactory.createColor("Player3")));
		expectedPlayerModels.add(new PlayerModel(25, ColorFactory.createColor("Player4")));
		expectedPlayerModels.add(new PlayerModel(25, ColorFactory.createColor("Player5")));

		SetupController controller = new SetupController(gameViewMock, null, null);
		assetPlayerModelArraysEqual(controller.playerModels, expectedPlayerModels);
	}


	@Test
	public void testDetermineFirstPlayerFivePlayersNonMatchingRolls(){
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);
		gameViewMock.updateCurrentPlayerClaimingLabel(3);
		EasyMock.replay(gameViewMock);

		SetupController controller = new SetupController(gameViewMock, null, null);
		controller.startingRolls = new int[5];
		controller.startingRolls[0] = 1;
		controller.startingRolls[1] = 2;
		controller.startingRolls[2] = 5;
		controller.startingRolls[3] = 3;
		controller.startingRolls[4] = 4;

		controller.determineFirstPlayer();
		EasyMock.verify(gameViewMock);
	}

	@Test
	public void testDetermineFirstPlayerFivePlayersMatchingRolls(){
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(1);
		gameViewMock.updateCurrentPlayerClaimingLabel(1);
		EasyMock.replay(gameViewMock);

		SetupController controller = new SetupController(gameViewMock, null, null);
		controller.startingRolls = new int[5];
		controller.startingRolls[0] = 5;
		controller.startingRolls[1] = 2;
		controller.startingRolls[2] = 5;
		controller.startingRolls[3] = 5;
		controller.startingRolls[4] = 4;

		controller.determineFirstPlayer();
		EasyMock.verify(gameViewMock);
	}

	@Test
	public void testDoPlayersHaveUnplacedArmiesTwoPlayersUnplacedArmies() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(2);
		EasyMock.replay(gameViewMock);
		SetupController controller = new SetupController(gameViewMock, null, null);
		controller.playerModels.get(0).setNumberOfUnplacedArmies(10);
		controller.playerModels.get(1).setNumberOfUnplacedArmies(0);

		Assert.assertTrue(controller.doPlayersHaveUnplacedArmies());
	}

	@Test
	public void testDoPlayersHaveUnplacedArmiesTwoPlayersNoUnplacedArmies() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(2);
		EasyMock.replay(gameViewMock);
		SetupController controller = new SetupController(gameViewMock, null, null);
		controller.playerModels.get(0).setNumberOfUnplacedArmies(0);
		controller.playerModels.get(1).setNumberOfUnplacedArmies(0);

		Assert.assertFalse(controller.doPlayersHaveUnplacedArmies());
	}

	@Test
	public void testDoPlayersHaveUnplacedArmiesFivePlayersUnplacedArmies() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(5);
		EasyMock.replay(gameViewMock);
		SetupController controller = new SetupController(gameViewMock, null, null);
		controller.playerModels.get(0).setNumberOfUnplacedArmies(10);
		controller.playerModels.get(1).setNumberOfUnplacedArmies(0);
		controller.playerModels.get(2).setNumberOfUnplacedArmies(1000);
		controller.playerModels.get(3).setNumberOfUnplacedArmies(50);
		controller.playerModels.get(4).setNumberOfUnplacedArmies(70);

		Assert.assertTrue(controller.doPlayersHaveUnplacedArmies());
	}

	@Test
	public void testDoPlayersHaveUnplacedArmiesFivePlayersNoUnplacedArmies() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(5);
		EasyMock.replay(gameViewMock);
		SetupController controller = new SetupController(gameViewMock, null, null);
		controller.playerModels.get(0).setNumberOfUnplacedArmies(0);
		controller.playerModels.get(1).setNumberOfUnplacedArmies(0);
		controller.playerModels.get(2).setNumberOfUnplacedArmies(0);
		controller.playerModels.get(3).setNumberOfUnplacedArmies(0);
		controller.playerModels.get(4).setNumberOfUnplacedArmies(0);

		Assert.assertFalse(controller.doPlayersHaveUnplacedArmies());
	}

	@Test
	public void testTerritoryUnclaimedTerritoriesPressedUnclaimed(){
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);

		TerritoryModel territoryModelMock = createTerritoryToBeClaimedByPlayer(1);
		TerritoryMapController territoryMapControllerMock = EasyMock.strictMock(TerritoryMapController.class);

		EasyMock.expect(territoryMapControllerMock.getTerritoryByName("China")).andReturn(territoryModelMock);
		EasyMock.expect(territoryMapControllerMock.areThereUnclaimedTerritories()).andReturn(true);

		this.subtestClaimUnclaimedTerritory(territoryMapControllerMock, gameViewMock, territoryModelMock);

		gameViewMock.updateTerritoryArmyCountDisplay("China", 2);
		EasyMock.expect(territoryMapControllerMock.areThereUnclaimedTerritories()).andReturn(true);

		this.subtestClaimTerritoryOnPress(territoryMapControllerMock, gameViewMock, territoryModelMock);

		EasyMock.verify(gameViewMock, territoryMapControllerMock, territoryModelMock);
	}

	private void subtestClaimUnclaimedTerritory(TerritoryMapController territoryMapControllerMock, GameView gameViewMock, TerritoryModel territoryModelMock){
		territoryMapControllerMock.setTerritoryOwnerByName("China", ColorFactory.createColor("Player1"));
		gameViewMock.updateTerritoryOwnerDisplay("China", ColorFactory.createColor("Player1"));
		territoryMapControllerMock.changeTerritoryArmyAmountBy("China", 1);
		EasyMock.expect(territoryMapControllerMock.getTerritoryByName("China")).andReturn(territoryModelMock);
		EasyMock.expect(territoryModelMock.getNumberOfArmies()).andReturn(2);
	}

	private void subtestClaimTerritoryOnPress(TerritoryMapController territoryMapControllerMock, GameView gameViewMock, TerritoryModel territoryModelMock){
		gameViewMock.updateCurrentPlayerClaimingLabel(2);
		EasyMock.replay(gameViewMock, territoryMapControllerMock, territoryModelMock);
		SetupController controller = new SetupController(gameViewMock, null, territoryMapControllerMock);
		controller.gameState = SetupController.GameState.CLAIMING;

		controller.territoryPressed("China", true);

		assertEquals(34, controller.playerModels.get(0).getNumberOfUnplacedArmies());
	}

	private TerritoryModel createTerritoryToBeClaimedByPlayer(int player) {
		TerritoryModel territoryModelMock = EasyMock.strictMock(TerritoryModel.class);
		EasyMock.expect(territoryModelMock.isOwned()).andReturn(false);
		return territoryModelMock;
	}

	@Test
	public void testTerritoryPressedClaimedWithUnclaimedTerritories(){
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);

		TerritoryModel territoryModelMock = EasyMock.strictMock(TerritoryModel.class);
		EasyMock.expect(territoryModelMock.isOwned()).andReturn(true);

		TerritoryMapController territoryMapControllerMock = EasyMock.strictMock(TerritoryMapController.class);
		EasyMock.expect(territoryMapControllerMock.getTerritoryByName("China")).andReturn(territoryModelMock);
		EasyMock.expect(territoryMapControllerMock.areThereUnclaimedTerritories()).andReturn(true);

		EasyMock.replay(gameViewMock, territoryMapControllerMock, territoryModelMock);
		SetupController controller = new SetupController(gameViewMock, null, territoryMapControllerMock);
		controller.gameState = SetupController.GameState.CLAIMING;

		controller.territoryPressed("China", true);

		assertEquals(35, controller.playerModels.get(0).getNumberOfUnplacedArmies());

		EasyMock.verify(gameViewMock, territoryMapControllerMock, territoryModelMock);
	}

	@Test
	public void testTerritoryPressedClaimedWithNoUnclaimedTerritories(){
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);
		TerritoryModel territoryModelMock = EasyMock.strictMock(TerritoryModel.class);
		TerritoryMapController territoryMapControllerMock = EasyMock.strictMock(TerritoryMapController.class);

		EasyMock.expect(territoryMapControllerMock.getTerritoryByName("China")).andReturn(territoryModelMock);
		EasyMock.expect(territoryMapControllerMock.areThereUnclaimedTerritories()).andReturn(false);
		EasyMock.expect(territoryModelMock.getOwner()).andReturn(ColorFactory.createColor("Player1"));

		this.subtestPressClaimedTerritoryChangeTerritoryAmount(territoryMapControllerMock, territoryModelMock);

		this.subtestPressClaimedTerritoryAction(territoryMapControllerMock, gameViewMock, territoryModelMock);

		EasyMock.verify(gameViewMock, territoryMapControllerMock, territoryModelMock);
	}

	private void subtestPressClaimedTerritoryChangeTerritoryAmount(TerritoryMapController territoryMapControllerMock, TerritoryModel territoryModelMock){
		territoryMapControllerMock.changeTerritoryArmyAmountBy("China", 1);
		EasyMock.expect(territoryMapControllerMock.getTerritoryByName("China")).andReturn(territoryModelMock);
		EasyMock.expect(territoryModelMock.getNumberOfArmies()).andReturn(2);
	}

	private void subtestPressClaimedTerritoryAction(TerritoryMapController territoryMapControllerMock, GameView gameViewMock, TerritoryModel territoryModelMock){
		gameViewMock.updateTerritoryArmyCountDisplay("China", 2);
		gameViewMock.updateCurrentPlacingDisplay(1, 35);

		EasyMock.replay(gameViewMock, territoryMapControllerMock, territoryModelMock);
		SetupController controller = new SetupController(gameViewMock, null, territoryMapControllerMock);
		controller.gameState = SetupController.GameState.CLAIMING;

		controller.territoryPressed("China", true);

		assertEquals(34, controller.playerModels.get(0).getNumberOfUnplacedArmies());
	}

	@Test
	public void testTerritoryPressedClaimedByDifferentPlayerNoUnclaimedTerritories(){
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);

		TerritoryModel territoryModelMock = EasyMock.strictMock(TerritoryModel.class);
		EasyMock.expect(territoryModelMock.getOwner()).andReturn(ColorFactory.createColor("Player2"));

		TerritoryMapController territoryMapControllerMock = EasyMock.strictMock(TerritoryMapController.class);
		EasyMock.expect(territoryMapControllerMock.getTerritoryByName("China")).andReturn(territoryModelMock);
		EasyMock.expect(territoryMapControllerMock.areThereUnclaimedTerritories()).andReturn(false);

		EasyMock.replay(gameViewMock, territoryMapControllerMock, territoryModelMock);
		SetupController controller = new SetupController(gameViewMock, null, territoryMapControllerMock);
		controller.gameState = SetupController.GameState.CLAIMING;

		controller.territoryPressed("China", true);

		assertEquals(35, controller.playerModels.get(0).getNumberOfUnplacedArmies());

		EasyMock.verify(gameViewMock, territoryMapControllerMock, territoryModelMock);
	}

	@Test
	public void testNoUnclaimedTerritoriesNoArmiesToPlace(){
		GameView gameViewMock = EasyMock.niceMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);

		TerritoryModel territoryModelMock = EasyMock.strictMock(TerritoryModel.class);

		TerritoryMapController territoryMapControllerMock = EasyMock.niceMock(TerritoryMapController.class);
		EasyMock.expect(territoryMapControllerMock.getTerritoryByName("China")).andReturn(territoryModelMock);
		EasyMock.expect(territoryMapControllerMock.areThereUnclaimedTerritories()).andReturn(false);

		EasyMock.replay(gameViewMock, territoryMapControllerMock, territoryModelMock);
		SetupController controller = new SetupController(gameViewMock, null, territoryMapControllerMock);

		for(int i = 0; i < 3; i++) {
			controller.playerModels.get(i).setNumberOfUnplacedArmies(0);
		}

		controller.gameState = SetupController.GameState.CLAIMING;

		controller.territoryPressed("China", true);

		assertEquals(SetupController.GameState.PLAYING, controller.gameState);

		EasyMock.verify(gameViewMock, territoryMapControllerMock, territoryModelMock);
	}

	@Test
	public void playerRollsCurrentPlayer0() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);
		gameViewMock.updateCurrentPlayerRollingLabel(2);

		SixSidedDie dieMock = EasyMock.strictMock(SixSidedDie.class);
		EasyMock.expect(dieMock.roll()).andReturn(3);
		EasyMock.replay(gameViewMock, dieMock);

		SetupController controller = new SetupController(gameViewMock, dieMock, null);
		controller.currentPlayer = 0;
		controller.startingRolls = new int[3];

		controller.playerRolls();

		assertEquals(3, controller.startingRolls[0]);
		assertEquals(1, controller.currentPlayer);
		EasyMock.verify(gameViewMock, dieMock);
	}

	@Test
	public void playerRollsCurrentPlayerLastPlayer() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);
		gameViewMock.updateCurrentPlayerClaimingLabel(2);

		SixSidedDie dieMock = EasyMock.strictMock(SixSidedDie.class);
		EasyMock.expect(dieMock.roll()).andReturn(3);
		EasyMock.replay(gameViewMock, dieMock);

		SetupController controller = new SetupController(gameViewMock, dieMock, null);
		controller.currentPlayer = 2;
		controller.startingRolls = new int[3];
		controller.startingRolls[0] = 1;
		controller.startingRolls[1] = 5;

		controller.playerRolls();

		assertEquals(3, controller.startingRolls[2]);
		assertEquals(1, controller.currentPlayer);
		EasyMock.verify(gameViewMock, dieMock);
	}

	@Test
	public void testIncrementPlayerCurrentPlayer0() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);

		EasyMock.replay(gameViewMock);

		SetupController controller = new SetupController(gameViewMock, null, null);
		controller.currentPlayer = 0;
		controller.incrementCurrentPlayer();

		assertEquals(1, controller.currentPlayer);
		EasyMock.verify(gameViewMock);
	}

	@Test
	public void testIncrementPlayerCurrentPlayerLastPlayer() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);

		EasyMock.replay(gameViewMock);

		SetupController controller = new SetupController(gameViewMock, null, null);
		controller.currentPlayer = 2;
		controller.incrementCurrentPlayer();

		assertEquals(0, controller.currentPlayer);
		EasyMock.verify(gameViewMock);
	}

	@Test
	public void testTerritoryPressedGameStateNotClaiming() {
		GameView gameViewMock = EasyMock.strictMock(GameView.class);
		EasyMock.expect(gameViewMock.getNumberOfPlayers()).andReturn(3);

		EasyMock.replay(gameViewMock);

		SetupController controller = new SetupController(gameViewMock, null, null);
		controller.gameState = SetupController.GameState.PLAYING;
		controller.territoryPressed("China", true);

		assertEquals(SetupController.GameState.PLAYING, controller.gameState);
		EasyMock.verify(gameViewMock);
	}
}