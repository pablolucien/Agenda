package misc.ingedigit.IntraApp;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class Imec extends Frame implements ActionListener, WindowListener{
    private static final long serialVersionUID = 4691529107276876068L;
    private Panel         PanelDatos;
	private Panel PanelBotones;
	private final Button    []  Botones= new Button[5];
	Button    [] OkFrame;
	private TextField []  CamposDatos;
       	private   int statusImec;
	Table 	      TablaActual;
	private FrameBrowser  Browser;
        MsgBox        DlgMsg;
        public Imec(final Table Tabla){
          super("Mantenimiento de: " + Tabla.sNomTabla);
	  try{
		TablaActual = Tabla;
		int i=0;	
		setLayout(new BorderLayout());
                PanelDatos   = new Panel( new GridLayout(TablaActual.iColumnCount,TablaActual.iColumnCount) );
		PanelBotones = new Panel();
		CamposDatos  = new TextField[TablaActual.iColumnCount];
                Browser      = new FrameBrowser("Consulta de " + TablaActual.sNomTabla,TablaActual,this);

		// Se agregan los campos 
		for(i = 0; i < TablaActual.iColumnCount;i++){

			CamposDatos[i] = new TextField(30);
                        PanelDatos.add( new Label( TablaActual.sFieldLabels[i],Label.RIGHT ) );
                        PanelDatos.add( CamposDatos[i] );
			
     		}
	
		Botones[0]= new Button("Incluir");
     		Botones[1]= new Button("Modificar");
		Botones[2]= new Button("Eliminar");
		Botones[3]= new Button("Consultar");
		Botones[4]= new Button("Browser");
 
		for( i = 0; i < 5; i++) {
			Botones[i].addActionListener(this);
		}

		for( i = 0; i < 5; i++) {
			PanelBotones.add(Botones[i]);
		}
   		
                addWindowListener(this);

                add("Center",PanelDatos);
		add("South" ,PanelBotones);
		DisplayData();
			 
	   }
	   catch(final Exception e){
		System.out.println ("Error Construyendo Objeto Imec...");	
		e.printStackTrace();
	   }
	}
	
	// Respuestas a los Eventos
        public void windowActivated(final WindowEvent e) {}
        public void windowDeactivated(final WindowEvent e) {}
        public void windowDeiconified(final WindowEvent e) {}
        public void windowIconified(final WindowEvent e) {}
        public void windowOpened(final WindowEvent e) {}
        public void windowClosing(final WindowEvent e) {
                System.out.println("Cerrar window");
                setVisible(false);
                dispose();
        }

        public void windowClosed(final WindowEvent e) {
                System.out.println("Cerrar window");
                setVisible(false);
                dispose();
        }

        protected void processWindowEvent(final WindowEvent e){
                if( (e.paramString()).equals("WINDOW_CLOSING") ){
                        setVisible(false);
                        dispose();
                }
        }

 	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand() == ">"){
			TablaActual.NextRecord();
			DisplayData();
		}
		if (e.getActionCommand() == "Incluir"){
			ClearScreen();
			Botones[0].setLabel("Guardar");
		}
		if (e.getActionCommand() == "Guardar"){
			if ( TablaActual.Insert(CamposDatos) ){
			   Botones[0].setLabel("Incluir");
			}
		}
                if (e.getActionCommand() == "Modificar") {
					TablaActual.Update(CamposDatos);
				}

		if (e.getActionCommand() == "Eliminar"){
                       if ((new MsgBox(this,"Eliminar...", "YESNO","Realmente desea eliminar el registro ?")).Resultado == 1)
					   {
						   TablaActual.Delete(CamposDatos);
					   }
			DisplayData();	
		}
		if (e.getActionCommand() == "Consultar"){
			Botones[3].setLabel("Buscar");
			ClearScreen();
		}
		if (e.getActionCommand() == "Buscar"){
			if ( TablaActual.Seek(CamposDatos[0].getText()) ) {
				DisplayData();
			}
                        Botones[3].setLabel("Consultar");
		}

                if (e.getActionCommand() == "Browser"){
                    Browser.Active();
		    if(Browser.Lista.getSelectedItem() != null){
			System.out.println( (Browser.Lista.getSelectedItem()).substring(1,4));
		    }		
		}
                else{   System.out.println(e.getActionCommand());}

	}

        void DisplayData(){
		try{
			final String[] CurRecord = TablaActual.getCurRecord();
			ClearScreen();
			for (int i = 0; i < TablaActual.iColumnCount; i++) {
				CamposDatos[i].setText(CurRecord[i]);
			}
		}
	  	catch(final Exception e){
			System.out.println ("Error Mostrando registro...");	
			e.printStackTrace();
	   	}
	}

	void ClearScreen(){
		for(int i = 0 ; i< TablaActual.iColumnCount; i++) {
			CamposDatos[i].setText("");
		}
	}

}
