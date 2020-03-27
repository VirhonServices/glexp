package com.virhon.fintech.glexp.model.page;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.range.PostgreSQLRangeType;
import com.vladmihalcea.hibernate.type.range.Range;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity(name = "PageData")
@TypeDefs({
        @TypeDef(typeClass = PostgreSQLRangeType.class, defaultForType = Range.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class, defaultForType = String.class)
})
public class PageData {
    @Id
    private Long id;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "period", columnDefinition = "tstzrange")
    private Range<ZonedDateTime> period;
    @Column(name = "page", columnDefinition = "jsonb")
    private String page;

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

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
