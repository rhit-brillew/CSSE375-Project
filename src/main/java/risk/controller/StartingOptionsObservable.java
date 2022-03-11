package risk.controller;

import java.util.HashSet;
import java.util.Set;

public abstract class StartingOptionsObservable {
	protected Set<StartingOptionsObserver> observers = new HashSet<>();

	public void addObserver(StartingOptionsObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(StartingOptionsObserver observer) {
		observers.remove(observer);
	}
}
