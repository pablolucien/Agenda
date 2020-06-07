// ******************************** package
package org.pclg.tools;

// ******************************** imports
import java.awt.Component;
/**
	Los implementadores de esta interfaz cambiaran la caracteristica del componente
	@author El Coyote cojo
	@version 2001.02.19
*/
public interface ComponentMutator {
	/**
		Hace lo que le da la gana con 'c'
		@param c El componente con el que se va a actuar
		--author El Coyote cojo
		--version 2001.02.19
	*/
	void mutate(Component c);
}
