## ToDo's

- Transaktionen synchronisieren und im cluster betrieb vertesten
- connection pool ausprobieren und grafisch aufzeigen
- Threadpool überwachen
- multithread servlets schreiben, Instanziierung, debugging und überwachung
- asynchrone non-blocking Servlets [servlet-api](https://jakarta.ee/specifications/servlet/5.0/jakarta-servlet-spec-5.0#asynchronous-processing)
  - 
- dispatcher mit jsp content negotiation
- errorhandler schreiben
- java moduls und native image (GraalVm) ausprobieren!
- Vererbung mit TopPlayer und Amateurplayer
- Buffering um Performance zu steigern ausprobieren
- WAF ausprobieren
- OpenIdConnect & oauth2 integration implementieren


## Erledigt:

- jpa-model gen mit hibernate 6 und java 17
- dirty check mit plugin
- embedded jetty ohne artifakt
- jetty 11 und servlet 5 specification
- Redis & hibernate cache integration



## Probleme:
- wie stoppt man maven-exec prozesse? -> es gibt kein stop goal!
  - mit jetty-maven plugin gibt es das Problem nicht!
- wie startet man die Application in einem anderen thread, separat vom maven lifecycle?
  - exec-maven-plugin führt alles nur in einer VM aus. -> forkMode oder jetty plugin
- wie schaltet man cache für integrationsTests aus?