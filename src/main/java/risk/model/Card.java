package risk.model;

public class Card {

	private String territoryName;
	private String troopType;

	public Card(String territoryName, String troopType) {
		this.territoryName = territoryName;
		this.troopType = troopType;
	}

	public String getTerritoryName() {
		return this.territoryName;
	}

	public String getTroopType() {
		return this.troopType;
	}
}
