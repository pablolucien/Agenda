package org.pclg.tools;

import org.apache.log4j.Category;
import org.pclg.log.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A properties class that can notify of changes. 
 * @author El Coyote
 * @since 2014.06.19  
 */
public class ObservableProperties extends Properties implements Changeable<ObservableProperties> {
	/** serialVersionUID. */
	private static final long serialVersionUID = -2671331071912752130L;
	private static final Category LOGGER = LoggerFactory.makeLog4J();
	private final List<ChangeObserver<ObservableProperties>> observers = new LinkedList<>();
	private boolean enabled = true;
	private boolean somethingChanged;

	private final ScheduledExecutorService notificationExecutor = 
		Executors.newSingleThreadScheduledExecutor();
	private final Runnable notificationCommand = new Runnable() {
		@Override
		public void run() {
			//noinspection HardCodedStringLiteral
			LOGGER.debug("*-*-*NotificationCommand called. observers = " + observers);
            observers.forEach(observer -> observer.objectChanged(ObservableProperties.this));
			somethingChanged = false;
		}
	};
	private ScheduledFuture<?> notificationTask;

	/**
	 * Adds a ChangeObserver to the observer list.
	 * @param observer the ChangeObserver to be added.
	 */
	public void addChangeObserver(final ChangeObserver<ObservableProperties> observer) {
		observers.add(observer);
	}

	/**
	 * Removes a ChangeObserver from the observer list.
     * Useful to ChangeObserver's that want to be garbage collected.
	 * @param observer the ChangeObserver to be removed.
	 */
    public void removeObserver(final ChangeObserver<ObservableProperties> observer) {
		observers.removeIf(observablePropertiesChangeObserver -> observablePropertiesChangeObserver == observer);
    }

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

	/**
	 * This method just calls super.put() and then notifies that a change
	 * has been made. This is the method finally being called by setProperty(),
	 * load() and loadFromXML().
	 *
	 * @deprecated Because Properties inherits from Hashtable, the put and
	 * putAll methods can be applied to a Properties object. Their use is
	 * strongly discouraged as they allow the caller to insert entries whose
	 * keys or values are not Strings. The setProperty method should be
	 * used instead.
	 */
	@Override
	@Deprecated
	public Object put(final Object key, final Object value) {
		//noinspection HardCodedStringLiteral
		LOGGER.debug("put() -> Property " + key + " changed to " + value);
		final Object oldValue = super.put(key, value);
		if (!value.equals(oldValue)) {
			somethingChanged = true;
			if (enabled) {
				LOGGER.debug("put() -> firing PropertyChanged() ");
				firePropertyChanged();
			}
		}
		return oldValue;
	}

	/**
	 * This method just calls super.putAll() and then notifies that a change
	 * has been made.
	 *
	 * @deprecated Because Properties inherits from Hashtable, the put and
	 * putAll methods can be applied to a Properties object. Their use is
	 * strongly discouraged as they allow the caller to insert entries whose
	 * keys or values are not Strings. The setProperty method should be
	 * used instead.
	 */
	@SuppressWarnings("TypeParameterExplicitlyExtendsObject") // Es un override.
	@Override
	@Deprecated
	public void putAll(final Map<?, ?> map) {
		//noinspection HardCodedStringLiteral
		LOGGER.debug("putAll() called.");
		super.putAll(map);
		somethingChanged = true;	// I suppose that something changed
		// Calling firePropertyChanged() since the documentation doesn't state
		// that put() is called although it's the observed behaviour.
		// Hopefully the redundant calls will be collapsed.
		if (enabled) {
			firePropertyChanged();
		}
	}

	public void enableNotifications(final boolean enabled) {
		this.enabled = enabled;
		if (enabled && somethingChanged) {
			firePropertyChanged();
		}
	}
}
