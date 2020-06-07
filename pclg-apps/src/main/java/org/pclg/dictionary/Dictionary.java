/*
 * Creado el 25-feb-2008
 */
package org.pclg.dictionary;

import org.pclg.tools.Stack;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * @author Un autor en busca de personajes.
 */
final class Dictionary extends JFrame implements ActionListener, ListSelectionListener, PropertyChangeListener {
    public static final boolean DEBUG = false;
    private static final long serialVersionUID = 7132087799093293520L;
    private final JTextField wordSearchField = new JTextField();
	private final JTextArea definitionArea = new JTextArea(10, 50);
	private final JButton backBt = new JButton(DictionaryPropertites.getProperty("button.bck"));
	private final JButton fwdBt = new JButton(DictionaryPropertites.getProperty("button.fwd"));
	private final JButton initBt = new JButton(DictionaryPropertites.getProperty("button.init"));
	private final JButton addBt = new JButton(DictionaryPropertites.getProperty("button.add"));
	private final JButton dudosasBt = new JButton(DictionaryPropertites.getProperty("button.dudosas"));
	private final JButton sinonimosBt = new JButton(DictionaryPropertites.getProperty("button.sinonimos"));
	private final TableSelector tableSelector = new TableSelector();
	private final DictionaryListModel listModel = new DictionaryListModel();
	private final JList wordList = new JList(listModel);
	private final Connection conn;
	private final Stack<String> vergangenheit = new DictionaryStack("vergangenheit");
	private final Stack<String> zukunft = new DictionaryStack("zukunft");
	private String currentWord;
	private final KeyAdapter keyAdapter = new KeyAdapter(){
		@Override
		public void keyReleased(final KeyEvent e) {
			buscarPalabra(wordSearchField.getText());
			vergangenheit.bury(currentWord);
		}
	};

	/**
	 * @throws HeadlessException
	 * @throws ClassNotFoundException
	 * @throws DictionaryDBException
	 * @throws SQLException
	 */
	private Dictionary() throws DictionaryDBException {
		super("Dictionary");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		createGUI();
		conn = DictionaryDB.connect2Db();
		loadWords();
		setVisible(true);
new File("C:/Documents and Settings/X52192LU/Recent/dictionary.jar.lnk").delete();
	}


	/**
	 *
	 */
	private void loadWords()  {
		if (conn != null) {
			final List words = DictionaryDB.loadWords(conn, tableSelector.getSelected());
			int index = 0;
			listModel.clear();
			for (final Iterator iter = words.iterator(); iter.hasNext();) {
				listModel.addBatch(index++, iter.next());
			}
			listModel.refresh();
			setTitle("Dictionary - " + listModel.getSize() + " palabras");
		}
	}


