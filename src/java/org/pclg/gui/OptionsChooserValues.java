package org.pclg.gui;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para seleccionar una serie de opciones. Las clases anotadas con esto
 * deben definir los posible valores a ser usados en el OptionsChooser.
 * @author El Coyote Cojo
 * @since 29/04/16 19:03
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionsChooserValues {
	String[] value();
}
