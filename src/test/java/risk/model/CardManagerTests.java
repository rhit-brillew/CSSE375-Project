package risk.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class CardManagerTests {
    @Test
    public void testDetermineCardToRemove(){
        try {
            CardManager manager = new CardManager();
            manager.determineCardToRemove();
        } catch(IllegalArgumentException ex){
            Assert.assertTrue(true);
        }
    }

    public CardManager createManagerWithThreeCardsOfType(String type){
        CardManager manager = new CardManager();
        manager.add(new Card("", type));
        manager.add(new Card("", type));
        manager.add(new Card("", type));
        return manager;
    }

    @Test
    public void testDetermineCardToRemove2(){
        try {
            CardManager manager = createManagerWithThreeCardsOfType("Wild");
            manager.determineCardToRemove();
        } catch(IllegalArgumentException ex){
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDetermineCardToRemove3(){
        CardManager manager = new CardManager();
        manager.add(new Card("", "Infantry"));
        manager.add(new Card("", "Cavalry"));
        manager.add(new Card("", "Artillery"));
        ArrayList<Card> actual = manager.determineCardToRemove();
        Assert.assertEquals(3, actual.size());
    }

    @Test
    public void testDetermineCardToRemove4(){
        CardManager manager = createManagerWithThreeCardsOfType("Infantry");
        ArrayList<Card> actual = manager.determineCardToRemove();
        Assert.assertEquals(3, actual.size());
    }

    @Test
    public void testDetermineCardToRemove5(){
        CardManager manager = new CardManager();
        manager.add(new Card("", "Infantry"));
        manager.add(new Card("", "Infantry"));
        manager.add(new Card("", "Wild"));
        ArrayList<Card> actual = manager.determineCardToRemove();
        Assert.assertEquals(3, actual.size());
    }

    @Test
    public void testDetermineCardToRemove6(){
        CardManager manager = createManagerWithThreeCardsOfType("Cavalry");
        ArrayList<Card> actual = manager.determineCardToRemove();
        Assert.assertEquals(3, actual.size());
    }

    @Test
    public void testDetermineCardToRemove7(){
        CardManager manager = new CardManager();
        manager.add(new Card("", "Cavalry"));
        manager.add(new Card("", "Cavalry"));
        manager.add(new Card("", "Wild"));
        ArrayList<Card> actual = manager.determineCardToRemove();
        Assert.assertEquals(3, actual.size());
    }

    @Test
    public void testDetermineCardToRemove8(){
        CardManager manager = createManagerWithThreeCardsOfType("Artillery");
        ArrayList<Card> actual = manager.determineCardToRemove();
        Assert.assertEquals(3, actual.size());
    }

    @Test
    public void testDetermineCardToRemove9(){
        CardManager manager = new CardManager();
        manager.add(new Card("", "Artillery"));
        manager.add(new Card("", "Artillery"));
        manager.add(new Card("", "Wild"));
        ArrayList<Card> actual = manager.determineCardToRemove();
        Assert.assertEquals(3, actual.size());
    }

    @Test
    public void testDetermineCardToRemove10(){
        CardManager manager = createManagerWithThreeCardsOfType("Infantry");
        manager.add(new Card("", "Artillery"));
        manager.add(new Card("", "Artillery"));
        manager.add(new Card("", "Wild"));
        ArrayList<Card> actual = manager.determineCardToRemove();
        Assert.assertEquals(3, actual.size());
    }
}
