package risk.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.*;
import risk.controller.StartingOptionsObservable;
import risk.controller.StartingOptionsObserver;
import java.awt.Toolkit;

public class StartingOptionsView extends StartingOptionsObservable {
	private JButton[] playerNumberButtons;
	private int selectedPlayerButtonIndex = 0;
	private static final int numberOfPlayerButtons = 4;

	private JButton[] localizationButtons;
	private int selectedLocalizationIndex = 0;
	private static final int numberOfLocalizationButtons = 2;

	private JButton[] boardButtons;
	private int selectedBoardIndex = 0;
	private static final int numberOfBoardButtons = 2;

	private static final Color selectedColor = new Color(69, 131, 196);

	public StartingOptionsView() {
		JFrame frame = new JFrame("Initialization Settings");
		frame.setSize(700, 450);
		frame.setVisible(true);
		addBackgroundImageToFrame(frame);
		addPlayerButtonsPanelToFrame(frame);
		addLocalizationButtonsPanelToFrame(frame);
		addBoardButtonsPanelToFrame(frame);
		addStartGamePanelToFrame(frame);
		frame.validate();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void addBackgroundImageToFrame(JFrame frame) {
		try {
			ImageIcon icon  = new ImageIcon(new ImageIcon(ImageIO.read(new File("src/main/resources/images/RISKBOX.PNG"))).getImage().getScaledInstance(frame.getWidth(),frame.getHeight(), Image.SCALE_SMOOTH));
			frame.setContentPane(new JLabel(icon));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addPlayerButtonsPanelToFrame(JFrame frame) {

		JPanel playerButtonsPanel = new JPanel();
		playerButtonsPanel.setLayout(new GridBagLayout());
		playerButtonsPanel.setBounds(new Rectangle(50, 250, 175, 100));
		playerButtonsPanel.setOpaque(false);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = numberOfPlayerButtons;
		c.anchor = GridBagConstraints.LINE_START;
		JLabel numberOfPlayersLabel = new JLabel("Number of Players");
		numberOfPlayersLabel.setForeground(Color.WHITE);
		playerButtonsPanel.add(numberOfPlayersLabel, c);

		addPlayerButtonsToPanel(playerButtonsPanel);
		correctlyHighlightSelectedPlayerButton();

		frame.add(playerButtonsPanel);
	}

	private void addPlayerButtonsToPanel(JPanel panel) {
		playerNumberButtons = new JButton[numberOfPlayerButtons];
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		for(int i = 0; i < numberOfPlayerButtons; i++) {
			c.gridx = i;
			JButton button = new JButton(String.valueOf(i+2));
			button.setFocusable(false);
			int finalI = i;
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedPlayerButtonIndex = finalI;
					correctlyHighlightSelectedPlayerButton();
				}
			});
			playerNumberButtons[i] = button;
			panel.add(button, c);
		}
	}

	private void correctlyHighlightSelectedPlayerButton() {
		for(int i = 0; i < numberOfPlayerButtons; i++) {
			if(i == selectedPlayerButtonIndex) {
				playerNumberButtons[i].setBackground(selectedColor);
			} else {
				playerNumberButtons[i].setBackground(Color.WHITE);
			}
		}
	}

	private void addLocalizationButtonsPanelToFrame(JFrame frame) {
		JPanel localizationButtonsPanel = new JPanel();
		localizationButtonsPanel.setLayout(new GridBagLayout());
		localizationButtonsPanel.setBounds(new Rectangle(250, 250, 175, 100));
		localizationButtonsPanel.setOpaque(false);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = numberOfLocalizationButtons;
		c.anchor = GridBagConstraints.LINE_START;
		JLabel localizationLabel = new JLabel("Language");
		localizationLabel.setForeground(Color.WHITE);
		localizationButtonsPanel.add(localizationLabel, c);

		addLocalizationButtonsToPanel(localizationButtonsPanel);
		correctlyHighlightSelectedLocalizationButton();

		frame.add(localizationButtonsPanel);
	}

