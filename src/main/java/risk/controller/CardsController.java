package risk.controller;

import risk.model.Card;
import risk.model.PlayerModel;
import risk.view.GameView;

import java.util.ArrayList;

public class CardsController {

    private TurnController turnController;
    public CardsController(TurnController turnController) {
        this.turnController = turnController;
    }

    public void tradeInCards() {
        ArrayList<PlayerModel> playerModels = turnController.getPlayerModels();
        int currentPlayer = turnController.getCurrentPlayer();
        GameView gameView = turnController.getGameView();
        int cardCount = playerModels.get(currentPlayer).getCardCount();
        if(cardCount < 3) {
            gameView.updateErrorLabel(turnController.getMessages().getString("noCardWarning"));
            return;
        }
        ArrayList<Card> infantryCards = new ArrayList<>();
        ArrayList<Card> cavalryCards = new ArrayList<>();
        ArrayList<Card> artilleryCards = new ArrayList<>();

        for(int k=0; k<cardCount; k++) {
            Card currentCard = playerModels.get(currentPlayer).getCardAtIndex(k);
            if(currentCard.getTroopType().equals("Infantry")) {
                infantryCards.add(currentCard);
            }else if(currentCard.getTroopType().equals("Cavalry")) {
                cavalryCards.add(currentCard);
            }else {
                artilleryCards.add(currentCard);
            }
        }
        determineCardToRemoveAndRemoveThem(infantryCards, cavalryCards, artilleryCards);
        gameView.updateErrorLabel(turnController.getMessages().getString("cardTrade"));
    }

    private void determineCardToRemoveAndRemoveThem(ArrayList<Card> infantryCards, ArrayList<Card> cavalryCards,
                                                    ArrayList<Card> artilleryCards) {
        if(infantryCards.size()>=3) {
            removeCardsFromHandAndAddUnplacedArmiesToPlayer(new ArrayList<>(infantryCards.subList(0, 3)));
        }else if(cavalryCards.size()>=3) {
            removeCardsFromHandAndAddUnplacedArmiesToPlayer(new ArrayList<>(cavalryCards.subList(0, 3)));
        }else if(artilleryCards.size()>=3) {
            removeCardsFromHandAndAddUnplacedArmiesToPlayer(new ArrayList<>(artilleryCards.subList(0, 3)));
        }else if(infantryCards.size()>0 && cavalryCards.size()>0 && artilleryCards.size()>0) {
            ArrayList<Card> cardsToRemove = new ArrayList<>();
            cardsToRemove.add(infantryCards.get(0));
            cardsToRemove.add(cavalryCards.get(0));
            cardsToRemove.add(artilleryCards.get(0));
            removeCardsFromHandAndAddUnplacedArmiesToPlayer(cardsToRemove);
        }else {
            turnController.getGameView().updateErrorLabel(turnController.getMessages().getString("cardCriteriaWarning"));
            return;
        }
    }

    private void removeCardsFromHandAndAddUnplacedArmiesToPlayer(ArrayList<Card> cards) {
        ArrayList<PlayerModel> playerModels = turnController.getPlayerModels();
        int currentPlayer = turnController.getCurrentPlayer();
        TerritoryMapController territories = turnController.getTerritories();
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
        int setsTurnedIn = turnController.getSetsTurnedIn();
        turnController.setSetsTurnedIn(setsTurnedIn++);
    }

    private int calculateNumberOfUnplacedArmiesToAddForTurningInCards() {
        int setsTurnedIn = turnController.getSetsTurnedIn();
        if(setsTurnedIn < 5) {
            return 4 + (2 * setsTurnedIn);
        } else {
            return 15 + (5 * (setsTurnedIn - 5));
        }
    }

}
