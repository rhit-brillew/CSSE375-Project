package risk.controller;

import java.util.Locale;
import risk.model.StaticResourceBundle;
import risk.view.GameView;
import risk.view.StartingOptionsView;

public class InitializationController implements Runnable, StartingOptionsObserver {
	private StartingOptionsView startingOptionsView = new StartingOptionsView();

	private TerritoryMapController territories;
	private Locale locale;
	private int numberOfPlayers;

	@Override
	public void run() {
		startingOptionsView.addObserver(this);
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public TerritoryMapController getTerritoryMapController() {
		return territories;
	}

	@Override
	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public void startGame() {
		synchronized (this) {
			territories = TerritoryMapController.loadTerritoryXMLData();
			StaticResourceBundle.createResourceBundle(locale);
			GameView.create(numberOfPlayers, territories.getNameLocationHashMap());
			this.notify();
		}
	}
}