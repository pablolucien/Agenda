package misc.terasystems;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
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
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
	Utility para la configuracion del servlet WDispatcher
*/
public class Config extends Frame implements ActionListener, WindowListener {
    private static final long serialVersionUID = -7624748983616726112L;
    private final String filename;
	private Label [] labels;
	private TextField [] fields;
	private Button saveBt, loadBt, exitBt;

	/**
		@param filename El nombre del archivo de configuracion
	*/
	private Config(final String filename) {
		super("Servlet configuration");
        addWindowListener(this);    // Me interesan los eventos
		this.filename = filename;
		load();
		try {
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
			final Panel panel = new Panel();
			final GridBagLayout gridbag = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			panel.setLayout(gridbag);
			labels = new Label[paramNr];
			fields = new TextField[paramNr];
			for(int i = 0; i < paramNr; i++) {
				c.gridwidth = GridBagConstraints.RELATIVE;
				c.weightx = 0.0;
				labels[i] = new Label(st.nextToken().trim() + ":", Label.RIGHT);
				gridbag.setConstraints(labels[i], c);
				panel.add(labels[i]);
				c.gridwidth = GridBagConstraints.REMAINDER;
				c.weightx = 1.0;
				fields[i] = new TextField(st.nextToken().trim(), 25);
				gridbag.setConstraints(fields[i], c);
				panel.add(fields[i]);
			}
			add("North", new TextField(servletName));
			add("Center", panel);
			final Panel buttonPanel = new Panel();
            loadBt = new Button("Load");
			saveBt = new Button("Save");
			exitBt = new Button("Exit");
            loadBt.addActionListener(this);
			saveBt.addActionListener(this);
			exitBt.addActionListener(this);
//                      buttonPanel.add(loadBt);
			buttonPanel.add(saveBt);
			buttonPanel.add(exitBt);
			add("South", buttonPanel);
			pack();
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
			load();
		}
		if(e.getSource() == saveBt) {
			save();
		}
	}

	void load() {
		System.out.println("Loading " + filename);
	}

	void exit() {
		setVisible(false);
		dispose();
		System.exit(0);
	}

	void save() {
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
			for(int i = 0; i < labels.length; i++) {
				out.print("\t" + labels[i].getText() + "=" + fields[i].getText());
				if(i < labels.length - 1)
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
			(new Config(args[0])).setVisible(true);
		else
			(new Config("servlet.properties")).setVisible(true);
	}
}
