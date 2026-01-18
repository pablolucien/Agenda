module pclg.agenda {
    requires java.datatransfer;
    requires java.desktop;
    requires java.logging;
    requires java.management;
    requires java.sql;
    requires com.googlecode.ezvcard;
    requires jpatterns;
    requires org.apache.logging.log4j;
    requires pclg.tools;
    requires jakarta.xml.bind;

    opens org.pclg.agenda.entities to jakarta.xml.bind;
}