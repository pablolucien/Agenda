package org.pclg.tools;

/**
 * A class can implement the ChangeObserver interface when it wants to be
 * informed of changes in Changeable objects.
 * @author El Coyote
 * @since 2014.06.19  
 */
@FunctionalInterface
public interface ChangeObserver<T extends Changeable<?>> {
    /**
     * This method is called whenever the observed object is changed. 
     * @param observable the object that changed.
     */
    void objectChanged(T observable);
}
