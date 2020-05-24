package org.pclg.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a piece of code (class, constructor or method) as dummy: used only 
 * to get other code to compile. Dummy elements should be ignored by code
 * analysis tools (and people).
 */
@Documented
@Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Dummy {

}
