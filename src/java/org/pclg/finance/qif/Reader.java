package org.pclg.finance.qif;

import org.pclg.finance.entities.Account;
import org.pclg.finance.entities.Category;
import org.pclg.finance.entities.Class;
import org.pclg.finance.entities.InvestTransaction;
import org.pclg.finance.entities.Split;
import org.pclg.finance.entities.Transaction;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author paceLucien
 * @since 18-jun-2010 8:37:09
 */
public final class Reader {
    private final BufferedReader reader;
    private static final int READ_AHEAD_LIMIT = 1024;
    private static final char END_OF_ENTRY = '^';
    private static final BigDecimal ZERO = new BigDecimal("0.0");
    public static final String ACCOUNT_KEY = "Account";
    public static final String CLASS_KEY = "Class";
    public static final String CATEGORY_KEY = "Category";
    public static final String MEMORIZED_KEY = "Memorized";
    private static final char END_OF_BATCH = '!';
    private static final Date NULL_DATE = null;// new Date(0L);


    /**
     * Reads the contents of a QIF file.
     *
     * @param fileName the QIF file to read.
     * @throws FileNotFoundException if something goes wrong.
     */
    public Reader(final String fileName) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(fileName));
    }

    /**
     * Description copied from class: Reader
     * Closes the stream and releases any system resources associated with it.
     * Once the stream has been closed, further read(), ready(), mark(),
     * reset(), or skip() invocations will throw an IOException. Closing a
     * previously closed stream has no effect.
     *
     * @throws IOException If an I/O error occurs
     */
    public void close() throws IOException {
        reader.close();
    }

    /**
     * Reads a generic QIF file that contains accounts, categories, classes and
     * memorized transactions.
     *
     * @return a list of the objects contained in the file.
     * @throws IOException If an I/O error occurs
     */
    public Map<String, List> readFile() throws IOException {
        final Map<String, List> map = new HashMap<String, List>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            if (line.equals("!Type:Class")) {
                map.put(CLASS_KEY, loadClasses());
            } else if (line.equals("!Type:Cat")) {
                map.put(CATEGORY_KEY, loadCategories());
            } else if (line.equals("!Account")) {
                map.put(ACCOUNT_KEY, loadAccounts());
            } else if (line.equals("!Type:Memorized")) {
                map.put(MEMORIZED_KEY, loadTransactions(null));
            } else if (line.equals("!Option:AutoSwitch")) {
            } else if (line.equals("!Clear:AutoSwitch")) {
            } else {
                System.out.println("line = " + line);
            }
        }

        return map;
    }

    /**
     * Reads a QIF file that contains transactions for an account.
     *
     * @return a list of the objects contained in the file.
     * @throws IOException If an I/O error occurs
     */
    public List<Transaction> readTransactions(final String account) throws IOException {
        String line;
        List<Transaction> transactions = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            if (line.equals("!Type:Cash")) {
                transactions = loadTransactions(account);
            }
        }
        return transactions;
    }

    /*
       Identifiers for noninvestment accounts

       Use these letters to identify specific items in a noninvestment account
       transaction.  Each line in the transaction must begin with one of
       these letters:

       Letter	What it means

       D	Date
       T	AMOUNT
       C	Cleared status
       N	Number (check or reference)
       P	Payee/description
       M	Memo
       A	Address (up to 5 lines; 6th line is an optional message)
       L	CATEGORY (category/transfer/class)
       S	CATEGORY in split (category/transfer/class)
       E	Memo in split
       $	Dollar amount of split

       ^	End of entry
    */
    private List<Transaction> loadTransactions(final String account)
            throws IOException {
        final List<Transaction> list =
                new ArrayList<Transaction>();
        String line;
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        Split split = null;
        loop:
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            final char c = line.charAt(0);
            switch (c) {
            case Transaction.CATEGORY:
                setActCatClass(transaction, line.substring(1));
                break;
            case Transaction.PAYEE:
                transaction.setPayee(line.substring(1));
                break;
            case Transaction.AMOUNT:
                transaction.setAmount(new BigDecimal(
                    line.substring(1).replaceAll(",", "")));
                break;
            case Transaction.MEMO:
                transaction.setMemo(line.substring(1));
                break;
            case Transaction.DATE:
                transaction.setDate(parseDate(line.substring(1)));
                break;
            case Transaction.REFERENCE:
                transaction.setReference(line.substring(1));
                break;
            case Transaction.CLEARED:
                transaction.setCleared(line.charAt(1) == 'x');
                break;
            case Transaction.CATEGORY_SPLIT:
                split = new Split();
                split.setCategory(line.substring(1));
                transaction.addSplit(split);
                break;
            case Transaction.MEMO_SPLIT:
                split.setMemo(line.substring(1));
                break;
            case Transaction.AMOUNT_SPLIT:
                //System.out.println("line = " + line);
                split.setAmount(new BigDecimal(line.substring(1).replaceAll(",", "")));
                break;
            case 'K':
                // KI = invest, KP = no invest ??
            case InvestTransaction.QUANTITY:
            case InvestTransaction.SECURITY:
                // TODO: De momento me lo trago
                break;
            case END_OF_ENTRY:
                list.add(transaction);
                transaction = new Transaction();
                transaction.setAccount(account);
                break;
            case END_OF_BATCH:
//                reader.reset();
                break loop;
            default:
                alertUnexpected(c, line);
            }
//            reader.mark(READ_AHEAD_LIMIT);
        }
