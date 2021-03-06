package risk.controller;

import java.awt.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

import risk.model.*;
import risk.view.GameView;

public class SetupController implements Runnable, GameViewObserver {
	static final int twoPlayersStartingInfantry = 40;
	static final int threePlayersStartingInfantry = 35;
	static final int fourPlayersStartingInfantry = 30;
	static final int fivePlayersStartingInfantry = 25;

	private int numberOfPlayers;
	int currentPlayer = 0;
	private ResourceBundle messages;

	int[] startingRolls;

	public enum GameState {
		ROLLING,
		CLAIMING,
		PLAYING
	}
	GameState gameState;

	ArrayList<PlayerModel> playerModels = new ArrayList<>();
	TerritoryMapController territories;
	PlayerModel neutralPlayer;
	int twoPlayerGameArmiesPlacedOnTurn = 0;

	private SixSidedDie die;

	private GameView gameView;

	public SetupController(GameView gameView, SixSidedDie die, TerritoryMapController territoryMapController) {
		this.gameView = gameView;
		this.numberOfPlayers = gameView.getNumberOfPlayers();
		this.die = die;
		this.territories = territoryMapController;
		startingRolls = new int[numberOfPlayers];
		messages = StaticResourceBundle.getResourceBundle();
		initializePlayers();
	}

	void initializePlayers() {
		for(int i = 0; i < numberOfPlayers; i++) {
			playerModels.add(new PlayerModel(getStartingInfantryFor(numberOfPlayers),
								ColorFactory.createColor("Player" + (i+1))));
		}
	}

	private int getStartingInfantryFor(int numberOfPlayers) {
		if(numberOfPlayers == 2) {
			return twoPlayersStartingInfantry;
		} else if(numberOfPlayers == 3) {
			return threePlayersStartingInfantry;
		} else if(numberOfPlayers == 4) {
			return fourPlayersStartingInfantry;
		} else {
			return fivePlayersStartingInfantry;
		}
	}

	@Override
	public void run() {
		gameView.addObserver(this);
		currentPlayer = 0;
		setupPhase();
	}
	
	public ArrayList<Integer> playerRolls() {
		int rollResult = die.roll();
		startingRolls[currentPlayer] = rollResult;
		incrementCurrentPlayer();
		if(currentPlayer==0) {
			determineFirstPlayer();
		} else {
			gameView.updateCurrentPlayerRollingLabel(currentPlayer + 1);
		}

		ArrayList<Integer> totalRolls = new ArrayList<Integer>();
		totalRolls.add(rollResult);
		return totalRolls;
	}

	private void setupPhase() {
		if(isTwoPlayerGame()) {
			twoPlayerSetupPhase();
		}
		addWildCardsToController();
		gameState=GameState.ROLLING;
		gameView.updateCurrentPlayerRollingLabel(1);
	}

	private void addWildCardsToController() {
		for(int i = 0; i < 2; i++){
			territories.addCard(new Card("", "Wild"));
		}
	}

	void twoPlayerSetupPhase() {
		neutralPlayer = new PlayerModel(getStartingInfantryFor(2), Color.DARK_GRAY);
		playerModels.add(neutralPlayer);
		territories.shuffleDeck();
		dealCards();
		placeArmies();
	}

	private void placeArmies(){
		for(int i = 0; i < playerModels.size(); i++){
			currentPlayer = i;
			CardManager cards = playerModels.get(i).getCards();
			for(Card card : cards.getTotalCards()){
				claimTerritory(card.getTerritoryName());
			}
		}
		currentPlayer = 0;
	}

	private void dealCards(){
		for(int i = 0; i < playerModels.size(); i++){
			int start = i * (territories.deck.size() / 3);
			for(int j = start; j < (start + territories.deck.size() / 3); j++){
				playerModels.get(i).addCard(territories.deck.get(j));
			}
		}
	}

	public void replaceCards(){
		for(int i = 0; i < playerModels.size(); i++){
			int start = i * (territories.deck.size() / 3);
			for(int j = start; j < start + territories.deck.size() / 3; j++){
				playerModels.get(i).removeCard(territories.deck.get(j));
			}
		}
	}

