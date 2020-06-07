/**
*/

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
	Clase para probar partes del API que no uso frecuentemente
*/
public class Tutor extends Frame implements /*ActionListener,*/ WindowListener {
    private static final long serialVersionUID = 8479354243101527281L;

    public static void main(final String [] args) {
		String s;
		String command;

		// Uso de la clase Date
        final Date d1 = new Date(98, 9, 3, 4, 57, 8);
		final Date d2 = new Date(98, 9, 4, 0, 35, 27);
		System.out.println("diferencia en milisegundos: " + Math.abs(d1.getTime() - d2.getTime()));
//        System.exit(0);

        // Criptografia

        // Ver las properties
		System.out.println("\n**** Estas son las propiedades del sistema");
		final Properties props = System.getProperties();
		props.list(System.out);

		System.out.println("\n**** Estas son propiedades en blanco");
		final Properties props1 = new Properties();
		props1.list(System.out);

		// ejecutar un proceso dependiendo del sistema operativo
		command = null;
		final String osType = System.getProperty("os.name");
		/* JDK1.1 returns "Solaris", 1.2 returns "SunOS". */
		if(osType.equals("SunOS") || osType.equals("Solaris")) {
			command = "ls -C";
		}
		else if(osType.startsWith("Windows 2000")) {
			command = "cmd /c dir";
		}
		else if(osType.startsWith("Windows NT")) {
			command = "cmd /c dir";
		}
		else if(osType.startsWith("Windows")) {
			command = "command /c dir";
		}
		if(command != null) {
			System.out.println("\n**** Ejecutamos un comando (" + command + ")");
			try {
				final Runtime rt = Runtime.getRuntime();
				final Process proc = rt.exec(command);
				final InputStream is = proc.getInputStream();
				final BufferedReader bis = new BufferedReader(new InputStreamReader(is));
				while((s = bis.readLine()) != null) {
					System.out.println(s);
				}
				try { proc.waitFor(); }	// Esperamos a que termine el proceso externo
				catch(final InterruptedException ex) {Thread.currentThread().interrupt();}
				System.out.println(command + " termino con codigo: " + proc.exitValue());
			}
			catch(final IOException ex) {
				ex.printStackTrace();
			}
		}

		System.out.println("Resolucion de la pantalla en DPI: " + Toolkit.getDefaultToolkit().getScreenResolution());

		// Un pitico
		Toolkit.getDefaultToolkit().beep();
		// fonts del sistema
		System.out.println("\n**** Existen los siguientes tipos de letra");
		final String [] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();  // FIXME: comparar con la version anterior
		for(int i = 0; i < fonts.length; i++) {
			System.out.println(fonts[i]);
		}

		// "Customizaciones" dependientes del "locale"
		System.out.println("\n**** Dependencias del pais donde estamos");
		testLocale(Locale.FRANCE, "Francia");
		testLocale(Locale.US, "USA");
		testLocale(Locale.UK, "UK");
		testLocale(Locale.ITALIAN, "IT");
		Locale loc = new Locale("es", "VE");	// espanyol en Venezuela
		testLocale(loc, "Venezuela");
		loc = Locale.getDefault();	// locale default
		testLocale(loc, "default");

		// bug de java? a veces los programas no terminan al llegar aqui cuando se usa Toolkit
		System.exit(0);
	}

	private static void testLocale(final Locale loc, final String pais) {
		NumberFormat nf = NumberFormat.getInstance(loc);
		System.out.println("1525.32 en " + pais + " = " + nf.format(1525.327));
		nf = NumberFormat.getCurrencyInstance(loc);
		System.out.println("\tlas monedas se escriben asi: " + nf.format(1525.327));
		System.out.println("\tel lenguaje es: " + loc.getDisplayLanguage());
		System.out.println("\tel pais es: " + loc.getDisplayCountry());
		System.out.println("\tgetDisplayName(): " + loc.getDisplayName());
		System.out.println("\tgetDisplayVariant(): " +  loc.getDisplayVariant());
	}

    // Todos estos putos metodos hay que "implementarlos" para poder
    // tener WindowClosing()
    @Override
	public void windowOpened(final WindowEvent e) {System.out.println("me llamaron 1"); }
    @Override
	public void windowClosed(final WindowEvent e) {System.out.println("me llamaron 2"); }
    @Override
	public void windowIconified(final WindowEvent e) {System.out.println("me llamaron 3"); }
    @Override
	public void windowDeiconified(final WindowEvent e) {System.out.println("me llamaron 4"); }
    @Override
	public void windowActivated(final WindowEvent e) { System.out.println("me llamaron 5");}
    @Override
	public void windowDeactivated(final WindowEvent e) {System.out.println("me llamaron 6"); }

    @Override
	public void windowClosing(final WindowEvent e) {
        setVisible(false);
		dispose();
        System.exit(0);
    }
}
