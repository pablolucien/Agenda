package org.pclg.finance.entities;

import java.math.BigDecimal;

/**
 * Split<BR>
 *
 * @author El Coyote Cojo
 * @since 18-jun-2010 18:27:32
 */
public class Split {
    private long transactionId;
    private long id;
    private String txAccount;
    private String category;
    private String aClass;

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

    private String memo;
    private BigDecimal amount;

    public Split() {
    }

    public Split(final long transactionId, final long id, final String category,
            final String memo, final BigDecimal amount) {
        this.id = id;
        this.transactionId = transactionId;
        this.category = category;
        this.memo = memo;
        this.amount = amount;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(final long transactionId) {
        this.transactionId = transactionId;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(final String memo) {
        this.memo = memo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Split{" +
                "transactionId=" + transactionId +
                ", id=" + id +
                ", txAccount='" + txAccount + '\'' +
                ", category='" + category + '\'' +
                ", aClass='" + aClass + '\'' +
                ", memo='" + memo + '\'' +
                ", amount=" + amount +
                '}';
    }
}