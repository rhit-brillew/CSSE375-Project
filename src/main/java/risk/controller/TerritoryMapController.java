package risk.controller;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import risk.model.Card;
import risk.model.PlayerModel;
import risk.model.TerritoryModel;

public class TerritoryMapController {
	ArrayList<TerritoryModel> allTerritories = new ArrayList<>();
	ArrayList<Card> deck = new ArrayList<>();
	ArrayList<Edge> edges = new ArrayList<>();

	static class Edge {
		private String t1;
		private String t2;
		public boolean traversed;

		Edge(String t1, String t2) {
			this.t1 = t1;
			this.t2 = t2;
			traversed = false;
		}

		public String getT1() {
			return  t1;
		}

		public String getT2() {
			return t2;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Edge edge = (Edge) o;
			return traversed == edge.traversed && t1.equals(edge.t1) && t2.equals(edge.t2);
		}
		
		@Override
		public int hashCode() {
			throw new UnsupportedOperationException("Hashcode not supported");
		}
	}

	public void addTerritory(TerritoryModel territoryModel) {
		allTerritories.add(territoryModel);
	}
	
	public void addCard(Card newCard) {
		deck.add(newCard);
	}

	public HashMap<String, Point> getNameLocationHashMap() {
		HashMap<String, Point> nameLocationHashMap = new HashMap<>();
		for(TerritoryModel territory : allTerritories) {
			nameLocationHashMap.put(territory.getName(), territory.getLocation());
		}
		return nameLocationHashMap;
	}

	public TerritoryModel getTerritoryByName(String name) {
		for(TerritoryModel territory : allTerritories) {
			if(territory.getName().equals(name)) {
				return territory;
			}
		}
		throw new IllegalArgumentException("Territory name must be a valid string");
	}

	public boolean areThereUnclaimedTerritories() {
		for(TerritoryModel territory : allTerritories) {
			if(!territory.isOwned()) {
				return true;
			}
		}
		return false;
	}

	public void addEdge(String t1, String t2) {
		edges.add(new Edge(t1, t2));
	}

	public boolean areTerritoriesAdjacent(String t1, String t2) {
		for(Edge edge : edges) {
			if(edge.getT1().equals(t1) && edge.getT2().equals(t2)) {
				return true;
			} else if(edge.getT1().equals(t2) && edge.getT2().equals(t1)) {
				return true;
			}
		}
		return false;
	}

	public boolean areTerritoriesConnectedByOwnedTerritories(String starting, String target) {
		if(!getTerritoryByName(starting).isOwned() || !getTerritoryByName(target).isOwned()) {
			return false;
		}
		if(getTerritoryByName(starting).getOwner() != getTerritoryByName(target).getOwner()) {
			return false;
		}

		setTraversedFalseForAllEdges();
		ArrayList<Edge> edgesToCheck = getAllNotTraversedEdgesConnectedToTerritoryOfSameOwner(starting);
		for(Edge edge : edgesToCheck) {
			if(recursiveSearch(starting, target, edge)) {
				return true;
			}
		}
		return false;
	}

	private boolean recursiveSearch(String startingTerritory, String targetTerritory, Edge edge) {
		edge.traversed = true;
		String otherTerritory;
		if(edge.getT1().equals(startingTerritory)) {
			otherTerritory = edge.getT2();
		} else {
			otherTerritory = edge.getT1();
		}

		if(otherTerritory.equals(targetTerritory)) {
			return true;
		}

		ArrayList<Edge> edgesToCheck = getAllNotTraversedEdgesConnectedToTerritoryOfSameOwner(otherTerritory);
		for(Edge otherEdge : edgesToCheck) {
			if(recursiveSearch(otherTerritory, targetTerritory, otherEdge)) {
				return true;
			}
		}
		return false;
	}

	private void setTraversedFalseForAllEdges() {
		for(Edge edge : edges) {
			edge.traversed = false;
		}
	}

	private ArrayList<Edge> getAllNotTraversedEdgesConnectedToTerritoryOfSameOwner(String name) {
		ArrayList<Edge> results = new ArrayList<>();
		for(Edge edge : edges) {
			if(!edge.traversed) {
				if (edge.getT1().equals(name) || edge.getT2().equals(name)) {
					if (getTerritoryByName(edge.getT1()).isOwned()) {
						if (getTerritoryByName(edge.getT1()).getOwner() == getTerritoryByName(edge.getT2()).getOwner()) {
							results.add(edge);
						}
					}
				}
			}
		}
		return results;
	}

