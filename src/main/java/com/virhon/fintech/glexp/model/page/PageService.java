package com.virhon.fintech.glexp.model.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    private PageRepository pageRepository;

    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public Optional<PageData> findById(Long id) {
        return this.pageRepository.findById(id);
    }

    public void createBalance(PageData balance) {
        final PageData r = this.pageRepository.save(balance);
        return;
    }

    public void createBalances(List<PageData> balances) {
        this.pageRepository.saveAll(balances);
        return;
    }

    public Long totalCount() {
        return this.pageRepository.count();
    }

    public PageData getAt(Long accountId, ZonedDateTime at) {
        return this.pageRepository.getBalanceAt(accountId, at);
    }
}
