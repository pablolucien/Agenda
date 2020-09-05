package org.pclg.tools;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A dialog to show and select Fonts.
 */
public final class FontDialog extends JDialog implements ItemListener, ListSelectionListener {
    private static final long serialVersionUID = -8088982887680610264L;
    private final JList<String> fontList = new JList<>();
    private final JCheckBox fontBold = new JCheckBox("Bold");
    private final JCheckBox fontItalic = new JCheckBox("Italic");
    private final JCheckBox wrap = new JCheckBox("Wrap", false);
    private final Box buttonPanel = new Box(BoxLayout.Y_AXIS);
    private final JTextArea testArea = new JTextArea("", 20, 40);
    private final JScrollPane scrollArea = new JScrollPane(testArea);
    private final JButton testBt = new JButton("Test");
    private final JButton hideBt = new JButton("Hide");
    private final Font defaultFont = getFont();
    private Font selectedFont;
    private final boolean standAlone;
    private static final String[] SIZES = {
        "8", "9", "10", "12", "14", "18", "20", "32", "48", "72"
    };
    private final JComboBox<String> fontSizes = new JComboBox<>(SIZES);

    public FontDialog(final Frame parent) {
        this(parent, false);
    }

    private FontDialog(final Frame parent, final boolean alone) {
        super(parent, "Font Dialog", false);
		standAlone = alone;
        addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(final WindowEvent ev) {
                exit();
            }
        });

        final String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontList.setListData(fonts);

        fontSizes.setEditable(true);
        final JPanel stylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stylePanel.add(fontSizes);
        stylePanel.add(fontBold);
        stylePanel.add(fontItalic);

        final JButton exitBt = new JButton("Done");
        buttonPanel.add(exitBt);
        buttonPanel.add(hideBt);
        buttonPanel.add(testBt);
        buttonPanel.add(wrap);


