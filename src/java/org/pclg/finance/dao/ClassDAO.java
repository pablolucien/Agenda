package org.pclg.finance.dao;

import org.pclg.finance.entities.Class;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CuentaDAO<BR>
 *
 * @author El Coyote Cojo
 * @since 19-jun-2010 11:53
 */
public class ClassDAO {
    private final Connection conn;
    private final PreparedStatement pstmtInsert;
    private final PreparedStatement pstmtSelectAll;
    public ClassDAO(final Connection conn) throws SQLException {
        this.conn = conn;
        pstmtInsert = conn.prepareStatement("INSERT INTO Classes "
            + " (Name, Description) "
            + " VALUES (?, ?) ");
        pstmtSelectAll = conn.prepareStatement("SELECT PK, Name, Description "
            + "FROM Classes");
    }

    public void save(final List<org.pclg.finance.entities.Class> list) throws SQLException {
        for (final Class aClass : list) {
            save(aClass);
        }
        conn.commit();
    }

    private void save(final Class aClass) throws SQLException {
        int pos = 0;
        pstmtInsert.setString(++pos, aClass.getName());
        pstmtInsert.setString(++pos, aClass.getDescription());
        pstmtInsert.execute();
    }

    public Map<String, Class> loadClasses() throws SQLException {
        final Map<String, Class> map = new HashMap<String, Class>();
        final ResultSet rset = pstmtSelectAll.executeQuery();
        while (rset.next()) {
            int col = 0;
            final Class aClass = new Class(rset.getInt(++col),
                rset.getString(++col), rset.getString(++col));
            map.put(aClass.getName(), aClass);
        }
        rset.close();
        return map;
    }
}