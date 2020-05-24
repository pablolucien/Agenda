// ******************************** package
package net.asintec.migrator.preprocessors;

// ******************************** imports

import org.pclg.tools.ToolBox;
import org.pclg.xtras.Parser;

/**
	Realiza calculos matematicos sobre el objeto que se le pasa
	@author El Coyote Cojo
	@version 2001.13.11
	@see net.asintec.migrator.Migrator
*/
public class PreprocessorCalculator implements Preprocessor {
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
									+ "\n\t Los parametros van asi:"
									+ "\n\t X + 5 le suma 5 al objeto, etc."
									+ "\n\t [int] convierte el resultado a int"
									+ "\n\t vg. '[int] X / 10', devuelve 2 si X vale 25"
									;
		return description;
	}

	/**
		Obtiene los posibles parametros que utilizará este preprocesador
		@param param Los parametros
	*/
	@Override
	public void setParameters(final String param) {
		formula = "";
		modifiers = "";
		boolean isModifier = param.charAt(0) == '[';
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
			return null;
		}

		final String formulaAEvaluar = ToolBox.replaceAll(formula, "X", o.toString());

		//System.out.println(getClass().getName() + " formulaAEvaluar: <" + formulaAEvaluar + ">");

		parser.setFormula(formulaAEvaluar);

		final double result = parser.getResult();

		//System.out.println(getClass().getName() + " result: <" + result + ">");

		if (modifiers.indexOf("[int]") > -1) {
			return String.valueOf((long) result);
		}
		return String.valueOf(result);
	}
}
