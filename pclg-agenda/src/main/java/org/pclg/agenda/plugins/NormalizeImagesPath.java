package org.pclg.agenda.plugins;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Delete the leading 'C:\' and change '\' to '/'.
 */
public class NormalizeImagesPath implements Plugin {

    @Override
    public void execute(final Properties properties, final Connection conn, final String... args) throws SQLException {
        final String sql = "SELECT imagePath FROM root.imagen";

        try (final Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
             final ResultSet rset = stmt.executeQuery(sql)) {
            while (rset.next()) {
                final String path = rset.getString(1);
                if (path != null && path.toUpperCase().startsWith("C:\\")) {
                    final String newPath = path.substring(3).replace("\\", "/");
                    rset.updateString(1, newPath);
                    rset.updateRow();
                }
            }
        }
    }
}