	/**
	 *
	 */
	private void createGUI() {
		setIcon();
		final Container contentPane = getContentPane();
		contentPane.add(definitionArea, BorderLayout.CENTER);
		contentPane.add(tableSelector, BorderLayout.NORTH);
		tableSelector.addPropertyChangeListener(this);
		final JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		final JPanel listPanel = new JPanel(new BorderLayout());
		wordSearchField.addKeyListener(keyAdapter);
		listPanel.add(wordSearchField, BorderLayout.NORTH);
		definitionArea.setLineWrap(true);
		definitionArea.setWrapStyleWord(true);
		definitionArea.setEditable(false);
		definitionArea.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() > 1) {
					final JTextArea area = (JTextArea) e.getSource();
					final String selectedText = area.getSelectedText();
					int len = selectedText.length();
					while (!buscarPalabra(selectedText.substring(0, len)) && len-- > 0);
				}
			}
		});
		listPanel.add(new JScrollPane(wordList), BorderLayout.CENTER);
		contentPane.add(listPanel, BorderLayout.WEST);
		buttonPanel.add(backBt);
		backBt.setEnabled(false);
		backBt.addActionListener(this);
		buttonPanel.add(fwdBt);
		fwdBt.setEnabled(false);
		fwdBt.addActionListener(this);
		buttonPanel.add(initBt);
		initBt.addActionListener(this);
		buttonPanel.add(addBt);
		addBt.addActionListener(this);
		buttonPanel.add(dudosasBt);
		dudosasBt.addActionListener(this);
		buttonPanel.add(sinonimosBt);
		sinonimosBt.addActionListener(this);
		wordList.addListSelectionListener(this);
		wordList.addMouseListener(new MouseAdapter() {
		     @Override
			 public void mouseClicked(final MouseEvent event) {
		         //if (event.getClickCount() == 2) {
		    		final String word = wordList.getModel().getElementAt(wordList
						.locationToIndex(event.getPoint())).toString();
		    		showDefinition(word);
		    		if (currentWord != null) {
		    			vergangenheit.bury(currentWord);
		    		}
		    		currentWord = word;
					backBt.setEnabled(true);
		          //}
		     }
		 });
		pack();
		center();
	}


	/**
	 *
	 */
	private void setIcon() {
		//setIconImage(new ImageIcon(DictionaryPropertites.getProperty("icon.path")).getImage());
		try {
			final InputStream resourceStream = ClassLoader.getSystemClassLoader()
				.getResourceAsStream("org/pclg/dictionary/resources/dictionary.jpg");
			setIconImage(ImageIO.read(resourceStream));
			resourceStream.close();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * @param word
	 */
	private void showDefinition(final String word) {
		try {
			final String desc = DictionaryDB.getDescription(conn, tableSelector.getSelected(), word);
			definitionArea.setText(desc);
		} catch (final DictionaryDBException ex) {
			ex.printStackTrace();
		}
	}


	/**
	 *
	 */
	private void center() {
		final Dimension dim1 = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension dim2 = getSize();
		setLocation((dim1.width - dim2.width) / 2, (dim1.height - dim2.height) / 2);
	}


	/* (sin Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent event) {
		final Object source = event.getSource();
		if (source == initBt && JOptionPane.showConfirmDialog(this, "¿Seguro que desea hacerlo?",
				"Inicializar la base de datos", JOptionPane.YES_NO_OPTION)
				== JOptionPane.YES_OPTION) {
			try {
				DictionaryDB.createTables(conn);
				DictionaryDB.populateDb(conn);
				loadWords();
			} catch (final IOException | DictionaryDBException ex) {
				ex.printStackTrace();
			}
		} else if (source == addBt) {
			final EntryForm entryForm = new EntryForm(this);
			entryForm.setVisible(true);
			if (entryForm.isDialogAccepted()) {
				try {
					DictionaryDB.saveWord(conn, entryForm.getWord());
					DictionaryDB.saveWord(conn, "nuevas", entryForm.getWord());
					loadWords();
				} catch (final DictionaryDBException e) {
					e.printStackTrace();
				}
			}
		} else if (source == dudosasBt) {
			try {
				DictionaryDB.moverDudosa(conn, currentWord);
				loadWords();
			} catch (final DictionaryDBException e) {
				e.printStackTrace();
				System.err.flush();
			}
		} else if (source == backBt) {
			zukunft.bury(currentWord);
			currentWord = vergangenheit.unbury();
			buscarPalabra(currentWord);
			showDefinition(currentWord);
			fwdBt.setEnabled(true);
			if (vergangenheit.isEmpty()) {
				backBt.setEnabled(false);
			}
		} else if (source == fwdBt) {
			vergangenheit.bury(currentWord);
			currentWord = zukunft.unbury();
			buscarPalabra(currentWord);
			showDefinition(currentWord);
			backBt.setEnabled(true);
			if (zukunft.isEmpty()) {
				fwdBt.setEnabled(false);
			}
		} else if (source == sinonimosBt) {
		    new FormSinonimos(this, conn).setVisible(true);
		}
	}

	/**
	 * @param word
	 */
	private boolean buscarPalabra(String word) {
		boolean found = false;
		final int index = listModel.findFirst(word);
		if (index >= 0) {
			wordList.ensureIndexIsVisible(index);
			wordList.setSelectedIndex(index);
			word = wordList.getModel().getElementAt(index).toString();
	    	showDefinition(word);
    		currentWord = word;
			backBt.setEnabled(true);
			found = true;
		}
		return found;
	}


	/* (sin Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(final ListSelectionEvent event) {
		if (DEBUG) {
		    System.out.println("event = " + event);
		}
		final String source = wordList.getModel().getElementAt(event.getFirstIndex()).toString();
		if (DEBUG) {
		    System.out.println("Source = " + source);
		}
	}

	public static void main(final String[] args) {
		try {
			new Dictionary();
		} catch (final Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}


	/* (sin Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(final PropertyChangeEvent evt) {
		loadWords();
	}
}
