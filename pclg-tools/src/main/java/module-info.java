module pclg.tools {
    requires java.datatransfer;
    requires java.desktop;
    requires java.logging;
    requires java.sql;
    requires jpatterns;
    requires velocity;
    requires org.apache.log4j;
    requires org.apache.logging.log4j;

    exports dbinfo;
    exports org.pclg.annotations;
    exports org.pclg.dbutil;
    exports org.pclg.filelist;
    exports org.pclg.filesystem;
    exports org.pclg.filesystem.fileattributes;
    exports org.pclg.fortunes;
    exports org.pclg.gui;
    exports org.pclg.log;
    exports org.pclg.media.image;
    exports org.pclg.runtime;
    exports org.pclg.security;
    exports org.pclg.tools;
    exports org.pclg.xtras;
    exports org.pclg.media.imagetools;
    exports org.pclg;
}