package org.pclg.filesystem.fileattributes;

import java.io.File;
import java.io.Serializable;

/**
 *  Los implementadores de esta interfaz deberán poder obtener y aplicar los atributos de 
 *  un archivo, dependiendo de las caracteristicas del S.O.
 *  @author El Coyote Cojo
 *  @version 1.0
 *  @since 2004.10.30
 */
public interface FileAttributes extends Serializable {
    /**
     * Obtiene los atributos del archivo file.
     * @param file el archivo cuyos atributos se quiere.
     */
    void grabAttributes(File file);

    /**
     * Aplica al archivo file los atributos presumiblemente obtenidos previamente de el u otro
     * archivo.
     * @param file el archivo al que se quiere aplicar los atributos .
     */
    void applyAttributes(File file);
}

