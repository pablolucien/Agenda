package org.pclg.finance;

import org.pclg.finance.dao.AccountDAO;
import org.pclg.finance.dao.CategoryDAO;
import org.pclg.finance.dao.ClassDAO;
import org.pclg.finance.dao.TransactionDAO;
import org.pclg.finance.entities.Transaction;
import org.pclg.finance.qif.Reader;
import org.pclg.tools.ToolBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author El Coyote Cojo.
 * @since 18-jun-2010 10:54:08
 */
public class Main {
    public static void main(final String[] args) throws IOException, SQLException {
        //readGeneric();
        loadAll();
        readTransactions();
    }

    private static void readTransactions() throws IOException, SQLException {
        final String accountName = "Euros";
        final Reader reader = new Reader(accountName + ".qif");
        final List<Transaction> transactions = reader.readTransactions(accountName);
        final Connection conn = getConnection();
        final TransactionDAO transactionDAO = new TransactionDAO(conn, 'T');
        transactionDAO.save(transactions);
        conn.close();
    }

    private static void readGeneric() throws IOException, SQLException {
        final Reader reader = new Reader("cats.qif");
        final Map<String, List> map = reader.readFile();
        final Connection conn = getConnection();
        final AccountDAO cuentaDAO = new AccountDAO(conn);
        cuentaDAO.save(map.get(Reader.ACCOUNT_KEY));
        final ClassDAO claseDAO = new ClassDAO(conn);
        claseDAO.save(map.get(Reader.CLASS_KEY));
        final CategoryDAO categoriaDAO = new CategoryDAO(conn);
        categoriaDAO.save(map.get(Reader.CATEGORY_KEY));
        final TransactionDAO transactionDAO = new TransactionDAO(conn, 'M');
        transactionDAO.save(map.get(Reader.MEMORIZED_KEY));
        conn.close();
    }

    private static void loadAll() throws SQLException {
        final Map<String, Map<String, ?>> map =
            new HashMap<String, Map<String, ?>>();
        final Connection conn = getConnection();
        final AccountDAO cuentaDAO = new AccountDAO(conn);
        map.put(Reader.ACCOUNT_KEY, cuentaDAO.loadAccounts());
        final ClassDAO claseDAO = new ClassDAO(conn);
        map.put(Reader.CLASS_KEY, claseDAO.loadClasses());
        final CategoryDAO categoriaDAO = new CategoryDAO(conn);
        map.put(Reader.CATEGORY_KEY, categoriaDAO.loadCategories());
        conn.close();
    }

    /**
     * Establece la comunicacion con la base de datos
     *
     * @since 2003.01.07
     * @return  una conexión a la base de datos.
     * @throws SQLException si hay problemas.
     */
    private static Connection getConnection() throws SQLException {
        final String dbURL="jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};"
            + "DBQ=C:/home/development/modules/resources/Finance/Finance.mdb;"
            + "UID=;PWD=";
        final String driverClassName = "sun.jdbc.odbc.JdbcOdbcDriver";

        try {
            Class.forName(driverClassName).newInstance();
            return DriverManager.getConnection(dbURL);
        } catch (final ClassNotFoundException ex) {
            ToolBox.showInfo(ex, true);
            throw new SQLException(driverClassName + ": ClassNotFoundException");
        } catch (final InstantiationException ex) {
            ToolBox.showInfo(ex, true);
            throw new SQLException(driverClassName + ": InstantiationException");
        } catch (final IllegalAccessException ex) {
            ToolBox.showInfo(ex, true);
            throw new SQLException(driverClassName + ": IllegalAccessException");
        }
    }
}
