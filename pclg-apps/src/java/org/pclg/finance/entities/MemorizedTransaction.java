package org.pclg.finance.entities;

import java.math.BigDecimal;

/**
 * MemorizedTransaction<BR>
 *
 * @author El Coyote Cojo
 * @since 18-jun-2010 18:27:32
 */
class MemorizedTransaction {
    private final long id;
    private final String payee;
    private final String memo;
    private final String category;
    private final BigDecimal amount;

    public MemorizedTransaction(final long id, final String payee, final String memo,
            final String category, final BigDecimal amount) {
        this.id = id;
        this.payee = payee;
        this.memo = memo;
        this.category = category;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "MemorizedTransaction{" +
                "id=" + id +
                ", payee='" + payee + '\'' +
                ", memo='" + memo + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                '}';
    }
}
