package org.pclg.finance.dao;

import org.pclg.finance.entities.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * CuentaDAO<BR>
 *
 * @author El Coyote Cojo
 * @since 18-jun-2010 22:07:53
 */
public class TransactionDAO {
    private final Connection conn;
    private final String tablePrefix;

    public TransactionDAO(final Connection conn, final char key) throws SQLException {
        this.conn = conn;
        tablePrefix = key == 'M' ? "Memorised" : "";
        final PreparedStatement pstmtInsertSplits = conn.prepareStatement(
            "INSERT INTO " + tablePrefix
            + "Splits (Transaction, Line, TxAccount, Category, Class, Memo, Amount) "
            + " VALUES (?, ?, ?, ?, ?, ?, ?) ");
    }

    public void save(final List<Transaction> list) throws SQLException {
        final PreparedStatement pstmtSelect =
            conn.prepareStatement("SELECT PK, Account, Tran_Date, Payee, "
                + "TxAccount, Category, Class, Amount, Memo, Cleared, Reference"
                + " FROM " + tablePrefix + "Transactions",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        final ResultSet rs = pstmtSelect.executeQuery();

        for (final Transaction transaction : list) {
            save(rs, transaction);
            break;
        }
    }

    private void save(final ResultSet rs, final Transaction transaction) throws SQLException {
        rs.moveToInsertRow();
        int pos = 1;
        rs.updateString(++pos, transaction.getAccount());
        rs.updateDate(++pos, transaction.getDate());
        rs.updateString(++pos, transaction.getPayee());
        rs.updateString(++pos, transaction.getTxAccount());
        rs.updateString(++pos, transaction.getCategory());
        rs.updateString(++pos, transaction.getAClass());
        rs.updateBigDecimal(++pos, transaction.getAmount());
        rs.updateString(++pos, transaction.getMemo());
        rs.updateBoolean(++pos, transaction.isCleared());
        rs.updateString(++pos, transaction.getReference());
        rs.insertRow();
        //rs.moveToCurrentRow();
        //rs.next();
        rs.last();
        System.out.println("rs = " + rs.getString(2));
        rs.close();
    }

    private void save(final Transaction transaction) throws SQLException {
        int pos = 0;
        final PreparedStatement pstmtInsert =
            conn.prepareStatement("INSERT INTO " + tablePrefix
            + "Transactions (Account, Tran_Date, Payee, TxAccount, Category, " +
                "Class, Amount, Memo, Cleared, Reference) "
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
        pstmtInsert.setString(++pos, transaction.getAccount());
        pstmtInsert.setDate(++pos, transaction.getDate());
        pstmtInsert.setString(++pos, transaction.getPayee());
        pstmtInsert.setString(++pos, transaction.getTxAccount());
        pstmtInsert.setString(++pos, transaction.getCategory());
        pstmtInsert.setString(++pos, transaction.getAClass());
        pstmtInsert.setBigDecimal(++pos, transaction.getAmount());
        pstmtInsert.setString(++pos, transaction.getMemo());
        pstmtInsert.setBoolean(++pos, transaction.isCleared());
        pstmtInsert.setString(++pos, transaction.getReference());
        pstmtInsert.execute();
        pstmtInsert.close();
//        final List<Split> splitList = transaction.getSplits();
//        if (splitList != null) {
//            int splitNr = 0;
//            for (Split split : splitList) {
//                int posSplits = 0;
//                // El (int) es para evitar esto: java.sql.SQLException:
//                // [Microsoft][Controlador ODBC Microsoft Access]Función opcional no implementada
//                final int trId = (int) transaction.getId();
//                pstmtInsertSplits.setInt(++posSplits, trId);
//                pstmtInsertSplits.setInt(++posSplits, splitNr++);
//                pstmtInsertSplits.setString(++posSplits, split.getTxAccount());
//                pstmtInsertSplits.setString(++posSplits, split.getCategory());
//                pstmtInsertSplits.setString(++posSplits, split.getAClass());
//                pstmtInsertSplits.setString(++posSplits, split.getMemo());
//                pstmtInsertSplits.setBigDecimal(++posSplits, split.getAmount());
//                try {
//                    pstmtInsertSplits.execute();
//                } catch (SQLException e) {
//                    System.out.println("split = " + split);
//                    throw e;
//                }
//            }
//        }
        conn.commit();
    }
}