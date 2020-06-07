package misc.terasystems;

import org.pclg.gui.MapEditor;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
	Utility para la configuracion del servlet WDispatcher
*/
public class ConfigNew extends Frame implements ActionListener, WindowListener {
    private static final long serialVersionUID = -2586650169754369596L;
    private final String filename;
	private Button loadBt, exitBt;

	/**
		@param filename El nombre del archivo de configuracion
	*/
	private ConfigNew(final String filename) {
		super("Servlet configuration");
        addWindowListener(this);    // Me interesan los eventos
		this.filename = filename;
		try {
			add("North", new TextField("servletName"));
			final Panel buttonPanel = new Panel();
            loadBt = new Button("Edit Properties");
			exitBt = new Button("Exit");
			loadBt.addActionListener(this);
			exitBt.addActionListener(this);
			buttonPanel.add(loadBt);
			buttonPanel.add(exitBt);
			add("South", buttonPanel);
			pack();
			setVisible(true);
			load();
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}


    // Todos estos putos metodos hay que "implementarlos" para poder
    // tener WindowClosing()
    public void windowOpened(final WindowEvent e) { }
    public void windowClosed(final WindowEvent e) { }
    public void windowIconified(final WindowEvent e) { }
    public void windowDeiconified(final WindowEvent e) { }
    public void windowActivated(final WindowEvent e) { }
    public void windowDeactivated(final WindowEvent e) { }

    public void windowClosing(final WindowEvent e) {
        exit();
    }

	public void actionPerformed(final ActionEvent e) {
		if(e.getSource() == exitBt) {
			exit();
		}
		if(e.getSource() == loadBt) {
			try {
				load();
			} catch (final IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

	void load() throws IOException {
		System.out.println("Loading " + filename);
		final File f = new  File(filename);
		final BufferedReader in = new BufferedReader(new FileReader(f));
		final StringBuffer sb = new StringBuffer();
		String s;
		String servletName = "";
		final int paramNr;

		// Descartamos lo que no hace falta
		while((s = in.readLine()) != null) {
			if(s.indexOf("servlet.trucks.code") >= 0)
				servletName = s;
			if(s.indexOf("servlet.trucks.initArgs") >= 0)
				break;
		}
		// podemos tener parametros en esta linea
		sb.append(s.substring(s.indexOf('=')).trim());
		// Leemos lo parametros hasta que nos consigamos con otro servlet
		while((s = in.readLine()) != null) {
			s = s.trim();
			if(s.startsWith("servlet."))
				break;
			if(s.length() == 0 || s.startsWith("#"))
				continue;
			sb.append(s);
		}
		in.close();
		final StringTokenizer st = new StringTokenizer(sb.toString(), "=,\\");
		paramNr = st.countTokens() / 2;
		
		final Map<MapEditor.Property, String> map = new TreeMap<>();
		for(int i = 0; i < paramNr; i++) {
			final String key = st.nextToken().trim();
			final String value = st.nextToken().trim();
			final MapEditor.Property property = new MapEditor.Property(key, null);
			map.put(property, value);
		}
		
		final MapEditor propertiesPanel = new MapEditor(this, map);
		if (propertiesPanel.editMap("Modification des properties")) {
			save(propertiesPanel.getMap());
		}
	}

	void exit() {
		setVisible(false);
		dispose();
		System.exit(0);
	}

	void save(final Map<String, String> map) {
		System.out.println("Saving " + filename);
		try {
			final File inFile = new File(filename);
			final BufferedReader in = new BufferedReader(new FileReader(inFile));
			final File outFile = new File(filename + "_tmp");
			final PrintWriter out = new PrintWriter(new FileWriter(outFile));
			String s;
			// Primero escribimos lo que no modificamos
			while((s = in.readLine()) != null) {
				if(s.indexOf("servlet.trucks.initArgs") >= 0)
					break;
				out.println(s);
			}
			// Escribimos nuestros parametros
			out.println("servlet.trucks.initArgs=\\");
			final int size = map.size();
			int i = 0;
			for (final Entry<String, String> entry : map.entrySet()) {
				out.print("\t" + entry.getKey() + "=" + entry.getValue());
				if(i++ < size - 1)
					out.println(",\\");
				else
					out.println();
			}
			// Descartamos los parametros antiguos
			while((s = in.readLine()) != null) {
				final String t;
				t = s.trim();
				if(t.length() == 0 || t.startsWith("#"))
					out.println(s);
				if(t.startsWith("servlet.")) {
					out.println(s);
					break;
				}
			}
			// Y el resto que pudiera haber
			while((s = in.readLine()) != null) {
				out.println(s);
			}
			out.close();
			in.close();
			if(!inFile.delete()) {
				System.out.println("No pude borrar");
			}
			if(!outFile.renameTo(inFile)) {
				System.out.println("No pude renombrar");
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
		Entry point de la aplicacion
        @param args El nombre del archivo de configuracion.
        Si no es provisto se asume 'servlet.properties'
	*/
	public static void main(final String [] args) {
		if(args.length == 1)
			new ConfigNew(args[0]);
		else
			new ConfigNew("C:/plucien/PERSONNEL/servlet.properties");
	}
}
