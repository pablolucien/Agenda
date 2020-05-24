package org.pclg.gui;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Clase para seleccionar una serie de opciones. Las clases que implementen esto
 * deben definir los posible valores en una anotacion de tipo OptionsChooser.Values.
 *
 * @author El Coyote Cojo
 * @since 29/04/16 19:03
 */
public interface OptionsChooser {
	@Retention(RetentionPolicy.RUNTIME)
	@interface Values {
		String[] value();
	}
}
