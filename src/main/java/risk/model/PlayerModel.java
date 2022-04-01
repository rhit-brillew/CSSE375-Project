package risk.model;

import java.awt.*;
import java.util.ArrayList;

public class PlayerModel {
	private int numberOfUnplacedArmies;
	private Color color;
	private CardManager cards;

	public PlayerModel(Integer unplacedArmies, Color color){
		if(unplacedArmies > 40){
			throw new IllegalArgumentException("Player must be created with less than 40 unplaced armies");
		} else if(unplacedArmies < 0){
			throw new IllegalArgumentException("Player must be created with a positive number of armies");
		} else {
			this.numberOfUnplacedArmies = unplacedArmies;
			this.color = color;
			this.cards = new CardManager();
		}
	}

	public int getNumberOfUnplacedArmies() {
		return numberOfUnplacedArmies;
	}

	public Color getColor() {
		return this.color;
	}

	public void setNumberOfUnplacedArmies(int numberOfUnplacedArmies) {
		this.numberOfUnplacedArmies = numberOfUnplacedArmies;
	}

	public void addNumberOfUnplacedArmies(int numberOfUnplacedArmies) {
		this.numberOfUnplacedArmies += numberOfUnplacedArmies;
	}

	public void placeArmy() {
		numberOfUnplacedArmies--;
	}

	public void addCard(Card newCard) {
		cards.add(newCard);
	}

	public int getCardCount() { return cards.getCardCount(); }

	public CardManager getCards() { return cards; }

	public void removeCard(Card cardToRemove) {
		this.cards.remove(cardToRemove);
	}
}