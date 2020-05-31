package org.pclg.tools;

//import javafx.scene.layout.Region;

import org.apache.log4j.Logger;
import org.pclg.gui.JLabeledField;
import org.pclg.gui.VersatileJMenu;
import org.pclg.log.LoggerFactory;
import org.pclg.runtime.RuntimeControl;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.MAXIMIZED_BOTH;
import static org.pclg.tools.BoundsInfo.NULL_RECTANGLE;


/**
 * Provee algunas funciones de uso comun para manejo de archivos
 *
 * @author El Coyote Cojo
 * @version 2002.05.16
 */
public final class GUITools {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	/** Tama�o por defecto de las fuentes. */
	private static final int FONT_SIZE = 12;

	/** Tipo de fuente por defecto. */
	private static final String FONT_FACE = "Dialog";

	/** Usado por addButton */
	private static final FontUIResource toolTipFont = new FontUIResource(FONT_FACE, Font.BOLD, FONT_SIZE);

	/** Indica que hay que poner un separador */
	public static final String SEPARATOR = "**--GUITools.SEPARATOR--**";

	/**
	 * El constructor por omision es privado para evitar que a algun capullo se le ocurra
	 * hacer new GUITools()
	 */
	private GUITools() {
	}

	/**
	 * Crea un JTabbedPane segun la informacion de configuracion
	 *
	 * @param tabs   Los paneles a mostrar
	 * @param titles Los titulos de los paneles a mostrar
	 * @param fields Los campos de cada panel (un set por cada panel)
	 *
     * @return Adivina qu�
	 *
     * @since 2002.05.16
     */
    public static JTabbedPane createTabbedPanel(final String[] tabs,
                                                final String[] titles, final String[][] fields) {
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(SwingConstants.RIGHT);
        for (int i = 0; i < tabs.length; i++) {
            final Box panel = new Box(BoxLayout.Y_AXIS);

            // Si hay titulo lo ponemos
            if (titles.length > i) {
                panel.add(new JLabel(titles[i]));
            }

            // Si hay campos los ponemos
            if (fields.length > i && fields[i] != null) {
                for (int j = 0; j < fields[i].length; j++) {
                    if (fields[i][j].equals(SEPARATOR)) {
                        panel.add(Box.createVerticalStrut(15));
                    } else {
                        panel.add(new JLabeledField(fields[i][j]));
                    }
                }
            }

            //panel.add(javax.swing.Box.createGlue());
            tabbedPane.addTab(tabs[i], null, panel, tabs[i]);
        }

        tabbedPane.setSelectedIndex(0);
        return tabbedPane;
    }

    // ===================================================================================================================================
    /*
            @since 2002.05.26
	*/

    /**
     * Centrar una ventana en el area de la ventana padre o en la pantalla
     *
     * @param target La ventana que queremos centrar
     * @param parent La ventana donde queremos centrar a 'target'. Si es null se entiende que es la pantalla
     */
    public static void center(final Window target, final Window parent) {
        target.setLocation(getTargetLocation(target, parent));
    }

