package risk.view;

import org.easymock.EasyMock;
import org.junit.Test;
import risk.controller.StartingOptionsObserver;

/**
 * BVA: addObserver
 * CASE                         | EXPECTED
 * EmptyCollection              | The new observer is added and notified.
 * CollectionOfSizeOne          | The new observer is added and both are notified.
 * CollectionOfSizeMoreThanOne  | The new observer is added and all are notified.
 *
 * BVA: removeObserver
 * CASE                         | EXPECTED
 * EmptyCollection              | Nothing happens.
 * CollectionOfSizeOne          | The only observer is removed and not notified.
 * CollectionOfSizeMoreThanOne  | The selected observer is removed and not notified; 1remaining observers are notified.
 */

public class TestStartingOptionsView {
	@Test
	public void addObserverEmptyCollection() {
		testAddNObserves(1);
	}

	@Test
	public void addObserverToCollectionSizeOne() {
		testAddNObserves(2);
	}

	@Test
	public void addObserverToCollectionSizeMoreThanOne() {
		testAddNObserves(5);
	}

	private void testAddNObserves(int n) {
		StartingOptionsView view = new StartingOptionsView();

		// RECORD
		StartingOptionsObserver[] observers = createNObserversThatExpectSetNumberOfPlayersTwo(n);

		// REPLAY
		EasyMock.replay((Object[]) observers);
		for(StartingOptionsObserver observer : observers) {
			view.addObserver(observer);
		}
		view.notifyObserversOfPlayerCount();

		// VERIFY
		EasyMock.verify((Object[]) observers);
	}

	private StartingOptionsObserver[] createNObserversThatExpectSetNumberOfPlayersTwo(int n) {
		StartingOptionsObserver[] observers = new StartingOptionsObserver[n];
		for(int i = 0; i < n; i++) {
			StartingOptionsObserver startingOptionsObserver = EasyMock.strictMock(StartingOptionsObserver.class);
			startingOptionsObserver.setNumberOfPlayers(2);
			observers[i] = startingOptionsObserver;
		}
		return observers;
	}

	@Test
	public void testRemoveObserverEmptyCollection() {
		StartingOptionsView view = new StartingOptionsView();

		// RECORD
		StartingOptionsObserver startingOptionsObserver = EasyMock.strictMock(StartingOptionsObserver.class);

		// REPLAY
		EasyMock.replay(startingOptionsObserver);
		view.removeObserver(startingOptionsObserver);
		view.notifyObserversOfPlayerCount();

		// VERIFY
		EasyMock.verify(startingOptionsObserver);
	}

	@Test
	public void testRemoveObserverCollectionSizeOne() {
		StartingOptionsView view = new StartingOptionsView();

		// RECORD
		StartingOptionsObserver startingOptionsObserver = EasyMock.strictMock(StartingOptionsObserver.class);

		// REPLAY
		EasyMock.replay(startingOptionsObserver);
		view.addObserver(startingOptionsObserver);
		view.removeObserver(startingOptionsObserver);
		view.notifyObserversOfPlayerCount();

		// VERIFY
		EasyMock.verify(startingOptionsObserver);
	}

	@Test
	public void testRemoveObserverCollectionSizeMoreThanOne() {
		StartingOptionsView view = new StartingOptionsView();

		// RECORD
		StartingOptionsObserver removedStartingOptionsObserver = EasyMock.strictMock(StartingOptionsObserver.class);
		StartingOptionsObserver[] observers = createNObserversThatExpectSetNumberOfPlayersTwo(5);

		// REPLAY
		EasyMock.replay((Object[]) observers);
		EasyMock.replay(removedStartingOptionsObserver);
		view.addObserver(removedStartingOptionsObserver);
		for(StartingOptionsObserver observer : observers) {
			view.addObserver(observer);
		}
		view.removeObserver(removedStartingOptionsObserver);
		view.notifyObserversOfPlayerCount();

		// VERIFY
		EasyMock.verify((Object[]) observers);
		EasyMock.verify(removedStartingOptionsObserver);
	}
}
