package org.pclg.finance.dao;

import org.pclg.finance.entities.Account;

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
public class AccountDAO {
    private final Connection conn;
    private final PreparedStatement pstmtInsert;
    private final PreparedStatement pstmtSelectAll;
    public AccountDAO(final Connection conn) throws SQLException {
        this.conn = conn;
        pstmtInsert = conn.prepareStatement("INSERT INTO Accounts "
            + " (Name, Description, Type, Limit) "
            + " VALUES (?, ?, ?, ?) ");
        pstmtSelectAll = conn.prepareStatement("SELECT PK, Name, Description, "
            + "Type, Limit FROM Accounts");
    }

    public void save(final List<Account> list) throws SQLException {
        for (final Account account : list) {
            save(account);
        }
        conn.commit();
    }

    private void save(final Account account) throws SQLException {
        int pos = 0;
        pstmtInsert.setString(++pos, account.getName());
        pstmtInsert.setString(++pos, account.getDescription());
        pstmtInsert.setInt(++pos, tipo(account.getTipo()));
        pstmtInsert.setBigDecimal(++pos, account.getLimit());
        pstmtInsert.execute();
    }

    public Map<String, Account> loadAccounts() throws SQLException {
        final Map<String, Account> map = new HashMap<String, Account>();
        final ResultSet rset = pstmtSelectAll.executeQuery();
        while (rset.next()) {
            int col = 0;
            final Account account = new Account(rset.getInt(++col),
                rset.getString(++col), rset.getString(++col),
                rset.getString(++col), rset.getBigDecimal(++col));
            map.put(account.getName(), account);
        }
        rset.close();
        return map;
    }

    private int tipo(final String tipo) {
        return tipo.equals("Bank") ? 1
                : tipo.equals("CCard") ? 2
                : tipo.equals("Cash") ? 3
                : tipo.equals("Invst") ? 4
                : tipo.equals("Oth A") ? 5
                : tipo.equals("Oth L") ? 6
                : 0;
    }
}
