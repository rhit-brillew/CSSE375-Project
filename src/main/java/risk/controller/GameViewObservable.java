package risk.controller;

import java.util.HashSet;
import java.util.Set;

public abstract class GameViewObservable {
	protected Set<GameViewObserver> observers = new HashSet<>();

	public void addObserver(GameViewObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(GameViewObserver observer) {
		observers.remove(observer);
	}
}
