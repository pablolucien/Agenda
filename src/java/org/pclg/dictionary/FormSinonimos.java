/*
 * Creado el 19-Mar-2008
 */
package org.pclg.dictionary;

import org.pclg.tools.GUITools;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.List;

/**
 * @author Un autor en busca de personajes.
 */
class FormSinonimos extends JDialog implements ActionListener {
    private static final long serialVersionUID = -5750401576030492703L;
    private final JTextField textField = new JTextField();
    private final JList list = new JList();
    private final JPanel buttonPanel = new JPanel();
    private final JButton okButton = new JButton("Buscar");
    private final JButton byeButton = new JButton("Adeus");
    private final Connection conn;

    /**
     * @param owner
     * @param conn
     * @throws java.awt.HeadlessException
     */
    public FormSinonimos(final Frame owner, final Connection conn) throws HeadlessException {
        super(owner, "Sinónimos", true);
        this.conn = conn;
        final Container container = getContentPane();
        container.add(textField, BorderLayout.NORTH);
        container.add(new JScrollPane(list), BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(okButton);
        okButton.addActionListener(this);
        buttonPanel.add(byeButton);
        pack();
		GUITools.center(this, owner);
    }

    /* (sin Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	public void actionPerformed(final ActionEvent event) {
	    final List sinónimos = DictionaryDB.obtenerSinonimos(conn, textField.getText());
	    //list.getModel().
    }
}
