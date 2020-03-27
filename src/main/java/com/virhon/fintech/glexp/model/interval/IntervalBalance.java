package com.virhon.fintech.glexp.model.interval;

import com.vladmihalcea.hibernate.type.range.PostgreSQLRangeType;
import com.vladmihalcea.hibernate.type.range.Range;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity(name = "IntervalBalance")
@TypeDef(typeClass = PostgreSQLRangeType.class,defaultForType = Range.class)
public class IntervalBalance {
    @Id
    private Long id;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "period", columnDefinition = "tstzrange")
    private Range<ZonedDateTime> period;
    @Column(name = "balance")
    private BigDecimal balance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Range<ZonedDateTime> getPeriod() {
        return period;
    }

    public void setPeriod(Range<ZonedDateTime> period) {
        this.period = period;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
