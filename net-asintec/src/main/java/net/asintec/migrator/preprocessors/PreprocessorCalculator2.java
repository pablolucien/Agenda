package net.asintec.migrator.preprocessors;

import net.asintec.xtras.Parser;

/**
	Realiza calculos matematicos sobre el objeto que se le pasa
	@author El Coyote Cojo
	@version 2001.13.11
	@see net.asintec.migrator.Migrator
*/
public class PreprocessorCalculator2 implements Preprocessor {
	/** La formula a aplicar */
	private String formula;

	/** Los modificadores a aplicar */
	private String modifiers;

	/** Este tio se encarga de hacer los cálculos */
	private final Parser parser = new Parser();

	/** informa de lo que es capaz de hacer este señor */
	@Override
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t Realiza calculos matematicos sobre el objeto que se le pasa,"
									+ "\n\t que debe ser un numero"
									+ "\n\t Los parametros van asi: bla, bla, bla"
									+ "\n\t X + 5 le suma 5 al objeto, etc."
									+ "\n\t Los modificadores van asi: bla, bla, bla"
									;
		return(description);
	}

	/**
		Obtiene los posibles parametros que utilizará este preprocesador
		@param param Los parametros
	*/
	@Override
	public void setParameters(final String param) {
		formula = "";
		modifiers = "";
		boolean isModifier = (param.charAt(0) == '[');
		for(int i = 0; i < param.length(); i++) {
			if(isModifier) {
				modifiers += param.charAt(i);
				isModifier = !(param.charAt(i) == ']');
			}
			else {
				formula += param.charAt(i);
				if(i < param.length() - 1) {
					isModifier = (param.charAt(i + 1) == '[');
				}
			}
		}
		modifiers = modifiers.toLowerCase();
		//System.out.println(getClass().getName() + " parametros: <" + param + ">");
		//System.out.println(getClass().getName() + " formula: <" + formula + ">");
		//System.out.println(getClass().getName() + " modifiers: <" + modifiers + ">");
	}

	/**
		Procesa a 'o', presumiblemente segun lo que informa getDescription()
		@param o El objeto a procesar
		@return El resultado de procesar 'o'
	*/
	@Override
	public Object process(final Object o) {
		if(o == null) {
			return (null);
		}

		final String dato = (String) o;
		final String formulaAEvaluar = /*ToolBox.*/replaceAll(formula, "X", dato);		// ??? Debe estar en ToolBox
		//System.out.println(getClass().getName() + " formulaAEvaluar: <" + formulaAEvaluar + ">");

		parser.setFormula(formulaAEvaluar/*dato + " + 2"*/);

		final double result = parser.getResult();

		if (modifiers.indexOf("[int]") > -1) {
			return String.valueOf((long) result);
		}
		return String.valueOf(result);
	}


	/**
		estamos jodidos si 'replacement' contiene 'notWanted'
	*/
	private static String replaceAll(final String oldString,
		final String notWanted, final String replacement) {
//OJO: ESTE MÉTODO NO FUNCIONA
		String newString = oldString;
		while(true) {
			// estamos jodidos si 'replacement' contiene 'notWanted'
			final int start = newString.indexOf(notWanted);
			final int len = notWanted.length();
			if(start == -1) {
				return newString;
			}
//??? Ademas estas 3 intrucciones se pueden convertir en 2
			// Elimino
			newString  = oldString.substring(0, start);    // Guardo los primeros caracteres
			newString += oldString.substring(start + len); // Agrego los restantes
			// y agrego
			newString = newString.substring(0, start) + replacement + newString.substring(start);
		}
	}
}
