package org.pclg.misc;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.io.text.VCardWriter;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Categories;
import ezvcard.property.FormattedName;
import ezvcard.property.Nickname;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.Browser;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// https://code.google.com/p/ez-vcard/
public class UpdateContactosMovilVcards {
    /** El logger. */
    private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final String BASE_DIR = "C:/tmp/Contactos Yahoo";

    private UpdateContactosMovilVcards() {
        //	test1();
        //	test2();
        //	load();
        renameVcardFiles();
    }

    /**
     * Rnombra los ficheros de vcard seg�n el nombre de la persona.
     */
    private void renameVcardFiles() {
        final File dir = new File(BASE_DIR);
        final File dirP = new File("C:/tmp/Contactos Yahoo/procesados");
        final File dirC1 = new File("C:/tmp/Contactos Yahoo/procesados/Deutsch_C1");

        for (final File file : dir.listFiles()) {
            if (!file.isFile()) {
                continue;
            }
            try {
                final VCard vcard = Ezvcard.parse(file).first();
                final List<Address> addresses = vcard.getAddresses();
                final List<Telephone> telephoneNumbers = vcard.getTelephoneNumbers();
                final List<Nickname> nicknames = vcard.getNicknames();
                final FormattedName formattedName = vcard.getFormattedName();
                final StructuredName structuredName = vcard.getStructuredName();
                final Categories categories = vcard.getCategories();

                if (formattedName != null) {
                    final String formattedNameValue =
                        new String(formattedName.getValue().getBytes(), "utf-8");
                    final File newFile;
                    if (categories != null && categories.getValues().contains("Deutsch_C1")) {
                        newFile = new File(dirC1, formattedNameValue + ".vcf");
                    } else {
                        newFile = new File(dirP, formattedNameValue + ".vcf");
                    }
                    if (!file.renameTo(newFile)) {
                        LOGGER.warn("No pude renombrar " + file + " to " + newFile);
                    }
                }
            } catch (final Exception ex) {
                LOGGER.error("Error", ex);
            }
        }
    }

    private void load() {
        //-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
        final File dir = new File(BASE_DIR);
        final String[] headers = {"Nombre", "Apellido", "Telefono 1", "Tipo 1",
            "Telefono 2", "Tipo 2", "Telefono 3", "Tipo 3",
            "Telefono 4", "Tipo 4", "Telefono 5", "Tipo 5"};
        final List<String[]> data = new ArrayList<String[]>();
        for (final File file : dir.listFiles()) {
            if (!file.isFile()) {
                continue;
            }
            try {
                final String[] datum = new String[headers.length];
                final VCard vcard = Ezvcard.parse(file).first();
                final List<Address> addresses = vcard.getAddresses();
                final List<Telephone> telephoneNumbers = vcard.getTelephoneNumbers();
                final List<Nickname> nicknames = vcard.getNicknames();
                final FormattedName formattedName = vcard.getFormattedName();
                final StructuredName structuredName = vcard.getStructuredName();

                if (structuredName != null) {
                    final String apellido = structuredName.getFamily();
                    final String nombre = structuredName.getGiven();
                    int index = 0;
                    if (nombre != null) {
                        datum[index] = nombre.replaceAll("/", "_");
                    }
                    index++;
                    if (apellido != null) {
                        datum[index] = apellido.replaceAll("/", "_");
                    }
                    index++;
                    if (telephoneNumbers != null) {
                        for (final Telephone telephone : telephoneNumbers) {
                            datum[index++] = telephone.getText();
                            datum[index++] = telephone.getTypes().toString();
                        }
                    }
                }
                data.add(datum);
            } catch (final Exception ex) {
                LOGGER.error("Error", ex);
            }
        }

        final Browser browser = new Browser(headers, data);
        browser.addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosed(final WindowEvent e) {
                saveVcards(data);
            }
        });
        browser.setSize(1300, 800);
    }

    void saveVcards(final List<String[]> data) {
        final File dir = new File(BASE_DIR, "out");
        for (final String[] datum : data) {
            final VCard vcard = new VCard();

            final StructuredName n = new StructuredName();
            n.setGiven(datum[0]);
            n.setFamily(datum[1]);
            vcard.setStructuredName(n);
            for (int ii = 2; ii < datum.length; ii += 2) {
                if (datum[ii] != null && datum[ii].trim().length() > 0) {
                    vcard.addTelephoneNumber(datum[ii], getTypes(datum[ii + 1]));
                }
            }

            final VCardVersion version = VCardVersion.V2_1;
            int counter = 0;
            final String fullName = (datum[0] == null ? "" : datum[0]) + " " + (datum[1] == null ? "" : datum[1]);
            File file = new File(dir, fullName + ".vcf");
            while (file.exists()) {
                file = new File(dir, fullName + "_" + counter++ + ".vcf");
            }
            final VCardWriter writer;
            try {
                writer = new VCardWriter(file, true, version);
                writer.write(vcard);
                writer.close();
            } catch (final IOException ex) {
                // TODO Auto-generated catch block
                LOGGER.error("Error", ex);
            }
        }
    }

    private final TelephoneType[] NULL_TYPES = new TelephoneType[0];

    private TelephoneType[] getTypes(final String string) {
        if (string == null || string.trim().length() == 0) {
            return NULL_TYPES;
        }
        final String[] parts = string.split(", ");
        final TelephoneType[] types = new TelephoneType[parts.length];
        int ii = 0;
        for (final String part : parts) {
            types[ii++] = TelephoneType.get(part);
        }
        return types;
    }

    private void test1() {
        final String str = "BEGIN:VCARD\r\n"
            + "VERSION:4.0\r\n"
            + "N:Doe;Jonathan;;Mr;\r\n"
            + "FN:John Doe\r\n"
            + "END:VCARD\r\n";

        final VCard vcard = Ezvcard.parse(str).first();
        final String fullName = vcard.getFormattedName().getValue();
        final String lastName = vcard.getStructuredName().getFamily();
        System.out.println(fullName + " - " + lastName);
    }

    private void test2() {
        final VCard vcard = new VCard();

        final StructuredName n = new StructuredName();
        n.setFamily("Doe");
        n.setGiven("Jonathan");
        n.addPrefix("Mr");
        vcard.setStructuredName(n);

        vcard.setFormattedName("John Doe");

        final String str = Ezvcard.write(vcard).version(VCardVersion.V4_0).go();
        System.out.println(str);
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        new UpdateContactosMovilVcards();
//		String driver = "org.apache.derby.jdbc.ClientDriver";
//		String url = "jdbc:derby://localhost:1527/memory:agendaDB;create=false;dataEncryption=true;bootPassword=222";
//		Class.forName(driver);
//		Connection conn = DriverManager.getConnection(url);
    }
}
