package org.pclg.runtime;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.*;

/**
 * @author El Coyote Cojo
 * @since 17/08/19 1:59
 */
public final class Executioner {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private final String masterName;
    private final boolean showStderr;
    private final boolean showStdout;
    private Process process;
    private PrintWriter writer;

    public Executioner(final String masterName, final boolean showStdout, final boolean showStderr, final String... cmdarray) throws IOException {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        this.showStdout = showStdout;
        this.showStderr = showStderr;
        this.masterName = masterName;
        try {
            // I know that this is non portable, but this is exactly the point of this class
            // noinspection CallToRuntimeExec
            process = Runtime.getRuntime().exec(cmdarray);
            final OutputStream outputStream = process.getOutputStream();
            writer = new PrintWriter(outputStream);
        } catch (final IOException ex) {
            LOGGER.error(ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                "", JOptionPane.ERROR_MESSAGE);
            throw ex;
        }
        LOGGER.debug(LoggerFactory.EXIT_METHOD);
    }

    /**
     * Accepts input to be forwarded to the subyacent process.
     *
     * @param input the input to be forwarded to the subyacent process.
     */
    public void accept(final String input) {
        writer.println(input);
        writer.flush();
    }

    public void launchAndWait() {
        LOGGER.debug(LoggerFactory.ENTER_METHOD);
        Future<String> futureStdout = null;
        Future<String> futureStderr = null;
        if (showStderr || showStdout) {
            final ExecutorService es = Executors.newFixedThreadPool(2);
            if (showStdout) {
                futureStdout = es.submit(() -> capture(process.getInputStream()));
            }
            if (showStderr) {
                futureStderr = es.submit(() -> capture(process.getErrorStream()));
            }
        }
        try {
            process.waitFor();
            if (futureStderr != null) {
                final String msg = futureStderr.get();
                if (msg.length() > 0) {
                    displayMessage(msg, masterName + ": stderr");
                }
            }
            if (futureStdout != null) {
                final String msg = futureStdout.get();
                if (msg.length() > 0) {
                    displayMessage(msg, masterName + ": stdout");
                }
            }
            LOGGER.debug(LoggerFactory.EXIT_METHOD);
        } catch (final InterruptedException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            Thread.currentThread().interrupt();
        } catch (final ExecutionException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
        }
    }

    private static void displayMessage(final String msg, final String title) {
        LOGGER.error(String.format("[%s] %s", title, msg));
        final JTextArea textArea = new JTextArea(msg);
        textArea.setEditable(false);
//        textArea.setPreferredSize(new Dimension(100, 100));
        final JScrollPane scrollPane = new JScrollPane(textArea);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private String capture(final InputStream stream) {
        final StringBuilder builder = new StringBuilder(1024);
        try (final BufferedReader errReader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = errReader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        } catch (final IOException ex) {
            LOGGER.error(LoggerFactory.ERROR_TAG, ex);
            builder.append("\n\n").append(getClass().getName()).append(ex);
        }
        return builder.toString().trim();
    }
}
