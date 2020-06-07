package org.pclg.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation reflects that the class or method annotated fullfils an
 * urgent need, but is otherwise a shame.
 * People and tools analising so-annotated artifacts shouldn't be too strict.
 * 
 * There once was a master programmer who wrote unstructured programs. A novice
 * programmer, seeking to imitate him, also began to write unstructured programs.
 * When the novice asked the master to evaluate his progress, the master
 * criticized him for writing unstructured programs, saying, “What is appropriate
 * for the master is not appropriate for the novice. You must understand the Tao
 * before transcending structure.” - The Tao Of Programming 3.2

 * @author paceLucien
 * @since Orléans 2011 12 13
 */
@Documented
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface QuickAndDirty {
	/** Reason of why this is dirty (if confessable). */
	String reason() default "N/A";
}
