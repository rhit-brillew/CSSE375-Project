package risk.engine;

import risk.controller.InitializationController;
import risk.controller.SetupController;
import risk.controller.TerritoryMapController;
import risk.model.SixSidedDie;
import risk.view.GameView;

public class MainController implements Runnable{
	public static void main(String[] args) {
		MainController mainController = new MainController();
		mainController.run();
	}

	@Override
	public void run() {
		InitializationController initializationController = new InitializationController();
		initializationController.run();
		TerritoryMapController territories = initializationController.getTerritoryMapController();
		Runnable setupController = new SetupController(GameView.getGameView(), new SixSidedDie(), territories);
		setupController.run();
	}
}
