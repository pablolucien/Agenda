package org.pclg.root;

import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static org.pclg.tools.StringTools.EMPTY_STRING_ARRAY;


/**
 * Skel Crea el esqueleto de una clase, basado en un modelo en el cual se
 * substituyen las siguientes palabras reservadas:
 * <br>#CLASSNAME#
 * <br>#AUTHOR#
 * <br>#DATE# .
 *
 * @author El Coyote Cojo
 * @version ?????  Version inicial
 */
public final class Skel {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MMM.dd HH:mm:ss, z");
    private static final TimeZone LOCAL_ZONE = TimeZone.getDefault();
    private static final GregorianCalendar LOCAL_CALENDAR = new GregorianCalendar(LOCAL_ZONE);
    private static final String NOW = SDF.format(LOCAL_CALENDAR.getTime());
    private static final String MSG_EXISTS = "Ya existe, �capullo!";
    private static final String PROMPT_SELECT_MODEL = "Seleccione un nro, o introduzca otro modelo -> ";
    private static final String MSG_BYE = "A ver si te decides";
    private static final String MSG_EXIT = "\tE: Salir";
    private static final String MSG_USAGE = "Usage: java Skel <className> [<modelName>]";
    private static final String DEFAULT_USER = "El Coyote Cojo";


    /**
     * --author El Coyote Cojo
     * @since 2002.05.21
     *
     * @param args Los argumentos
     * @throws IOException Si hay problemas con los archivos.
     */
    private Skel(final String[] args) throws IOException {
        final String className = args[0];
        switch (args.length) {
            case 1:
                final String modelName = getModelName();
                create(className, modelName);
                break;
            case 2:
                create(className, args[1]);
                break;
            default:
                usage();
        }
    }

    private final String getModelName() throws IOException {
        final String[] models = readModels();
        for (int ii = 0; ii < models.length; ii++) {
            System.out.println("\t" + ii + ": " + models[ii]);
        }
        System.out.println(MSG_EXIT);
        System.out.println();
        String model = ToolBox.getString(PROMPT_SELECT_MODEL);
        if (model.trim().equalsIgnoreCase("E")) {
            System.out.println(MSG_BYE);
            System.exit(0);
        }
        int option = -1;
        try {
            option = Integer.parseInt(model.trim());
        }
        catch (final NumberFormatException ex) {
        }
        if (option != -1) {
            model = models[option];
        }
        return model;
    }

    /**
     * Crea el archivo segun un modelo --author El Coyote Cojo
     * @since
     * 2001.dic.31 12:58:41, CET
     *
     * @param className El nombre de la clase a crear
     * @param modelName El nombre del modelo a usar
     */
    private void create(final String className, final String modelName) {
        try {
            final File file = new File(className + ".java");
            if (file.exists()) {
                System.out.println(MSG_EXISTS);
            } else {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(getClass().getClassLoader()
                                .getResourceAsStream(modelName)));
                final PrintStream ps = new PrintStream(new FileOutputStream(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = parseLine(line, className);
                    ps.println(line);
                }
                ps.close();
                reader.close();
            }

            Runtime.getRuntime().exec("vi " + file);
        }
        catch (final IOException ex) {
            ToolBox.showInfo(ex);
        }
    }

    /**
     * Substituye en una linea todas las ocurrencias de una palabra reservada
     * por su valor.
     * --author El Coyote Cojo
     * @since 2001.dic.31 12:58:41, CET
     * @param line la linea a parsear.
     * @param className el nombre de la clase.
     * @return la l�nea con las substituciones.
     */
    private String parseLine(String line, final String className) {
        int index;

        while ((index = line.indexOf("#CLASSNAME#")) > -1) {
            line = ToolBox.replace(line, index, "#CLASSNAME#".length(), className);
        }

        while ((index = line.indexOf("#AUTHOR#")) > -1) {
            line = ToolBox.replace(line, index, "#AUTHOR#".length(), DEFAULT_USER);
        }

        while ((index = line.indexOf("#DATE#")) > -1) {
            line = ToolBox.replace(line, index, "#DATE#".length(), NOW);
        }

        return (line);
    }

    /**
     * Ayuda al usuario
     * --author El Coyote Cojo
     * @since 2001.dic.31 12:58:41,
     * CET
     */
    private static void usage() {
        System.out.println(MSG_USAGE);
        System.exit(1);
    }

    /**
     * Lee los nombres de los modelos de el archivo de configuracion
     * --author El Coyote Cojo
     * @since 2002.05.21
     *
     * @return Un String[] con los nombres
     */
    private String[] readModels() {
        final List<String> models = new ArrayList<String>();
        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getClass().getClassLoader().getResourceAsStream(
                            getClass().getSimpleName() + ".properties")));
            String line;
            while ((line = reader.readLine()) != null) {
                models.add(line);
            }
            reader.close();
        }
        catch (final IOException ex) {
            ToolBox.showInfo(ex);
        }
        return models.toArray(EMPTY_STRING_ARRAY);
    }

    public static void main(final String[] args) throws IOException {
        new Skel(args);
    }
} 
