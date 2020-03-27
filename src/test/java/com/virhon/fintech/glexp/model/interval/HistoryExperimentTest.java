package com.virhon.fintech.glexp.model.interval;

import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.glexp.model.Application;
import com.virhon.fintech.glexp.model.HistoryExperiment;
import com.virhon.fintech.glexp.model.page.PageData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = Application.class)
public class HistoryExperimentTest extends AbstractTestNGSpringContextTests {
    final static Logger LOGGER = Logger.getLogger(HistoryExperimentTest.class);
    public static final int CYCLES = 66;
    public static final long BULK_SIZE = 1500L;
    public static final int BASE = 1000;
//    public static final long ACCOUNT_ID = 10926L;
    public static final long ACCOUNT_ID = 40030L;

    @Autowired
    private HistoryExperiment intervalExperiment;

    @Test(enabled = true)
    void createData() throws LedgerException {
        for (int i = 0; i< CYCLES; i++) {
            final Long bulkSize = BULK_SIZE;
            final Long startFrom = i * bulkSize;
            System.out.println("*********************************************************************************");
            System.out.println("* ITERATION #".concat(String.valueOf(i)));
            System.out.println("*********************************************************************************");
            this.intervalExperiment.createData(bulkSize, BASE, startFrom, 1000);
        }
    }

    @Test
    void createPagesFromExistingBalances500() throws LedgerException {
        createPagesFromExistingBalances(500);
    }

    @Test
    void createPagesFromExistingBalances200() throws LedgerException {
        createPagesFromExistingBalances(200);
    }

    @Test
    void createPagesFromExistingBalances100() throws LedgerException {
        createPagesFromExistingBalances(100);
    }

    void createPagesFromExistingBalances(Integer pageSize) throws LedgerException {
        final List<Long> allIds = this.intervalExperiment.getIntervalService().getAccountIds();
        final Map<Long, List<IntervalBalance>> part = new HashMap<>();
        Long startFrom = 0L;
        List<IntervalBalance> list = new ArrayList<>();
        System.out.println("Starting a conversation process for ".concat(pageSize.toString())
                .concat(" record on one page. Total accounts = ".concat(String.valueOf(allIds.size()))));
        for (int i=0, j=0; i< allIds.size(); i++, j++) {
            final Long accountId = allIds.get(i);
            list = this.intervalExperiment.getIntervalService().getAccountBalances(accountId);
            part.put(accountId, list);
            if (j == BULK_SIZE) {
                final List<PageData> pages = this.intervalExperiment.createPages(part, startFrom, pageSize);
                this.intervalExperiment.savePages(pages);
                startFrom = startFrom + pages.size();
                j = 0;
                part.clear();
                System.out.println(String.valueOf(pages.size()).concat(" pages created ").concat(String.valueOf(i)
                        .concat(" accounts processed")));
            }
        }
        final List<PageData> pages = this.intervalExperiment.createPages(part, startFrom, pageSize);
        this.intervalExperiment.savePages(pages);
        System.out.println(String.valueOf(allIds.size()).concat(" accounts done"));
    }

    @Test
    void getBalancesTest() throws Exception {
//        final ZonedDateTime moment = ZonedDateTime.of(LocalDate.of(2019, 11, 12)
        final ZonedDateTime moment = ZonedDateTime.of(LocalDate.of(2020, 03, 18)
                .atTime(12,0), ZoneId.systemDefault());

        final LocalDateTime beforeInterval = LocalDateTime.now();
        final BigDecimal intervalBalance = this.intervalExperiment.intervalGetAt(ACCOUNT_ID, moment);
        final LocalDateTime afterInterval = LocalDateTime.now();
        final Long intervalDuration = ChronoUnit.MILLIS.between(beforeInterval, afterInterval);

        final LocalDateTime beforePage = LocalDateTime.now();
        final BigDecimal pageBalance = this.intervalExperiment.pageGetAt(ACCOUNT_ID, moment);
        final LocalDateTime afterPage = LocalDateTime.now();
        final Long pageDuration = ChronoUnit.MILLIS.between(beforePage, afterPage);

        System.out.println(intervalDuration - pageDuration);
    }
}