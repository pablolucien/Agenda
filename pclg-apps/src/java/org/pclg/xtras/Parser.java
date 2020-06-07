//// Este es un paquete ad hoc para poner cosas que he obtenido de otras fuentes
package org.pclg.xtras;

/******************************************************************************/
/******************************************************************************/
/* CLASS Parser											*/
/* Creador: Juan-Cho										*/
/* Fecha de última modificación: 07/08/2001						*/
/******************************************************************************/
/******************************************************************************/

/* IMPORTS */

import java.util.Vector;

/**
 * Realiza operaciones matemáticas a partir de una expresión textual de las mismas.
 *
 * @author Juan-Cho
 * @version 1.00 07/08/2001
 */
public class Parser {

	/****************************/
	/* Definicion de constantes */
	/****************************/

	/** Nombre de la clase. */
	private static final String className = "Parser -> ";

	/***************************/
	/* Definicion de variables */
	/***************************/

	/** Expresión a evaluar por la función. */
	private String expression;

	/** Resultado de la operación indicada en expression. */
	private double res;

	/** Indica el número de grupos de función (entre paréntesis) que se evalúan. */
	private int fIndex;

	/**
	 */
	private Vector vector;

	/*******************************/
	/* Definicion de constructores */
	/*******************************/

	/** Inicializa la clase y sus componentes. */
	// Es absurdo tener que crear un nuevo objeto cada vez que se usa el parser.
	public Parser(final String value) {
		setFormula(value);
	}

	// Es absurdo tener que crear un nuevo objeto cada vez que se usa el parser.
	public Parser() {
	}

	// Es absurdo tener que crear un nuevo objeto cada vez que se usa el parser.
	public void setFormula(final String value) {
		vector = new Vector(5, 2);
		fIndex = 0;
		expression = value;
		expression = arrange(expression);
		addOperator(expression);
		res = cFunc(0);
	}

	/*************************/
	/* Definicion de métodos */
	/*************************/

	/**
	 * Detecta si un carácter es numérico o no.
	 *
	 * @param c El carácter a evaluar.
	 *
	 * @return True en caso de que sea un valor numérico (0..9), 'X', '$' ó '.', y False en caso contrario.
	 */
	boolean isNumber(final char c) {
		return c >= '0' && c <= '9' || c == 'X' || c == '$' || c == '.';
	}

	/**
	 * Convierte la fórmula pasada como parámetro a otra con paréntesis intermedios que separen las operaciones.
	 *
	 * @param value La cadena a evaluar.
	 */
	public String arrange(String value) {
		if (value == null || value.length() == 0) {
			value = "0";
		}
		int length = value.length();
		String result = "";
		int k = 0;
		char c = ' ';

		// Recorremos la cadena
		for (int j = 0; j < length; j++) {
			switch (value.charAt(j)) {
			case 40: // '('
				k++;
				break;

			case 41: // ')'
				k--;
				break;
			}
			if (value.charAt(j) != ' ') {
				// Si el carácter no es ni un número ni '+' ni '-' y c es '(' añadimos un signo '+'
				if (c == 40 && value.charAt(j) != '+' && value.charAt(j) != '-'
						&& !isNumber(value.charAt(j))) {
					result += "+";
				} else {
					// Si no estamos en el primer carácter, y c una operación y el carácter analizado es un número
					if (j > 1 && (c == 42 || c == 43 || c == 45 || c == 47)
							&& isNumber(value.charAt(j))) {
						final String aux = nextOpe(value, j);
						if (aux != null) {
							value = aux;
							length = value.length();
						}
					}
				}
				c = value.charAt(j);
				if (value.charAt(j) == ',') {
					c = '.';
				}
				result += c;
			}
		}

		// Añadimos un signo al primer carácter
		if (result.charAt(0) != '+' && result.charAt(0) != '-') {
			result = "+" + result;
		}
		result = result.toUpperCase();
		return result;
	}

	/**
	 * Añade los paréntesis de agrupación de operaciones desde una posición.
	 *
	 * @param value   La cadena original.
	 * @param initPos La posición de comienzo de la operación a tratar.
	 *
	 * @return Una cadena compuesta con los controles de los paréntesis.
	 */
	public String nextOpe(final String value, final int initPos) {
		String result = "";
		final int length = value.length();
		int i1 = 0;
		int numParenthesis = 0;
		char c = ' ';
		int k = 0;

		// Recorremos la cadena de entrada
		for (k = 0; k < length; k++) {
			if (k < initPos) {
				// Añadimos el mismo carácter
				result += value.charAt(k);
				// Realiza un salto al for sin pasar por el resto de instrucciones
				continue;
			}
			if (i1 == 0) {
				if (value.charAt(k) == ')' || value.charAt(k) == '+'
						|| value.charAt(k) == '-') {
					break;
				}
				if (value.charAt(k) == '*' || value.charAt(k) == '/'
						|| value.charAt(k) == '^') {
					i1 = 1;
					c = value.charAt(k);
				}
				continue;
			}
			if (numParenthesis <= 0 && (value.charAt(k) == '+'
					|| value.charAt(k) == '-' || value.charAt(k) == '*' &&
					c == '^' || value.charAt(k) == '/' && c == '^'
					|| value.charAt(k) == ')')) {
				i1 = k;
				break;
			}
			if (value.charAt(k) == '(') {
				numParenthesis++;
			}
			if (value.charAt(k) == ')') {
				numParenthesis--;
			}
		}
		if (i1 == 1 && numParenthesis <= 0) {
			i1 = k;
		}
		if (i1 > 1) {
			int l = initPos;
			result += '(';
			while (l < length) {
				result += value.charAt(l++);
				if (l == i1) {
					result += ')';
				}
			}
			return result;
		} else {
			return null;
		}
	}

