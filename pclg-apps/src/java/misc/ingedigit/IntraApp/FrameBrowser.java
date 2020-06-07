package misc.ingedigit.IntraApp;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class FrameBrowser extends Frame implements ActionListener,ItemListener{
    private static final long serialVersionUID = 7597648744974738986L;
    private final Table TablaActual;
	private final Font Fuente = new Font("Monospaced",15,15);
	private final Panel PanelBotones = new Panel();
	private final Button Ok = new Button("Ok");
	private final Button Filtrar = new Button("Filtrar");
	final List Lista= new List(20,true);
        private final Imec Parent;
        private QueryDlg  QueryDialog;

        FrameBrowser(final String Titulo, final Table Tabla, final Imec Parent){
		super(Titulo);
                this.Parent = Parent;
		TablaActual = Tabla;
		setLayout(new BorderLayout());
		PanelBotones.setLayout(new FlowLayout());
		Lista.setMultipleMode(false);
		Lista.setFont(Fuente);
		PanelBotones.add(Ok);
		PanelBotones.add(Filtrar);
		add("Center",Lista);
		add("South",PanelBotones);
		Ok.addActionListener(this);
		Filtrar.addActionListener(this);
		Lista.addActionListener(this);
		Lista.addItemListener(this);
		
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand() == "Ok"){
                    if (Lista.getSelectedItem() != null ){
                        Parent.TablaActual.Seek( (Lista.getSelectedItem()).substring(1,4) );
                        Parent.DisplayData();
                    }
                    System.out.println("Ok...");
                    setVisible(false);
                    dispose();
		}	
		
		if (e.getActionCommand() == "Filtrar"){
			QueryDialog = new QueryDlg(this,TablaActual);
			
		}
	}
	
        @Override
		public void itemStateChanged(final ItemEvent e){}
        
        
        void Doquery(final String Condicion){
             TablaActual.Requery(Condicion);
             Lista.removeAll();
   	     String Values ="";
        	
             try{
                while ( TablaActual.Cursor.next()) {
				Values = "";
				for(int i =0; i<TablaActual.iColumnCount; i++){ 
					if(i == 0){
						for(int j = (TablaActual.Cursor.getString(1)).length(); j <4 ; j++)
						{
							Values += " ";
						}
					}
 					Values += (i == 0)? TablaActual.Cursor.getString(i+1): " " + TablaActual.Cursor.getString(i+1);
 				}
				Lista.add(Values);
		}    	
	     }
	     catch(final Exception e){
	 	System.out.println ("Error Filtrando el Browser...");	
		e.printStackTrace();
	     }
	     
        }

	void Active (){
		String Values ="";
		try{
                     	TablaActual.ReloadTable();
             	     	Lista.removeAll();
                        while ( TablaActual.Cursor.next()) {
				Values = "";
				for(int i =0; i<TablaActual.iColumnCount; i++){ 
					if(i == 0){
						for(int j = (TablaActual.Cursor.getString(1)).length(); j <4 ; j++)
						{
							Values += " ";
						}
					}
 					Values += (i == 0)? TablaActual.Cursor.getString(i+1): " " + TablaActual.Cursor.getString(i+1);
 				}
				Lista.add(Values);
			}
		}
	   	catch(final Exception e){
			System.out.println ("Error Construyendo el Browser...");	
			e.printStackTrace();
	   	}
                setSize(450,250);
		setVisible(true);
	}
}

