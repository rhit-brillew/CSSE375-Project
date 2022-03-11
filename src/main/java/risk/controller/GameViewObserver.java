package risk.controller;

public interface GameViewObserver {
	void territoryPressed(String territoryName, boolean placingTroop);
	
	void tradeInCards();

	int playerRolls();
	
	void nextPhase();
	
	void determineNumberOfRolls(int attackAmount);
	
	void moveTroops(int troopCountToMove);
}
