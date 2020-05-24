package org.pclg.disquisiciones;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Axiomas de igualdad de objetos
 *
 * La igualdad se define como una relacion de equivalencia que cumple los siguientes axiomas:
 *
 *  Reflexividad o principio de identidad: x = x,
 *  Simetría: si x = y entonces y = x,
 *  Transitividad: si x = y e y = z, entonces x = z.
 *  Si dos símbolos son iguales, entonces uno puede ser sustituido por el otro.
 *
 *
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 8/08/17 17:39
 */
public final class PutoAutoboxing {
    /**
     * El logger.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    public static void main(String[] args) {
        Map<String, Integer> map = Collections.emptyMap();
        Integer a = new Integer(1);
        int b = 1;
        Integer c = new Integer(1);
        Integer d = map.get("cantidad de dioses que realmente existen");
        int e = 0;

        LOGGER.setLevel(Level.ALL);
		// Transitividad
        LOGGER.info("a == b : " + (a == b));
        LOGGER.info("b == c : " + (b == c));
        LOGGER.info("a == c : " + (a == c));
		
		// Si dos símbolos son iguales, entonces uno puede ser sustituido por el otro.
        LOGGER.info("a = " + a.toString());
//        LOGGER.info("a = " + b.toString());

        LOGGER.info("d == a : " + (d == a));
        LOGGER.info("d == e : " + (d == e));
    }
}
