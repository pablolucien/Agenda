// ******************************** package
package org.pclg.tools;

// ******************************** imports

import java.io.PrintStream;
import java.lang.reflect.Array;

/**
 * Provides some commonly used functions for handling arrays.
 * These methods were previously in ToolBox.
 *
 * @author El Coyote Cojo
 * @version 2002.05.13
 */
public final class ArrayTools {

    public static final byte[] NULL_BYTE_ARRAY = new byte[0];
    public static final int[] NULL_INT_ARRAY = new int[0];
    private static final int CAPACITY = 1024;

    /**
	 * El constructor por omision es privado para evitar que a algun capullo
	 * se le ocurra
	 * hacer new ArrayTools()
	 */
	private ArrayTools() {
	}


	/**
	 * Finds the position of an object in an array. It's a sequential search, ergo, potentially slow. Uses equals().
	 *
	 * @param array  The array where we are going to search.
	 * @param object The object that we are looking for.
	 *
	 * @return the first position of the object in the array, or -1 if it is not there or any of the parameters is null.
	 *
	 * @since 2004.08.19
	 */
	public static int search(final Object[] array, final Object object) {
		if (array == null || object == null) {
			return -1;
		}
		for (int ii = array.length - 1; ii >= 0; ii--) {
			if (object.equals(array[ii])) {
				return ii;
			}
		}
		return -1;
	}

