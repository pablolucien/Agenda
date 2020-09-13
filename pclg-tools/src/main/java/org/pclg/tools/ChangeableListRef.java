package org.pclg.tools;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class ChangeableListRef<T> extends ArrayList<T> implements Changeable<ChangeableListRef<T>> {
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = 6520450779265428655L;
    private final List<SoftReference<ChangeObserver<ChangeableListRef<T>>>> observersRefs =
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
	public void addChangeObserver(final ChangeObserver<ChangeableListRef<T>> observer) {
		observersRefs.add(new SoftReference<>(observer));
	}

	/**
	 * Removes a ChangeObserver from the observer list.
     * Useful to ChangeObserver's that want to be garbage collected.
	 * @param observer the ChangeObserver to be removed.
	 */
	public void removeChangeObserver(final ChangeObserver<ChangeableListRef<T>> observer) {
		for (final SoftReference<ChangeObserver<ChangeableListRef<T>>> observerRef : observersRefs) {
			if (observerRef.get() == observer) {
				observerRef.clear();
				observersRefs.remove(observerRef);
			}
		}
	}

	private final ScheduledExecutorService notificationExecutor = 
        Executors.newSingleThreadScheduledExecutor();
    private final Runnable notificationCommand = new Runnable() {
        @Override
        public void run() {
            //noinspection HardCodedStringLiteral
            LOGGER.debug("*-*-*NotificationCommand called. observers = " + observersRefs + " con " + observersRefs.size() + " elementos");
            for (final Iterator<SoftReference<ChangeObserver<ChangeableListRef<T>>>> iterator = observersRefs.iterator();
                    iterator.hasNext();) {
                final SoftReference<ChangeObserver<ChangeableListRef<T>>> observerRef = iterator.next();
                final ChangeObserver<ChangeableListRef<T>> observer = observerRef.get();
                if (observer == null) {
                    LOGGER.debug("El referente de " + observerRef + " ya no existe");
                    iterator.remove();
                } else {
                    LOGGER.debug("El referente de " + observerRef + " si existe");
                    observer.objectChanged(ChangeableListRef.this);
                }
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
