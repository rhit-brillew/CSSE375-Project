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
import risk.model.Edge;
import risk.model.PlayerModel;
import risk.model.TerritoryModel;

public class TerritoryMapController {
	ArrayList<TerritoryModel> allTerritories = new ArrayList<>();
	ArrayList<Card> deck = new ArrayList<>();
	ArrayList<Edge> edges = new ArrayList<>();
	HashMap<String, Integer> continents = new HashMap<String, Integer>();

	public void addTerritory(TerritoryModel territoryModel) {
		allTerritories.add(territoryModel);
	}

	public void addContinent(String name, Integer troopValue){
		continents.put(name, troopValue);
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

	public int numberTroopsForOwningContinent(Color territoryOwner){
		int bonusTroops = 0;
		for(String cont : this.continents.keySet()){
			if(checkOwnsContinent(territoryOwner, cont)){
				bonusTroops += this.continents.get(cont);
			}
		}
		return bonusTroops;
	}

	public boolean checkOwnsContinent(Color territoryOwner, String continentName){
		ArrayList<String> territories = getAllTerritoriesInContinent(continentName);
		for (String territory : territories) {
			if (!getTerritoryByName(territory).getOwner().equals(territoryOwner)) {
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

	public static TerritoryMapController loadTerritoryXMLData(int board) {
		TerritoryMapController territories = readTerritoriesXML(board);
		addEdgeMapFromXML(territories, board);
		return territories;
	}

	private static TerritoryMapController readTerritoriesXML(int board) {
		TerritoryMapController territoryMapController = new TerritoryMapController();
		NodeList list, continentList;

		try {
			File file = new File("src/main/resources/board"+ board +"/territoryLocations.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document document = docBuilder.parse(file);
			document.getDocumentElement().normalize();
			list = document.getElementsByTagName("territory");
			continentList = document.getElementsByTagName("continent");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		addTerritoriesToController(list, territoryMapController);
		addContinentsToController(continentList, territoryMapController);
		return territoryMapController;
	}

	private static void addTerritoriesToController(NodeList list, TerritoryMapController controller){
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Element element = (Element) node;
			String name = element.getAttribute("name");
			int x = Integer.parseInt(element.getAttribute("x"));
			int y = Integer.parseInt(element.getAttribute("y"));
			String continent = element.getAttribute("continent");
			Point location = new Point(x, y);
			String troopType = element.getAttribute("troopType");
			controller.addTerritory(new TerritoryModel(name, location, continent));
			controller.addCard(new Card(name, troopType));
		}
	}

	private static void addContinentsToController(NodeList contList, TerritoryMapController controller){
		for (int i = 0; i < contList.getLength(); i++) {
			Node node = contList.item(i);
			Element element = (Element) node;
			String name = element.getAttribute("name");
			int troopValue = Integer.parseInt(element.getAttribute("troopValue"));
			controller.addContinent(name,troopValue);
		}
	}

	private static void addEdgeMapFromXML(TerritoryMapController territoryMapController, int board) {
		NodeList nodeList;
		try {
			File file = new File("src/main/resources/board"+ board +"/territoryEdges.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			nodeList = doc.getElementsByTagName("edge");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		addEdgesToController(nodeList, territoryMapController);
	}

	private static void addEdgesToController(NodeList nodeList, TerritoryMapController controller){
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			Element element = (Element) node;
			String t1 = element.getAttribute("t1");
			String t2 = element.getAttribute("t2");
			controller.addEdge(t1, t2);
		}
	}
}