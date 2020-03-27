package com.virhon.fintech.glexp.model.page;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface PageRepository extends CrudRepository<PageData, Long> {
    @Query(value = "select * from page_data pb where pb.account_id = :accountId and pb.period @> to_timestamp(:at,'YYYY-MM-DD\"T\"HH24:MI:SS:MS\"Z\"')",
            nativeQuery = true)
    PageData getBalanceAt(@Param("accountId") Long accountId, @Param("at") ZonedDateTime at);
}