	/**
	 * Se crea un objeto de tipo Function que se almacenará en el vector.
	 *
	 * @param value La función a evaluar.
	 *
	 * @return -1 si se realizó la operación con errores, y si se realizó con éxito la posición del vector en la que se almacenó
	 *         el objeto Function correspondiente.
	 */
	public int addOperator(String value) {
		if (value == null) {
			value = expression;
		}
		final int i = fIndex++;
		byte byte0 = 0;
		int j = 0;
		// Longitud de la cadena de texto a evaluar
		final int length = value.length();
		String source = value;
		final Function func = new Function();

		// Si el primer carácter no es un número
		if (!isNumber(source.charAt(0))) {
			// Si el primer carácter es un punto añadimos un 0 por la izquierda
			if (source.charAt(0) == '.') {
				source = "0" + source;
			}
			// Evaluamos el carácter de la posición 0
			func.setCharacter(evalFunc(source, 0));
			// Si el resultado de la evaluación es Z (no reconocido) retornamos -1
			if (func.getCharacter() == 'Z') {
				return -1;
			}
			// En otro caso analizamos el carácter devuelto
			switch (func.getCharacter()) {
			case 43:		// '+'
			case 45:		// '-'
				func.setCharacter('=');
				byte0 = 0;
				break;
			case 65:		// 'A'
			case 67:		// 'C'
			case 69:		// 'E'
			case 76:		// 'L'
			case 83:		// 'S'
			case 84:		// 'T'
				byte0 = 4;
				break;
			case 82:		// 'R'
			case 99:		// 'c'
			case 115:		// 's'
			case 116:		// 't'
				byte0 = 5;
				break;
			case 66:		// 'B'
				byte0 = 6;
				break;
			}
		}
		// Añadimos al vector el objeto Function
		vector.addElement(func);
		j = byte0;
		// NO USADA boolean flag = false;
		// NO USADA byte byte1 = 63;
		char c4 = 'E';
		// NO USADA double d = 0.0D;
		while (j < length) {
			if (!isNumber(source.charAt(j))) {
				c4 = evalFunc(source, j);
				if (c4 == 'Z') {
					return -1;
				}
				if (c4 == '+' || c4 == '-' || c4 == '*' || c4 == '/'
						|| c4 == '^') {
					j++;
				}
				if (source.charAt(j) == 'S' || source.charAt(j) == 'C'
						|| source.charAt(j) == 'T' || source.charAt(j) == 'A'
						|| source.charAt(j) == 'R' || source.charAt(j) == 'L'
						|| source.charAt(j) == 'E' || source.charAt(j) == '(') {
					int l = -1;
					int i1 = j;
					int j1 = 0;
					do {
						switch (source.charAt(i1)) {
						case 40: // '('
							j1++;
							break;

						case 41: // ')'
							j1--;
							l = i1;
							break;
						}
					} while ((j1 != 0 || l <= 0) && ++i1 < length);
					if (l == -1) {
						return -1;
					}
					final String s2 = source.substring(j, l);
					final char c = 'F';
					final double d1 = addOperator(s2);
					if (d1 == -1D) {
						return -1;
					}
					func.add(c, d1, c4);
					j = l + 1;
				}
			} else if (source.charAt(j) == 'X') {
				final char c1 = 'P';
				final double d2 = 0.0D;
				func.add(c1, d2, c4);
				j++;
			} else if (source.charAt(j) == '$') {
				if (++j >= length) {
					return -1;
				}
				final double d3;
				switch (source.charAt(j)) {
				case 69: // 'E'
					d3 = 2D;
					break;

				case 80: // 'P'
					j++;
					d3 = 1.0D;
					break;

				default:
					return -1;
				}
				final char c2 = 'P';
				func.add(c2, d3, c4);
				j++;
			} else {
				String s3 = "";
				while (isNumber(source.charAt(j))) {
					s3 += source.charAt(j++);
					if (j >= length) {
						break;
					}
				}
				final double d4 = (new Double(s3)).doubleValue();
				final char c3 = 'V';
				func.add(c3, d4, c4);
			}
		}
		vector.setElementAt(func, i);
		return i;
	}