//        System.out.println("list = " + list);
        return list;
    }

    /**
     * Sets TxAccount, Category & Class in a transaction.
     * @param transaction
     * @param str
     */
    private void setActCatClass(final Transaction transaction, String str) {
        String account = "";
        String category = "";
        String clazz = "";
        final int indexOfSlash = str.indexOf('/');
        if (indexOfSlash >= 0) {
            clazz = str.substring(indexOfSlash + 1);
            str = str.substring(0, indexOfSlash);
        }
        final int length = str.length();
        if (str.charAt(0) == '[' && str.charAt(length - 1) == ']') {
            account = str.substring(1, length - 1);
        } else {
            category = str;
        }
        transaction.setTxAccount(account);
        transaction.setCategory(category);
        transaction.setAClass(clazz);
    }

    private Date parseDate(final String strDate) {
        final String[] fields = strDate.split("[/']");
        final int century = strDate.indexOf('\'') > 0 ? 100 : 0;
        final int year = century + Integer.parseInt(fields[2].trim());
        final int month = Integer.parseInt(fields[1].trim()) - 1;
        final int day = Integer.parseInt(fields[0].trim());
        return new Date(year, month, day);
    }

    private List<Account> loadAccounts() throws IOException {
        final List<Account> list = new ArrayList<>();
        String line;
        long id = 0;
        String name = "";
        String desc = "";
        String tipo = "";
        BigDecimal limit = ZERO;
        loop:
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            final char c = line.charAt(0);
            switch (c) {
            case Transaction.REFERENCE:
                name = line.substring(1);
                break;
            case 'D':
                desc = line.substring(1);
                break;
            case 'L':
                limit = new BigDecimal(line.substring(1).replaceAll(",", ""));
                break;
            case 'T':
                tipo = line.substring(1);
                break;
            case END_OF_ENTRY:
                list.add(new Account(id, name, desc, tipo, limit));
                id = 0;
                name = "";
                desc = "";
                tipo = "";
                limit = ZERO;
                break;
            case END_OF_BATCH:
                reader.reset();
                break loop;
            default:
                alertUnexpected(c, line);
            }
            reader.mark(READ_AHEAD_LIMIT);
        }
        //System.out.println("list = " + list);
        return list;
    }

    private List<Category> loadCategories() throws IOException {
        final List<Category> list = new ArrayList<Category>();
        String line;
        long id = 0;
        String name = "";
        String desc = "";
        char tipo = '\0';
        boolean taxRelated = false;
        loop:
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            final char c = line.charAt(0);
            switch (c) {
            case Transaction.REFERENCE:
                name = line.substring(1);
                break;
            case 'D':
                desc = line.substring(1);
                break;
            case 'T':
                taxRelated = true;
                break;
            case 'B':
            case 'R':
                // TODO: De momento me lo trago
                break;
            case 'I':
            case Transaction.MEMO_SPLIT:
                tipo = c;
                break;
            case END_OF_ENTRY:
                list.add(new Category(id, name, desc, tipo, taxRelated));
                id = 0;
                name = "";
                desc = "";
                tipo = '\0';
                taxRelated = false;
                break;
            case END_OF_BATCH:
                reader.reset();
                break loop;
            default:
                alertUnexpected(c, line);
            }
            reader.mark(READ_AHEAD_LIMIT);
        }
        //System.out.println("list = " + list);
        return list;
    }

    private List<Class> loadClasses() throws IOException {
        final List<Class> list = new ArrayList<Class>();
        String line;
        long id = 0;
        String name = "";
        String desc = "";
        loop:
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            final char c = line.charAt(0);
            switch (c) {
            case Transaction.REFERENCE:
                name = line.substring(1);
                break;
            case 'D':
                desc = line.substring(1);
                break;
            case END_OF_ENTRY:
                list.add(new org.pclg.finance.entities.Class(id, name, desc));
                id = 0;
                name = "";
                desc = "";
                break;
            case END_OF_BATCH:
                reader.reset();
                break loop;
            default:
                alertUnexpected(c, line);
            }
            reader.mark(READ_AHEAD_LIMIT);
        }
        //System.out.println("list = " + list);
        return list;
    }

    private void alertUnexpected(final char c, final String line) {
        throw new RuntimeException("Unexpected character: "
                + c + " in line: " + line);
    }
}
