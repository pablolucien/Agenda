package org.pclg.migrator.preprocessors;

import net.asintec.migrator.preprocessors.Preprocessor;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Pablo
 * @since 03/08/13 12:32
 */
public class PreprocessorYesNo2OneZero implements Preprocessor {
	/** Posibles formas de decir que si (debría ser un Set o algo parecido) */
	private static final Collection<String> afirmaciones = new HashSet<>();
	private static final Integer ZERO = Integer.valueOf(0);
	private static final Integer ONE = Integer.valueOf(1);

	/** Inicializacion del vector de afirmaciones */
	static {
		afirmaciones.add("true");
		afirmaciones.add("s");
		afirmaciones.add("si");
		afirmaciones.add("sí");
		afirmaciones.add("oui");
		afirmaciones.add("yes");
		afirmaciones.add("ja");
		afirmaciones.add("1");
		afirmaciones.add("sim");
		afirmaciones.add("igen");
		afirmaciones.add("ne");
		afirmaciones.add("bai");
		afirmaciones.add("hai");
	}

	@Override
	public void setParameters(final String param) {
	}

	@Override
	public String getDescription() {
		return getClass().getName()
			+ "\n\t Retorna uno o cero dependiendo de si el valor es uno de"
			+ "\n\t un conjunto de afirmaciones (Sí, NO, yes, TrUe, ...)"
			+ "\n\t No necesita parametros.";
	}

	@Override
	public Object process(final Object o) {
		final String s = String.valueOf(o);
		return s != null && afirmaciones.contains(s.toLowerCase()) ?
				ONE : ZERO;
	}
}
