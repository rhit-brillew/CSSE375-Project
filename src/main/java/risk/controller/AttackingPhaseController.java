package risk.controller;

import risk.view.GameView;

public class AttackingPhaseController {
    TurnController turnController;

    public AttackingPhaseController(TurnController turnController) {
        this.turnController = turnController;
    }

    private boolean checkAdjacentTerritories(String territoryName) {
        return turnController.getTerritories().getTerritoryByName(territoryName)!=null
                && turnController.getTerritories().areTerritoriesAdjacent(turnController.getCurrentAttacker(), territoryName);
    }

    private void beginAttack(String territoryName) {
        if(turnController.getTerritories().getTerritoryByName(territoryName).getOwner()
                .equals(turnController.getPlayerModels().get(turnController.getCurrentPlayer()).getColor())) {
            turnController.getGameView().updateErrorLabel(turnController.getMessages().getString("attackOpponentWarning"));
            return;
        }
       turnController.setCurrentDefender(territoryName);
        turnController.getGameView().highlightTerritory(territoryName);
        getDiceAmount();
    }

    private void updateViewAndAttacker(String territoryName) {
        turnController.verifyPlayerOwnsTerritoryAndTerritoryExists(territoryName);
        if(turnController.getTerritories().getTerritoryByName(territoryName).getNumberOfArmies()==1) {
            turnController.getGameView().updateErrorLabel(turnController.getMessages().getString("minimumAttack"));
            return;
        }
        turnController.getGameView().highlightTerritory(territoryName);
        turnController.setCurrentAttacker(territoryName);
    }

    private void getDiceAmount() {
        GameView gameView = turnController.getGameView();
        int armyCount = turnController.getTerritories().getTerritoryByName(turnController.getCurrentAttacker()).getNumberOfArmies();
        if(armyCount==2) {
            gameView.showAttackCount(1);
        } else if(armyCount==3) {
            gameView.showAttackCount(2);
        } else {
            gameView.showAttackCount(3);
        }
    }

    public void attackingPhase(String territoryName) {
        GameView gameView = turnController.getGameView();
        if(turnController.getCurrentAttacker() != null) {
            if(turnController.getCurrentDefender() != null) {
                gameView.updateErrorLabel(turnController.getMessages().getString("battleInProgress"));
                return;
            }
            if(checkAdjacentTerritories(territoryName)) {
                beginAttack(territoryName);
            } else {
                gameView.updateErrorLabel(turnController.getMessages().getString("attackInstructions"));
            }
        } else {
            try{
                updateViewAndAttacker(territoryName);
            }catch(Exception e) {
                gameView.updateErrorLabel(e.getMessage());
            }
        }
    }

}
