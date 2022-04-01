package risk.controller;

import risk.model.PlayerModel;
import risk.view.GameView;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class ReinforcingPhaseController {
    TurnController turnController;
    GameView gameView;
    ArrayList<PlayerModel> playerModels;
    ResourceBundle messages;
    int currentPlayer;
    TerritoryMapController territories;

    public ReinforcingPhaseController(TurnController turnController) {
        this.turnController = turnController;
        this.gameView = turnController.getGameView();
        this.playerModels = turnController.getPlayerModels();
        this.messages = turnController.getMessages();
        this.currentPlayer = turnController.getCurrentPlayer();
        this.territories = turnController.getTerritories();
    }

    public boolean checkRemovedFromOrConnected(String territoryName) {
        return territoryName.equals(turnController.territoryRemovedFrom)
                || territories.areTerritoriesConnectedByOwnedTerritories(territoryName, turnController.territoryRemovedFrom);
    }

    private void addAndUpdateView(String territoryName) {
        turnController.addArmyToTerritory(territoryName);
        gameView.updateCurrentReinforcingDisplay(currentPlayer,
                playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
        gameView.updateTerritoryArmyCountDisplay(territoryName,
                territories.getTerritoryByName(territoryName).getNumberOfArmies());
    }

    private boolean checkPlacedTroops(String territoryName) {
        return turnController.territoryRemovedFrom != null && !turnController.territoryRemovedFrom.equals(territoryName)
                && playerModels.get(currentPlayer).getNumberOfUnplacedArmies() != 0;
    }

    private void subtractArmyFromTerritory(String territoryName) {
        turnController.verifyPlayerOwnsTerritoryAndTerritoryExists(territoryName);
        if(territories.getTerritoryByName(territoryName).getNumberOfArmies()==1) {
            throw new IndexOutOfBoundsException(messages.getString("leaveTroopWarning"));
        }
        turnController.setTerritoryRemovedFrom(territoryName);
        territories.getTerritoryByName(territoryName).changeArmyAmountBy(-1);
        int currentNumberOfArmies = playerModels.get(currentPlayer).getNumberOfUnplacedArmies();
        playerModels.get(currentPlayer).setNumberOfUnplacedArmies(++currentNumberOfArmies);
    }

    private void removeAndUpdateview(String territoryName) {
        subtractArmyFromTerritory(territoryName);
        gameView.updateCurrentReinforcingDisplay(currentPlayer,
                playerModels.get(currentPlayer).getNumberOfUnplacedArmies());
        gameView.updateTerritoryArmyCountDisplay(territoryName,
                territories.getTerritoryByName(territoryName).getNumberOfArmies());
    }

    private void placeTroop(String territoryName) {
        try {
            if(checkRemovedFromOrConnected(territoryName)) {
                addAndUpdateView(territoryName);
            } else {
                gameView.updateErrorLabel(messages.getString("unreachableWarning"));
            }
        }catch(Exception e) {
            gameView.updateErrorLabel(e.getMessage());
        }
    }

    void reinforcingPhase(String territoryName, boolean placingTroop) {
        if(placingTroop) {
            placeTroop(territoryName);
        } else {
            try {
                if(checkPlacedTroops(territoryName)) {
                    gameView.updateErrorLabel(messages.getString("singleTerritoryMove"));
                    return;
                }
                removeAndUpdateview(territoryName);
            }catch(Exception e) {
                gameView.updateErrorLabel(e.getMessage());
            }
        }
    }
}
