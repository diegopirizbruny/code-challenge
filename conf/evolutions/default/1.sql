# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table players (
  id                            varchar(255) not null,
  balance                       decimal(38),
  constraint pk_players primary key (id)
);

create table transactions (
  id                            varchar(255) not null,
  player_id                     varchar(255) not null,
  type                          varchar(6),
  amount                        decimal(38),
  when_created                  timestamp not null,
  constraint ck_transactions_type check ( type in ('CREDIT','DEBIT')),
  constraint pk_transactions primary key (id)
);

create index ix_transactions_player_id on transactions (player_id);
alter table transactions add constraint fk_transactions_player_id foreign key (player_id) references players (id) on delete restrict on update restrict;


# --- !Downs

alter table transactions drop constraint if exists fk_transactions_player_id;
drop index if exists ix_transactions_player_id;

drop table if exists players;

drop table if exists transactions;