	public int calculateNumberOfTerritoriesPlayerOwns(PlayerModel playerModel){
		int numberOfTerritories = 0;
		for(TerritoryModel territory: allTerritories){
			if(territory.getOwner().equals(playerModel.getColor())){
				numberOfTerritories++;
			}
		}
		return numberOfTerritories;
	}

	public void shuffleDeck() {
		Collections.shuffle(deck);
	}

	public void setDeck(ArrayList<Card> cardList){
		this.deck = cardList;
	}

	public boolean ownsContinentAsia(Color territoryOwner){
		ArrayList<String> asianTerritories = getAllTerritoriesInContinent("Asia");
		for(int i = 0; i < asianTerritories.size(); i++){
			if(!getTerritoryByName(asianTerritories.get(i)).getOwner().equals(territoryOwner)){
				return false;
			}
		}
		return true;
	}

	private ArrayList<String> getAllTerritoriesInContinent(String continent) {
		ArrayList<String> result = new ArrayList<>();
		for(TerritoryModel territory : allTerritories) {
			if(territory.getContinent().equals(continent)) {
				result.add(territory.getName());
			}
		}
		return result;
	}

	public boolean ownsContinentEurope(Color territoryOwner){
		ArrayList<String> europeanTerritories = getAllTerritoriesInContinent("Europe");
		for(int i = 0; i < europeanTerritories.size(); i++){
			if(!getTerritoryByName(europeanTerritories.get(i)).getOwner().equals(territoryOwner)){
				return false;
			}
		}
		return true;
	}

	public boolean ownsContinentNorthAmerica(Color territoryOwner){
		ArrayList<String> northAmericanTerritories = getAllTerritoriesInContinent("North America");
		for(int i = 0; i < northAmericanTerritories.size(); i++){
			if(!getTerritoryByName(northAmericanTerritories.get(i)).getOwner().equals(territoryOwner)){
				return false;
			}
		}
		return true;
	}

	public boolean ownsContinentSouthAmerica(Color territoryOwner){
		ArrayList<String> southAmericanTerritories = getAllTerritoriesInContinent("South America");
		for(int i = 0; i < southAmericanTerritories.size(); i++){
			if(!getTerritoryByName(southAmericanTerritories.get(i)).getOwner().equals(territoryOwner)){
				return false;
			}
		}
		return true;
	}

	public boolean ownsContinentAfrica(Color territoryOwner){
		ArrayList<String> africanTerritories = getAllTerritoriesInContinent("Africa");
		for(int i = 0; i < africanTerritories.size(); i++){
			if(!getTerritoryByName(africanTerritories.get(i)).getOwner().equals(territoryOwner)){
				return false;
			}
		}
		return true;
	}

	public boolean ownsContinentAustralia(Color territoryOwner){
		ArrayList<String> australianTerritories = getAllTerritoriesInContinent("Australia");
		for(int i = 0; i < australianTerritories.size(); i++){
			if(!getTerritoryByName(australianTerritories.get(i)).getOwner().equals(territoryOwner)){
				return false;
			}
		}
		return true;
	}

	public static TerritoryMapController loadTerritoryXMLData() {
		TerritoryMapController territories = readTerritoriesXML();
		addEdgeMapFromXML(territories);
		return territories;
	}

	private static TerritoryMapController readTerritoriesXML() {
		TerritoryMapController territoryMapController = new TerritoryMapController();
		NodeList list;

		try {
			File file = new File("src/main/resources/territoryLocations.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document document = docBuilder.parse(file);
			document.getDocumentElement().normalize();
			list = document.getElementsByTagName("territory");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Element element = (Element) node;
			String name = element.getAttribute("name");
			int x = Integer.parseInt(element.getAttribute("x"));
			int y = Integer.parseInt(element.getAttribute("y"));
			String continent = element.getAttribute("continent");
			Point location = new Point(x, y);
			String troopType = element.getAttribute("troopType");
			territoryMapController.addTerritory(new TerritoryModel(name, location, continent));
			territoryMapController.addCard(new Card(name, troopType));
		}
		return territoryMapController;
	}

	private static void addEdgeMapFromXML(TerritoryMapController territoryMapController) {
		NodeList nodeList;
		try {
			File file = new File("src/main/resources/territoryEdges.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			nodeList = doc.getElementsByTagName("edge");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			Element element = (Element) node;
			String t1 = element.getAttribute("t1");
			String t2 = element.getAttribute("t2");
			territoryMapController.addEdge(t1, t2);
		}
	}
}
