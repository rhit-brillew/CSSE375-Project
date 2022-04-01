package risk.controller;

import risk.model.Card;
import risk.model.CardManager;
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
            gameView.updateErrorLabel(turnController.messages.getString("noCardWarning"));
            return;
        }

        CardManager cardManager = playerModels.get(currentPlayer).getCards();
        determineCardToRemoveAndRemoveThem(cardManager);
        gameView.updateErrorLabel(turnController.messages.getString("cardTrade"));
    }

    private void determineCardToRemoveAndRemoveThem(CardManager cardManager) {
        try {
            ArrayList<Card> cardsToRemove = cardManager.determineCardToRemove();
            removeCardsFromHandAndAddUnplacedArmiesToPlayer(cardsToRemove);
        } catch(Exception e) {
            turnController.getGameView().updateErrorLabel(turnController.messages.getString("cardCriteriaWarning"));
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
        turnController.setsTurnedIn++;
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
