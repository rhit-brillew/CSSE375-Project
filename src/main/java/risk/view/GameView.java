package risk.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import risk.controller.ColorFactory;
import risk.controller.GameViewObservable;
import risk.controller.GameViewObserver;
import risk.controller.TurnController;
import risk.model.StaticResourceBundle;

public class GameView extends GameViewObservable {
	private static final int FRAME_WIDTH = 1920;
	private static final int FRAME_HEIGHT = 1080;
	private static final int GAME_BAR_PANEL_HEIGHT = 100;

	private static GameView gameView;
	
	private final int numberOfPlayers;
	private JFrame frame;
	private JLayeredPane mapPane;
	private JPanel gameBarPanel;
	private JLabel gameState;
	private JLabel errorLabel;
	private JLabel diceLabel;
	private JLabel cardLabel;
	private JButton nextPhaseLabel;
	private JButton submit;
	private JSlider attackCount;
	private JSlider troopCount;
	private BufferedImage territoryImage;
	private int territorySize = 30;
	private HashMap<String, JLabel> territoryCircles;
	private HashMap<String, JLabel> territoryArmyCounts;

	//TODO:
	private JLabel globalGameStateLabel;

	private ResourceBundle messages;
	
	private GameView(int numberOfPlayers, HashMap<String, Point> territories) {
		this.numberOfPlayers = numberOfPlayers;
		this.messages = StaticResourceBundle.getResourceBundle();
		territoryCircles = new HashMap<>();
		territoryArmyCounts = new HashMap<>();
		
		initializeFrame();
		initializeMapPane();
		initializeGameBarPanel();
		initializeGameStateLabel();
		initializeDiceLabel();
		initializeCardLabel();
		initializeNextPhaseLabel();
		initializeAttackCount();
		initializeTroopCount();
		addMap();
		addPlayerIcons();
		addTerritories(territories);

		//TODO:
		initializeGlobalGameStateLabel();

		frame.setVisible(true);
	}

	public static void create(int numberOfPlayers, HashMap<String, Point> territories) {
		gameView = new GameView(numberOfPlayers, territories);
	}

