// ******************************** package
package org.pclg.tools;

// ******************************** imports

import java.util.ArrayList;
import java.util.List;

/**
 * Pool <BR>
 * Un pool genérico de clases
 * Las clases que se metan aqui deben tener un constructor por omision
 * y un metodo init() que es llamado antes de que se provea este objeto
 * a alguien. (Muss das sein?)
 *
 * @author El Coyote Cojo
 * @version 2003.jul.17 21:13:25, CEST
 */
class Pool {
//	public interface Poolable {
//		public void init();
//	}

	// ******************************** Variables de clase

	// ******************************** Variables de instancia
	private final List pool;
	private int lastValidObject = -1;
	private final Class my_class;

	// ******************************** Inicializacion estática

	// ******************************** Constructores

	/**
	 * Constructor por omision
	 * Este es bastante inutil. Puede servir para un pool de objetos para sincronizar, pero para poco más
	 * (creo, pero Alá sabe más)
	 * --version 2003.jul.17 21:13:25, CEST
	 */
	public Pool() throws InstantiationException, IllegalAccessException {
		this(Object.class, 10);
	}

	/**
	 * Constructor
	 * --version 2003.jul.17 21:13:25, CEST
	 */
	public Pool(final int size)
			throws InstantiationException, IllegalAccessException {
		this(Object.class, size);
	}

	/**
	 * Constructor
	 * --version 2003.jul.17 21:13:25, CEST
	 */
	public Pool(final Class the_class)
			throws InstantiationException, IllegalAccessException {
		this(the_class, 10);
	}

	/**
	 * Constructor
	 * --version 2003.jul.17 21:13:25, CEST
	 */
	private Pool(final Class the_class, final int size)
			throws InstantiationException, IllegalAccessException {
		pool = new ArrayList(size);
		my_class = the_class;
		growPool();
	}

	// ******************************** Metodos de instancia

	/** Agrega miembros al pool */
	private void growPool()
			throws InstantiationException, IllegalAccessException {
		pool.add(my_class.newInstance());
		lastValidObject++;
	}

	/** Obtiene un objeto del pool */
	public Object get() {
		return (null);
	}

	/** Devuelve un objeto al pool */
	public void release(final Object obj) {
	}

	// ******************************** Metodos estaticos
}
