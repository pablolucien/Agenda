package misc.ingedigit.IntraApp;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class QueryDlg extends Dialog implements ActionListener{
    private static final long serialVersionUID = 2295455291029435714L;
    private final Panel     PanelEast   = new Panel(new GridLayout(3,1));
        private final Panel     PanelWest   = new Panel();
        private final Panel     PanelCenter = new Panel(new GridLayout(3,1));
        private final Panel     PanelSouth  = new Panel(new BorderLayout());
        private final Panel     PanelBotones= new Panel();
        
        private final Button[]    Botones;
	private final Button Agregar = new Button("Agregar");
	private final Button bY= new Button("And");
	private final Button bO = new Button("Or");
        private final List      ListaCampos = new List();
        private final Choice    Condicion   = new Choice();
        private final TextField Valor       = new TextField(15);
        private final TextArea  Salida      = new TextArea("",5,20,TextArea.SCROLLBARS_NONE);
        private final Label     lValor      = new Label("    Al Valor:");
        private final Table     TablaActual;
        private final FrameBrowser Parent;
        QueryDlg(final FrameBrowser Parent, final Table Tabla){
                super(Parent,"Filtrar tabla: " + Tabla.sNomTabla);
                 this.Parent = Parent;
		TablaActual = Tabla;
                setLayout(new BorderLayout());
                setModal(true);
                
                try{
			for(int i = 0; i< TablaActual.iColumnCount;i++){
    				ListaCampos.add(TablaActual.TableInfo.getColumnLabel(i+1));
    			}
	    	}
	    	catch(final Exception e){
	  	 System.out.println ("Error: Cargando campos para el query");	
		 e.printStackTrace();
           	} 


		PanelWest.add(ListaCampos);
		
		Condicion.add("=");
		Condicion.add("<>");
		Condicion.add("<");
		Condicion.add(">");
		bY.addActionListener(this);
		bO.addActionListener(this);
		Agregar.addActionListener(this);

		PanelCenter.add(Condicion);
		PanelCenter.add(bY);
		PanelCenter.add(bO);
		
		PanelEast.add(lValor);
		PanelEast.add(Valor);
		PanelEast.add(Agregar);
		Botones = new Button[4];
		Botones[0] = new Button("Filtrar");
		Botones[1] = new Button("Borrar");
		Botones[2] = new Button("Salir");
		Botones[3] = new Button("Ver Todos");


		
		for(int i = 0; i < 4 ; i++){
		   Botones[i].addActionListener(this);
		   PanelBotones.add(Botones[i]);
		}
		
		Salida.setEditable(false);
		PanelSouth.add("North",Salida);
		PanelSouth.add("South",PanelBotones);
		
		
                add("West"  ,PanelWest);
                add("Center",PanelCenter);
                add("East"  ,PanelEast);
                add("South" ,PanelSouth);
                pack();
                setVisible(true);
        }

 	public void actionPerformed(final ActionEvent e) {
                if (e.getActionCommand() == "Salir"){
                	setVisible(false);
                	dispose();
                }
                if (e.getActionCommand() == "Agregar"){
                	Salida.append(" " + ListaCampos.getSelectedItem() + " " + Condicion.getSelectedItem() + "'" + Valor.getText() + "'");
                }
               if (e.getActionCommand() == "Borrar"){
                	Salida.setText("");
                }             
                if (e.getActionCommand() == "And"){
                	Salida.append(" AND ");
                }
                if (e.getActionCommand() == "Or"){
                	Salida.append(" OR ");
                }
                if (e.getActionCommand() == "Filtrar"){
                	Parent.Doquery(Salida.getText());
                	setVisible(false);
                	dispose();
                }  
                if (e.getActionCommand() == "Ver Todos"){
                	Parent.Active();
			setVisible(false);
			dispose();
                }

            
        }
}