    /**
     * Centrar una ventana en el area de la ventana padre o en la pantalla
     *
     * @param target La ventana que queremos centrar
     * @param parent La ventana donde queremos centrar a 'target'. Si es null se entiende que es la pantalla
     */
    public static void scrollToCenter(final Window target,final Window parent) {
        final Point point = getTargetLocation(target, parent);
        for (int i = 0; i <= point.x; i++) {
            target.setLocation(i, point.y);
            try {
                Thread.sleep(5);
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static Point getTargetLocation(final Window target, final Window parent) {
        final int x;
        final int y;
        final Rectangle r = target.getBounds();
        if (parent == null) {    // Centrar en la pantalla
            final Dimension d = Toolkit.getDefaultToolkit()
                    .getScreenSize();
            x = (d.width - r.width) / 2;
            y = (d.height - r.height) / 2;
        } else {                  // Centrar en el area del padre
            final Rectangle r1 = parent.getBounds();
            x = r1.x + (r1.width - r.width) / 2;
            y = r1.y + (r1.height - r.height) / 2;
        }
        return new Point(x, y);
    }

    /**
     * Cambia una caracteristica de todos los hijos de un Container recursivamente
     * <pre>
     * ejemplo de uso:
     * containerMutator(padre, new ComponentMutator() {
     * public void mutate(Component c) {
     * if(c instanceof JTextField) {
     * ((JTextField) c).setText("");
     * ((JTextField) c).setEnabled(true);
     * }
     * else if(c instanceof JTable) {
     * .......
     * }
     * }
     * });
     * </pre>
     * --author Juan-Cho
     * --author El Coyote cojo
     * --version 2001.02.19
     *
     * @param padre   El contenedor a cuyos componentes se le va a aplicar la mutacion
     * @param mutator La clase encargada de efectuar la mutacion
     */
    public static void containerMutator(final Container padre,
                                        final ComponentMutator mutator) {
        final Component[] hijos = padre.getComponents();
        for (final Component hijo : hijos) {
            mutator.mutate(hijo);
            if (hijo instanceof Container) {
                containerMutator((Container) hijo, mutator);
            }
        }
    }

    /**
     * Metodo de utilidad para agregrar items a un menu
     *
     * @param listener El encargado de procesar los eventos de este objeto
     * @param parent   El menu donde yace este se�or
     * @param group    El grupo de botones al que pertenece
     * @param label    guess...
	 *
     * @return Una referencia al JRadioButtonMenuItem agregado al men�
     */
    public static JRadioButtonMenuItem addRadioButtonMenuItem(final ActionListener listener,
            final JMenu parent, final ButtonGroup group, final String label) {
        return addRadioButtonMenuItem(listener, parent, group, label, null);
    }

    /**
     * Metodo de utilidad para agregrar items a un menu
     *
     * @param listener El encargado de procesar los eventos de este objeto
     * @param parent   El menu donde yace este se�or
     * @param group    El grupo de botones al que pertenece
     * @param label    guess...
     * @param icon     guess...
	 *
     * @return Una referencia al JRadioButtonMenuItem agregado al men�
     */
    public static JRadioButtonMenuItem addRadioButtonMenuItem(final ActionListener listener, final JMenu parent,
            final ButtonGroup group, final String label, final Icon icon) {
        return addRadioButtonMenuItem(listener, parent, group, label, icon, new JRadioButtonMenuItem());
    }

    /**
     * Metodo de utilidad para agregrar items a un menu
     *
     * @param listener El encargado de procesar los eventos de este objeto
     * @param parent   El menu donde yace este se�or
     * @param group    El grupo de botones al que pertenece
     * @param label    guess...
     * @param icon     guess...
     * @param mi       the MenuItem to be added.
	 *
     * @return Una referencia al JRadioButtonMenuItem agregado al men�
     */
    public static JRadioButtonMenuItem addRadioButtonMenuItem(final ActionListener listener, final MenuElement parent,
            final ButtonGroup group, String label, final Icon icon, final JRadioButtonMenuItem mi) {
        if (label.length() > 0 && label.charAt(0) == '-') {
            // FIXME: Chapucilla los instanceof
            if (parent instanceof JMenu) {
                ((JMenu) parent).addSeparator();
            } else if (parent instanceof JPopupMenu) {
                ((JPopupMenu) parent).addSeparator();
            } else {
               throw new IllegalArgumentException("Invalid class for parent: " + parent.getClass());
            }
            if (label.length() == 1) {    // Only the separator
                return null;
            }
            label = label.substring(1);
        }

        // Si el label contiene '&' y no es el ultimo caracter
        // el siguiente caracter es el mnemonico
        final int indice = label.indexOf('&');
        if (indice > -1 && indice < label.length() - 1) {
            final char mnemo = label.charAt(indice + 1);
            label = label.substring(0, indice) + label
                .substring(indice + 1);
            mi.setMnemonic(mnemo);
        }

        mi.setText(label);

        // FIXME: Chapucilla los instanceof
        if (parent instanceof JMenu) {
            ((JMenu) parent).add(mi);
        } else if (parent instanceof JPopupMenu) {
            ((JPopupMenu) parent).add(mi);
        }
        group.add(mi);
        if (listener == null) {
            mi.setEnabled(false);
        } else {
            mi.addActionListener(listener);
        }

        if (icon != null) {
            mi.setIcon(icon);
        }

        return mi;
    }

    /**
     * lo que su nombre indica
     */
//	iccn deberia ser el nome
    private static void setIcon(final JMenuItem mi, final Icon icon) {
        try {
            if (icon != null) {
                mi.setIcon(icon);
            }
        } catch (final NullPointerException ex) {
            ToolBox.showInfo(ex);
            LOGGER.error("Imposible poner icono a " + mi);
        } catch (final Exception ex) {
            ToolBox.showInfo(ex);
        }
    }

    /**
     * Metodo de utilidad para agregrar submenues a un menu
     *
     * @param parent El menu donde se va a agregar
     * @param label  La etiqueta del JMenu. Si comienza por '-' se agrega
     *               un separador antes del item. Si solo es "-", solo se
     *               agrega el separador. Si la etiqueta contiene '&', el
     *               siguiente caracter es asignado como Mnemonico
     * @param icon   El icono a asignar al JMenuItem. Puede ser <code> null </code>
	 *
     * @return El JMenuItem creado o <code> null </code> si solo se agrego un separador
     */
    public static JMenu addMenu(final JMenu parent, String label,
        final Icon icon) {
        if (label.length() > 0 && label.charAt(0) == '-') {
            parent.addSeparator();
            if (label.length() == 1)            // S�lo el separador
            {
                return null;
            }
            label = label.substring(1);
        }
        final JMenu mi = new VersatileJMenu();
        mi.setText(label);
        setIcon(mi, icon);
        parent.add(mi);
        return mi;
    }

    public static JMenuItem addMenuItem(final ActionListener listener,
                                        final JMenu parent, final String label, final Optional<ImageIcon> optionalIcon) {
        return addMenuItem(listener, parent, label, optionalIcon.orElse(null));
    }

    /**
     * Metodo de utilidad para agregrar items a un menu
     *
     * @param listener El ActionListener que procesa las acciones del JMenuItem. Puede ser <code> null </code>
     * @param parent   El menu donde se va a agregar
     * @param label    La etiqueta del JMenuItem. Si comienza por '-' se agrega
     *                 un separador antes del item. Si solo es "-", solo se
     *                 agrega el separador. Si la etiqueta contiene '&', el
     *                 siguiente caracter es asignado como Mnemonico
     * @param icon     El icono a asignar al JMenuItem. Puede ser <code> null </code>
	 *
     * @return El JMenuItem creado o <code> null </code> si solo se agrego un separador
     */
    public static JMenuItem addMenuItem(final ActionListener listener,
                                        final JMenu parent,
                                        String label, final Icon icon) {
        if (label.length() > 0 && label.charAt(0) == '-') {
            parent.addSeparator();
            if (label.length() == 1)        // S�lo el separador
            {
                return null;
            }
            label = label.substring(1);
        }
        final JMenuItem mi = new JMenuItem();

        // Si el label contiene '&' y no es el ultimo caracter
        // el siguiente caracter es el mnemonico
        final int indice = label.indexOf('&');
        if (indice > -1 && indice < label.length() - 1) {
            final char mnemo = label.charAt(indice + 1);
            label = label.substring(0, indice) + label.substring(indice + 1);
            mi.setMnemonic(mnemo);
        }

        mi.setText(label);
        setIcon(mi, icon);

//		si el label contiene '^'
//		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));

        parent.add(mi);
        if (listener == null) {
            mi.setEnabled(false);
        } else {
            mi.addActionListener(listener);
        }
        return mi;
    }

    /**
     * Metodo de utilidad para agregrar items a un menu sin asignar icono
     *
     * @param listener El ActionListener que procesa las acciones del JMenuItem. Puede ser <code> null </code>
     * @param parent   El menu donde se va a agregar
     * @param label    La etiqueta del JMenuItem. Si comienza por '-' se agrega
     *                 un separador antes del item. Si solo es "-", solo se
     *                 agrega el separador. Si la etiqueta contiene '&', el
     *                 siguiente caracter es asignado como Mnemonico
	 *
     * @return El JMenuItem creado o <code> null </code> si solo se agrego un separador
     */
    public static JMenuItem addMenuItem(final ActionListener listener, final JMenu parent, final String label) {
        return addMenuItem(listener, parent, label, (Icon) null);
    }

    /**
     * Metodo de utilidad para agregrar items a un JPopupMenu
     *
     * @param listener El ActionListener que procesa las acciones del JMenuItem. Puede ser <code> null </code>
     * @param parent   El JPopupMenu donde se va a agregar
     * @param label    La etiqueta del JMenuItem. Si comienza por '-' se agrega
     *                 un separador antes del item. Si solo es "-", solo se
     *                 agrega el separador. Si la etiqueta contiene '&', el
     *                 siguiente caracter es asignado como Mnemonico
     * @param icon     El icono a asignar al JMenuItem. Puede ser <code> null </code>
	 *
     * @return El JMenuItem creado o <code> null </code> si solo se agrego un separador
     * --version 2002.feb.01 13:05:01, CET
     */
    private static JMenuItem addMenuItem(final ActionListener listener, final JPopupMenu parent,
            final String label, final Icon icon) {
        return addMenuItem(listener, parent, label, icon, new JMenuItem());
    }

    /**
     * Metodo de utilidad para agregrar items a un JPopupMenu
     *
     * @param listener El ActionListener que procesa las acciones del JMenuItem. Puede ser <code> null </code>
     * @param parent   El JPopupMenu donde se va a agregar
     * @param label    La etiqueta del JMenuItem. Si comienza por '-' se agrega
     *                 un separador antes del item. Si solo es "-", solo se
     *                 agrega el separador. Si la etiqueta contiene '&', el
     *                 siguiente caracter es asignado como Mnemonico
     * @param icon     El icono a asignar al JMenuItem. Puede ser <code> null </code>
	 *
     * @return El JMenuItem creado o <code> null </code> si solo se agrego un separador
     * --version 2002.feb.01 13:05:01, CET
     */
    private static JMenuItem addMenuItem(final ActionListener listener, final JPopupMenu parent,
            String label, final Icon icon, final JMenuItem mi) {
        if (label.length() > 0 && label.charAt(0) == '-') {
            parent.addSeparator();
            if (label.length() == 1)    // S�lo el separador
            {
                return null;
            }
            label = label.substring(1);
        }

        // Si el label contiene '&' y no es el ultimo caracter
        // el siguiente caracter es el mnemonico
        final int indice = label.indexOf('&');
        if (indice > -1 && indice < label.length() - 1) {
            final char mnemo = label.charAt(indice + 1);
            label = label.substring(0, indice) + label.substring(indice + 1);
            mi.setMnemonic(mnemo);
        }

        mi.setText(label);
        setIcon(mi, icon);

//		si el label contiene '^'
//		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));

        parent.add(mi);
        if (listener == null) {
            mi.setEnabled(false);
        } else {
            mi.addActionListener(listener);
        }

        return mi;
    }

    /**
     * Metodo de utilidad para agregrar items a un JPopupMenu sin asignar icono
     *
     * @param listener El ActionListener que procesa las acciones del JMenuItem. Puede ser <code> null </code>
     * @param parent   El JPopupMenu donde se va a agregar
     * @param label    La etiqueta del JMenuItem. Si comienza por '-' se agrega
     *                 un separador antes del item. Si solo es "-", solo se
     *                 agrega el separador. Si la etiqueta contiene '&', el
     *                 siguiente caracter es asignado como Mnemonico
	 *
     * @return El JMenuItem creado o <code> null </code> si solo se agrego un separador
     * --author El Coyote Cojo
     * --version 2002.feb.01 13:05:01, CET
     */
    public static JMenuItem addMenuItem(final ActionListener listener,
                                        final JPopupMenu parent, final String label) {
        return addMenuItem(listener, parent, label, null);
    }

    /**
     * Crea un boton y lo agrega a un container.
     * El foreground es azul por omision.
     * Sin tool tip
     *
     * @param listener Quien maneja los eventos del boton
     * @param parent   El container
     * @param caption  El texto del boton
	 *
     * @return Una referencia al boton recien creado
     */
    public static JButton addButton(final ActionListener listener,
                                    final Container parent,
                                    final String caption) {
        return addButton(listener, parent, caption, Color.blue);
    }

    /**
     * Crea un boton y lo agrega a un container.
     * El foreground es azul por omision.
     *
     * @param listener Quien maneja los eventos del boton
     * @param parent   El container
     * @param caption  El texto del boton
     * @param toolTip  El tooltip a mostrar
	 *
     * @return Una referencia al boton recien creado
     */
    public static JButton addButton(final ActionListener listener,
                                    final Container parent,
                                    final String caption, final String toolTip) {
        return addButton(listener, parent, caption, Color.blue, toolTip);
    }

    /**
     * Crea un boton y lo agrega a un container
     * Sin tool tip
     *
     * @param listener Quien maneja los eventos del boton
     * @param parent   El container
     * @param caption  El texto del boton
     * @param color    El foreground del boton
	 *
     * @return Una referencia al boton recien creado
     */
    private static JButton addButton(final ActionListener listener,
                                     final Container parent,
                                     final String caption, final Color color) {
        return addButton(listener, parent, caption, color, caption);
    }

    /**
     * Metodo de utilidad para agregrar botones a un contenedor
     *
     * @param listener Quien maneja los eventos del boton
     * @param parent   El container
     * @param caption  El texto del boton
     * @param color    El foreground del boton
     * @param toolTip  El tooltip a mostrar
	 *
     * @return Una referencia al boton recien creado
     */
    public static JButton addButton(final ActionListener listener,
                                    final Container parent,
                                    final String caption, final Color color,
                                    final String toolTip) {
        final ImageIcon stdIcon = createIcon("images/romb-cyan.gif");
        final ImageIcon rollIcon = createIcon("images/romb-violet.gif");
        final ImageIcon pressIcon = createIcon("images/romb-red.gif");
        final JButton button = new JButton(caption, stdIcon);
        button.setRolloverIcon(rollIcon);
        button.setPressedIcon(pressIcon);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setForeground(color);
        button.setToolTipText(toolTip);
        UIManager.put("ToolTip.background", Color.black);
        UIManager.put("ToolTip.foreground", Color.yellow);
        UIManager.getDefaults().put("ToolTip.font", toolTipFont);
        ToolTipManager.sharedInstance().setInitialDelay(100);

        if (parent != null) {
            parent.add(button);
        }
        if (listener == null) {
            button.setEnabled(false);
        } else {
            button.addActionListener(listener);
        }
        return button;
    }

    /**
     * Un icono representado por iconName o null si no existe.
     *
     * @return Un icono representado por iconName o null si no existe.
     */
    private static ImageIcon createIcon(final String iconName) {
        final URL url = ClassLoader.getSystemResource(iconName);
        return url == null ? null : new ImageIcon(url);
    }

    /**
     * Determina cual es la ventana principal que contiene
     * al susodicho componente
     *
     * @param comp el componente cuya ventana principal queremos
	 *
     * @return La ventana principal qu contiene a 'comp'
     */
    public static Window getFirstParent(Component comp) {
        Component parent = comp.getParent();
        while (parent != null) {
            comp = parent;
            parent = comp.getParent();
        }
        return (Window) comp;
    }

    /**
     * Finaliza una aplicacion, proporcionando una ventana de confirmacion
     *
     * @param parent La ventana padre del dialogo de confirmacion
     */
    public static void exitApplication(final Window parent) {
        exitApplication(parent, true);
    }

    /**
     * Finaliza una aplicacion, proporcionando una ventana de confirmacion
     *
     * @param parent    La ventana padre del dialogo de confirmacion
     * @param askBefore Si es true, pregunta antes de cerrar la aplicacion
     */
    public static void exitApplication(final Window parent,
                                       final boolean askBefore) {
        int choice = 0;

        if (askBefore) {
            final Object[] options = {"OK", "Cancelar"};
            final String msg = "Pulse OK para cerrar la aplicaci�n";
            final String title = "Atenci�n";
            choice = JOptionPane.showOptionDialog(parent, msg, title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        }

        if (choice == 0) {
            parent.setVisible(false);
            parent.dispose();
            System.exit(0);
        }
    }

    /**
     * Muestra un di�logo de mensaje con diferentes opciones. La primera opci�n
     * es la seleccionada por defecto.
     *
     * @param msg     El mensaje a mostrar.
     * @param title   El t�tulo del di�logo.
     * @param options Un array con las opciones a mostrar.
	 *
     * @return El �ndice de la opci�n seleccionada.
     */
    public static int showMessage(final String msg, final String title,
                                  final Object[] options) {
        return JOptionPane.showOptionDialog(null, msg, title,
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
            options, options[0]);
    }

    /**
     * Sets the same maximum, minimum and preferred dimensions to a set of components.
     *
     * @param components the components whose preferred dimensions are to be set.
     */
    public static void setEqualPreferredDimensions(final Component... components) {
        final Dimension commonSize = new Dimension();
        for (final Component component : components) {
            final Dimension preferredSize = component.getPreferredSize();
            if (commonSize.height < preferredSize.height) {
                commonSize.height = preferredSize.height;
            }
            if (commonSize.width < preferredSize.width) {
                commonSize.width = preferredSize.width;
            }
        }
        for (final Component component : components) {
            component.setMinimumSize(commonSize);
            component.setPreferredSize(commonSize);
            component.setMaximumSize(commonSize);
        }
    }

//    /**
//     * Sets the same maximum, minimum and preferred dimensions to a set of components.
//     *
//     * @param components the components whose preferred dimensions are to be set.
//     */
//    public static void setEqualPreferredDimensions(final Region... components) {
//        double commonHeight = Double.MIN_VALUE;
//        double commonWidth = Double.MIN_VALUE;
//
//        for (final Region component : components) {
//            final double preferredHeight = component.getPrefHeight();
//            final double preferredWidth = component.getPrefWidth();
//            if (commonHeight < preferredHeight) {
//                commonHeight = preferredHeight;
//            }
//            if (commonWidth < preferredWidth) {
//                commonWidth = preferredWidth;
//            }
//        }
//        for (final Region component : components) {
//            component.setMinHeight(commonHeight);
//            component.setMinWidth(commonWidth);
//            component.setPrefHeight(commonHeight);
//            component.setPrefWidth(commonWidth);
//            component.setMaxHeight(commonHeight);
//            component.setMaxWidth(commonWidth);
//        }
//    }

    /**
     * Formatea un numero seg�n una mascara.
     *
     * @param originalNumber guess.
	 * @param mask guess.
     * @return guess.
     */
    public static String formatNumberWithMask(final String originalNumber, final String mask) {
        if (originalNumber == null || mask == null) {
            return originalNumber;
        }
        final String number = originalNumber.replaceAll("\\D", "");
        final StringBuilder builder = new StringBuilder(mask);
        final int numberLength = number.length();
        int buiderPos = builder.length() - 1;
        if (numberLength > 0) {
            for (int ii = numberLength - 1; ii >= 0; ii--) {
                if (buiderPos >= 0) {
                    while (mask.charAt(buiderPos) != '#') {
                        buiderPos--;
                    }
                    builder.setCharAt(buiderPos--, number.charAt(ii));
                } else {
                    builder.insert(0, number.charAt(ii));
                }
            }
        }

        // FIXME:  && builder.charAt(0) != '(' est� puesto s�lo para que pase el test (y los casos correspondientes. Buscar una soluci�n de verd�
        while (builder.length() > 0 && !Character.isLetterOrDigit(builder.charAt(0)) && builder.charAt(0) != '(') {
            builder.deleteCharAt(0);
        }

        return builder.toString();
    }

    /**
     * TIP: Make the JOptionPane resizable using the HierarchyListener
     * https://blogs.oracle.com/scblog/entry/tip_making_joptionpane_dialog_resizable
     * The tip works if you are using any java.awt.Component subclass as
     * the message in javax.swing.JOptionPane. The tip works by listening
     * to the hierarchy event on the message component, finding the
     * java.awt.Dialog component and setting it's resizable property to true.
     *
     * @param component in the Dialog that we want to make resizable.
     */
    public static void setDialogResizable(final Component component) {
        component.addHierarchyListener(event -> {
            final Window window = SwingUtilities.getWindowAncestor(component);
            if (window instanceof Dialog) {
                final Dialog dialog = (Dialog) window;
                if (!dialog.isResizable()) {
                    dialog.setResizable(true);
                }
            }
        });
    }

    /**
     * Requests the focus for a component in a Dialog.
     * @param component the component for which the focus is desired.
     * @since 2017.06.19
     */
    public static void requestFocusInDialog(final Component component) {
        component.addHierarchyListener(event -> {
            final Window window = SwingUtilities.getWindowAncestor(component);
            if (window instanceof Dialog) {
                window.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentShown(final ComponentEvent e) {
                        component.requestFocusInWindow();
                    }
                });
            }
        });
    }

	/**
	 * Set the component follower to "follow" the position of followed in the screen, that is,
	 * it's origin coordinates will be those of followed when it is moved.
	 */
	public static void setComponentToFollow(final Window follower, final JComponent followed) {
		followed.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(final ComponentEvent e) {
				if (followed.isShowing()) {
                    final Component component = e.getComponent();
                    final int x = component.getX();
                    final int y = component.getY();
					follower.setLocation(x, y);
				}
			}
		});
	}