/*
		MouseListener mouseListener = new MouseAdapter() {
        	Robot rob = null;
			public void mouseExited(MouseEvent e) {
				Point p = testBt.getLocationOnScreen();
				if(rob == null) {
					try {
        				rob = new Robot();
					}
					catch(AWTException ex) {}
				}
				else {
					rob.mouseMove(p.x + 0, p.y + 0);
				}
			}

			public void mouseClicked(MouseEvent e) {
//		         if (e.getClickCount() == 2) {
//	             int index = fontList.locationToIndex(e.getPoint());
//	             System.out.println("Double clicked on Item " + index);
				 testFont();
//	          }
			}
		};
		fontList.addMouseListener(mouseListener);
*/


        final class MyCellRenderer extends DefaultListCellRenderer {
            private static final long serialVersionUID = 3248238577143220473L;
            final ImageIcon longIcon = new ImageIcon("plus.gif");
            final ImageIcon shortIcon = new ImageIcon("minus.gif");

            /**
             * @param list
             * @param value        the value to display.
             * @param index        the cell index.
             * @param isSelected   is the cell selected?
             * @param cellHasFocus the list and the cell have the focus?
             * @return the Cell Renderer Component
             */
            @Override
			public Component getListCellRendererComponent(final JList<?> list,  final Object value,
                    final int index,  final boolean isSelected,  final boolean cellHasFocus) {
                final String name = value.toString();
                setText(name);
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                setEnabled(list.isEnabled());

                final Font wantedFont = new Font(name, Font.PLAIN, 14);
                if (wantedFont.canDisplayUpTo(name) == name.length()) {
                    setIcon(longIcon);
                    setFont(wantedFont);
                } else {
                    setIcon(shortIcon);
                    setFont(defaultFont);
//System.out.println("canDisplayUpTo " + wantedFont.canDisplayUpTo(name));
                }
                return this;
            }
        }

        fontList.setCellRenderer(new MyCellRenderer());

        testBt.addActionListener(event -> testFont());
        exitBt.addActionListener(event -> exit());
        hideBt.addActionListener(event -> {
            if (hideBt.getText().equals("Hide")) {
                hideTestArea();
            } else {
                showTestArea();
            }
            invalidate();
            validate();
            pack();
        });

        final JPanel fontPanel = new JPanel(new BorderLayout());
        fontPanel.add(new JScrollPane(fontList), BorderLayout.WEST);
        fontPanel.add(stylePanel, BorderLayout.SOUTH);

        getContentPane().add("West", fontPanel);
        getContentPane().add("East", buttonPanel);
        getContentPane().add("Center", scrollArea);
        pack();

        final Font currentFont = getFont();
        fontBold.setSelected((currentFont.isBold()));
        fontItalic.setSelected((currentFont.isItalic()));
        final int listSize = fontList.getModel().getSize();
        for (int i = 0; i < listSize; i++) {
            if (fontList.getModel().getElementAt(i).equals(currentFont.getName())) {
                fontList.setSelectedIndex(i);
                break;
            }
        }
        fontSizes.setSelectedIndex(3);

        // Los listeners deben agregarse luego de que esten creadas las listas
        fontList.addListSelectionListener(this);
        fontBold.addItemListener(this);
        fontItalic.addItemListener(this);
        wrap.addItemListener(this);
        fontSizes.addItemListener(this);
        testFont();
    }

    private void exit() {
        dispose();
        setVisible(false);
        if (standAlone) {
            System.exit(0);
        }
    }

    /**
     * Oculta el área de visualización de la fuente.
     */
    private void hideTestArea() {
        getContentPane().remove(scrollArea);
        buttonPanel.remove(testBt);
        buttonPanel.remove(wrap);
        hideBt.setText("Show");
    }

    /**
     * Muestra el área de visualización de la fuente.
     */
    private void showTestArea() {
        getContentPane().add("Center", scrollArea);
        buttonPanel.add(testBt);
        buttonPanel.add(wrap);
        hideBt.setText("Hide");
    }

    // implementacion de ItemListener;
    @Override
	public void itemStateChanged(final ItemEvent event) {
        if (event.getSource() == fontItalic || event.getSource() == fontBold) {
            testFont();
        } else if (event.getSource() == fontSizes) {
            testFont();
        } else if (event.getSource() == wrap) {
            testArea.setLineWrap(wrap.isSelected());
            testArea.setWrapStyleWord(wrap.isSelected());
        }
    }


    // implementacion de ListSelectionListener;
    @Override
	public void valueChanged(final ListSelectionEvent e) {
        testFont();
    }

    /**
     * Muestra la fuente seleccionada en el área de texto.
     */
    private void testFont() {
        int style = 0;
        int size;
        if (fontBold.isSelected()) {
            style += Font.BOLD;
        }
        if (fontItalic.isSelected()) {
            style += Font.ITALIC;
        }
        try {
            size = Integer.parseInt(fontSizes.getSelectedItem().toString());
        } catch (final NumberFormatException ex) {
            size = 10;
        }
        selectedFont = new Font(fontList.getSelectedValue(), style, size);
        testArea.setFont(selectedFont);
        testArea.setText(selectedFont.getName()
                + "\nthe quick brown fox jumped over the lazy dog 1234567890 times"
                + "\nTHE QUICK BROWN FOX JUMPED OVER THE LAZY DOG 1234567890 TIMES"
                + "\nel veloz murciélago hindú comía feliz cardillo y kiwi. la cigüeña tocaba el saxofón detrás del palenque de paja."
                + "\nEL VELOZ MURCIÉLAGO HINDÚ COMÍA FELIZ CARDILLO Y KIWI. LA CIGÜEÑA TOCABA EL SAXOFÓN DETRÁS DEL PALENQUE DE PAJA.");
    }

    /**
     * Devuelve la fuente seleccionada.
     */
    public Font getSelectedFont() {
        return selectedFont;
    }

    public static void main(final String[] args) {
        new FontDialog(new Frame(), true).setVisible(true);
    }
}
