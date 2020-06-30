package org.pclg.filesystem;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.File;

/**
 * org.pclg.filesystem.CopyTreeStruct.java
 * Copia la estructura de directorios de un sitio a otro
 * 20000824
 */
public class CopyTreeStruct {
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final String NO_ES_UN_DIRECTORIO = "[%s] NO es un directorio";
    private static final String NO_EXISTE_Y_NO_PUDE_CREARLO = "[%s] no existe y no pude crearlo.";

    private CopyTreeStruct(final File from, final File to) {
        if (!from.isDirectory()) {
            LOGGER.error(String.format(NO_ES_UN_DIRECTORIO, from.getAbsolutePath()));
            System.exit(-1);
        }
        if (to.exists()) {
            if (!to.isDirectory()) {
                LOGGER.error(String.format(NO_ES_UN_DIRECTORIO, to.getAbsolutePath()));
                System.exit(-2);
            }
        } else if (!to.mkdir()) {
            LOGGER.error(String.format(NO_EXISTE_Y_NO_PUDE_CREARLO, to.getAbsolutePath()));
            System.exit(-3);
        }
        copyDirs(from, to);
    }

    /**
     * Copia directorios recursivamente
     */
    private void copyDirs(final File from, final File to) {
        final File[] files = from.listFiles(File::isDirectory);
        if (files != null) {
            for (final File file : files) {
                final File target = new File(to, file.getName());
                LOGGER.log(Level.OFF, "Creando " + target.getAbsolutePath());
                if (target.isDirectory() || target.mkdir()) {
                    copyDirs(file, target);
                } else {
                    LOGGER.error(String.format(NO_EXISTE_Y_NO_PUDE_CREARLO, target.getAbsolutePath()));
                }
            }
        }
    }

    public static void main(final String[] args) {
        final String src;
        final String target;
        switch (args.length) {
            case 1:
                src = ".";
                target = args[0];
                break;
            case 2:
                src = args[0];
                target = args[1];
                break;
            default:
//			src = "C:\\home\\development\\projects\\Alles\\pclg-template-tree";
//			target = "C:\\home\\development\\projects\\Alles\\pclg-condominio";
//			target = "C:\\home\\development\\projects\\Alles\\pclg-compdel";
//			target = "C:\\home\\development\\projects\\Alles\\pclg-mp3updater";
//			target = "C:\\home\\development\\projects\\Alles\\pclg-new-module";
                usage();
                return;
        }
        new CopyTreeStruct(new File(src), new File(target));
    }

    private static void usage() {
        LOGGER.warn("uso: org.pclg.filesystem.CopyTreeStruct [<src>] <target>");
        System.exit(-1);
    }
}
