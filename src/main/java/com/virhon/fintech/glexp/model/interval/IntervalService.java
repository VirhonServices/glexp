package com.virhon.fintech.glexp.model.interval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IntervalService {

    @Autowired
    private IntervalRepository intervalRepository;

    public IntervalService(IntervalRepository intervalRepository) {
        this.intervalRepository = intervalRepository;
    }

    public Optional<IntervalBalance> findById(Long id) {
        return this.intervalRepository.findById(id);
    }

    public void createBalance(IntervalBalance intervalBalance) {
        final IntervalBalance r = this.intervalRepository.save(intervalBalance);
        return;
    }

    public void createBalances(List<IntervalBalance> balances) {
        this.intervalRepository.saveAll(balances);
        return;
    }

    public Long totalCount() {
        return this.intervalRepository.count();
    }

    public void commit() {
    }

    public IntervalBalance getAt(Long accountId, ZonedDateTime at) {
        return this.intervalRepository.getBalanceAt(accountId, at);
    }

    public Iterable<IntervalBalance> getAll() {
        return this.intervalRepository.findAll();
    }

    public IntervalRepository getIntervalRepository() {
        return intervalRepository;
    }

    public List<IntervalBalance> getAccountBalances(Long accountId) {
        return this.intervalRepository.getAccountBalances(accountId);
    }

    public Long getTotalAccounts() {
        return this.intervalRepository.getTotalAccountsNumber();
    }

    public List<Long> getAccountIds() {
        return this.intervalRepository.getAccountsIds();
    }
}
