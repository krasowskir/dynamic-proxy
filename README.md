# Server

run app:

```
mvn exec:java -Dexec.mainClass=org.richard.home.App
```

tests:

get player by age
```
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

get player by id
```
curl -i -XGET "http://localhost:8080/api/players/177321" -H "Content-Type: application/json"
HTTP/1.1 200 OK
Date: Wed, 31 Jul 2024 14:13:13 GMT
Content-Type: application/json
X-Powered-By: Jetty 11
Content-Length: 125
Server: Jetty(11.0.20)

{"id":177321,"name":"Richard Johanson","alter":33,"position":"STRIKER","dateOfBirth":[1991,6,20],"countryOfBirth":"GERMANY"}
```
use of path parameters is exclusive to use of request parameters
```
curl -i -XGET "http://localhost:8080/api/players/177321?name=Richard%20Johanson" -H "Content-Type: application/x-www-form-urlencoded" 
HTTP/1.1 400 Bad Request
Date: Thu, 01 Aug 2024 09:06:20 GMT
Content-Type: application/json
X-Powered-By: Jetty 11
Content-Length: 105
Server: Jetty(11.0.20)

If request parameters are used, no path parameters are allowed! URI path contianed a player id: 177321. 
```

get player by name
```
curl -i -XGET "http://localhost:8080/api/players/177321?name=Richard%20Johanson" -H "Content-Type: application/x-www-form-urlencoded" 
HTTP/1.1 200 OK
Date: Wed, 31 Jul 2024 14:27:25 GMT
Content-Type: application/json
X-Powered-By: Jetty 11
Content-Length: 125
Server: Jetty(11.0.20)

{"id":177322,"name":"Richard Johanson","alter":33,"position":"STRIKER","dateOfBirth":[1991,6,20],"countryOfBirth":"GERMANY"}
```

update player by id
```
curl -i -XPUT "http://localhost:8080/api/players/177322" -H "Content-Type: application/json" -d '{"name":"Richard Johanson","age":33,"position":"STRIKER","dateOfBirth":"1991-06-21","countryOfBirth":"SENEGAL"}'
HTTP/1.1 200 OK
Date: Thu, 01 Aug 2024 09:45:40 GMT
Content-Type: application/json
X-Powered-By: Jetty 11
Content-Length: 125
Server: Jetty(11.0.20)

{"id":177322,"name":"Richard Johanson","alter":33,"position":"STRIKER","dateOfBirth":[1991,6,21],"countryOfBirth":"SENEGAL"}
```

delete player by id
```
HTTP/1.1 200 OK
Date: Wed, 31 Jul 2024 14:13:28 GMT
Content-Type: application/json
X-Powered-By: Jetty 11
Content-Length: 37
Server: Jetty(11.0.20)

player: 177321 deleted successfully!
```

## Leagues
create a league
```
curl -i -XPOST "http://localhost:8080/api/leagues" -H "Content-Type: application/json" -d '{"name":"Rich-league","code":"RL1"}'
HTTP/1.1 201 Created
Date: Fri, 02 Aug 2024 07:04:02 GMT
Content-Type: application/json
X-Powered-By: Jetty 11
Content-Length: 46
Server: Jetty(11.0.20)

{"id":2141,"code":"RL1","name":"Rich-league"}
```

get a league by (id, code, name)
```
curl -i -XGET "http://localhost:8080/api/leagues?code=BL1" -H "Content-Type: application/x-www-form-urlencoded"
curl -i -XGET "http://localhost:8080/api/leagues?id=2144" -H "Content-Type: application/x-www-form-urlencoded"
HTTP/1.1 200 OK
Date: Sat, 03 Aug 2024 06:50:11 GMT
Content-Type: application/json
X-Powered-By: Jetty 11
Content-Length: 46
Server: Jetty(11.0.20)

{"id":2144,"code":"RL1","name":"Rich-league"}
```

delete a league
```
curl -i -XDELETE "http://localhost:8080/api/leagues/2143" 
HTTP/1.1 200 OK
Date: Fri, 02 Aug 2024 07:44:04 GMT
Content-Type: application/json
X-Powered-By: Jetty 11
Content-Length: 41
Server: Jetty(11.0.20)

deletion of league: 2143 was successful!
```

update a league
```
curl -i -XPUT "http://localhost:8080/api/leagues/2144" -H "Content-Type: application/json" -d '{"code":"RL0","name":"Richard-league"}'
HTTP/1.1 200 OK
Date: Sat, 03 Aug 2024 06:51:29 GMT
Content-Type: application/json
X-Powered-By: Jetty 11
Content-Length: 49
Server: Jetty(11.0.20)

{"id":2144,"code":"RL0","name":"Richard-league"}
```

