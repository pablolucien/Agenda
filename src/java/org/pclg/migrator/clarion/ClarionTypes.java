// ******************************** package
package org.pclg.migrator.clarion;

// ******************************** imports

/**
 * ClarionTypes: Los tipos de dato de Clasrion.
 *
 * @author El Coyote Cojo
 * @version 2002.ene.16 18:15:07, CEST
 */
final class ClarionTypes {
    /** A Clarion data type.*/
    public static final int LONG = 1;

    /** A Clarion data type.*/
    public static final int REAL = 2;

    /** A Clarion data type.*/
    public static final int STRING = 3;

    /** A Clarion data type.*/
    public static final int STRING_WITH_PICTURE_TOKEN = 4;

    /** A Clarion data type.*/
    public static final int BYTE = 5;

    /** A Clarion data type.*/
    public static final int SHORT = 6;

    /** A Clarion data type.*/
    public static final int GROUP = 7;

    /** A Clarion data type.*/
    public static final int DECIMAL = 8;

    /** Agregado por mi para los campos tipo MEMO. */
    public static final int MEMO = 99;

    /** Agregado por mi para saber los registros borrados.
     * TODO: Esta indentado con espacio para que jivelint me recuerde que debo implementarlo.
     */
    public static final int DELETED = 98;

    /**
     * Avoids instantiation.
     */
    private ClarionTypes() {
    }

    /**
     * Devuelve el nombre del tipo de dato.
     * @param fldtype el tipo de dato.
     * @return el nombre del tipo de dato.
     */
    public static String fieldTypeName(final int fldtype) {
        switch (fldtype) {
        case LONG:
            return "LONG";
        case REAL:
            return "REAL";
        case STRING:
            return "STRING";
        case STRING_WITH_PICTURE_TOKEN:
            return "STRING WITH PICTURE TOKEN";
        case BYTE:
            return "BYTE";
        case SHORT:
            return "SHORT";
        case GROUP:
            return "GROUP";
        case DECIMAL:
            return "DECIMAL";
        case MEMO:
            return "MEMO";
        default:
            return "TIPO DE DATO DESCONOCIDO: " + fldtype;
        }
    }
}
