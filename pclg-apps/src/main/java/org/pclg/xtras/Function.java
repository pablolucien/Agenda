// Este es un paquete ad hoc para poner cosas que he obtenido de otras fuentes
package org.pclg.xtras;

/******************************************************************************/
/******************************************************************************/
/* CLASS Function											*/
/* Creador: Juan-Cho										*/
/* Fecha de última modificación: 24/08/2001						*/
/******************************************************************************/
/******************************************************************************/

/**	Mantiene grupos de operaciones para proceder a su posterior cálculo.
	@version 1.00 24/08/2001
	@author Juan-Cho
*/
class Function {

	/****************************/
	/* Definicion de constantes */
	/****************************/

	/**	Nombre de la clase.
	*/
	private static final String className = "Function -> ";

	/***************************/
	/* Definicion de variables */
	/***************************/

	/**	Indica el número de datos contenidos en el objeto.
	*/
	private int index;

	/**	Indica el número máximo de datos que se podrán almacenar en el objeto.
	*/
	private final int maxIndex;

	/**	Carácter almacenado en el objeto y que se corresponderá con la función a interpretar.
	*/
	private char character;

	/**	Arreglo de símbolos de operación.
	*/
	private final char[] symbolArray;

	/**	Arreglo de valores a operar.
	*/
	private final double[] valueArray;

	/**	Arreglo de operaciones.
	*/
	private final char[] operandArray;

	/*******************************/
	/* Definicion de constructores */
	/*******************************/

	/**	Inicializa la clase y sus componentes.
	*/
	Function() {
		index = -1;
		character = '+';
		final int i = 50;
		symbolArray = new char[i];
		valueArray = new double[i];
		operandArray = new char[i];
		maxIndex = i;
	}

	/*************************/
	/* Definicion de métodos */
	/*************************/

	/**	
	*/
	public int add(final char symbol, final double value, final char operand) {
		index++;
		if (index < maxIndex) {
			symbolArray[index] = symbol;
			valueArray[index] = value;
			operandArray[index] = operand;
			return index;
		}
		else {
			return -1;
		}
	}

	/**	Establece el carácter de la operación a realizar.
		@param value El carácter a utilizar.
	*/
	public void setCharacter(final char value) {
		character = value;
	}

	/**	Obtiene el carácter de la operación a utilizar.
		@return el carácter representación de la operación a utilizar.
	*/
	public char getCharacter() {
		return character;
	}

	/**	Obtiene el número de elementos contenidos por este objeto.
		@return un valor entero con el número de elementos del objeto.
	*/
	public int getIndex() {
		return index;
	}

	/**	Obtiene el símbolo correspondiente a uno de los elementos del objeto.
		@param i La posición de la que se obtendrá el símbolo.
		@return el carácter correspondiente a la posición indicada como parámetro.
	*/
	public char getSymbol(final int i) {
		return symbolArray[i];
	}

	/**	Obtiene el valor correspondiente a uno de los elementos del objeto.
		@param i La posición de la que se obtendrá el valor.
		@return el valor correspondiente a la posición indicada como parámetro.
	*/
	public double getValue(final int i) {
		return valueArray[i];
	}

	/**	Obtiene la operación correspondiente a uno de los elementos del objeto.
		@param i La posición de la que se obtendrá la operación.
		@return la operación correspondiente a la posición indicada como parámetro.
	*/
	public char getOperand(final int i) {
		return operandArray[i];
	}
}
