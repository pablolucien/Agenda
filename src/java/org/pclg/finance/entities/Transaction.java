package org.pclg.finance.entities;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction<BR>
 *
 * @author El Coyote Cojo
 * @since 18-jun-2010 18:27:32
 */
public class Transaction {
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

    public static final char DATE = 'D';
    public static final char CATEGORY = 'L';
    public static final char PAYEE = 'P';
    public static final char AMOUNT = 'T';
    public static final char MEMO = 'M';
    public static final char CLEARED = 'C';
    public static final char REFERENCE = 'N';
    public static final char MEMO_SPLIT = 'E';
    public static final char CATEGORY_SPLIT = 'S';
    public static final char AMOUNT_SPLIT = '$';

    private long id;
    private String account;
    private Date date;
    private String payee;
    private String memo;
    private String reference;
    private boolean cleared;
    private String txAccount;
    private String category;
    private String aClass;
    private BigDecimal amount;
    private List<Split> splits;

    public Transaction() {
    }

    public Transaction(final long id, final String account, final Date date, final String payee,
            final String memo, final String reference, final boolean cleared, final String txAccount,
            final String category, final String aClass, final BigDecimal amount,
            final List<Split> splits) {
        this.id = id;
        this.account = account;
        this.date = date;
        this.payee = payee;
        this.memo = memo;
        this.reference = reference;
        this.cleared = cleared;
        this.txAccount = txAccount;
        this.category = category;
        this.aClass = aClass;
        this.amount = amount;
        this.splits = splits;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public void setPayee(final String payee) {
        this.payee = payee;
    }

    public void setMemo(final String memo) {
        this.memo = memo;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public void setCleared(final boolean cleared) {
        this.cleared = cleared;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public String getTxAccount() {
        return txAccount;
    }

    public void setTxAccount(final String txAccount) {
        this.txAccount = txAccount;
    }

    public String getAClass() {
        return aClass;
    }

    public void setAClass(final String aClass) {
        this.aClass = aClass;
    }

    public void addSplit(final Split split) {
        if (splits == null) {
            splits = new ArrayList<Split>();
        }
        splits.add(split);
    }

    public void setSplits(final List<Split> splits) {
        this.splits = splits;
    }

    public List<Split> getSplits() {
        return splits;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public boolean isCleared() {
        return cleared;
    }

    public String getReference() {
        return reference;
    }

    public String getMemo() {
        return memo;
    }

    public String getPayee() {
        return payee;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", date=" + date +
                ", payee='" + payee + '\'' +
                ", memo='" + memo + '\'' +
                ", reference='" + reference + '\'' +
                ", cleared=" + cleared +
                ", txAccount='" + txAccount + '\'' +
                ", category='" + category + '\'' +
                ", aClass='" + aClass + '\'' +
                ", amount=" + amount +
                ", splits=" + splits +
                '}';
    }
}
