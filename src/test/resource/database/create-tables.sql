drop table interval_balance;
create table interval_balance(
  id          bigint  primary key,
  account_id  bigint  not null,
  period      tstzrange not null,
  balance     numeric(44,22) not null
);

drop index idx_interval_ranges;
create index idx_interval_ranges on interval_balance using gist(account_id, period);

drop table page_data;
create table page_data(
  id          bigint  primary key,
  account_id  bigint  not null,
  period      tstzrange not null,
  page        jsonb not null
);

drop index idx_page_ranges;
create index idx_page_ranges on page_data using gist(account_id, period);

alter table interval_balance rename to interval_balance_1K;
alter index idx_interval_ranges rename to idx_interval_ranges_1K;
alter table page_data rename to page_data_50M200;
alter index idx_page_ranges rename to idx_page_ranges_50M200;

// select * from (select account_id id, count(*) cnt from page_data group by account_id) r where r.cnt>1 order by 1;
