package org.pclg.tools.productivity.condominio;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.GUITools;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Sides;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 4/11/17 11:25
 */
public final class Recibos extends JFrame {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final Font PREVIEW_FONT = new Font("Monospaced", Font.PLAIN, 18);
    private static final Font PRINTER_FONT = new Font("Monospaced", Font.PLAIN, 8);
    private static final String[] lines = {
        "COMUNIDAD DE PROPIETARIOS",
        "N�m: %s",
        "Domicilio: Calle Encajeras 7, 28037, Madrid.",
        "He recibido de D. %s",
        "propietario del piso %s",
        "la cantidad de %s",
        "como tanto alzado mensual y a posterior liquidaci�n, correspondiente al mes de la fecha.",
        "Madrid, a %s de %s de %s",
        "Importe %s",
        "I.V.A. %% %s",
        "Total %s",
        "El pago del presente recibo no exime de la responsabilidad de pagar los anteriores pendientes de pago.",
        "Fdo.:"
    };
    private static final long serialVersionUID = 7040707892533995024L;

    public Recibos() throws HeadlessException {
        super("�Condominio de encajeras!");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JTextArea taPrintPreview = new JTextArea();
        taPrintPreview.setFont(PREVIEW_FONT);
        JButton okButton = new JButton("Print");
        add(taPrintPreview, BorderLayout.CENTER);
        add(okButton, BorderLayout.SOUTH);
        setUpAction(taPrintPreview, okButton);
        setSize(800, 600);
        Data data = new Data();
        addLine(taPrintPreview, lines[0]);
        addLine(taPrintPreview, String.format(lines[1], data.empfangNummer));
        addLine(taPrintPreview, lines[2]);
        addLine(taPrintPreview, String.format(lines[3], data.nachbarnName));
        addLine(taPrintPreview, String.format(lines[4], data.wohnung));
        addLine(taPrintPreview, String.format(lines[5], data.betrag));
        addLine(taPrintPreview, lines[6]);
        Calendar date = Calendar.getInstance();
        addLine(taPrintPreview, String.format(lines[7], date.get(Calendar.DAY_OF_MONTH), date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), date.get(Calendar.YEAR)));
//        addLine(taPrintPreview, String.format("-- %s", date.getDisplayName(Calendar.MONTH, Calendar.NARROW_FORMAT, Locale.getDefault())));
//        addLine(taPrintPreview, String.format("-- %s", date.getDisplayName(Calendar.MONTH, Calendar.NARROW_STANDALONE, Locale.getDefault())));
//        addLine(taPrintPreview, String.format("-- %s", date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())));
//        addLine(taPrintPreview, String.format("-- %s", date.getDisplayName(Calendar.MONTH, Calendar.SHORT_FORMAT, Locale.getDefault())));
//        addLine(taPrintPreview, String.format("-- %s", date.getDisplayName(Calendar.MONTH, Calendar.SHORT_STANDALONE, Locale.getDefault())));
//        addLine(taPrintPreview, String.format("-- %s", date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())));
//        addLine(taPrintPreview, String.format("-- %s", date.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.getDefault())));
//        addLine(taPrintPreview, String.format("-- %s", date.getDisplayName(Calendar.MONTH, Calendar.LONG_STANDALONE, Locale.getDefault())));
        addLine(taPrintPreview, String.format(lines[8], data.betrag));
        addLine(taPrintPreview, String.format(lines[9], ""));
        addLine(taPrintPreview, String.format(lines[10], data.betrag));
        addLine(taPrintPreview, lines[11]);
        addLine(taPrintPreview, lines[12]);
    }

    private void setUpAction(JTextArea taPrintPreview, JButton okButton) {
        okButton.addActionListener(e -> {
            try {
                final PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
                //attributeSet.add(new Copies(3));
                attributeSet.add(Sides.ONE_SIDED);
//                attributeSet.add(new DocumentName("pepito", null));
                attributeSet.add(new JobName("JobName", null));
                taPrintPreview.setFont(PRINTER_FONT);
                taPrintPreview.print(
                    new MessageFormat("header"),
                    new MessageFormat("footer"), true, null,
                    attributeSet, true);
                taPrintPreview.setFont(PREVIEW_FONT);
            } catch (final PrinterException e1) {
                JOptionPane.showMessageDialog(Recibos.this,
                    "print.error.msg" + e1.getMessage(),
                    "error.title",
                    JOptionPane.ERROR_MESSAGE);
                LOGGER.log(Level.ERROR, LoggerFactory.ERROR_TAG, e1);
            }
        });
    }

    public static void main(String[] args) {
        LOGGER.setLevel(Level.ALL);
        final Recibos plotter = new Recibos();
        plotter.setVisible(true);
        GUITools.scrollToCenter(plotter, null);
    }

    private void addLine(JTextArea textArea, String line) {
        textArea.append(line);
        textArea.append("\n");
    }

    private class Data {
        String empfangNummer = "505";
        String nachbarnName = "El Propio";
        String wohnung = "1� der";
        String betrag = "veinte �";
//        String ;
//        String ;
//        String ;
    }
}
