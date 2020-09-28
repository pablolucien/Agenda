package org.pclg.condominio;

import org.apache.logging.log4j.Logger;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.pclg.log.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;


/**
 * Generate docx file from a template
 */
public class GenerateEncajerasDocxService {
    //use to avoid to keep the word placeholder "Click here to enter text."
    public static final String EMPTY_VALUE = "EMPTY_VALUE";
    public static final String EMPTY_VALUE_PLACEHOLDER = "${" + EMPTY_VALUE + "}";
    /**
     * El Logger de esta clase.
     */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();

    public static String convertToXML(final Object pojo) {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(pojo.getClass());
            final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true/*LOG.isDebugEnabled()*/);
            final StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(pojo, sw);
//            LOG.debug(sw.toString());
            return sw.toString();
        } catch (final JAXBException ex) {
            throw new RuntimeException("Error during XML generation", ex);
        }
    }

    public ByteArrayOutputStream generateDocx(final Object pojo, final String templateDocxName) {
        final String xml = convertToXML(pojo);

        try (final InputStream stream = getClass().getResourceAsStream(templateDocxName)) {
            LOGGER.debug("templateDocxName: " + templateDocxName);
            LOGGER.debug("El stream es: " + stream);
            return generateDocx(xml, stream);
        } catch (IOException | Docx4JException | JAXBException ex) {
            LOGGER.error("Error during Docx generation", ex);
            throw new RuntimeException(ex);
        }
    }

    protected ByteArrayOutputStream generateDocx(final String xmlStr, final InputStream templateDocx) throws Docx4JException, JAXBException {

        // Load input_template.docx
        final WordprocessingMLPackage templateWord = Docx4J.load(templateDocx);

        // Do the binding:
        // FLAG_NONE means that all the steps of the binding will be done,
        // otherwise you could pass a combination of the following flags:
        // FLAG_BIND_INSERT_XML: inject the passed XML into the document
        // FLAG_BIND_BIND_XML: bind the document and the xml (including any OpenDope handling)
        // FLAG_BIND_REMOVE_SDT: remove the content controls from the document (only the content remains)
        // FLAG_BIND_REMOVE_XML: remove the custom xml parts from the document
        Docx4J.bind(templateWord, xmlStr, Docx4J.FLAG_BIND_INSERT_XML | Docx4J.FLAG_BIND_BIND_XML | Docx4J.FLAG_BIND_REMOVE_SDT);
        replaceEmptyValue(templateWord);

        //Save the document
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Docx4J.save(templateWord, outputStream, Docx4J.FLAG_NONE);

        return outputStream;
    }

    /**
     * Use to avoid to keep the word placeholder "Click here to enter text."
     *
     * @param wordMLPackage
     * @throws JAXBException
     * @throws Docx4JException
     */
    protected void replaceEmptyValue(final WordprocessingMLPackage wordMLPackage) throws JAXBException, Docx4JException {
        final MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        final HashMap<String, String> mappings = new HashMap<>();
        mappings.put(EMPTY_VALUE, "");
        documentPart.variableReplace(mappings);
    }
}
