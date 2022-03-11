package risk.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class TerritoryModel {
	int numberOfArmies;
	private Point location;
	private String name;
	Color owner; // Default of gray means no owner
	String continent;

	public TerritoryModel(String name, Point location, String continent) {
		numberOfArmies = 0;
		this.location = location;
		this.name = name;
		this.continent = continent;
		owner = Color.GRAY;
	}

	public int getNumberOfArmies() {
		return numberOfArmies;
	}

	public boolean isOwned() {
		return owner != Color.GRAY;
	}

	public Color getOwner() {
		return owner;
	}

	public Point getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public void setOwner(Color owner) {
		this.owner = owner;
	}

	public void changeArmyAmountBy(int value) {
		numberOfArmies += value;
	}

	public String getContinent() {
		return continent;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TerritoryModel that = (TerritoryModel) o;
		return numberOfArmies == that.numberOfArmies && location.equals(that.location)
				&& name.equals(that.name) && owner.equals(that.owner) && continent.equals(that.continent);
	}
	
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException("Hashcode not supported");
	}
}