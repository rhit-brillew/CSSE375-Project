package risk.controller;

import java.util.Locale;

public interface StartingOptionsObserver {
	void setNumberOfPlayers(int numberOfPlayers);
	
	void setLocale(Locale locale);

	void setBoard(int index);
	
	void startGame();
}