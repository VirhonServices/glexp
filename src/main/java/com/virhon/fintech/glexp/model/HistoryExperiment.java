package com.virhon.fintech.glexp.model;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Page;
import com.virhon.fintech.gl.model.Transfer;
import com.virhon.fintech.glexp.model.interval.IntervalBalance;
import com.virhon.fintech.glexp.model.interval.IntervalService;
import com.virhon.fintech.glexp.model.page.PageData;
import com.virhon.fintech.glexp.model.page.PageService;
import com.vladmihalcea.hibernate.type.range.Range;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class HistoryExperiment {
    final static Logger LOGGER = Logger.getLogger(HistoryExperiment.class);
    public static final int OFFSET_LIMIT = 6000;
    private Random random = new Random();
    private ZonedDateTime dayZero = ZonedDateTime.of(2000, 01, 01, 0, 0,
            0, 0, ZoneId.systemDefault());
    private ZonedDateTime now = ZonedDateTime.now();
    private Gson gson = GsonConverter.create();

    @Autowired
    private IntervalService intervalService;

    @Autowired
    private PageService pageService;

    private Map<Long, List<IntervalBalance>> group(List<IntervalBalance> allBalances) {
        return allBalances.stream().collect(Collectors.groupingBy(IntervalBalance::getAccountId));
    }

    public List<PageData> createPages(Map<Long, List<IntervalBalance>> allBalances, Long startFrom, Integer pageSize) throws LedgerException {
        final List<PageData> result = new ArrayList<>();
        if (allBalances.isEmpty()) {
            return result;
        }
        Long id = startFrom;
        final List<Long> allAccountsIds = new ArrayList<>(allBalances.keySet());
        for(int b=0;b<allAccountsIds.size();b++) {
            final Long accountId = allAccountsIds.get(b);
            final List<IntervalBalance> balances = allBalances.get(accountId);
            Integer pagesCount = balances.size() / pageSize;
            if (balances.size() % pageSize > 0) {
                pagesCount++;
            }
            Integer cur = 0;
            BigDecimal startBalance = BigDecimal.ZERO;
            IntervalBalance balCur = balances.get(cur);
            for (int i = 0; i < pagesCount; i++, id++) {
                BigDecimal finishBalance = new BigDecimal(startBalance.byteValueExact());
                final Page page = Page.create(UUID.randomUUID().toString(), balCur.getPeriod().lower(), balCur.getPeriod().lower().toLocalDate(), startBalance, startBalance);
                ZonedDateTime lower = balCur.getPeriod().lower();
                ZonedDateTime upper = lower;
                for (int j = 0; j < pageSize && cur < balances.size(); j++, cur++) {
                    balCur = balances.get(cur);
                    upper = balCur.getPeriod().lower();
                    final Transfer tr = new Transfer();
                    tr.setTransferUuid(UUID.randomUUID().toString());
                    tr.setTransferRef("DUMMY-REF");
                    tr.setClientUuid(UUID.randomUUID().toString());
                    tr.setClientCustomerId("CL-CUST-ID");
                    tr.setPostedAt(balCur.getPeriod().lower());
                    final BigDecimal amount = new BigDecimal(random.nextInt(100)).divide(new BigDecimal(10));
                    tr.setAmount(amount);
                    tr.setLocalAmount(amount);
                    tr.setReportedOn(balCur.getPeriod().lower().toLocalDate());
                    tr.setDescription("some description");
                    tr.setDebitPageUuid("debit page uuid");
                    tr.setCreditPageUuid("credit page uuid");
                    page.addTransfer(tr);
                    finishBalance = finishBalance.add(amount);
                }
                final PageData pageData = new PageData();
                pageData.setId(id);
                pageData.setAccountId(accountId);
                final Range<ZonedDateTime> range = Range.closed(lower, upper);
                pageData.setPeriod(range);
                pageData.setPage(gson.toJson(page));
                result.add(pageData);
            }
            LOGGER.info("Account #".concat(String.valueOf(b)).concat(" ").concat(String.valueOf(pagesCount)).concat(" created"));
        }
        return result;
    }

    public List<IntervalBalance> createData(Long accountsNumber, Integer base, Long startFrom, Integer pageSize) throws LedgerException {
        final List<Long> accountsIds = new ArrayList<>();
        final List<IntervalBalance> balances = new ArrayList<>();
        Long total = 0L;
        Long id = this.intervalService.totalCount();
        for (Long accountId=startFrom;accountId<startFrom+accountsNumber;accountId++) {
            LOGGER.info("Generating account #".concat(accountId.toString()));
            // 1. define an individual start date
            final Integer offset = this.random.nextInt(OFFSET_LIMIT);
            final ZonedDateTime begin = this.dayZero.plusDays(offset).plusSeconds(offset);
            ZonedDateTime end = begin;
            // 2. calculate number of days between the initial day and today
            final Long seconds = ChronoUnit.SECONDS.between(begin, now);
            // 3. randomize a number of balances
            final Integer balCount = this.random.nextInt(base) + 1;
            // 4. calculating balances durations
            final List<Long> durations = new ArrayList<>();
            for (int j=0;j<balCount;j++) {
                Long dur = seconds;
                Integer selected = 0;
                if (j!=0L) {
                    selected = this.random.nextInt(j);
                    dur = durations.get(selected)/2;
                    durations.set(selected, dur);
                }
                durations.add(dur);
            }
            // 5. generating balances
            for (int j=0;j<balCount;j++) {
                final Long dur = durations.get(j);
                ZonedDateTime beg = end;
                end = beg.plusSeconds(dur);
                final Range<ZonedDateTime> range = Range.closed(beg, end);
                final BigDecimal balance = new BigDecimal((1 + j * this.random.nextInt(1000)))
                        .divide(new BigDecimal("100.00"));
                final IntervalBalance ib = new IntervalBalance();
                ib.setId(id);
                ib.setAccountId(accountId);
                ib.setPeriod(range);
                ib.setBalance(balance);
                balances.add(ib);
                id = id + 1;
            }
            total = total + balCount;
            LOGGER.info(balCount.toString().concat(" balances have been generated successfully"));
        }
        this.intervalService.createBalances(balances);
        final List<PageData> pages = createPages(group(balances), this.pageService.totalCount(), pageSize);
        this.pageService.createBalances(pages);
        LOGGER.info("Done. ".concat(accountsNumber.toString()).concat(" accounts, ").concat(total.toString())
                .concat(" balances generated, ").concat(String.valueOf(pages.size())).concat(" pages created"));
        return balances;
    }

    public Iterable<IntervalBalance> getBalances() {
        return this.intervalService.getAll();
    }

    public void savePages(final List<PageData> pages) {
        this.pageService.createBalances(pages);
    }

    public void createAndSavePages(List<IntervalBalance> balances, Integer pageSize) throws LedgerException {
        final List<PageData> pages = createPages(group(balances),this.pageService.totalCount(), pageSize);
        this.pageService.createBalances(pages);
    }

   public BigDecimal intervalGetAt(Long accountId, ZonedDateTime at) throws Exception {
        final IntervalBalance ib = this.intervalService.getAt(accountId, at);
        if (ib == null) {
            throw new Exception("Interval for the date ".concat(at.toString())
                .concat(" or the account id=".concat(accountId.toString()).concat(" does not exist")));
        }
        return ib.getBalance();
    }

    public BigDecimal pageGetAt(Long accountId, ZonedDateTime at) throws Exception {
        final PageData pageData = this.pageService.getAt(accountId, at);
        if (pageData == null) {
            throw new Exception("Page for the date ".concat(at.toString())
                    .concat(" or the account id=".concat(accountId.toString()).concat(" does not exist")));
        }
        final Page page = this.gson.fromJson(pageData.getPage(), Page.class);
        return page.getBalanceAt(at);
    }

    public Long totalBalancesCount() {
        return this.intervalService.totalCount();
    }

    public IntervalService getIntervalService() {
        return intervalService;
    }

    public Long totalAccounts() {
        return this.intervalService.getTotalAccounts();
    }
}
