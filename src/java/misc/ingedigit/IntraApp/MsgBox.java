package misc.ingedigit.IntraApp;

/*
   Clase para el manejo gen‚rico de una caja de dialogo con o
   sin decisiones
   JLOR150698
*/

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MsgBox extends Dialog implements ActionListener{
    private static final long serialVersionUID = -1856548370885119962L;
    private final Panel     PanelBotones;
        private Button [] Botones;
        private final Label     AreaMensage;
        private final String[] Etiquetas= {"Si","No","Cancel"};
        int       Resultado;
        MsgBox(final Frame Parent, final String Title, final String Type, final String Msg){
                super(Parent,Title);
                setLayout(new BorderLayout());
                setModal(true);
                AreaMensage = new Label(Msg,Label.CENTER);

                add("Center", AreaMensage );
                PanelBotones = new Panel();

                if( Type.equalsIgnoreCase("OK") ){
                        Botones = new Button[1];
                        Botones[0] = new Button("Ok");
                        PanelBotones.add(Botones[0]);
                        Botones[0].addActionListener(this);
                }
                if( Type.equalsIgnoreCase("YESNOCANCEL") ){
                        Botones = new Button[3];
                        for (int i = 0; i<3;i++){
                                Botones[i] = new Button(Etiquetas[i]);
                                PanelBotones.add(Botones[i]);
                                Botones[i].addActionListener(this);
                        }
                }
                if( Type.equalsIgnoreCase("YESNO") ){
                        Botones = new Button[2];
                        for (int i = 0; i<2;i++){
                                Botones[i] = new Button(Etiquetas[i]);
                                PanelBotones.add(Botones[i]);
                                Botones[i].addActionListener(this);
                        }
                }
                add("South",PanelBotones);
                pack();
                setVisible(true);
        }

 	public void actionPerformed(final ActionEvent e) {
                if (e.getActionCommand() == "Si") {
					Resultado = 1;
				}

                if (e.getActionCommand() =="No") {
					Resultado = 2;
				}

                if (e.getActionCommand() == "Cancel") {
					Resultado = 3;
				}
                setVisible(false);
                dispose();
        }
}
