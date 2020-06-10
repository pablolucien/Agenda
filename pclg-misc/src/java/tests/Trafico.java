package tests;// package

// imports

import org.pclg.gui.JLabeledField;
import org.pclg.tools.GUITools;
import org.pclg.tools.JRadioButtonPanel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.BorderUIResource;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
	tests.Trafico
	@author El Coyote Cojo
	@version 2001.jun.24 18:16:02, CEST
*/
public class Trafico extends JFrame implements ActionListener {
    private static final long serialVersionUID = -4151745556083479181L;

    // Clases internas
	class Item extends JPanel implements ChangeListener {
        private static final long serialVersionUID = 232696914688096075L;
        private final int id;
		private final JRadioButtonPanel rbp = new JRadioButtonPanel();
		private final JRadioButton aCheck = rbp.createButton("a");
		private final JRadioButton bCheck = rbp.createButton("b");
		private final JRadioButton cCheck = rbp.createButton("c");
		private final JRadioButton nullCheck = rbp.createButton(" ");        // There is no way to turn a button programmatically to "off", in order to clear the button group
		private final JCheckBox result = new JCheckBox("OK");
		private final Color bg = getBackground();
		
		Item(final int id) {
			this.id = id;
			setLayout(new GridLayout(4, 1));
			add(aCheck);
			add(bCheck);
			add(cCheck);
			add(result);
			aCheck.addChangeListener(this);
			bCheck.addChangeListener(this);
			cCheck.addChangeListener(this);
			result.addChangeListener(this);
			setBorder(new BorderUIResource.TitledBorderUIResource(
                String.valueOf(id)));
		}

		String selected() {
			if(aCheck.isSelected()) {
				return "a";
			}
			if(bCheck.isSelected()) {
				return "b";
			}
			if(cCheck.isSelected()) {
				return "c";
			}
			return " ";
		}

		void select(final String opt) {
			nullCheck.setSelected(true);
			//aCheck.setSelected(false);
			//bCheck.setSelected(false);
			//cCheck.setSelected(false);
			if(opt.equals("a")) {
				aCheck.setSelected(true);
			}
			if(opt.equals("b")) {
				bCheck.setSelected(true);
			}
			if(opt.equals("c")) {
				cCheck.setSelected(true);
			}
		}

		void select(final boolean ok) {
			result.setSelected(ok);
		}

		boolean ok() {
			return result.isSelected();
		}

		@Override
		public void paint(final Graphics g) {
			if(result.isSelected()) {
				setBackground(Color.green);
			}
			else if(aCheck.isSelected() || bCheck.isSelected() || cCheck.isSelected())
			{
				setBackground(Color.yellow);
			}
			else {
				setBackground(bg);
			}
			super.paint(g);
		}

		@Override
		public void stateChanged(final ChangeEvent e) {
			repaint();
		}
	}

	// Variables de clase
	
	// Variables de instancia
	private final int nrQuestions = 40;
	private final Item [] questions = new Item[nrQuestions];
	private final JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
	private final JButton initBt	= GUITools.addButton(this, buttonPanel, "Iniciar", Color.red, "Comineza a medir el tiempo");
	private final JButton evalBt	= GUITools.addButton(this, buttonPanel, "Evaluar", Color.red, "Evalua los resultados");
	private final JButton loadBt	= GUITools.addButton(this, buttonPanel, "Cargar", Color.red, "Cargar los resultados");
	private final JButton saveBt	= GUITools.addButton(this, buttonPanel, "Guardar", Color.red, "Guardar los resultados");
	private final JButton updtBt	= GUITools.addButton(this, buttonPanel, "Respuestas", Color.red, "Guardar las respuestas correctas");
	private final JButton quitBt	= GUITools.addButton(this, buttonPanel, "Salir par coño", Color.red, "Fin de la aplicación");
	private final JPanel northPanel = new JPanel(new GridLayout(1, 0));
	private final JLabeledField testNr = new JLabeledField("Numero", "1");
	private final JLabeledField errNr = new JLabeledField("Errores", "0");
	private final String driverClassName = "sun.jdbc.odbc.JdbcOdbcDriver";
	private final String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=/databases/Trafico.mdb;PWD=";
	private Connection conn;
	
	// Constructores
	