    public static class WindowInfo {
        final Window window;
        final String windowName;
        final boolean visible;

        public WindowInfo(final Window window, final String windowName, final boolean visible) {
            this.window = window;
            this.windowName = windowName;
            this.visible = visible;
        }
    }

    /**
     * Shows the window adjusting to last saved bounds and save these bounds on exit.
     */
    public static void fancyShowWindow(final Properties properties, final String customPropertiesBaseName, final WindowInfo ... windowInfos) {
        for (final WindowInfo windowInfo : windowInfos) {
            final String windowName = windowInfo.windowName;
            final BoundsInfo applicationBounds = PropertiesHelper.getBounds(properties, windowName);
            final Window window = windowInfo.window;
            setBounds(window, applicationBounds);
            window.invalidate();
            window.setVisible(windowInfo.visible);
        }
        // Antes de salir de la aplicacion salvamos las propiedades persistentes
        RuntimeControl.registerShutdownHook(() -> {
            try {
                final Properties customProperties = new Properties();
                PropertiesHelper.loadCustomProperties(customProperties, customPropertiesBaseName);
                for (final WindowInfo windowInfo : windowInfos) {
                    final Window window = windowInfo.window;
                    final String windowName = windowInfo.windowName;
                    final BoundsInfo componentBounds = new BoundsInfo();
                    final int extendedState = window instanceof Frame ? ((Frame) window).getExtendedState() : -1;
                    if ((extendedState & ICONIFIED) == 0 && (extendedState & MAXIMIZED_BOTH) == 0) {
                        componentBounds.setBounds(window.getBounds());
                    }
                    componentBounds.setMinimized(extendedState == ICONIFIED);
                    componentBounds.setMaximized(extendedState == MAXIMIZED_BOTH);
                    PropertiesHelper.saveBounds(componentBounds, customProperties, windowName);
                }
                PropertiesHelper.saveCustomProperties(customProperties, customPropertiesBaseName);
            } catch (final Throwable throwable) {
                LOGGER.error(LoggerFactory.ERROR_TAG, throwable);
            }
        });
    }

