package org.pclg.migrator.preprocessors;

import net.asintec.migrator.preprocessors.Preprocessor;

/**
 * @author Pablo
 * @since 03/08/13 12:32
 */
public class PreprocessorAddCentury implements Preprocessor {
	@Override
	public void setParameters(final String param) {
	}

	@Override
	public String getDescription() {
		return getClass().getName()
			+ "\n\t Suma 1900 a un nro si este es != 0 y < que 1900"
			+ "\n\t (se supone que es un año)"
			+ "\n\t Si no logra hacer la suma devuelve el objeto original"
			+ "\n\t No necesita parametros.";
	}

	@Override
	public Object process(final Object o) {
		final String s = String.valueOf(o);
		try {
			int val = Integer.parseInt(s);
			if (val > 0 && val < 1900) {
				val += 1900;
				return String.valueOf(val);
			}
			return o;
		} catch (final NumberFormatException e) {
			return o;
		}
	}
}
