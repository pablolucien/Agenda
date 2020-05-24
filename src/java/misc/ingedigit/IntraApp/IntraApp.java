package misc.ingedigit.IntraApp;

/* Prueba de la intranet conectando a Ora8 */
/* Programa principal crea la conexion y la pantalla inicial */


import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class IntraApp extends java.applet.Applet implements ActionListener{

    private static final long serialVersionUID = -6456220850111814815L;
    private Connection Db;
  Table    TNoticia;
	Table TEventos;
  private Imec      Mantenimiento;
  private ResultSet DBMetaData;
  private final Button    bEventos = new Button("Eventos");
	private final Button bNoticias = new Button("Noticias");
  private Statement MiSql;
  // Etiquetas en la pantalla por cada campo. Esto en algunos manejadores puede recidir en la BD
  // es decir puede traerse con el resto la metadata
  private final String[]  sLabelsEvento  = {"Codigo: ","Fecha: ", "Tipo: ", "Clase: ","Nombre: ","Orador: ", "Lugar: ", "Descripci¢n: ","Telefono: ","E-mail: "};
  private final String[]  sLabelsNoticia = {"Codigo: ","Fecha: ", "Tipo: ", "Titulo: ","Fuente: ","Descripcion: "};

  // tipo de datos para la validacion no se esta usando
  private final String[]  sFieldDispType = {"SRT", "DAT", "CHAR", "STR","STR", "STR", "STR","STR","STR","STR"};


  @Override
  public void init () {
	try{
		//Cargar el Driver Oracle JDBC
		Class.forName ("oracle.jdbc.driver.OracleDriver");

		// Conectarse a la BDD,                                                 DBname, PW    ,ID
//		Db = DriverManager.getConnection ("jdbc:oracle:thin:@161.196.58.68:1521:ORA8", "Intra", "Intra");
//pclg        Db = DriverManager.getConnection ("jdbc:oracle:thin:@161.196.58.3:1521:WG73", "Intra", "Intra");
/*pclg*/Db = DriverManager.getConnection ("jdbc:oracle:thin:scott/tiger@192.168.0.101:1521:orcl");

		// Crear un objeto Statement
		MiSql = Db.createStatement ();
    		DBMetaData = MiSql.executeQuery("select * from cat");

      		while(DBMetaData.next()){
 			System.out.println (DBMetaData.getString(1));
    		}
		
		System.out.println ("Conectado...");
		
                bEventos.addActionListener(this);
                bNoticias.addActionListener(this);
                add(bEventos);
                add(bNoticias);


	}
	catch(final Exception e){
		System.out.println ("Error Conectando Base de Datos...!");	
		e.printStackTrace();
	}
  }
  
  public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand() == "Eventos"){
              Mantenimiento = new Imec(new Table("EVENTO", sLabelsEvento ,sFieldDispType, Db));
        }

        if (e.getActionCommand() == "Noticias"){
//pclg              Mantenimiento = new Imec(new Table("NOTICIA",sLabelsNoticia,sFieldDispType, Db));
/*pclg*/Mantenimiento = new Imec(new Table("ASINTEC_TEST",sLabelsNoticia,sFieldDispType, Db));
        }

        Mantenimiento.pack();
        Mantenimiento.setVisible(true);
  }
}