	/**
		Constructor por omision
		-author El Coyote Cojo
		-version 2001.jun.24 18:16:02, CEST
	*/
	private Trafico() {
		super("Examenes de Tráfico");
		setDefaultCloseOperation(EXIT_ON_CLOSE);	// La accion por omision es HIDE_ON_CLOSE
		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(4, 10));
		for(int i = 0; i < nrQuestions; i++) {
			questions[i] = new Item(i + 1);
			centerPanel.add(questions[i]);
		}
		northPanel.add(testNr);
		northPanel.add(errNr);
		getContentPane().add(northPanel, BorderLayout.NORTH);
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		pack();
		setVisible(true);
		initBt.doClick();
	}
	
	@Override
	public void actionPerformed(final ActionEvent event) {
		if(event.getSource() == quitBt) {
			try {
				conn.close();
			}
			catch(final Exception ex) {
			}
			GUITools.exitApplication(this, false);
		}

		if(event.getSource() == initBt) {
			for(int i = 0; i < nrQuestions; i++) {
				questions[i].select(" ");
				questions[i].select(false);
			}
			errNr.setText("0");
			try {
				Class.forName(driverClassName);
				conn = DriverManager.getConnection(url);
				final Statement stmt = conn.createStatement();
				final ResultSet rset = stmt.executeQuery("SELECT max(test) FROM Tests");
				if(rset.next()) {
					testNr.setText(String.valueOf((rset.getInt(1) + 1)));
				}
				stmt.close();
			}
			catch(final SQLException ex) {
				ex.printStackTrace();
			} catch (final ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		else if(event.getSource() == evalBt) {
			int err = 0;
			for(int i = 0; i < nrQuestions; i++) {
				if(!questions[i].ok()) {
					err++;
				}
			}
			errNr.setText(String.valueOf(err));
		}
		else if(event.getSource() == loadBt) {
			try {
				final Statement stmt = conn.createStatement();
				final ResultSet rset = stmt.executeQuery("SELECT * FROM Tests WHERE test = " + Integer.parseInt(testNr.getText()));
				while(rset.next()) {
					final int item = rset.getInt(2);
					questions[item - 1].select(rset.getString(3));
					questions[item - 1].select(rset.getBoolean(4));
				}
				stmt.close();
			}
			catch(final SQLException ex) {
				ex.printStackTrace();
			}
			evalBt.doClick();
		}
		else if(event.getSource() == saveBt) {
			try {
				final PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Tests VALUES (?, ?, ?, ?, ?)");
				final java.util.Date now1 = new java.util.Date();
				final java.sql.Date now = new java.sql.Date(now1.getTime());
				for(int i = 0; i < nrQuestions; i++) {
					pstmt.setInt(1, Integer.parseInt(testNr.getText()));
					pstmt.setInt(2, i + 1);
					pstmt.setString(3, questions[i].selected());
					pstmt.setBoolean(4, questions[i].ok());
					pstmt.setDate(5, now);
					pstmt.executeUpdate();
				}
			}
			catch(final SQLException ex) {
				ex.printStackTrace();
			}
		}
		else if(event.getSource() == updtBt) {
			try {
				final PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Respuestas VALUES (?, ?, ?)");
				final Statement stmt = conn.createStatement();
				final ResultSet rset = stmt.executeQuery("SELECT * FROM Tests");
				while(rset.next()) {
					final int test = rset.getInt(1);
					final int item = rset.getInt(2);
					final String ans = rset.getString(3);
					final boolean ok = rset.getBoolean(4);
					if(!ok) {
						continue;
					}
					pstmt.setInt(1, test);
					pstmt.setInt(2, item);
					pstmt.setString(3, ans);
					try {
							pstmt.executeUpdate();
					}
					catch(final SQLException ex) {
					}
				}
				stmt.close();
			}
			catch(final SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Metodos estaticos
	
	/**
		Ayuda al usuario
		-author El Coyote Cojo
		-version 2001.jun.24 18:16:02, CEST
	*/
	public static void usage() {
		System.out.println("Usage: java tests.Trafico " +  "");
		System.exit(0);
	}
	
	/**
		Ejecuta la aplicación
		-author El Coyote Cojo
		-version 2001.jun.24 18:16:02, CEST
	*/
	public static void main(final String [] args) {
		new Trafico();
	}
}
