package risk.controller;

import risk.view.GameView;

public class AttackingPhaseController {
    TurnController turnController;

    public AttackingPhaseController(TurnController turnController) {
        this.turnController = turnController;
    }

    private boolean checkAdjacentTerritories(String territoryName) {
        return turnController.getTerritories().getTerritoryByName(territoryName)!=null
                && turnController.isAttackerAdjacent(territoryName);
    }

    private void beginAttack(String territoryName) {
        if(turnController.getTerritoryOwner(territoryName).equals(turnController.getCurrentPlayerColor())) {
            turnController.updateErrorLabel("attackOpponentWarning");
            return;
        }
       turnController.setCurrentDefender(territoryName);
        turnController.getGameView().highlightTerritory(territoryName);
        getDiceAmount();
    }

    private void updateViewAndAttacker(String territoryName) {
        turnController.verifyPlayerOwnsTerritoryAndTerritoryExists(territoryName);
        if(turnController.getTerritories().getTerritoryByName(territoryName).getNumberOfArmies()==1) {
            turnController.updateErrorLabel("minimumAttack");
            return;
        }
        turnController.getGameView().highlightTerritory(territoryName);
        turnController.setCurrentAttacker(territoryName);
    }

    private void getDiceAmount() {
        GameView gameView = turnController.getGameView();
        int armyCount = turnController.getAttackingTerritoryArmyCount();
        if(armyCount==2) {
            gameView.showAttackCount(1);
        } else if(armyCount==3) {
            gameView.showAttackCount(2);
        } else {
            gameView.showAttackCount(3);
        }
    }

    public void attackingPhase(String territoryName) {
        if(turnController.getCurrentAttacker() != null) {
            if(turnController.getCurrentDefender() != null) {
                turnController.updateErrorLabel("battleInProgress");
                return;
            }
            if(checkAdjacentTerritories(territoryName)) {
                beginAttack(territoryName);
            } else {
                turnController.updateErrorLabel("attackInstructions");
            }
        } else {
            try{
                updateViewAndAttacker(territoryName);
            }catch(Exception e) {
                turnController.updateErrorLabel("defaultError");
            }
        }
    }

}
