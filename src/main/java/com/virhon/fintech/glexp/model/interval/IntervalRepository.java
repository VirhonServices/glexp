package com.virhon.fintech.glexp.model.interval;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface IntervalRepository extends PagingAndSortingRepository<IntervalBalance, Long> {
    @Query(value = "select * from interval_balance ib where ib.account_id = :accountId and ib.period @> to_timestamp(:at,'YYYY-MM-DD\"T\"HH24:MI:SS:MS\"Z\"')",
        nativeQuery = true)
    IntervalBalance getBalanceAt(@Param("accountId") Long accountId, @Param("at") ZonedDateTime at);

    @Query(value = "select * from interval_balance ib where ib.account_id = :accountId", nativeQuery = true)
    List<IntervalBalance> getAccountBalances(@Param("accountId") Long accountId);

    @Query(value = "select count(distinct account_id) from interval_balance", nativeQuery = true)
    Long getTotalAccountsNumber();

    @Query(value = "select distinct account_id from interval_balance order by 1", nativeQuery = true)
    List<Long> getAccountsIds();
}
