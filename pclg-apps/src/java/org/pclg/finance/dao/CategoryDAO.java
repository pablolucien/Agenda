package org.pclg.finance.dao;

import org.pclg.finance.entities.Category;

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
 * @since 18-jun-2010 22:07:53
 */
public class CategoryDAO {
    private final Connection conn;
    private final PreparedStatement pstmtInsert;
    private final PreparedStatement pstmtSelectAll;
    public CategoryDAO(final Connection conn) throws SQLException {
        this.conn = conn;
        pstmtInsert = conn.prepareStatement("INSERT INTO Categories "
            + " (Name, Description, Type, Tax) "
            + " VALUES (?, ?, ?, ?) ");
        pstmtSelectAll = conn.prepareStatement("SELECT PK, Name, Description, "
            + "Type, Tax FROM Categories");
    }

    public void save(final List<Category> list) throws SQLException {
        for (final Category category : list) {
            save(category);
        }
        conn.commit();
    }

    private void save(final Category category) throws SQLException {
        int pos = 0;
        pstmtInsert.setString(++pos, category.getName());
        pstmtInsert.setString(++pos, category.getDescription());
        pstmtInsert.setString(++pos, String.valueOf(category.getTipo()));
        pstmtInsert.setBoolean(++pos, category.isTaxRelated());
        pstmtInsert.execute();
    }

    public Map<String, Category> loadCategories() throws SQLException {
        final Map<String, Category> map = new HashMap<String, Category>();
        final ResultSet rset = pstmtSelectAll.executeQuery();
        while (rset.next()) {
            int col = 0;
            final Category category = new Category(rset.getInt(++col),
                rset.getString(++col), rset.getString(++col),
                rset.getString(++col).charAt(0), rset.getBoolean(++col));
            map.put(category.getName(), category);
        }
        rset.close();
        return map;
    }
}