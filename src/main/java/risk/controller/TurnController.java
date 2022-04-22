package risk.controller;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import risk.model.*;
import risk.view.GameView;

public class TurnController implements GameViewObserver {
	TerritoryMapController territories;
	ArrayList<PlayerModel> playerModels;
	String currentAttacker;
	int currentPlayer = 0;
	ArrayList<Integer> attackerRolls;
	ArrayList<Integer> defenderRolls;
	String currentDefender;
	boolean getsCard = false;
	String territoryRemovedFrom;
	ResourceBundle messages;
	PhaseController phaseController;
	CardsController cardsController;

	int attackerRollCount = 0;
	int defenderRollCount = 0;
	
	int setsTurnedIn = 0;


	private SixSidedDie die;

	public enum GamePhase {
		PLACING,
		TRADING,
		ATTACKING,
		REINFORCING
	}
	GamePhase gamePhase;
	
	private GameView gameView;

	public TurnController(GameView gameView, TerritoryMapController territories,
							ArrayList<PlayerModel> playerModels, int currentPlayer, SixSidedDie die) {
		this.territories = territories;
		this.playerModels = playerModels;
		this.currentPlayer = currentPlayer;
		this.gameView = gameView;
		this.die = die;
		gamePhase = GamePhase.TRADING;
		messages = StaticResourceBundle.getResourceBundle();
		gameView.addObserver(this);
		phaseController = new PhaseController(this);
		cardsController = new CardsController(this);
		phaseController.startNextPhase();
	}

	void verifyPlayerOwnsTerritoryAndTerritoryExists(String territoryName){
		if(!territories.getTerritoryByName(territoryName).getOwner().equals(playerModels.get(currentPlayer).getColor())) {
			throw new IllegalArgumentException(messages.getString("unownedWarning"));
		}
	}

	void addArmyToTerritory(String territoryName) {
		verifyPlayerOwnsTerritoryAndTerritoryExists(territoryName);
		int currentNumberOfArmies  = playerModels.get(currentPlayer).getNumberOfUnplacedArmies();
		if(currentNumberOfArmies == 0){
			throw new IndexOutOfBoundsException(messages.getString("noArmiesWarning"));
		}
		territories.getTerritoryByName(territoryName).changeArmyAmountBy(1);
		playerModels.get(currentPlayer).setNumberOfUnplacedArmies(--currentNumberOfArmies);
	}

	int calculateNumberOfArmies(){
		return phaseController.calculateNumberOfArmies();
	}

	boolean hasWon(){
		for(TerritoryModel territory: territories.allTerritories){
			if(!territory.getOwner().equals(playerModels.get(currentPlayer).getColor())){
				return false;
			}
		}
		return true;
	}

	void determineBattleWinner(){
		attackerRolls.sort(Collections.reverseOrder());
		defenderRolls.sort(Collections.reverseOrder());
		for(int i = 0; i < defenderRolls.size(); i++){
			if(attackerRolls.get(i) > defenderRolls.get(i)){
				if(territories.getTerritoryByName(currentDefender).getNumberOfArmies() == 1){
					captureTerritory(currentDefender);
					return;
				} else {
					territories.getTerritoryByName(currentDefender).changeArmyAmountBy(-1);
				}
			} else {
				territories.getTerritoryByName(currentAttacker).changeArmyAmountBy(-1);
			}
		}
		updateBattleResults();
	}

	private void captureTerritory(String territoryName){
		Color playerColor = playerModels.get(currentPlayer).getColor();
		territories.getTerritoryByName(currentDefender)
			.changeArmyAmountBy(-territories.getTerritoryByName(currentDefender).getNumberOfArmies());
		territories.getTerritoryByName(currentDefender).setOwner(playerColor);
		if(hasWon()) {
			gameView.showWinMessage(currentPlayer+1);
			gameView.updateGlobalGameState(currentPlayer+1, "the winner!");
			gameView.removeObserver(this);
			return;
		}
		gameView.showTroopMovementCount(territories.getTerritoryByName(currentAttacker)
				.getNumberOfArmies()-1);
	}
	
	public void moveTroops(int troopCountToMove) {
		if(troopCountToMove > territories.getTerritoryByName(currentAttacker).getNumberOfArmies() - 1) {
			gameView.updateErrorLabel(messages.getString("leaveTroopWarning"));
			return;
		}
		territories.getTerritoryByName(currentAttacker).changeArmyAmountBy(-troopCountToMove);
		territories.getTerritoryByName(currentDefender).changeArmyAmountBy(troopCountToMove);
		Color playerColor = playerModels.get(currentPlayer).getColor();
		gameView.updateTerritoryOwnerDisplay(currentDefender, playerColor);
		getsCard = true;
		updateBattleResults();
	}

	public void territoryPressed(String territoryName, boolean placingTroop) {
		if(gamePhase == GamePhase.PLACING) {
			placingPhase(territoryName);
		} else if(gamePhase == GamePhase.TRADING) {
			gameView.updateErrorLabel(messages.getString("tradingSkip"));
			return;
		} else if(gamePhase == GamePhase.ATTACKING) {
            AttackingPhaseController attackingPhaseController = new AttackingPhaseController(this);
			attackingPhaseController.attackingPhase(territoryName);
		} else {
            ReinforcingPhaseController reinforcingPhaseController = new ReinforcingPhaseController(this);
			reinforcingPhaseController.reinforcingPhase(territoryName, placingTroop);
		}
	}

