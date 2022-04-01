package risk.model;

import java.util.ArrayList;

public class CardManager {

    ArrayList<Card> infantryCards;
    ArrayList<Card> cavalryCards;
    ArrayList<Card> artilleryCards;

    public CardManager(){
        infantryCards = new ArrayList<>();
        cavalryCards = new ArrayList<>();
        artilleryCards = new ArrayList<>();
    }

    public void add(Card card){
        if(card.getTroopType().equals("Infantry")){
            infantryCards.add(card);
        } else if(card.getTroopType().equals("Cavalry")){
            cavalryCards.add(card);
        } else {
            artilleryCards.add(card);
        }
    }

    public int getCardCount(){
        return infantryCards.size() + cavalryCards.size() + artilleryCards.size();
    }

    public ArrayList<Card> getInfantryCards() {
        return infantryCards;
    }

    public ArrayList<Card> getCavalryCards() {
        return cavalryCards;
    }

    public ArrayList<Card> getArtilleryCards() {
        return artilleryCards;
    }

    public ArrayList<Card> getTotalCards(){
        ArrayList<Card> allCards = new ArrayList<>();
        allCards.addAll(infantryCards);
        allCards.addAll(cavalryCards);
        allCards.addAll(artilleryCards);
        return allCards;
    }

    public void remove(Card cardToRemove){
        infantryCards.remove(cardToRemove);
        cavalryCards.remove(cardToRemove);
        artilleryCards.remove(cardToRemove);
    }

    public ArrayList<Card> determineCardToRemove() {
        if(infantryCards.size()>=3) {
            return(new ArrayList<>(infantryCards.subList(0, 3)));
        }else if(cavalryCards.size()>=3) {
            return(new ArrayList<>(cavalryCards.subList(0, 3)));
        }else if(artilleryCards.size()>=3) {
            return(new ArrayList<>(artilleryCards.subList(0, 3)));
        }else if(infantryCards.size()>0 && cavalryCards.size()>0 && artilleryCards.size()>0) {
            ArrayList<Card> cardsToRemove = new ArrayList<>();
            cardsToRemove.add(infantryCards.get(0));
            cardsToRemove.add(cavalryCards.get(0));
            cardsToRemove.add(artilleryCards.get(0));
            return(cardsToRemove);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
