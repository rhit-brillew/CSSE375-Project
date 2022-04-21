package risk.controller;

import java.util.ArrayList;

public interface GameViewObserver {
	void territoryPressed(String territoryName, boolean placingTroop);
	
	void tradeInCards();

	ArrayList<Integer> playerRolls();
	
	void nextPhase();
	
	void determineNumberOfRolls(int attackAmount);
	
	void moveTroops(int troopCountToMove);
}
