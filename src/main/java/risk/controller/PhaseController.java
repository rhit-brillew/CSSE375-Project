package risk.controller;

import risk.model.Card;
import risk.model.PlayerModel;
import risk.view.GameView;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PhaseController {
    TurnController turnController;
    public PhaseController(TurnController turnController) {
        this.turnController = turnController;
    }

    public void startNextPhase() {
        TurnController.GamePhase gamePhase = turnController.getGamePhase();
        GameView gameView = turnController.getGameView();
        ArrayList<PlayerModel> playerModels = turnController.getPlayerModels();
        int currentPlayer = turnController.getCurrentPlayer();
        gameView.updateGlobalGameState(currentPlayer, gamePhase.toString());
        if(gamePhase == TurnController.GamePhase.TRADING) {
            gameView.updateCurrentPlayerTrading(currentPlayer, playerModels.get(currentPlayer).getCardCount());
        }else if(gamePhase == TurnController.GamePhase.PLACING) {
            int newTroops = calculateNumberOfArmies()
                    + playerModels.get(currentPlayer).getNumberOfUnplacedArmies();
            playerModels.get(currentPlayer).setNumberOfUnplacedArmies(newTroops);
            gameView.updateCurrentPlacingDisplay(currentPlayer,
                    playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
        }else if(gamePhase == TurnController.GamePhase.ATTACKING) {
            gameView.updateCurrentAttackingDisplay(currentPlayer);
        }else {
            turnController.setTerritoryRemovedFrom(null);
            gameView.updateCurrentReinforcingDisplay(currentPlayer,
                    playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
        }
    }

    private void drawCard() {
        if(turnController.territories.deck.size()==0) {
            return;
        }
        Random rand = new Random();
        int cardIndex = rand.nextInt(turnController.territories.deck.size());
        Card cardDrawn = turnController.territories.deck.remove(cardIndex);
        turnController.getPlayerModels().get(turnController.getCurrentPlayer()).addCard(cardDrawn);
    }

    private int numberOfArmiesFromContinent(){
        Color playerColor = turnController.getPlayerModels().get(turnController.getCurrentPlayer()).getColor();
        return this.turnController.territories.numberTroopsForOwningContinent(playerColor);
    }

    int calculateNumberOfArmies(){
        int numberOfTerritories = turnController.getTerritories().calculateNumberOfTerritoriesPlayerOwns(turnController.getPlayerModels().get(turnController.getCurrentPlayer()));
        int additionalTerritories = numberOfArmiesFromContinent();
        if (numberOfTerritories <= 9){
            return 3;
        }
        return (numberOfTerritories / 3) + additionalTerritories;
    }

    private void changeGamePhaseFromTrading() {
        int currentPlayer = turnController.getCurrentPlayer();
        if(turnController.getPlayerModels().get(currentPlayer).getCardCount() >= 5) {
            turnController.getGameView().updateErrorLabel(turnController.getMessages().getString("mustTradeWarning"));
            return;
        }
        turnController.setGamePhase(TurnController.GamePhase.PLACING);
        startNextPhase();
    }

    private void changeGamePhaseFromPlacing() {
        int currentPlayer = turnController.getCurrentPlayer();
        if(turnController.getPlayerModels().get(currentPlayer).getNumberOfUnplacedArmies()!=0) {
            turnController.getGameView().updateErrorLabel(turnController.getMessages().getString("troopsNotPlaced"));
        } else {
            turnController.setGamePhase(TurnController.GamePhase.ATTACKING);
            startNextPhase();
        }
    }

    private void changeGamePhaseFromAttacking() {
        if(turnController.getGetsCard()) {
            drawCard();
        }
        turnController.setGetsCard(false);
        turnController.setGamePhase(TurnController.GamePhase.REINFORCING);
        startNextPhase();
    }

    public void nextPhase() {
        if(turnController.getGamePhase() == TurnController.GamePhase.TRADING) {
            changeGamePhaseFromTrading();
        }else if(turnController.getGamePhase() == TurnController.GamePhase.PLACING) {
            changeGamePhaseFromPlacing();
        }else if(turnController.getGamePhase() == TurnController.GamePhase.ATTACKING) {
            changeGamePhaseFromAttacking();
        } else {
            if(turnController.getPlayerModels().get(turnController.getCurrentPlayer()).getNumberOfUnplacedArmies()!=0) {
                turnController.getGameView().updateErrorLabel(turnController.getMessages().getString("troopsNotPlaced"));
            } else {
                turnController.incrementCurrentPlayer();
                turnController.setGamePhase(TurnController.GamePhase.TRADING);
                startNextPhase();
            }
        }
    }
}
