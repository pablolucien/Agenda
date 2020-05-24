/**
 * WordWrap
 * hace word wrap de un archivo de texto
 */

import org.pclg.tools.StringTools;
import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WordWrap {

    public static void main(final String[] args) {
        if (args.length < 2) {
            System.out.println("usage: java WordWrap  [-i[ndent]] <len> <filespec>");
            System.exit(1);
        }

        final int len;

        String indentPad = "";
        int lenArgumentPosition = 0;
        if (args[0].toLowerCase().startsWith("-i")) {
            indentPad = "\t";
            lenArgumentPosition++;
        }

        try {
            len = Integer.parseInt(args[lenArgumentPosition]);
            if (len <= 0) {
                throw new NumberFormatException();
            }
        } catch (final NumberFormatException e) {
            System.out.println("Formato de la longitud incorrecto: debe ser un numero natural");
            System.exit(2);
            return;        //java no sabe de System.exit();
        }

        for (int count = lenArgumentPosition + 1; count < args.length; count++) {
            wordWrap(args[count], len, indentPad);
        }
    }

    private static void wordWrap(String fileName, int len, String indentPad) {
        final File input = new File(fileName);
        final File output = new File(fileName + ".ww.txt");
        try {
            doTheWrapping(input, output, len, indentPad);
            final Path inputPath = input.toPath();
            Files.delete(inputPath);
            Files.move(output.toPath(), inputPath);
        } catch (IOException ex) {
            ToolBox.showInfo(ex);
        }
    }

    private static void doTheWrapping(File input, File output, int len, String indentPad) throws IOException {
        try (final BufferedReader inputFile = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
             final PrintStream outputFile = new PrintStream(new FileOutputStream(output))) {
            String line;
            boolean continuation;
            while ((line = inputFile.readLine()) != null) {
                final String s = StringTools.wrapLine(line, len, indentPad);
                outputFile.print(s);
            }
        }
    }

}

