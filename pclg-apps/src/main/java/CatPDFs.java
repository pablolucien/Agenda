import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.pclg.Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;

/**
 * Concatenates all the PDFs contained in one directory to a single PDF.
 * Based on http://www.roseindia.net/java/itext/ConcatenatePdfFiles.shtml
 *
 * @author EL Coyote Cojo
 * @since 2012.11.07
 */
@SuppressWarnings({"HardCodedStringLiteral"})
public class CatPDFs {

    private static final String PDF_EXTENSION = ".pdf";
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    private CatPDFs() {
    }

    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            LOGGER.error("Usage: CatPDFs <target file> {<source dir>|<source files>}");
            System.exit(0);
        }

        File targetFile = new File(args[0].endsWith(PDF_EXTENSION) ? args[0] : args[0] + PDF_EXTENSION);
        if (targetFile.exists()) {
            targetFile = FileTools.getAlternativeFile(targetFile);
            LOGGER.warn(String.format("'%s' already exists. Instead creating '%s'", args[1], targetFile.getName()));
        }

        final File[] sourceFiles = getSourceFiles(args);
        concatPDFs(targetFile, sourceFiles);
    }

    /**
     * @param args the array with the files to concat starting with args[1]. If args.length == 2 then args[1] is assumed
     *             to represent a directory containing the source files; if greater then args[1] to args[n] are the
     *             names of the files.
     * @return the list of files.
     */
    private static File[] getSourceFiles(final String[] args) {
        final File[] files;
        if (args.length == 2) {
            LOGGER.info(String.format("Concatenating files from %s", args[1]));
            files = getFilesFromDir(args[1]);
        } else {
            files = new File[args.length - 1];
            LOGGER.info(String.format("Concatenating %d files", files.length));
            for (int ii = 0; ii < files.length; ii++) {
                files[ii] = new File(args[ii + 1]);
            }
        }
        return files;
    }

    private static File[] getFilesFromDir(final String arg) {
        final File sourceDir = new File(arg);
        final File[] files = sourceDir.listFiles(File::isFile);
        if (files == null || files.length == 0) {
            LOGGER.error(String.format("'%s' does not exist or is empty", sourceDir));
            System.exit(-THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING);
        }
        LOGGER.info(String.format("%d files are to be concatenated", files.length));
        Arrays.sort(files);
        return files;
    }

    private static void concatPDFs(final File targetFile, final File[] sourceFiles) throws IOException, DocumentException {
        try (final FileOutputStream os = new FileOutputStream(targetFile)) {
            final PdfCopyFields copy = new PdfCopyFields(os);
            int count = 0;
            try {
                for (final File file : sourceFiles) {
                    if (!file.isFile()) {
                        LOGGER.error(String.format("'%s' is not a file", file));
                        continue;
                    }
                    LOGGER.info(String.format("Concatenating %s", file));
                    final PdfReader reader = new PdfReader(file.getAbsolutePath());
                    try {
                        copy.addDocument(reader);
                        count++;
                    } catch (final Exception e) {
                        LOGGER.error("\tWith this file: " + file + " I couldn't ", e);
                    } finally {
                        reader.close();
                    }
                }
            } finally {
                copy.close();
            }
            LOGGER.info(String.format("%d files where concatenated", count));
        }
    }
}