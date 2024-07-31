# Server

run app:

```
mvn exec:java -Dexec.mainClass=org.richard.home.App
```

tests:

```
curl -i -XGET http://localhost:8080/api/player?name=Ridge%20Munsy
Player found: Player{id=6783, name='Ridge Munsy', alter=32, position='Attacker', dateOfBirth=1989-07-09, countryOfBirth='null'}


curl -i  -XGET http://localhost:8080/api/player?age=32
```

curl http://localhost:51221/api/player

# local debugging of the servlet

```
export MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
mvn exec:java -Dexec.mainClass=org.richard.home.App
```

## Teams
creating a team:

```
curl -i -XPOST -H "Content-Type: application/x-www-form-urlencoded" -d 'name=Manchester United FC' -d 'budget=1000000' -d 'logoUrl=abcde' -d 'owner=18
78' -d 'tla=MUN' -d 'address=Sir Matt Busby Way Manchester M16 0RA' -d 'leagueId=2021' "http://localhost:8080/api/teams" 
```

deleting a team:

```
curl -i -XDELETE "http://localhost:8080/api/teams?id=80"
```

finding a team:

```
curl -i -X GET "http://localhost:8080/api/teams?id=66"
```

## Players
create a single player
```
curl -i -XPOST "http://localhost:8080/api/players" -H "Content-Type: application/json" -d '{
  "name": "Richard Johanson",
  "age": 33,
  "position": "STRIKER",
  "dateOfBirth": "1991-06-20",
  "countryOfBirth": "GERMANY"
}'
HTTP/1.1 201 Created
Date: Wed, 31 Jul 2024 07:51:37 GMT
Content-Type: application/json
X-Powered-By: Jetty 11
Content-Length: 125
Server: Jetty(11.0.20)

{"id":177320,"name":"Richard Johanson","alter":33,"position":"STRIKER","dateOfBirth":[1991,6,20],"countryOfBirth":"GERMANY"}

```