    public static void setBounds(final Window window, final BoundsInfo applicationBounds) {
        if (window instanceof Frame) {
            final Frame frame = (Frame) window;
            if (applicationBounds.isMinimized()) {
                frame.setExtendedState(ICONIFIED);
            } else if (applicationBounds.isMaximized()) {
                frame.setExtendedState(MAXIMIZED_BOTH);
            }
        }
        final Rectangle bounds = applicationBounds.getBounds();
        if (bounds == null || bounds.equals(NULL_RECTANGLE)) {
            final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            window.setSize(dim.width, dim.height * 9 / 10);
        } else {
            window.setBounds(bounds);
        }
    }

    public static void executeWithWaitCursor(final Component component, final Command command) {
        final Cursor oldCursor = component.getCursor();
        component.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            command.execute();
        } finally {
            component.setCursor(oldCursor);
        }
    }

    private static class XPopupMenu extends JPopupMenu {
        private static final long serialVersionUID = 7283686399923057310L;
        private Point popupMenuPoint;

        Point getPopupMenuPoint() {
            return popupMenuPoint;
        }

        void setPopupMenuPoint(final Point popupMenuPoint) {
            this.popupMenuPoint = popupMenuPoint;
        }
    }

    /**
     * Clase para controlar las columnas en un PopUp menu.
     */
    public static class ColumnControl {

        /**
         * la cantidad de columnas de la tabla asociada.
         */
        private final int columnCount;

        @FunctionalInterface
        interface ColumnControlListener {
            void columnControlChanged();
        }

        private final Collection<ColumnControlListener> listeners =
            new ArrayList<>();

		/** Conjunto de las columnas ocultas. */
        private final Map<Object, TableColumn> columnMap = new HashMap<>();
        private final JTable table;

        private ColumnControl(final JTable table) {
            this.table = table;
            columnCount = table.getColumnModel().getColumnCount();
        }

        public static ColumnControl getInstance(final JTable dataTable) {
            return new ColumnControl(dataTable);
        }

        void addColumnControlListener(final ColumnControlListener listener) {
            listeners.add(listener);
        }

        public void removeColumnControlListener(final ColumnControlListener listener) {
            listeners.remove(listener);
        }

        private void notifyListeners() {
            for (final ColumnControlListener listener : listeners) {
                listener.columnControlChanged();
            }
        }

        public void hide(final TableColumn column) {
            final TableColumnModel tcm = table.getColumnModel();
            tcm.removeColumn(column);
            columnMap.put(column.getIdentifier(), column);
            notifyListeners();
        }

        public TableColumn show(final TableColumn column) {
            return show(column.getIdentifier());
        }

        public void show(final TableColumn column, final int pos) {
            final Object identifier = column.getIdentifier();
            show(identifier);
            final TableColumnModel tcm = table.getColumnModel();
            tcm.moveColumn(tcm.getColumnIndex(identifier), pos);
        }

        public TableColumn show(final Object selectedValue) {
            final TableColumnModel tcm = table.getColumnModel();
            final TableColumn column = columnMap.remove(selectedValue);
            if (column != null) {
                // Agregarla al final
                tcm.addColumn(column);
                notifyListeners();
            }
            return column;
        }

        public Object[] keys() {
            return columnMap.keySet().toArray();
        }

        public int shownCount() {
            return columnCount - columnMap.size();
        }

        public boolean isEmpty() {
            return columnMap.isEmpty();
        }
    }

    /**
     * Gesti�n de la ocultaci�n de las columnas de una tabla.
     *
	 * @param parent parent Component para los di�logos.
	 * @param dataTable la tabla a la que se le agregar� el men�.
     * @param i18nProperties debe contener las etiquetas a mostrar:
	 *     GUITools.OcultarColumna, GUITools.MostrarColumna y GUITools.Escojer
     */
    public static ColumnControl addColumnControlMenu(final Component parent,
                                                     final JTable dataTable, final Properties i18nProperties) {
        final ColumnControl columnControl = ColumnControl.getInstance(dataTable);
        final XPopupMenu popupMenu = new XPopupMenu();
        final JMenuItem ocultarMenuItem = addMenuItem(null,
            popupMenu, PropertiesHelper.getStringFromProperties(i18nProperties,
                "GUITools.OcultarColumna"));
        final JMenuItem mostrarMenuItem = addMenuItem(null,
            popupMenu, PropertiesHelper.getStringFromProperties(i18nProperties,
                "GUITools.MostrarColumna"));
        addMenuItem(null, popupMenu, "-");
        final JMenuItem unsortMenuItem = addMenuItem(null,
            popupMenu, PropertiesHelper.getStringFromProperties(i18nProperties,
                "GUITools.Unsort"));

        /* El punto donde se muestra el menu. */
        final JTableHeader tableHeader = dataTable.getTableHeader();
        tableHeader.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent ev) {
                    if (SwingUtilities.isRightMouseButton(ev)) {
                        final Point popupMenuPoint = ev.getPoint();
                        popupMenu.setPopupMenuPoint(popupMenuPoint);
                        popupMenu.show(tableHeader, popupMenuPoint.x,
                            popupMenuPoint.y);
                    }
                }
            }
        );

        final ActionListener listener = actionEvent -> {
            final Point location = popupMenu.getPopupMenuPoint();
            if (actionEvent.getSource() == ocultarMenuItem) {
                final TableColumnModel tcm = dataTable.getColumnModel();
                final TableColumn col = tcm
                    .getColumn(dataTable.columnAtPoint(location));
                columnControl.hide(col);
            } else if (actionEvent.getSource() == mostrarMenuItem) {
                final Object[] possibleValues = columnControl.keys();
                final Object selectedValue = JOptionPane
                    .showInputDialog(parent,
                        PropertiesHelper.getStringFromProperties(i18nProperties,
                            "GUITools.Escojer"),
                        PropertiesHelper.getStringFromProperties(i18nProperties,
                            "GUITools.MostrarColumna"),
                        JOptionPane.INFORMATION_MESSAGE, null,
                        possibleValues, possibleValues[0]);
                if (selectedValue == null) {
                    return;
                }
                final TableColumnModel tcm = dataTable.getColumnModel();
                final int index = dataTable.columnAtPoint(location);
                final TableColumn newCol = columnControl.show(selectedValue);
                // Moverla a la posicion seleccionada
                if (index > -1 && newCol != null) {
                    tcm.moveColumn(
                        tcm.getColumnIndex(newCol.getIdentifier()), index);
                }
            } else if (actionEvent.getSource() == unsortMenuItem) {
                dataTable.getRowSorter().setSortKeys(null);
            }
        };
        ocultarMenuItem.addActionListener(listener);
        mostrarMenuItem.addActionListener(listener);
        ocultarMenuItem.setEnabled(true);
        unsortMenuItem.addActionListener(listener);
        columnControl.addColumnControlListener(
            () -> mostrarMenuItem.setEnabled(!columnControl.isEmpty()));
        columnControl.addColumnControlListener(
            () -> ocultarMenuItem.setEnabled(columnControl.shownCount() > 1));
        dataTable.getRowSorter().addRowSorterListener(
            event -> unsortMenuItem.setEnabled(!event.getSource().getSortKeys().isEmpty()));
        return columnControl;
    }

    public static void vibrate(final Window window) {
        Point locationOnScreen = window.getLocationOnScreen();
        final int originalX = locationOnScreen.x;
        final int originalY = locationOnScreen.y;

        try {
            for (int i = 0; i < 20; i++) {
                Thread.sleep(10);
                window.setLocation(locationOnScreen.x, locationOnScreen.y + 10);
                Thread.sleep(10);
                locationOnScreen = window.getLocationOnScreen();
                window.setLocation(locationOnScreen.x, locationOnScreen.y - 10);
                Thread.sleep(10);
                locationOnScreen = window.getLocationOnScreen();
                window.setLocation(locationOnScreen.x - 10, locationOnScreen.y);
                Thread.sleep(10);
                window.setLocation(originalX, originalY);
            }
        } catch (final Exception err) {
            LOGGER.error("", err);
        }
    }

    /**
     * Saves the properties of a JTable in a Properties object.
     */
    public static void saveTableProperties(final JTable table, final Properties properties) {
        final TableColumnModel columnModel = table.getColumnModel();
        // OJO, esto puede dar algo distinto a super.getColumnCount();
        // puesto que considera todas las columnas, independientemente de
        // que est�n visibles o no.
        final int columnCount = table.getModel().getColumnCount();
        for (int ii = 0; ii < columnCount; ii++) {
            final int columnIndex = table.convertColumnIndexToView(ii);
            final String currentCol = "Col" + ii;
            if (columnIndex >= 0) {
                final TableColumn column = columnModel.getColumn(columnIndex);
                properties.setProperty(currentCol + "Pos", String.valueOf(columnIndex));
                properties.setProperty(currentCol + "Width", String.valueOf(column.getWidth()));
                properties.setProperty(currentCol + "Visible", "true");
            } else {
                properties.setProperty(currentCol + "Visible", "false");
                properties.remove(currentCol + "Pos");
                properties.remove(currentCol + "Width");
            }
        }
    }

    /**
     * Sets up the columns of a table, given a Properties and a ColumnControl.
     * @see GUITools#saveTableProperties
     */
    public static void setUpColumns(final JTable table, final Properties properties, final ColumnControl columnControl) {
        final TableColumnModel columnModel = table.getColumnModel();

        final int columnCount = table.getModel().getColumnCount();
        for (int ii = 0; ii < columnCount; ii++) {
            final boolean visible = Boolean.valueOf(properties
                .getProperty("Col" + ii + "Visible", "true")).booleanValue();
            final int columnIndex = table.convertColumnIndexToView(ii);
            if (columnIndex < 0) {
                continue;
            }
            if (visible) {
                final int pos = Integer.parseInt(
                    properties.getProperty("Col" + ii + "Pos", String.valueOf(columnIndex)));
                final int width = Integer.parseInt(properties
                    .getProperty("Col" + ii + "Width", "100"));
                final TableColumn column = columnModel.getColumn(columnIndex);
                column.setPreferredWidth(width);
                columnModel.moveColumn(columnIndex, pos);
            } else {
                final TableColumn column = columnModel.getColumn(columnIndex);
                columnControl.hide(column);
            }
        }
    }
}
