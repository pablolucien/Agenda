package tests;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * Creado el 05-feb-2008
 * @author X52192LU
 */
public final class SerialTest implements Serializable {
	private static final String FILE_NAME = "abc.obj"; 
	private final String nombre;
	private final transient String apellido;
    private static final long serialVersionUID = -6625607898075819915L;

	/**
	 *
	 */
	private SerialTest(final String[] args) {
		nombre = args[0];
		apellido = args[1];
		sayHello();
		persist();
	}

	/**
	 * Se almacena en un fichero.
	 */
	private void persist() {
		try {
			final ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
			outputStream.writeObject(this);
			outputStream.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 */
	private static SerialTest load() {
		SerialTest obj = null;
		try {
			final ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(FILE_NAME));
			obj = (SerialTest) inputStream.readObject();
		} catch (final ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 
	 */
	private void sayHello() {
		System.out.println("Creado " + nombre + " " + apellido);
	}

	public static void main(final String[] args) {
		final byte i = 1;
		final byte j = 1;
		short k = i;
		k += j;
//		k = i + j;

		final float n = 1.0F;
		final float m = 1.0F;
		final float o = n + m;
		
		if (args.length == 0) {
			final SerialTest serialTest = load();
			serialTest.sayHello();
		} else {
			new SerialTest(args);
		}
	}

}
