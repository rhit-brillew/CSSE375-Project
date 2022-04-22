package risk.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CardManager {

    ArrayList<Card> infantryCards;
    ArrayList<Card> cavalryCards;
    ArrayList<Card> artilleryCards;
    ArrayList<Card> wildCards;

    public CardManager(){
        infantryCards = new ArrayList<>();
        cavalryCards = new ArrayList<>();
        artilleryCards = new ArrayList<>();
        wildCards = new ArrayList<>();
    }

    public void add(Card card){
        if(card.getTroopType().equals("Infantry")){
            infantryCards.add(card);
        } else if(card.getTroopType().equals("Cavalry")){
            cavalryCards.add(card);
        } else if(card.getTroopType().equals("Artillery")){
            artilleryCards.add(card);
        } else {
            wildCards.add(card);
        }
    }

    public int getCardCount(){
        return infantryCards.size() + cavalryCards.size() + artilleryCards.size() + wildCards.size();
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

    public ArrayList<Card> getWildCards() {
        return wildCards;
    }

    public ArrayList<Card> getTotalCards(){
        ArrayList<Card> allCards = new ArrayList<>();
        allCards.addAll(infantryCards);
        allCards.addAll(cavalryCards);
        allCards.addAll(artilleryCards);
        allCards.addAll(wildCards);
        return allCards;
    }

    public void remove(Card cardToRemove){
        infantryCards.remove(cardToRemove);
        cavalryCards.remove(cardToRemove);
        artilleryCards.remove(cardToRemove);
        wildCards.remove(cardToRemove);
    }

    public void removeAll(ArrayList<Card> cardsToRemove){
        for(Card currentCard : cardsToRemove){
            remove(currentCard);
        }
    }

    public ArrayList<Card> determineCardToRemove() {
        ArrayList<Card> cardsToRemove = tryRemoveCardsOfType(infantryCards);
        if(cardsToRemove != null){
            return cardsToRemove;
        }

        cardsToRemove = tryRemoveCardsOfType(cavalryCards);
        if(cardsToRemove != null){
            return cardsToRemove;
        }

        cardsToRemove = tryRemoveCardsOfType(artilleryCards);
        if(cardsToRemove != null){
            return cardsToRemove;
        }

        if(infantryCards.size()>0 && cavalryCards.size()>0 && artilleryCards.size()>0) {
            cardsToRemove = new ArrayList<>();
            cardsToRemove.add(infantryCards.get(0));
            cardsToRemove.add(cavalryCards.get(0));
            cardsToRemove.add(artilleryCards.get(0));
            return(cardsToRemove);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public ArrayList<Card> tryRemoveCardsOfType(ArrayList<Card> troopCards){
        if(troopCards.size() >= 3){
            return(new ArrayList<>(troopCards.subList(0, 3)));
        } else if(troopCards.size() == 2 && wildCards.size() >= 1){
            ArrayList<Card> returnList = new ArrayList<>(troopCards.subList(0, 2));
            returnList.add(wildCards.get(0));
            return(returnList);
        }
        return null;
    }
}
