package risk.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.*;
import risk.controller.StartingOptionsObservable;
import risk.controller.StartingOptionsObserver;

public class StartingOptionsView extends StartingOptionsObservable {
	private JButton[] playerNumberButtons;
	private int selectedPlayerButtonIndex = 0;
	private static final int numberOfPlayerButtons = 4;

	private JButton[] localizationButtons;
	private int selectedLocalizationIndex = 0;
	private static final int numberOfLocalizationButtons = 2;

	private static final Color selectedColor = new Color(69, 131, 196);

	public StartingOptionsView() {
		JFrame frame = new JFrame("Initialization Settings");
		frame.setSize(300, 475);
		frame.setVisible(true);
		addPlayerButtonsPanelToFrame(frame);
		addLocalizationButtonsPanelToFrame(frame);
		addStartGamePanelToFrame(frame);
		frame.validate();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void addPlayerButtonsPanelToFrame(JFrame frame) {
		JPanel playerButtonsPanel = new JPanel();
		playerButtonsPanel.setLayout(new GridBagLayout());
		playerButtonsPanel.setBounds(new Rectangle(0, 0, 175, 100));
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = numberOfPlayerButtons;
		c.anchor = GridBagConstraints.LINE_START;
		JLabel numberOfPlayersLabel = new JLabel("Number of Players");
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
		localizationButtonsPanel.setBounds(new Rectangle(0, 100, 175, 100));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = numberOfLocalizationButtons;
		c.anchor = GridBagConstraints.LINE_START;
		JLabel localizationLabel = new JLabel("Language");
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

	private void addStartGamePanelToFrame(JFrame frame) {
		JPanel startGamePanel = new JPanel();
		startGamePanel.setLayout(new GridBagLayout());
		startGamePanel.setBounds(new Rectangle(0, 0, 175, 200));

		JButton button = new JButton("Start Game");
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				notifyObserversOfPlayerCount();
				notifyObserversOfLocale();
				notifyObserversToStartGame();
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LAST_LINE_START;
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

	void notifyObserversToStartGame() {
		for(StartingOptionsObserver observer : observers) {
			observer.startGame();
		}
	}
}
