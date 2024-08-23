# restore backup

schema must exist beforehands and should be created with schema.sql

create the selective dump:
```
pg_dump -d playerdb -U richard -h 192.168.0.11 -p 5432 -F c -t addresses -t coaches -t league -t players -t teams -t trainers -t under_contract -t lives_in -v > playerdb_players_dump_uncompressed.dump
```

restore the dump
```
docker cp database_dump/playerdb_players_dump_uncompressed.dump 6f5a203066ef:/
pg_restore -U richard -c -d playerdb /playerdb_players_dump_uncompressed.dump
```