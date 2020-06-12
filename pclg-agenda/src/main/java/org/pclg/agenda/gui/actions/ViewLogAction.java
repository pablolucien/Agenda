package org.pclg.agenda.gui.actions;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.pclg.agenda.gui.AgendaGUI;
import org.pclg.gui.JTabbedPaneWithCloseIcons;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @since 15/03/2018.
 */
public class ViewLogAction extends AbstractAction {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final AgendaGUI agendaGUI;
    private final JTabbedPaneWithCloseIcons tabbedPane;

    public ViewLogAction(AgendaGUI agendaGUI, final JTabbedPaneWithCloseIcons tabbedPane) {
        this.agendaGUI = agendaGUI;
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void actionPerformed(final ActionEvent ev) {
        final JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        final Enumeration allAppenders = Logger.getRootLogger().getAllAppenders();
        final StringBuilder fileNames = new StringBuilder();
        while (allAppenders.hasMoreElements()){
            final Appender appender = (Appender) allAppenders.nextElement();
            if (appender instanceof FileAppender){
                final String fileName = ((FileAppender) appender).getFile();
                fileNames.append(fileName).append(", ");
                textArea.append("File: " + fileName + '\n');
                try {
                    textArea.append(new String(FileTools.readFromFile(fileName)) + '\n');
                } catch (IOException ex) {
                    LOGGER.error(LoggerFactory.ERROR_TAG, ex);
                    textArea.append(ex.toString() + '\n');
                }
                textArea.append("=================================================\n");
            }
		}

		class ClosableJScrollPane extends JScrollPane implements ActionListener {
			private ClosableJScrollPane(Component textArea) {
                super(textArea);
            }

            /** Action a ejecutar por el aspa del TabPanel. */
            @Override
            public void actionPerformed(final ActionEvent e) {
                tabbedPane.remove(this);
            }
        }

        agendaGUI.add2TabbedPane(fileNames.toString(), new ClosableJScrollPane(textArea));

//		textArea.append("=======================   ClassPath     ==========================\n");
//		final URL[] classPath = ClassPathHacker.getClassPath();
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		final PrintStream printStream = new PrintStream(baos);
//		ArrayTools.printArray(printStream, classPath);
//		textArea.append(baos.toString());
//		for (URL url : classPath) {
//			final File path = new File(url.getPath());
//			final File path = getExecutionPath();
//			final File path = new File("C:\\home\\development\\build");
//			System.err.println(">> Plugins disponibles en " + path + " <<");
//	  		final Class<?>[] clases = ToolBox.findClasses(Plugin.class, path);
//			for (Class<?> clase : clases) {
//				System.err.println("clase = " + clase);
//			}
//
//		}

/*
		// El problema con este approach es que instancia todas las clases que haya por el camino. FIXME: Buscar otra solución
        final File path = getExecutionPath(Agenda.class);
        System.err.println(">> Plugins disponibles en " + path + " <<");
        final Class[] clases = ToolBox.findClasses(Plugin.class, path);
        for (Class clase : clases) {
            System.err.println("clase = " + clase);
        }

 */
	}


	/**
	 * Determina de donde se está ejecutando la aplicacion: un directorio o un jar
	 */
//	private File getExecutionPath(final Class myClass) {
//		String myName = myClass.getName();
//		myName = myName.substring(myName.lastIndexOf('.') + 1);
//
//		final URL appURL = myClass.getResource(myName + ".class");
//		if (appURL == null) {
//			System.err.println("No puedo determinar el punto de ejecucion");
//			return null;
//		}
//
//		// Determinar que jar o directorio se está ejecutando
//		final String path = appURL.getPath();
//		int end = path
//			.lastIndexOf('!');        // Si es un jar, termina con ! y el nombre de la clase
//		if (end == -1) {
//			end = path.lastIndexOf(
//				'/');        // Si es un directorio, termina con / y el nombre de la clase
//		}
//		final String pathStr = path.substring(path.indexOf('/') + 1, end);
//		final String aPackage = myClass.getPackage().getName().replace('.', '/');
//		final int indexOf = pathStr.indexOf(aPackage);
//		final String baseDir = pathStr.substring(0, indexOf);
//		return new File(baseDir);
//	}
}