	public static GameView getGameView() {
		return gameView;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public ResourceBundle getResourceBundle() {
		return messages;
	}
	
	private void initializeFrame() {
		frame = new JFrame();
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setTitle(messages.getString("gameTitle"));
		frame.setLocation(0, 0);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initializeMapPane() {
		mapPane = new JLayeredPane();
		mapPane.setBounds(0, 100, FRAME_WIDTH, FRAME_HEIGHT - GAME_BAR_PANEL_HEIGHT);
		mapPane.setLayout(null);
		mapPane.setBackground(new Color(135, 206, 250));
		frame.add(mapPane);
	}
	
	private void initializeGameStateLabel() {
		gameState = new JLabel(messages.getString("welcomeMessage"));
		gameState.setForeground(Color.white);
		gameState.setBounds(800, 0, 700, GAME_BAR_PANEL_HEIGHT-20);
		gameState.setFont(new Font(gameState.getFont().getName(), Font.PLAIN, 26));
		
		errorLabel = new JLabel("");
		errorLabel.setForeground(Color.white);
		errorLabel.setBounds(800, GAME_BAR_PANEL_HEIGHT-20, 600, 20);
		errorLabel.setFont(new Font(errorLabel.getFont().getName(), Font.PLAIN, 18));
		
		gameBarPanel.add(errorLabel);
		gameBarPanel.add(gameState);
	}

	//TODO:
	private void initializeGlobalGameStateLabel(){
		ImageIcon image = scaleImage(FRAME_WIDTH, 70, "src/main/resources/images/bottomGameBar.png");
		JLabel label = new JLabel("", image, SwingConstants.CENTER);
		label.setLayout(null);
		label.setBounds(0, FRAME_HEIGHT-200, FRAME_WIDTH, 70);
		mapPane.add(label, JLayeredPane.PALETTE_LAYER, 2);
		label.setVisible(true);

		globalGameStateLabel = new JLabel(messages.getString("setupLabel"));
		globalGameStateLabel.setLayout(null);
		globalGameStateLabel.setBounds(FRAME_WIDTH/2 - (int)globalGameStateLabel.getPreferredSize().getWidth()/2, FRAME_HEIGHT-230, FRAME_WIDTH/2, 100);
		globalGameStateLabel.setFont(new Font(gameState.getFont().getName(), Font.PLAIN, 18));
		globalGameStateLabel.setForeground(Color.WHITE);
		mapPane.add(globalGameStateLabel, JLayeredPane.PALETTE_LAYER, 1);
		globalGameStateLabel.setVisible(true);
	}
	
	private void initializeAttackCount() {
		attackCount = new JSlider(1, 3);
		attackCount.setBounds(1500, 50, 300, 50);
		attackCount.setMajorTickSpacing(1);
		attackCount.setPaintTicks(true);
		attackCount.setBackground(Color.black);
		attackCount.setForeground(Color.white);
		attackCount.setPaintLabels(true);
		
		submit = new JButton(messages.getString("attackLabel"));
		submit.setBounds(1700, 0, 100, 50);
		submit.setLayout(null);
		
		gameBarPanel.add(attackCount);
		gameBarPanel.add(submit);
		attackCount.setVisible(false);
		submit.setVisible(false);
	}
	
	private void initializeTroopCount() {
		troopCount = new JSlider(1, 3);
		troopCount.setBounds(1500, 50, 300, 50);
		troopCount.setMajorTickSpacing(1);
		troopCount.setPaintTicks(true);
		troopCount.setBackground(Color.black);
		troopCount.setForeground(Color.white);
		troopCount.setPaintLabels(true);
		
		gameBarPanel.add(troopCount);
		troopCount.setVisible(false);
	}
	
	private void initializeDiceLabel() {
		ImageIcon image = scaleImage(100, 100, "src/main/resources/images/dice.png");
		diceLabel = new JLabel("", image, SwingConstants.RIGHT);
		diceLabel.setLayout(null);
		diceLabel.setBounds(1400, 0, 200, GAME_BAR_PANEL_HEIGHT);
		diceLabel.setForeground(Color.white);
		
		diceLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for(GameViewObserver observer : observers) {
					int result = observer.playerRolls();
					diceLabel.setText(messages.getString("startOfDiceLabel") + result);
				}
			}
		});
		
