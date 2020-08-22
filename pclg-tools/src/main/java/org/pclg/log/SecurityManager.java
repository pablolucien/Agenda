package org.pclg.log;

/**
 * This class is a workaround in order to use the method
 * <code>java.lang.SecurityManager.getClassContext()</code>
 * which is <code>protected</code>.

 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 23-oct-2007 14:30:43
 */
@SuppressWarnings({"ClassWithoutLogger"})
final class SecurityManager extends java.lang.SecurityManager {
	/**
	 * Returns the current execution stack as an array of classes.
	 * The length of the array is the number of methods on the execution stack.
	 * The element at index 0 is the class of the currently executing method,
	 * the element at index 1 is the class of that method's caller, and so on.
	 * (Javadoc out of the parent class.)
	 *
	 * @return the execution stack.
	 */
	public Class[] listClasses() {
		return super.getClassContext();
	}
}
