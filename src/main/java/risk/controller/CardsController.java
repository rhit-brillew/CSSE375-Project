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
        PlayerModel player = turnController.getCurrentPlayerModel();
        TerritoryMapController territories = turnController.getTerritories();
        territories.playerTurnedInCards(player, cards);
        player.removeCards(cards);
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