	private void placingPhase(String territoryName) {
		try {
			addArmyToTerritory(territoryName);
			gameView.updateCurrentPlacingDisplay(currentPlayer,
					playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
			gameView.updateTerritoryArmyCountDisplay(territoryName,
					territories.getTerritoryByName(territoryName).getNumberOfArmies());
		}catch(Exception e) {
			gameView.updateErrorLabel(e.getMessage());
		}
	}

	public void tradeInCards() {
		cardsController.tradeInCards();
	}

	public ArrayList<Integer> playerRolls() {
		int rollResult = die.roll();
		if(attackerRollCount==0) {
			defenderRolls.add(rollResult);
			defenderRollCount--;
			if(defenderRollCount==0) {
				gameView.updateCurrentAttackingDisplay(currentPlayer);
				determineBattleWinner();
			}
			return defenderRolls;
		} else {
			attackerRolls.add(rollResult);
			attackerRollCount--;
			if(attackerRollCount==0) {
				gameView.updateStateToDefenderRoll();
			}
			return attackerRolls;
		}
	}
	
	private void updateBattleResults() {
		gameView.updateTerritoryArmyCountDisplay(currentAttacker,
				territories.getTerritoryByName(currentAttacker).getNumberOfArmies());
		gameView.updateTerritoryArmyCountDisplay(currentDefender,
				territories.getTerritoryByName(currentDefender).getNumberOfArmies());
		gameView.removeHighlight(currentAttacker);
		gameView.removeHighlight(currentDefender);
		currentAttacker = null;
		currentDefender = null;
	}

	public void determineNumberOfRolls(int selectedAttackerRollNumber) {
		attackerRollCount = selectedAttackerRollNumber;
		if(territories.getTerritoryByName(currentDefender).getNumberOfArmies() == 1) {
			defenderRollCount = 1;
		} else if(attackerRollCount == 1) {
			defenderRollCount = 1;
		} else {
			defenderRollCount = selectedAttackerRollNumber - 1;
		}
		attackerRolls = new ArrayList<Integer>();
		defenderRolls = new ArrayList<Integer>();
		gameView.updateCurrentPlayerRollingLabel(currentPlayer+1);
	}
	
	void incrementCurrentPlayer() {
		if(playerModels.get(2).getColor() == Color.DARK_GRAY) {
			currentPlayer = (currentPlayer==1) ? 0 : 1;
		} else {
			currentPlayer = (currentPlayer+1)%playerModels.size();
		}
		if(isOut()){
			incrementCurrentPlayer();
		}
	}

	private boolean isOut(){
		for(int i = 0; i < territories.allTerritories.size(); i++){
			if(territories.allTerritories.get(i).getOwner() == playerModels.get(currentPlayer).getColor()){
				return false;
			}
		}
		return true;
	}

	public void nextPhase() {
		gameView.updateGlobalGameState(currentPlayer, gamePhase.toString());
		phaseController.nextPhase();
	}

	public TerritoryMapController getTerritories() {
		return this.territories;
	}

	public Color getTerritoryOwner(String name){
		return this.territories.getTerritoryByName(name).getOwner();
	}

	public Color getCurrentPlayerColor(){
		return this.playerModels.get(currentPlayer).getColor();
	}

	public void updateErrorLabel(String message){
		this.gameView.updateErrorLabel(messages.getString(message));
	}

	public int getAttackingTerritoryArmyCount() {
		return territories.getTerritoryByName(currentAttacker).getNumberOfArmies();
	}

	public boolean isAttackerAdjacent(String territoryName) {
		return territories.areTerritoriesAdjacent(currentAttacker, territoryName);
	}

	public GameView getGameView() {
		return this.gameView;
	}

	public void setGetsCard(boolean getsCard) {
		this.getsCard = getsCard;
	}

	public boolean getGetsCard() {
		return this.getsCard;
	}

	public void setGamePhase(GamePhase gamePhase) {
		this.gamePhase = gamePhase;
	}

	public GamePhase getGamePhase() {
		return this.gamePhase;
	}

	public ArrayList<PlayerModel> getPlayerModels() {
		return this.playerModels;
	}

	public int getCurrentPlayer() {
		return this.currentPlayer;
	}

	public PlayerModel getCurrentPlayerModel() {
		return this.playerModels.get(this.currentPlayer);
	}

	public void setTerritoryRemovedFrom(String territoryRemovedFrom) {
		this.territoryRemovedFrom = territoryRemovedFrom;
	}

	public ResourceBundle getMessages() {
		return this.messages;
	}

	public int getSetsTurnedIn() {
		return setsTurnedIn;
	}

    public String getCurrentAttacker() {
        return this.currentAttacker;
    }

    public void setCurrentDefender(String currentDefender) {
        this.currentDefender = currentDefender;
    }

    public void setCurrentAttacker(String currentAttacker) {
        this.currentAttacker = currentAttacker;
    }

    public String getCurrentDefender() {
        return this.currentDefender;
    }
}