	private void addLocalizationButtonsToPanel(JPanel panel) {
		localizationButtons = new JButton[numberOfLocalizationButtons];
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		for(int i = 0; i < numberOfLocalizationButtons; i++) {
			c.gridx = i;
			JButton button;
			if (i == 0) {
				button = new JButton("English");
			} else {
				button = new JButton("Spanish");
			}
			button.setFocusable(false);
			int finalI = i;
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedLocalizationIndex = finalI;
					correctlyHighlightSelectedLocalizationButton();
				}
			});
			localizationButtons[i] = button;
			panel.add(button, c);
		}
	}

	private void correctlyHighlightSelectedLocalizationButton() {
		for(int i = 0; i < numberOfLocalizationButtons; i++) {
			if(i == selectedLocalizationIndex) {
				localizationButtons[i].setBackground(selectedColor);
			} else {
				localizationButtons[i].setBackground(Color.WHITE);
			}
		}
	}

	private void addBoardButtonsPanelToFrame(JFrame frame) {
		JPanel boardButtonsPanel = new JPanel();
		boardButtonsPanel.setLayout(new GridBagLayout());
		boardButtonsPanel.setBounds(new Rectangle(450, 250, 175, 100));
		boardButtonsPanel.setOpaque(false);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = numberOfBoardButtons;
		c.anchor = GridBagConstraints.LINE_START;
		JLabel boardLabel = new JLabel("Board Option");
		boardLabel.setForeground(Color.WHITE);
		boardButtonsPanel.add(boardLabel, c);

		addBoardButtonsToPanel(boardButtonsPanel);
		correctlyHighlightSelectedBoardButton();

		frame.add(boardButtonsPanel);
	}

	private void addBoardButtonsToPanel(JPanel panel) {
		boardButtons = new JButton[numberOfBoardButtons];
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		for(int i = 0; i < numberOfBoardButtons; i++) {
			c.gridx = i;
			JButton button;
			button = new JButton("Board"+(i+1));
			button.setFocusable(false);
			int finalI = i;
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedBoardIndex = finalI;
					correctlyHighlightSelectedBoardButton();
				}
			});
			boardButtons[i] = button;
			panel.add(button, c);
		}
	}

	private void correctlyHighlightSelectedBoardButton() {
		for(int i = 0; i < numberOfBoardButtons; i++) {
			if(i == selectedBoardIndex) {
				boardButtons[i].setBackground(selectedColor);
			} else {
				boardButtons[i].setBackground(Color.WHITE);
			}
		}
	}

	private void addStartGamePanelToFrame(JFrame frame) {
		JPanel startGamePanel = new JPanel();
		startGamePanel.setLayout(new GridBagLayout());
		startGamePanel.setBounds(new Rectangle(250, 320, 175, 100));
		startGamePanel.setOpaque(false);

		JButton button = new JButton("Start Game");
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				notifyObserversOfPlayerCount();
				notifyObserversOfLocale();
				notifyObserversOfBoard();
				notifyObserversToStartGame();
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;

		startGamePanel.add(button, c);
		frame.add(startGamePanel);
	}

	void notifyObserversOfPlayerCount() {
		for(StartingOptionsObserver observer : observers) {
			observer.setNumberOfPlayers(selectedPlayerButtonIndex + 2);
		}
	}

	void notifyObserversOfLocale() {
		Locale locale;
		if(selectedLocalizationIndex == 0) {
			locale = new Locale("en", "US");
		} else {
			locale = new Locale("es", "ES");
		}
		for(StartingOptionsObserver observer : observers) {
			observer.setLocale(locale);
		}
	}

	void notifyObserversOfBoard(){
		for(StartingOptionsObserver observer : observers){
			observer.setBoard(selectedBoardIndex+1);
		}
	}

	void notifyObserversToStartGame() {
		for(StartingOptionsObserver observer : observers) {
			observer.startGame();
		}
	}
}
