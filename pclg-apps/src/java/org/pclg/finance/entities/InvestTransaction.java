package org.pclg.finance.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Transaction<BR>
 *
 * @author El Coyote Cojo
 * @since 18-jun-2010 18:27:32
 */
public class InvestTransaction {
    /*
    Identifiers for investment accounts

    Use these letters to identify specific items in an investment account transaction.  Each line in the
    transaction must begin with one of these letters:

    Letter	What it means

    D	Date (optional)
    N	Action
    Y	Security
    I	Price
    Q	Quantity (# of shares or split ratio)
    C	Cleared status
    P	1st line text for transfers/reminders
    M	Memo
    O	Commission
    L	Account for transfer
    $	AMOUNT transferred

    ^	End of entry
     */


    public static final char DATE = 'D';
    public static final char CATEGORY = 'L';
    public static final char PAYEE = 'P';
    public static final char AMOUNT = 'T';
    public static final char MEMO = 'M';
    public static final char CLEARED = 'C';
    public static final char NUMBER = 'N';
    public static final char MEMO_SPLIT = 'E';
    public static final char CATEGORY_SPLIT = 'S';
    public static final char AMOUNT_SPLIT = '$';

    private final long id;
    private final Date date;
    private final String payee;
    private final String memo;
    private final String number;
    private final boolean cleared;
    private final String category;
    private final BigDecimal amount;
    private List<Split> splits;
    public static final char QUANTITY = 'Q';
    public static final char SECURITY = 'Y';

    public InvestTransaction(final long id, final Date date, final String payee, final String memo,
            final String category, final BigDecimal amount, final String number, final boolean cleared,
            final List<Split> splits) {
        this.id = id;
        this.date = date;
        this.payee = payee;
        this.memo = memo;
        this.category = category;
        this.amount = amount;
        this.number = number;
        this.cleared = cleared;
        this.splits = splits;
    }

    public void addSplit(final Split split) {
        splits.add(split);
    }

    public void setSplits(final List<Split> splits) {
        this.splits = splits;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", date=" + date +
                ", payee='" + payee + '\'' +
                ", memo='" + memo + '\'' +
                ", number='" + number + '\'' +
                ", cleared=" + cleared +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", splits=" + splits +
                '}';
    }
}