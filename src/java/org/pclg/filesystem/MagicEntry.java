package org.pclg.filesystem;

import org.pclg.tools.Constants;
import org.pclg.tools.ToolBox;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Encapsula la información necesaria para determinar de que tipo(s) es un
 * archivo Representa la información de una entrada en el archivo Magic.magic
 *
 * @version 2003.jun.28 Modificado para que sea compatible con el archivo magic
 *          de file(1).
 * @since 2003.16.20
 */
final class MagicEntry {
	//Estos 3 son inutiles con la compatibilidad con file(1). */
	/** Indica que la busqueda de la magia es desde el principio del archivo. */
	private static final int FROM_START = 0;

	/** Indica que la busqueda de la magia es desde el final del archivo. */
	private static final int FROM_END = 1;

	/** Base hexadecimal. */
	public static final int HEX_RADIX = 16;

	/** Base octal. */
	public static final int OCTAL_RADIX = 8;

	/** La 'firma' que hay que buscar en el archivo. */
	private final byte[] magicBytes;

	/** Donde buscamos la 'firma': ¿desde el principio, desde el final, desde un punto intermedio? */
	private final int whereToLook;

	/** Distancia desde el principio o final del archivo, desde donde comenzamos la busqueda de la 'firma'. */
	private final long offset;

	/** La descripción del tipo del archivo desde el punto de vista de los humanos y demas seres inteligentes. */
	private final String magicType;
	
	/** (temporal) Indica si la busqueda de la magia es desde el final del archivo. */
	private final boolean fromEnd;

    /** La descripción del tipo del archivo desde el punto de vista de los humanos y demas seres inteligentes. */
   	private String msg;

	/** Entries que dan un poco más de detalle sobre el archivo. */
	private List<MagicEntry> children;

	/** Entries que representan otras condiciones que debe cumplir el archivo. */
	private List<MagicEntry> complementConditions;

    /**
     * Lo construye con una linea que tiene todos los datos.
     */
    MagicEntry(final String line) {
        final StringTokenizer st = new StringTokenizer(line);
        final String startingPoint = st.nextToken();
        offset = Long.parseLong(st.nextToken());
        final String criterium = st.nextToken();
        final String bytes = st.nextToken();

        final int startFrom;
        if (startingPoint.equalsIgnoreCase("START")) {
            startFrom = FROM_START;
        } else if (startingPoint.equalsIgnoreCase("END")) {
            startFrom = FROM_END;
        } else {
            throw new IllegalArgumentException(startingPoint);
        }


        // Leemos hasta el final, descartando los comentarios
        final StringBuilder msgBuffer = new StringBuilder(Constants.BUFFER_SIZE);
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (token.startsWith("#")) {
                break;
            }
            msgBuffer.append(' ').append(token);
        }
        magicBytes = stringToByteArray(criterium, bytes);
        whereToLook = startFrom;
        fromEnd = whereToLook != FROM_START;
        magicType = msgBuffer.toString().trim();
    }

    void addContinuation(final String line) {
    	if (children == null) {
    		children = new ArrayList<>();
    	}
    	children.add(new MagicEntry(line.substring(1)));
    }

	public void addComplementCondition(final String line) {
		if (complementConditions == null) {
			complementConditions = new ArrayList<>();
 		}
		complementConditions.add(new MagicEntry(line.substring(1)));
	}

    /**
     * Verifica que el archivo contenga los bytes mágicos.
     */
    boolean checkMagic(final File targetFile) {
        final byte[] magic = new byte[magicBytes.length];
        final long len = targetFile.length();
		final long fileOffset = fromEnd ? len - offset : offset;

        // No podemos leer más que lo que hay
        if (fileOffset >= len || fileOffset < 0) {
            return false;
        }

		try (final RandomAccessFile fis = new RandomAccessFile(targetFile, "r")) {
            fis.seek(fileOffset);
            fis.read(magic);
            if (Arrays.equals(magic, magicBytes)) {
				boolean conditionVerified = true;
				if (complementConditions != null) {
					for (final MagicEntry complement : complementConditions) {
						conditionVerified &= complement.checkMagic(targetFile);
					}
		 		}
				if (conditionVerified) {
					final StringBuilder builder = new StringBuilder();
					builder.append( magicType);
					if (children != null) {
						children.stream()
							.filter(child -> child.checkMagic(targetFile))
							.forEach(child -> builder.append(' ').append(child.getMsg()));
					}
					msg = builder.toString();
					return true;
				}
            }
        } catch (final IOException ex) {
            ToolBox.showInfo(ex);
		}

        return false;
    }

     /**
     * Convierte en un byte[] una String, dependiendo del criterio que se
     * le proporcione.
     *
     * @since 2003.16.20
     */
    private static byte[] stringToByteArray(final String criterium, final String datum) {
        if (criterium.equalsIgnoreCase("STRING")) {
            return datum.getBytes();
        } else if (criterium.equalsIgnoreCase("BYTES")) {
            final StringTokenizer st = new StringTokenizer(datum, "- .\\");	// 20030629 Agregado el '\' para usar lo que viene en octal del archivo magic
            final int count = st.countTokens();
            final byte[] bytes = new byte[count];
            for (int ii = 0; ii < count; ii++) {
                final String retStr = st.nextToken();
                final int strlen = retStr.length();
                if (strlen == 2) {			// hex
                    bytes[ii] = (byte) Integer.parseInt(retStr, HEX_RADIX);
                } else if (strlen == 3) {	// octal
                    bytes[ii] = (byte) Integer.parseInt(retStr, OCTAL_RADIX);
                } else {
                    throw new IllegalArgumentException(retStr);
                }
            }
            return bytes;
        } else {
            throw new IllegalArgumentException(criterium);
        }
    }
    
    final String getMsg() {
    	return msg;
	}

    public final String toString() {
    	final StringBuilder magicEntryString = new StringBuilder();
    	magicEntryString.append("org.pclg.filesystem.MagicEntry: looking for '")
//        		.append(new String(magicBytes)).append("' from ");
    		.append(magicType).append("' from ");
    	switch (whereToLook) {
    		case FROM_START:
    			magicEntryString.append("START");
    			break;
    		case FROM_END:
    			magicEntryString.append("END");
    			break;
    		default :
    			throw new IllegalArgumentException("whereToLook = " + whereToLook);
    	}
		if (complementConditions != null) {
    		magicEntryString.append(" -> complementConditions: ")
				.append(complementConditions);
    	}
		if (children != null) {
    		magicEntryString.append(" -> children: ").append(children);
    	}

    	return magicEntryString.toString();
    }
}
