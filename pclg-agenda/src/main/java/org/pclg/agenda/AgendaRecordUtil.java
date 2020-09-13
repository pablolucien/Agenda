package org.pclg.agenda;

import org.apache.log4j.Logger;
import org.pclg.agenda.entities.AgendaRecord;
import org.pclg.agenda.entities.AgendaRecordImpl;
import org.pclg.annotations.QuickAndDirty;
import org.pclg.log.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.pclg.tools.StringTools.isEmptyOrBlank;

/**
 * @since 05/05/2017.
 */
public class AgendaRecordUtil {
    /** El logger. */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

	private AgendaRecordUtil() {
	}

	/**
     * Constructs an AgendaRecord from its xml representation.
     */
    public static AgendaRecord xml2AgendaRecord(final String xml)
            throws ParserConfigurationException, SAXException, IOException,
            NoSuchFieldException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        final AgendaRecord record = new AgendaRecordImpl();
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                .newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));

        // optional, but recommended
        // read this -
        // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        final Element documentElement = doc.getDocumentElement();
        documentElement.normalize();

        LOGGER.debug("Root element :" + documentElement.getNodeName());

        final NodeList childNodes = documentElement.getChildNodes();

        for (int ii = 0, len = childNodes.getLength(); ii < len; ii++) {
            final Node nNode = childNodes.item(ii);
            LOGGER.debug("\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                final Element eElement = (Element) nNode;
                final String fieldName = eElement.getTagName();
                final String nodeValue = eElement.getTextContent();
                LOGGER.debug("eElement.getTagName(): " + fieldName
                        + " :: value: " + nodeValue);
                if (isEmptyOrBlank(nodeValue)) {
                    continue;
                }
                final Class<? extends AgendaRecord> myClass = record.getClass();
                final Field field = myClass.getDeclaredField(fieldName);
				field.setAccessible(true);
                final Class<?> fieldType = field.getType();
                if (fieldType == String.class) {
                    field.set(record, nodeValue);
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    field.set(record, Integer.valueOf(nodeValue));
                } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                    field.set(record, Boolean.valueOf(nodeValue));
//				} else if (fieldType == java.sql.Date.class) {
//					// LOGGER.debug(eElement.getAttribute("class"));
//					// LOGGER.debug(eElement.getAttribute("format"));
//					// final SimpleDateFormat dateFormat =
//					// new SimpleDateFormat(eElement.getAttribute("format"));
//					// field.set(record, dateFormat.parse(nodeValue));
//					field.set(record, java.sql.Date.valueOf(nodeValue));
                } else if (fieldType == List.class) {
                    processList(field, record, eElement);
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Tratando de procesar objeto de tipo <"
                            + fieldType + "> con valor <" + nodeValue + ">");
                    }
                    processValueOf(field, record, nodeValue);
                }
            }
        }
        return record;
    }

    /**
     * Procesa una lista. Si esta es parametrizada, la clase par�metro tiene que
     * tener el m�todo static valueOf(Object) para poder obtener un objeto de
     * esa clase de su representacion en el xml.
     */
    @QuickAndDirty
    // A esto le faltan varios hervores.
    private static void processList(final Field field, final Object obj,
            final Element element) throws IllegalArgumentException,
            IllegalAccessException, SecurityException, NoSuchMethodException,
            DOMException, InvocationTargetException {
        final Type genericFieldType = field.getGenericType();
        LOGGER.debug("genericFieldType = " + genericFieldType);
        if (genericFieldType instanceof ParameterizedType) {
            final ParameterizedType aType = (ParameterizedType) genericFieldType;
            final Type[] fieldArgTypes = aType.getActualTypeArguments();
            assert fieldArgTypes.length == 1;
            final Class<?> listItemsClass = (Class<?>) fieldArgTypes[0];
            LOGGER.debug("fieldArgClass = " + listItemsClass);
            final Method valueOf;
            if (listItemsClass == String.class) {
                // Parece una tonter�a, pero en definitiva devuelve el mismo
                // objeto y el c�digo no se complica.
                valueOf = listItemsClass.getDeclaredMethod("valueOf", Object.class);
            } else {
                valueOf = listItemsClass.getDeclaredMethod("valueOf", String.class);
            }
            final NodeList childNodes = element.getChildNodes();
            final List list = (List) field.get(obj);
            list.clear();
            for (int ii = 0, len = childNodes.getLength(); ii < len; ii++) {
                final Node nNode = childNodes.item(ii);
                LOGGER.debug("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    final Element childElement = (Element) nNode;
                    final String textContent = childElement.getTextContent();
                    LOGGER.debug("textContent :" + textContent);
                    list.add(valueOf.invoke(null, textContent));
                }
            }
        }
    }

    /**
     * Procesa un elmento que tiene que
     * tener el m�todo static valueOf(String) para poder obtener un objeto de
     * esa clase de su representacion en el xml.
     */
    @QuickAndDirty
    private static void processValueOf(final Field field, final AgendaRecord record,
            final String value) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException  {
        final Class<?> fieldType = field.getType();
        try {
            final Method valueOf = fieldType.getDeclaredMethod("valueOf", String.class);
            field.set(record, valueOf.invoke(null, value));
        } catch (final NoSuchMethodException e) {
            LOGGER.warn("Method valueOf(Object) non-existant in class " + fieldType);
        }
    }
}
