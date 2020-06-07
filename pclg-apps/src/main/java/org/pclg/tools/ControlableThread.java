package org.pclg.tools;

/**
 * Una interfaz que representa los Threads que pueden ser controladas con
 * métodos equivalentes a los deprecated de la clase Thread.
 *
 * @author Pablo
 * @since 28-jul-2007 16:21:50
 */
public interface ControlableThread {
	/** Reemplaza a Thread.stop(). */
	void halt();
}