		gameBarPanel.add(diceLabel);
	}
	
	private void initializeCardLabel() {
		ImageIcon image = scaleImage(100, 100, "src/main/resources/images/card.png");
		cardLabel = new JLabel(messages.getString("clickToTradeLabel"), image, SwingConstants.RIGHT);
		cardLabel.setLayout(null);
		cardLabel.setBounds(1400, 0, 200, GAME_BAR_PANEL_HEIGHT);
		cardLabel.setForeground(Color.white);
		
		cardLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for(GameViewObserver observer : observers) {
					observer.tradeInCards();
				}
			}
		});
		
		gameBarPanel.add(cardLabel);
		cardLabel.setVisible(false);
	}
	
	private void initializeNextPhaseLabel() {
		nextPhaseLabel = new JButton(messages.getString("nextPhaseLabel"));
		nextPhaseLabel.setBounds(1800, 0, 100, 50);
		
		nextPhaseLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for(GameViewObserver observer : observers) {
					observer.nextPhase();
				}
			}
		});
		gameBarPanel.add(nextPhaseLabel);
		nextPhaseLabel.setVisible(false);
	}
	
	private void initializeGameBarPanel() {
		gameBarPanel = new JPanel();
		gameBarPanel.setBounds(0, 0, FRAME_WIDTH, GAME_BAR_PANEL_HEIGHT);
		gameBarPanel.setLayout(null);
		gameBarPanel.setBackground(Color.black);
		frame.add(gameBarPanel);
	}
	
	private void addMap() {
		ImageIcon image = scaleImage(FRAME_WIDTH, FRAME_HEIGHT - GAME_BAR_PANEL_HEIGHT, 
					messages.getString("gameBoardLocation"));
		JLabel backgroundLabel = new JLabel(image);
		
		backgroundLabel.setLayout(null);
		backgroundLabel.setBounds(0, -GAME_BAR_PANEL_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT);
		mapPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER, 2);
		
		JPanel colorLabel = new JPanel();
		colorLabel.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		colorLabel.setBackground(new Color(135, 206, 235));
		mapPane.add(colorLabel, JLayeredPane.DEFAULT_LAYER, 3);
	}
	
	private void addPlayerIcons() {
		BufferedImage image;
		try {
			image = ImageIO.read(new File("src/main/resources/images/playerIcon" + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for(int k = 0; k < numberOfPlayers; k++) {
			int playerNumber = k + 1;
			changeImageToColor(image, ColorFactory.createColor("player" + playerNumber));
			int newIconSize = 75;
			ImageIcon imageIcon = new ImageIcon(image.getScaledInstance(newIconSize, newIconSize,
																		Image.SCALE_AREA_AVERAGING));
			JLabel playerLabel = new JLabel("Player "+(playerNumber), imageIcon, SwingConstants.LEFT);
			playerLabel.setLayout(null);
			playerLabel.setForeground(Color.white);
			playerLabel.setBounds(k*150, 0, 200, 100);

			gameBarPanel.add(playerLabel);
		}
	}

	static void changeImageToColor(BufferedImage image, Color color) {
		WritableRaster raster = image.getRaster();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int[] pixelRGBA = raster.getPixel(x, y, (int[]) null);
				pixelRGBA[0] = color.getRed();
				pixelRGBA[1] = color.getGreen();
				pixelRGBA[2] = color.getBlue();
				raster.setPixel(x, y, pixelRGBA);
			}
		}
	}

	private void addTerritories(HashMap<String, Point> territories) {
		try {
			territoryImage = ImageIO.read(new File("src/main/resources/images/circle" + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		changeImageToColor(territoryImage, Color.GRAY);

		for(String territoryName : territories.keySet()) {
			Point location = territories.get(territoryName);
			ImageIcon territoryCircle = new ImageIcon(territoryImage.getScaledInstance(territorySize, territorySize,
												Image.SCALE_AREA_AVERAGING));
			JLabel circleLabel = new JLabel(territoryCircle);
			circleLabel.setLayout(null);
			circleLabel.setBounds(location.x, location.y-100, 30, 30);
			territoryCircles.put(territoryName, circleLabel);
			
			addTerritoryArmyCount(territoryName, location);
			addTerritoryListener(territoryName);
			
			mapPane.add(circleLabel, JLayeredPane.PALETTE_LAYER, 2);
		}
	}
	
	private void addTerritoryArmyCount(String territoryName, Point location) {
		JLabel armyCountLabel = new JLabel("0");
		armyCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		armyCountLabel.setLayout(null);
		armyCountLabel.setBounds(location.x, location.y-100, 30, 30);
		armyCountLabel.setForeground(Color.white);
		territoryArmyCounts.put(territoryName, armyCountLabel);
		
		mapPane.add(armyCountLabel, JLayeredPane.PALETTE_LAYER, 1);
	}
	
	private void addTerritoryListener(String territoryName) {
		JLabel territoryLabel = territoryCircles.get(territoryName);
		territoryLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for(GameViewObserver observer : observers) {
					observer.territoryPressed(territoryName, e.getButton() == 1);
				}
			}
		});
	}
	
	public void updateCurrentPlayerRollingLabel(int player) {
		cardLabel.setVisible(false);
		diceLabel.setVisible(true);
		gameState.setText(MessageFormat.format(messages.getString("playerRollingLabel"), player));
	}
	
	public void updateCurrentPlayerClaimingLabel(int player) {
		gameState.setText(MessageFormat.format(messages.getString("playerClaimingLabel"), player));
		diceLabel.setVisible(false);
	}
	
	public void updateCurrentPlayerTrading(int player, int cardCount) {
		nextPhaseLabel.setVisible(true);
		cardLabel.setVisible(true);
		gameState.setText(MessageFormat.format(messages.getString("playerTradingLabel"), player+1, cardCount));
		errorLabel.setText("");
	}
	
	public void updateCurrentPlacingDisplay(int player, int armyCount) {
		nextPhaseLabel.setVisible(true);
		cardLabel.setVisible(false);
		diceLabel.setVisible(false);
		gameState.setText(MessageFormat.format(messages.getString("placingDisplayLabel"), player+1, armyCount));
		errorLabel.setText("");
	}

	public void updatePlaceNeutralArmy(){
		gameState.setText(messages.getString("placeNeutralArmy"));
	}
	
	public void updateCurrentAttackingDisplay(int player) {
		diceLabel.setVisible(false);
		nextPhaseLabel.setVisible(true);
		gameState.setText(MessageFormat.format(messages.getString("attackingDisplayLabel"), player+1));
		errorLabel.setText("");
	}
	
	public void updateCurrentReinforcingDisplay(int player, int availableCount) {
		gameState.setText(MessageFormat.format(messages.getString("reinforcingDisplayLabel"), player+1, availableCount));
		errorLabel.setText("");
	}
	
	public void updateStateToDefenderRoll() {
		gameState.setText(messages.getString("defenderRollPrompt"));
	}

	public void updateStateNonAdjacentMove(){
		errorLabel.setText(messages.getString("adjacentTerritoryWarning"));
	}
	
	public void updateErrorLabel(String message) {
		errorLabel.setText(message);
	}
	
	public void highlightTerritory(String territoryName) {
		Border border = BorderFactory.createLineBorder(Color.PINK, 5);
		territoryCircles.get(territoryName).setBorder(border);
	}
	
	public void removeHighlight(String territoryName) {
		territoryCircles.get(territoryName).setBorder(null);
	}

	public void showWinMessage(int currentPlayer){
		gameState.setText(MessageFormat.format(messages.getString("playerWonMessage"), currentPlayer));
	}
	
	public void showAttackCount(int max) {
		nextPhaseLabel.setVisible(false);
		attackCount.setMaximum(max);
		attackCount.setVisible(true);
		
		
		submit.setVisible(true);
		
		MouseListener[] listeners = submit.getMouseListeners();
		if(listeners.length>0) {
			submit.removeMouseListener(listeners[0]);
		}
		submit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for(GameViewObserver observer : observers) {
					observer.determineNumberOfRolls(attackCount.getValue());
					attackCount.setVisible(false);
					submit.setVisible(false);
				}
			}
		});
		gameBarPanel.add(submit);
	}
	
	public void showTroopMovementCount(int maxTroops) {
		attackCount.setVisible(false);
		troopCount.setMaximum(maxTroops);
		troopCount.setVisible(true);
		
		submit.setVisible(true);
		
		MouseListener[] listeners = submit.getMouseListeners();
		if(listeners.length>0) {
			submit.removeMouseListener(listeners[0]);
		}
		submit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for(GameViewObserver observer : observers) {
					observer.moveTroops(troopCount.getValue());
					troopCount.setVisible(false);
					submit.setVisible(false);
				}
			}
		});
		gameBarPanel.add(submit);
	}

	public void closeFrame() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	private ImageIcon scaleImage(int width, int height, String imageLocation) {
		ImageIcon imageIcon = new ImageIcon(imageLocation);
		Image image = imageIcon.getImage();
		Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		ImageIcon imageIconResized = new ImageIcon(resizedImage);
		return imageIconResized;
	}
	
	public void updateTerritoryOwnerDisplay(String territoryName, Color playerColor) {
		changeImageToColor(territoryImage, playerColor);
		ImageIcon image =  new ImageIcon(territoryImage.getScaledInstance(territorySize, territorySize,
				Image.SCALE_AREA_AVERAGING));
		JLabel territoryCircle = territoryCircles.get(territoryName);
		territoryCircle.setIcon(image);
	}
	
	public void updateTerritoryArmyCountDisplay(String territoryName, int armyCount) {
		JLabel territoryCountLabel = territoryArmyCounts.get(territoryName);
		territoryCountLabel.setText(""+armyCount);
	}

	//TODO:
	public void updateGlobalGameState(int currentPlayer, String state){
		globalGameStateLabel.setText("Player " + (currentPlayer+1) + " is " + state);
	}
}