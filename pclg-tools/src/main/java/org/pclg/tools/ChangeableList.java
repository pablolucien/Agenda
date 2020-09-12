package org.pclg.tools;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChangeableList<T> extends ArrayList<T> implements
		Changeable<ChangeableList<T>> {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = 937052912225672458L;
    private final List<ChangeObserver<ChangeableList<T>>> observers =
		new ArrayList<>();

	@Override
	public boolean add(final T t) {
		final boolean changed = super.add(t);
		if (enabled && changed) {
			LOGGER.debug("put() -> firing PropertyChanged() ");
			firePropertyChanged();
		}
		return changed;
	}

    
    @Override
	public boolean addAll(final Collection<? extends T> coll) {
		final boolean changed = super.addAll(coll);
		if (enabled && changed) {
			LOGGER.debug("put() -> firing PropertyChanged() ");
			firePropertyChanged();
		}
		return changed;
	}


	//FIXME: Todo el c�digo que sigue est� duplicado en ObservableProperties. Factorizarlo!!!
	private boolean enabled = true;
	private boolean somethingChanged;

    /**
	 * Adds a ChangeObserver to the observer list.
	 * @param observer the ChangeObserver to be added.
	 */
	public void addChangeObserver(final ChangeObserver<ChangeableList<T>> observer) {
		observers.add(observer);
	}

	/**
	 * Removes a ChangeObserver from the observer list.
     * Useful to ChangeObserver's that want to be garbage collected.
	 * @param observer the ChangeObserver to be removed.
	 */
	public void removeChangeObserver(final ChangeObserver<ChangeableList<T>> observer) {
		observers.remove(observer);
	}

	private final ScheduledExecutorService notificationExecutor = 
			Executors.newSingleThreadScheduledExecutor();
		private final Runnable notificationCommand = new Runnable() {
			@Override
			public void run() {
				//noinspection HardCodedStringLiteral
				LOGGER.debug("*-*-*NotificationCommand called. observers = " + observers);
				try {
					for (final ChangeObserver<ChangeableList<T>> observer : observers) {
						LOGGER.debug("*-*-*NotificationCommand notifying " + observer);
						observer.objectChanged(ChangeableList.this);
					}
				} catch (final Exception e) {
					LOGGER.error("Error", e);
				}
				somethingChanged = false;
			}
		};
		private ScheduledFuture<?> notificationTask;

	/**
	 * Fires the notification of a change in the properties. 
	 * Will wait 10 milliseconds before notifying, so to not make a lot of
	 * calls in sequence.
	 */
	private void firePropertyChanged() {
		//noinspection HardCodedStringLiteral
		LOGGER.debug("firePropertyChanged() called");
		if (notificationTask != null) {
			//noinspection HardCodedStringLiteral
			LOGGER.debug("------> notificationTask cancelled");
			notificationTask.cancel(true);
		}
		notificationTask = notificationExecutor.schedule(notificationCommand, 
			10, TimeUnit.MILLISECONDS);
	}


	public void enableNotifications(final boolean enabled) {
		this.enabled = enabled;
		if (enabled && somethingChanged) {
			firePropertyChanged();
		}
	}
}
