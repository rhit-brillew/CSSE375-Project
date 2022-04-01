package risk.controller;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
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
		startNextPhase();
	}

	private void verifyPlayerOwnsTerritoryAndTerritoryExists(String territoryName){
		if(!territories.getTerritoryByName(territoryName).getOwner().equals(playerModels.get(currentPlayer).getColor())) {
			throw new IllegalArgumentException(messages.getString("unownedWarning"));
		}
	}

	private void addArmyToTerritory(String territoryName) {
		verifyPlayerOwnsTerritoryAndTerritoryExists(territoryName);
		int currentNumberOfArmies  = playerModels.get(currentPlayer).getNumberOfUnplacedArmies();
		if(currentNumberOfArmies == 0){
			throw new IndexOutOfBoundsException(messages.getString("noArmiesWarning"));
		}
		territories.getTerritoryByName(territoryName).changeArmyAmountBy(1);
		playerModels.get(currentPlayer).setNumberOfUnplacedArmies(--currentNumberOfArmies);
	}

	int calculateNumberOfArmies(){
		int numberOfTerritories = territories.calculateNumberOfTerritoriesPlayerOwns(playerModels.get(currentPlayer));
		int additionalTerritories = numberOfArmiesFromContinent();
		if (numberOfTerritories <= 9){
			return 3;
		}
		return (numberOfTerritories / 3) + additionalTerritories;
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
			gameView.showWinMessage(currentPlayer);
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
	
	private void startNextPhase() {
		if(gamePhase == GamePhase.TRADING) {
			gameView.updateCurrentPlayerTrading(currentPlayer, playerModels.get(currentPlayer).getCardCount());
		}else if(gamePhase == GamePhase.PLACING) {
			int newTroops = calculateNumberOfArmies() 
				+ playerModels.get(currentPlayer).getNumberOfUnplacedArmies();
			playerModels.get(currentPlayer).setNumberOfUnplacedArmies(newTroops);
			gameView.updateCurrentPlacingDisplay(currentPlayer,
					playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
		}else if(gamePhase == GamePhase.ATTACKING) {
			gameView.updateCurrentAttackingDisplay(currentPlayer);
		}else {
			territoryRemovedFrom = null;
			gameView.updateCurrentReinforcingDisplay(currentPlayer,
					playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
		}
	}

	public void territoryPressed(String territoryName, boolean placingTroop) {
		if(gamePhase == GamePhase.PLACING) {
			placingPhase(territoryName);
		} else if(gamePhase == GamePhase.TRADING) {
			gameView.updateErrorLabel(messages.getString("tradingSkip"));
			return;
		} else if(gamePhase == GamePhase.ATTACKING) {
			attackingPhase(territoryName);
		} else {
			reinforcingPhase(territoryName, placingTroop);
		}
	}

	private void reinforcingPhase(String territoryName, boolean placingTroop) {
		if(placingTroop) {
			try {
				if(territoryName.equals(territoryRemovedFrom)
						|| territories.areTerritoriesConnectedByOwnedTerritories(territoryName, territoryRemovedFrom)) {
					addArmyToTerritory(territoryName);
					gameView.updateCurrentReinforcingDisplay(currentPlayer,
							playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
					gameView.updateTerritoryArmyCountDisplay(territoryName,
							territories.getTerritoryByName(territoryName).getNumberOfArmies());
				} else {
					gameView.updateErrorLabel(messages.getString("unreachableWarning"));
				}
			}catch(Exception e) {
				gameView.updateErrorLabel(e.getMessage());
			}
		} else {
			try {
				if(territoryRemovedFrom != null && !territoryRemovedFrom.equals(territoryName)
						&& playerModels.get(currentPlayer).getNumberOfUnplacedArmies() != 0) {
					gameView.updateErrorLabel(messages.getString("singleTerritoryMove"));
					return;
				}
				subtractArmyFromTerritory(territoryName);
				gameView.updateCurrentReinforcingDisplay(currentPlayer,
						playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
				gameView.updateTerritoryArmyCountDisplay(territoryName,
						territories.getTerritoryByName(territoryName).getNumberOfArmies());
			}catch(Exception e) {
				gameView.updateErrorLabel(e.getMessage());
			}
		}
	}

	private void subtractArmyFromTerritory(String territoryName) {
		verifyPlayerOwnsTerritoryAndTerritoryExists(territoryName);
		if(territories.getTerritoryByName(territoryName).getNumberOfArmies()==1) {
			throw new IndexOutOfBoundsException(messages.getString("leaveTroopWarning"));
		}
		territoryRemovedFrom = territoryName;
		territories.getTerritoryByName(territoryName).changeArmyAmountBy(-1);
		int currentNumberOfArmies = playerModels.get(currentPlayer).getNumberOfUnplacedArmies();
		playerModels.get(currentPlayer).setNumberOfUnplacedArmies(++currentNumberOfArmies);
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

	private void attackingPhase(String territoryName) {
		if(currentAttacker != null) {
			if(currentDefender!=null) {
				gameView.updateErrorLabel(messages.getString("battleInProgress"));
				return;
			}
			if(territories.getTerritoryByName(territoryName)!=null 
					&& territories.areTerritoriesAdjacent(currentAttacker, territoryName)) {
				if(territories.getTerritoryByName(territoryName).getOwner()
						.equals(playerModels.get(currentPlayer).getColor())) {
					gameView.updateErrorLabel(messages.getString("attackOpponentWarning"));
					return;
				}
				currentDefender = territoryName;
				gameView.highlightTerritory(territoryName);
				getDiceAmount();
			} else {
				gameView.updateErrorLabel(messages.getString("attackInstructions"));
			}
		} else {
			try{
				verifyPlayerOwnsTerritoryAndTerritoryExists(territoryName);
				if(territories.getTerritoryByName(territoryName).getNumberOfArmies()==1) {
					gameView.updateErrorLabel(messages.getString("minimumAttack"));
					return;
				}
				gameView.highlightTerritory(territoryName);
				currentAttacker= territoryName;
			}catch(Exception e) {
				gameView.updateErrorLabel(e.getMessage());
			}
		}
	}

	public void tradeInCards() {
		int cardCount = playerModels.get(currentPlayer).getCardCount();
		if(cardCount < 3) {
			gameView.updateErrorLabel(messages.getString("noCardWarning"));
			return;
		}

		CardManager cardManager = playerModels.get(currentPlayer).getCards();
		determineCardToRemoveAndRemoveThem(cardManager);
		gameView.updateErrorLabel(messages.getString("cardTrade"));
	}

	private void determineCardToRemoveAndRemoveThem(CardManager cardManager) {
		try {
			ArrayList<Card> cardsToRemove = cardManager.determineCardToRemove();
			removeCardsFromHandAndAddUnplacedArmiesToPlayer(cardsToRemove);
		} catch(Exception e) {
			gameView.updateErrorLabel(messages.getString("cardCriteriaWarning"));
			return;
		}
	}

	private void removeCardsFromHandAndAddUnplacedArmiesToPlayer(ArrayList<Card> cards) {
		boolean alreadyOwnedOne = false;
		PlayerModel player = playerModels.get(currentPlayer);
		for(Card currentCard: cards) {
			String territoryName = currentCard.getTerritoryName();
			if(territories.getTerritoryByName(territoryName).getOwner()
					== playerModels.get(currentPlayer).getColor()
					&& !alreadyOwnedOne) {
				territories.getTerritoryByName(territoryName).changeArmyAmountBy(2);
				alreadyOwnedOne = true;
			}
			player.removeCard(currentCard);
		}
		player.addNumberOfUnplacedArmies(calculateNumberOfUnplacedArmiesToAddForTurningInCards());
		this.setsTurnedIn++;
	}

	private int calculateNumberOfUnplacedArmiesToAddForTurningInCards() {
		if(setsTurnedIn < 5) {
			return 4 + (2 * setsTurnedIn);
		} else {
			return 15 + (5 * (setsTurnedIn - 5));
		}
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
	
	private void getDiceAmount() {
		int armyCount = territories.getTerritoryByName(currentAttacker).getNumberOfArmies();
		if(armyCount==2) {
			gameView.showAttackCount(1);
		} else if(armyCount==3) {
			gameView.showAttackCount(2);
		} else {
			gameView.showAttackCount(3);
		}
	}

	private int numberOfArmiesFromContinent(){
		int numberOfAdditionalArmies = 0;
		Color playerColor = playerModels.get(currentPlayer).getColor();
		if(territories.ownsContinentAfrica(playerColor)){
			numberOfAdditionalArmies += 3;
		}
		if(territories.ownsContinentAsia(playerColor)){
			numberOfAdditionalArmies += 7;
		}
		if(territories.ownsContinentAustralia(playerColor)){
			numberOfAdditionalArmies += 2;
		}
		if(territories.ownsContinentEurope(playerColor)){
			numberOfAdditionalArmies += 5;
		}
		if(territories.ownsContinentNorthAmerica(playerColor)){
			numberOfAdditionalArmies += 5;
		}
		if(territories.ownsContinentSouthAmerica(playerColor)){
			numberOfAdditionalArmies += 2;
		}
		return numberOfAdditionalArmies;
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
	
	private void drawCard() {
		if(territories.deck.size()==0) {
			return;
		}
		Random rand = new Random();
		int cardIndex = rand.nextInt(territories.deck.size());
		Card cardDrawn = territories.deck.remove(cardIndex);
		playerModels.get(currentPlayer).addCard(cardDrawn);
	}

	public void nextPhase() {
		if(gamePhase == GamePhase.TRADING) {
			if(playerModels.get(currentPlayer).getCardCount() >= 5) {
				gameView.updateErrorLabel(messages.getString("mustTradeWarning"));
				return;
			}
			gamePhase = GamePhase.PLACING;
			startNextPhase();
		}else if(gamePhase == GamePhase.PLACING) {
			if(playerModels.get(currentPlayer).getNumberOfUnplacedArmies()!=0) {
				gameView.updateErrorLabel(messages.getString("troopsNotPlaced"));
			} else {
				gamePhase = GamePhase.ATTACKING;
				startNextPhase();
			}
		}else if(gamePhase == GamePhase.ATTACKING) {
			if(getsCard) {
				drawCard();
			}
			getsCard = false;
			gamePhase = GamePhase.REINFORCING;
			startNextPhase();
		} else {
			if(playerModels.get(currentPlayer).getNumberOfUnplacedArmies()!=0) {
				gameView.updateErrorLabel(messages.getString("troopsNotPlaced"));
			} else {
				incrementCurrentPlayer();
				gamePhase = GamePhase.TRADING;
				startNextPhase();
			}
		}
	}
}