	/**
	 * Concatena dos arreglos de Objetos creando uno de la misma clase que
	 * los otros dos
	 * <pre>
	 * ejemplo de uso:
	 * <code>
	 * String[] a1 = new String[10];
	 * String[] a2 = {"Uno", "Dos"};
	 * String[] a3 = (String[]) concat(a1, a2);
	 * </code>
	 * </pre>
	 * --author El Coyote Cojo
	 * @since 2002.ene.11 18:24:01, CET
	 *
	 * @param a1 El primer arreglo a considerar
	 * @param a2 El segundo arreglo a considerar
	 *
	 * @return Un arreglo de Objects que es de la misma clase que los otros
	 * dos y tiene el contenido de estos
	 */
	public static <T> T[] concat(final T[] a1, final T[] a2) {
		final T[] a3 = (T[]) Array.newInstance(
				a1.getClass().getComponentType(), a1.length + a2.length);
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	/**
	 * Concatena dos arreglos de byte
	 * <pre>
	 * ejemplo de uso:
	 * <code>
	 * byte[] a4 = new byte[10];
	 * byte[] a5 = {1, 2};
	 * byte[] a6 = concat(a4, a5);
	 * </code>
	 * </pre>
	 * --author El Coyote Cojo
	 * @since 2002.ene.11 18:24:01, CET
	 *
	 * @param a1 El primer arreglo a considerar
	 * @param a2 El segundo arreglo a considerar
	 *
	 * @return Un arreglo de byte que tiene el contenido de los otros dos
	 */
	public static byte[] concat(final byte[] a1, final byte[] a2) {
		final byte[] a3 = new byte[a1.length + a2.length];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	/**
	 * Concatena dos arreglos de short
	 * <pre>
	 * ejemplo de uso:
	 * <code>
	 * short[] a4 = new short[10];
	 * short[] a5 = {1, 2};
	 * short[] a6 = concat(a4, a5);
	 * </code>
	 * </pre>
	 * --author El Coyote Cojo
	 * @since 2002.ene.11 18:24:01, CET
	 *
	 * @param a1 El primer arreglo a considerar
	 * @param a2 El segundo arreglo a considerar
	 *
	 * @return Un arreglo de short que tiene el contenido de los otros dos
	 */
	public static short[] concat(final short[] a1, final short[] a2) {
		final short[] a3 = new short[a1.length + a2.length];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	/**
	 * Concatena dos arreglos de int
	 * <pre>
	 * ejemplo de uso:
	 * <code>
	 * int[] a4 = new int[10];
	 * int[] a5 = {1, 2};
	 * int[] a6 = concat(a4, a5);
	 * </code>
	 * </pre>
	 * --author El Coyote Cojo
	 * @since 2002.ene.11 18:24:01, CET
	 *
	 * @param a1 El primer arreglo a considerar
	 * @param a2 El segundo arreglo a considerar
	 *
	 * @return Un arreglo de int que tiene el contenido de los otros dos
	 */
	public static int[] concat(final int[] a1, final int[] a2) {
		final int[] a3 = new int[a1.length + a2.length];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	/**
	 * Concatena dos arreglos de long
	 * <pre>
	 * ejemplo de uso:
	 * <code>
	 * long[] a4 = new long[10];
	 * long[] a5 = {1, 2};
	 * long[] a6 = concat(a4, a5);
	 * </code>
	 * </pre>
	 * --author El Coyote Cojo
	 * @since 2002.ene.11 18:24:01, CET
	 *
	 * @param a1 El primer arreglo a considerar
	 * @param a2 El segundo arreglo a considerar
	 *
	 * @return Un arreglo de long que tiene el contenido de los otros dos
	 */
	public static long[] concat(final long[] a1, final long[] a2) {
		final long[] a3 = new long[a1.length + a2.length];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	/**
	 * Concatena dos arreglos de float
	 * <pre>
	 * ejemplo de uso:
	 * <code>
	 * float[] a4 = new float[10];
	 * float[] a5 = {1.5, 2.5};
	 * float[] a6 = concat(a4, a5);
	 * </code>
	 * </pre>
	 * --author El Coyote Cojo
	 * @since 2002.ene.11 18:24:01, CET
	 *
	 * @param a1 El primer arreglo a considerar
	 * @param a2 El segundo arreglo a considerar
	 *
	 * @return Un arreglo de float que tiene el contenido de los otros dos
	 */
	public static float[] concat(final float[] a1, final float[] a2) {
		final float[] a3 = new float[a1.length + a2.length];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	/**
	 * Concatena dos arreglos de double
	 * <pre>
	 * ejemplo de uso:
	 * <code>
	 * double[] a4 = new double[10];
	 * double[] a5 = {1.5, 2.5};
	 * double[] a6 = concat(a4, a5);
	 * </code>
	 * </pre>
	 * --author El Coyote Cojo
	 * @since 2002.ene.11 18:24:01, CET
	 *
	 * @param a1 El primer arreglo a considerar
	 * @param a2 El segundo arreglo a considerar
	 *
	 * @return Un arreglo de double que tiene el contenido de los otros dos
	 */
	public static double[] concat(final double[] a1, final double[] a2) {
		final double[] a3 = new double[a1.length + a2.length];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	/**
	 * Concatena dos arreglos de char
	 * <pre>
	 * ejemplo de uso:
	 * <code>
	 * char[] a4 = new char[10];
	 * char[] a5 = {'a', 'b'};
	 * char[] a6 = concat(a4, a5);
	 * </code>
	 * </pre>
	 * --author El Coyote Cojo
	 * @since 2002.ene.11 18:24:01, CET
	 *
	 * @param a1 El primer arreglo a considerar
	 * @param a2 El segundo arreglo a considerar
	 *
	 * @return Un arreglo de char que tiene el contenido de los otros dos
	 */
	public static char[] concat(final char[] a1, final char[] a2) {
		final char[] a3 = new char[a1.length + a2.length];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	/**
	 * Concatena dos arreglos de boolean
	 * <pre>
	 * ejemplo de uso:
	 * <code>
	 * boolean[] a4 = new boolean[10];
	 * boolean[] a5 = {true, false};
	 * boolean[] a6 = concat(a4, a5);
	 * </code>
	 * </pre>
	 * --author El Coyote Cojo
	 * @since 2002.ene.11 18:24:01, CET
	 *
	 * @param a1 El primer arreglo a considerar
	 * @param a2 El segundo arreglo a considerar
	 *
	 * @return Un arreglo de boolean que tiene el contenido de los otros dos
	 */
	public static boolean[] concat(final boolean[] a1, final boolean[] a2) {
		final boolean[] a3 = new boolean[a1.length + a2.length];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	/**
	 * Imprime el contenido de un array de char's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final char[] array) {
        printArray(output, array, 0, array == null ? 0 : array.length);
	}

	/**
	 * Imprime el contenido de un array de byte's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final byte[] array) {
        printArray(output, array, 0, array == null ? 0 : array.length);
	}

	/**
	 * Imprime el contenido de un array de short's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final short[] array) {
        printArray(output, array, 0, array == null ? 0 : array.length);
	}

	/**
	 * Imprime el contenido de un array de int's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.17 22:42:01, CET
	 */
	public static void printArray(final PrintStream output, final int[] array) {
        printArray(output, array, 0, array == null ? 0 : array.length);
	}

	/**
	 * Imprime el contenido de un array de long's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final long[] array) {
        printArray(output, array, 0, array == null ? 0 : array.length);
	}

	/**
	 * Imprime el contenido de un array de float's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final float[] array) {
        printArray(output, array, 0, array == null ? 0 : array.length);
	}

	/**
	 * Imprime el contenido de un array de double's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final double[] array) {
        printArray(output, array, 0, array == null ? 0 : array.length);
	}

	/**
	 * Imprime el contenido de un array de boolean's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final boolean[] array) {
        printArray(output, array, 0, array == null ? 0 : array.length);
	}

	/**
	 * Imprime el contenido de un array de Object's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.17 22:42:01, CET
	 */
	public static void printArray(final PrintStream output, final Object[] array) {
        printArray(output, array, 0, array == null ? 0 : array.length);
	}

	/**
	 * Imprime parte del contenido de un array de boolean's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 * @param start  La posicion del array desde donde empezar a imprimir
	 * @param len	La cantidad de elementos del array a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output,
			final boolean[] array, final int start, final int len) {
		if (array == null) {
			output.println("boolean[] = null");
		} else if (array.length == 0) {
			output.println("boolean[] = []");
		} else {
            final StringBuilder builder = new StringBuilder(CAPACITY);
			builder.append("boolean[] = [");
			if (start > 0) {
				builder.append("..., ");
			}
			for (int i = start; i < start + len - 1; i++) {
				builder.append(array[i]).append(", ");
			}
			builder.append(array[start + len - 1]);
			if (start + len < array.length) {
				builder.append(", ...]");
			} else {
				builder.append(']');
			}
            output.println(builder);
		}
	}

	/**
	 * Imprime parte del contenido de un array de byte's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 * @param start  La posicion del array desde donde empezar a imprimir
	 * @param len	La cantidad de elementos del array a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final byte[] array,
			final int start, final int len) {
		if (array == null) {
			output.println("byte[] = null");
		} else if (array.length == 0) {
			output.println("byte[] = []");
		} else {
            final StringBuilder builder = new StringBuilder(CAPACITY);
			builder.append("byte[] = [");
			if (start > 0) {
				builder.append("..., ");
			}
			for (int i = start; i < start + len - 1; i++) {
				builder.append(array[i]).append(", ");
			}
			builder.append(array[start + len - 1]);
			if (start + len < array.length) {
				builder.append(", ...]");
			} else {
				builder.append(']');
			}
            output.println(builder);
		}
	}

	/**
	 * Imprime parte del contenido de un array de short's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 * @param start  La posicion del array desde donde empezar a imprimir
	 * @param len	La cantidad de elementos del array a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final short[] array,
			final int start, final int len) {
		if (array == null) {
			output.println("short[] = null");
		} else if (array.length == 0) {
			output.println("short[] = []");
		} else {
            final StringBuilder builder = new StringBuilder(CAPACITY);
			builder.append("short[] = [");
			if (start > 0) {
				builder.append("..., ");
			}
			for (int i = start; i < start + len - 1; i++) {
				builder.append(array[i]).append(", ");
			}
			builder.append(array[start + len - 1]);
			if (start + len < array.length) {
				builder.append(", ...]");
			} else {
				builder.append(']');
			}
            output.println(builder);
		}
	}

	/**
	 * Imprime parte del contenido de un array de int's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 * @param start  La posicion del array desde donde empezar a imprimir
	 * @param len	La cantidad de elementos del array a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final int[] array,
			final int start, final int len) {
		if (array == null) {
			output.println("int[] = null");
		} else if (array.length == 0) {
			output.println("int[] = []");
		} else {
            final StringBuilder builder = new StringBuilder(CAPACITY);
			builder.append("int[] = [");
			if (start > 0) {
				builder.append("..., ");
			}
			for (int i = start; i < start + len - 1; i++) {
				builder.append(array[i]).append(", ");
			}
			builder.append(array[start + len - 1]);
			if (start + len < array.length) {
				builder.append(", ...]");
			} else {
				builder.append(']');
			}
            output.println(builder);
		}
	}

	/**
	 * Imprime parte del contenido de un array de long's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 * @param start  La posicion del array desde donde empezar a imprimir
	 * @param len	La cantidad de elementos del array a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final long[] array,
			final int start, final int len) {
		if (array == null) {
			output.println("long[] = null");
		} else if (array.length == 0) {
			output.println("long[] = []");
		} else {
            final StringBuilder builder = new StringBuilder(CAPACITY);
			builder.append("long[] = [");
			if (start > 0) {
				builder.append("..., ");
			}
			for (int i = start; i < start + len - 1; i++) {
				builder.append(array[i]).append(", ");
			}
			builder.append(array[start + len - 1]);
			if (start + len < array.length) {
				builder.append(", ...]");
			} else {
				builder.append(']');
			}
            output.println(builder);
		}
	}

	/**
	 * Imprime parte del contenido de un array de float's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 * @param start  La posicion del array desde donde empezar a imprimir
	 * @param len	La cantidad de elementos del array a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final float[] array,
			final int start, final int len) {
		if (array == null) {
			output.println("float[] = null");
		} else if (array.length == 0) {
			output.println("float[] = []");
		} else {
            final StringBuilder builder = new StringBuilder(CAPACITY);
			builder.append("float[] = [");
			if (start > 0) {
				builder.append("..., ");
			}
			for (int i = start; i < start + len - 1; i++) {
				builder.append(array[i]).append(", ");
			}
			builder.append(array[start + len - 1]);
			if (start + len < array.length) {
				builder.append(", ...]");
			} else {
				builder.append(']');
			}
            output.println(builder);
		}
	}

	/**
	 * Imprime parte del contenido de un array de double's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 * @param start  La posicion del array desde donde empezar a imprimir
	 * @param len	La cantidad de elementos del array a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output,
			final double[] array, final int start, final int len) {
		if (array == null) {
			output.println("double[] = null");
		} else if (array.length == 0) {
			output.println("double[] = []");
		} else {
            final StringBuilder builder = new StringBuilder(CAPACITY);
			builder.append("double[] = [");
			if (start > 0) {
				builder.append("..., ");
			}
			for (int i = start; i < start + len - 1; i++) {
				builder.append(array[i]).append(", ");
			}
			builder.append(array[start + len - 1]);
			if (start + len < array.length) {
				builder.append(", ...]");
			} else {
				builder.append(']');
			}
            output.println(builder);
		}
	}

	/**
	 * Imprime parte del contenido de un array de char's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 * @param start  La posicion del array desde donde empezar a imprimir
	 * @param len	La cantidad de elementos del array a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output, final char[] array,
			final int start, final int len) {
		if (array == null) {
			output.println("char[] = null");
		} else if (array.length == 0) {
			output.println("char[] = []");
		} else {
            final StringBuilder builder = new StringBuilder(CAPACITY);
			builder.append("char[] = [");
			if (start > 0) {
				builder.append("..., ");
			}
            final int end = start + len - 1;
            for (int i = start; i < end; i++) {
				builder.append(array[i]).append(", ");
			}
			builder.append(array[end]);
			if (start + len < array.length) {
				builder.append(", ...]");
			} else {
				builder.append(']');
			}
            output.println(builder);
		}
	}

	/**
	 * Imprime parte del contenido de un array de Object's
	 *
	 * @param output Donde se va a imprimir
	 * @param array  El arreglo a imprimir
	 * @param start  La posicion del array desde donde empezar a imprimir
	 * @param len	La cantidad de elementos del array a imprimir
	 *               --author El Coyote Cojo
	 * @since 2002.ene.27 13:15:01, CET
	 */
	public static void printArray(final PrintStream output,
			final Object[] array, final int start, final int len) {
		if (array == null) {
			output.println("[] = null");
			return;
		}

		final String className = array.getClass().getComponentType().getName();
		if (array.length == 0) {
			output.println(className + "[] = []");
		} else {
            final StringBuilder builder = new StringBuilder(CAPACITY);
			builder.append(className).append("[] = [");
			if (start > 0) {
				builder.append("..., ");
			}
            final int end = start + len - 1;
            for (int i = start; i < end; i++) {
				builder.append(array[i]).append(", ");
			}
			builder.append(array[end]);
			if (start + len < array.length) {
				builder.append(", ...]");
			} else {
				builder.append(']');
			}
            output.println(builder);
		}
	}
}
