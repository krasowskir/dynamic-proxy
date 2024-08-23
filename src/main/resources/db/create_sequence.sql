create sequence IF NOT EXISTS teams_seq as integer increment by 1 minvalue 71;
create sequence IF NOT EXISTS players_seq as integer increment by 1 minvalue 177316;
create sequence IF NOT EXISTS leagues_seq as integer increment by 1 minvalue 2140;
create sequence IF NOT EXISTS address_seq as integer increment by 1 minvalue 4;


-- create lives_in table
create table if not exists lives_in(playerId integer, addressId integer);

insert into lives_in (playerId, addressId) SELECT p.id AS playerId, a.id AS addressId
FROM players p, addresses a
WHERE p.id % (SELECT COUNT(*) FROM addresses) = a.id % (SELECT COUNT(*) FROM players)
LIMIT 82727;

alter table lives_in add constraint fk_primkey_players foreign key (playerId) references players(id) on delete cascade on update cascade;
alter table lives_in add constraint fk_primkey_addresses foreign key (addressId) references addresses(id) on delete cascade on update cascade;
alter table lives_in add constraint pk_composite primary key (playerId,addressId);

-- create plays_in table
create table if not exists plays_in(teamId integer, addressId integer);
insert into plays_in (teamId, addressId) select t.id as teamId,a.id as addressId from teams t inner join addresses a on SIMILARITY(t.address,a.street) > 0.4 where a.id > '124525';

alter table plays_in add constraint fk_primkey_teams foreign key (teamId) references teams(id) on delete cascade on update cascade;
alter table plays_in add constraint fk_primkey_addresses foreign key (addressId) references addresses(id) on delete cascade on update cascade;
alter table plays_in add constraint pk_composite_pi primary key (teamId,addressId);