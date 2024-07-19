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