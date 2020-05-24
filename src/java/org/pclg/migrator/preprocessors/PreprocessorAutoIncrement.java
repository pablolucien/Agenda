package org.pclg.migrator.preprocessors;

import net.asintec.migrator.preprocessors.Preprocessor;

/**
 * @author Pablo
 * @since 28/07/13 17:47
 */
public class PreprocessorAutoIncrement implements Preprocessor {
	private int currentValue;

	@Override
	public void setParameters(final String param) {
		currentValue = Integer.parseInt(param);
	}

	@Override
	public String getDescription() {
		return getClass().getName()
			+ "\n\t Retorna un valor entero autoincrementado"
			+ "\n\t Parametro: el valor inicial";
	}

	@Override
	public Object process(final Object o) {
		return String.valueOf(currentValue++);
	}
}