	void determineFirstPlayer() {
		if(isTwoPlayerGame()) {
			replaceCards();
		}
		int firstPlayer = 0;
		int maxRoll = startingRolls[0];
		for(int k=1; k<numberOfPlayers; k++) {
			if(startingRolls[k]>maxRoll) {
				maxRoll = startingRolls[k];
				firstPlayer=k;
			}
		}
		gameState = GameState.CLAIMING;
		currentPlayer = firstPlayer;
		if(isTwoPlayerGame()) {
			gameView.updateCurrentPlacingDisplay(currentPlayer,
					playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
		}else{
			gameView.updateCurrentPlayerClaimingLabel(firstPlayer+1);
		}
	}
	
	public void territoryPressed(String territoryName, boolean placingTroop) {
		if(gameState==GameState.CLAIMING) {
			TerritoryModel territory = territories.getTerritoryByName(territoryName);
			if(territories.areThereUnclaimedTerritories()){
				stillUnclaimedTerritoriesClaiming(territoryName, territory);
			} else if (doPlayersHaveUnplacedArmies()) {
				allTerritoriesClaimedStillUnplacedArmies(territoryName, territory);
			} else {
				gameState = GameState.PLAYING;
				new TurnController(gameView, territories, playerModels, currentPlayer, die);
				gameView.removeObserver(this);
			}
		}
	}

	private void stillUnclaimedTerritoriesClaiming(String territoryName, TerritoryModel territory) {
		if(!territory.isOwned()) {
			claimTerritory(territoryName);
			incrementCurrentPlayer();
			if(territories.areThereUnclaimedTerritories()) {
				gameView.updateCurrentPlayerClaimingLabel(currentPlayer+1);
			} else {
				gameView.updateCurrentPlacingDisplay(currentPlayer,
						playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
			}
		}
	}

	private void allTerritoriesClaimedStillUnplacedArmies(String territoryName, TerritoryModel territory) {
		if(territory.getOwner() == playerModels.get(currentPlayer).getColor()){
			if(isTwoPlayerGame()){
				if(twoPlayerGameArmiesPlacedOnTurn < 1) {
					twoPlayerAddFirstArmy(territoryName);
				}else if(twoPlayerGameArmiesPlacedOnTurn == 1){
					twoPlayerAddArmy(territoryName);
				}else {
					gameView.updateErrorLabel(messages.getString("neutralWarning"));
				}
			} else {
				addArmy(territoryName);
			}
		} else if (isTwoPlayerGame() && territory.getOwner().equals(neutralPlayer.getColor())
				&& twoPlayerGameArmiesPlacedOnTurn == 2){
			twoPlayerAddArmyToNeutral(territoryName);
		}
	}

	private void twoPlayerAddFirstArmy(String territoryName) {
		addOneArmyToTerritory(territoryName);
		twoPlayerGameArmiesPlacedOnTurn++;
		gameView.updateCurrentPlacingDisplay(currentPlayer,
				playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
	}

	private void twoPlayerAddArmy(String territoryName) {
		addOneArmyToTerritory(territoryName);
		twoPlayerGameArmiesPlacedOnTurn++;
		gameView.updatePlaceNeutralArmy();
	}

	private void addArmy(String territoryName) {
		addOneArmyToTerritory(territoryName);
		incrementCurrentPlayer();
		gameView.updateCurrentPlacingDisplay(currentPlayer,
				playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
	}

	private void twoPlayerAddArmyToNeutral(String territoryName) {
		addOneArmyToTerritory(territoryName);
		twoPlayerGameArmiesPlacedOnTurn = 0;
		incrementCurrentPlayer();
		gameView.updateCurrentPlacingDisplay(currentPlayer,
				playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
	}

	private boolean isTwoPlayerGame() {
		return numberOfPlayers == 2;
	}

	public void nextPhase() {}
	
	public void determineNumberOfRolls(int attackAmount) {}
	
	public void tradeInCards() {}
	
	public void moveTroops(int troopCountToMove) {}

	private void claimTerritory(String territoryName) {
		Color playerColor = playerModels.get(currentPlayer).getColor();
		territories.setTerritoryOwnerByName(territoryName, playerColor);
		gameView.updateTerritoryOwnerDisplay(territoryName, playerColor);
		addOneArmyToTerritory(territoryName);
	}

	private void addOneArmyToTerritory(String territoryName) {
		if(twoPlayerGameArmiesPlacedOnTurn == 2){
			playerModels.get(playerModels.size() - 1).placeArmy();
		} else {
			playerModels.get(currentPlayer).placeArmy();
		}
		territories.changeTerritoryArmyAmountBy(territoryName, 1);
		gameView.updateTerritoryArmyCountDisplay(territoryName,
				territories.getTerritoryByName(territoryName).getNumberOfArmies());
	}

	boolean doPlayersHaveUnplacedArmies() {
		for(PlayerModel player : playerModels) {
			if(player.getNumberOfUnplacedArmies() > 0) {
				return true;
			}
		}
		return false;
	}

	void incrementCurrentPlayer() {
		if(currentPlayer == numberOfPlayers - 1) {
			currentPlayer = 0;
		} else {
			currentPlayer++;
		}
	}
}