	/**
	 */
	public double cFunc(int i) {
		double d1 = 0.0D;
		double d2 = 0.0D;
		int j = 0;

		final Function func = (Function) vector.elementAt(i);
		j = func.getIndex();
		for (i = 0; i <= j; i++) {
			switch (func.getSymbol(i)) {
			case 86: // 'V'
				d2 = func.getValue(i);
				break;

			case 80: // 'P'
				d2 = 0;
				if (func.getValue(i) == 1.0D) {
					d2 = 3.1415926535897931D;
				}
				if (func.getValue(i) == 2D) {
					d2 = 2.7182818284590451D;
				}
				break;

			case 70: // 'F'
				final Double double1 = new Double(cFunc((int) func.getValue(i)));
				if (double1.isNaN()) {
					return (0.0D / 0.0D);
				}
				d2 = double1.doubleValue();
				break;
			}
			switch (func.getOperand(i)) {
			default:
				break;

			case 43: // '+'
			case 69: // 'E'
				d1 += d2;
				break;

			case 45: // '-'
				d1 -= d2;
				break;

			case 42: // '*'
				d1 *= d2;
				break;

			case 47: // '/'
				if (d2 == 0.0D) {
					return (0.0D / 0.0D);
				}
				d1 /= d2;
				break;

			case 94: // '^'
				d1 = Math.pow(d1, d2);
				break;
			}
		}

		switch (func.getCharacter()) {
		case 43: // '+'
		case 61: // '='
		default:
			break;

		case 45: // '-'
			d1 = 0.0D - d1;
			break;

		case 82: // 'R'
			if (d1 < 0.0D) {
				return (0.0D / 0.0D);
			}
			d1 = Math.sqrt(d1);
			break;

		case 83: // 'S'
			d1 = Math.sin(d1);
			break;

		case 67: // 'C'
			d1 = Math.cos(d1);
			break;

		case 84: // 'T'
			d1 = Math.tan(d1);
			break;

		case 65: // 'A'
			d1 = Math.abs(d1);
			break;

		case 99: // 'c'
			d1 = Math.acos(d1);
			break;

		case 115: // 's'
			d1 = Math.asin(d1);
			break;

		case 116: // 't'
			d1 = Math.atan(d1);
			break;

		case 69: // 'E'
			if (d1 < 0.0D) {
				return (0.0D / 0.0D);
			}
			d1 = Math.exp(d1);
			break;

		case 76: // 'L'
			if (d1 <= 0.0D) {
				return (0.0D / 0.0D);
			}
			d1 = Math.log(d1);
			break;

		case 66: // 'B'
			d1 = Math.round(d1);
			break;
		}
		return d1;
	}


	/**
	 * Evalua el contenido de una cadena en una posición, devolviendo el carácter correspondiente a la función contenida.
	 *
	 * @param s La cadena original.
	 * @param i La posición a evaluar.
	 *
	 * @return El carácter correspondiente a la función detectada o Z si no se detectó ninguna función.
	 */
	private char evalFunc(final String s, final int i) {
		char c = 'Z';
		// NO USADA boolean flag = false;
		String s1 = "";
		switch (s.charAt(i)) {
		case 41: // ')'
			break;
		case 42: // '*'
		case 47: // '/'
		case 94: // '^'
			if (i != 0) {
				c = s.charAt(i);
			}
			break;
		case 40: // '('
		case 43: // '+'
			c = '+';
			break;
		case 45: // '-'
			c = '-';
			break;
		default:
			final int j = s.indexOf(40, i);
			if (j > -1) {
				s1 = new String(s.substring(i, j));
			}
			if (s1.equals("SQRT")) {
				c = 'R';
				break;
			}
			if (s1.equals("SIN")) {
				c = 'S';
				break;
			}
			if (s1.equals("COS")) {
				c = 'C';
				break;
			}
			if (s1.equals("TAN")) {
				c = 'T';
				break;
			}
			if (s1.equals("ABS")) {
				c = 'A';
				break;
			}
			if (s1.equals("ACOS")) {
				c = 'c';
				break;
			}
			if (s1.equals("ASIN")) {
				c = 's';
				break;
			}
			if (s1.equals("ATAN")) {
				c = 't';
				break;
			}
			if (s1.equals("EXP")) {
				c = 'E';
				break;
			}
			if (s1.equals("LOG")) {
				c = 'L';
				break;
			}
			if (s1.equals("ROUND")) {
				c = 'B';
			}
			break;
		}
		return c;
	}

	/**
	 * Devuelve el resultado de la operación realizada.
	 *
	 * @return El valor recogido en la variable res que mantendrá el resultado de la operación realizada.
	 */
	public double getResult() {
		return res;
	}
}