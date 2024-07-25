Vortrag
=========
- hibernate metamodel generator
    - plugin
    - annotations
    - intellij auto-import javax.persistence vs jakarta.persistence problem of IntelliJ cache
- dirty context check in runtime und compile time
- first level cache hibernate entityManager
  - checken von entities da drin
  - wann werden sie dahin aufgenommen
  - was ist der Sinn davon
- hibernate method validator 


Probleme: (Mapping, Validierung, Url Kodierung)
=========
  - wie mappt man auf url Pfade [URL path mapping]
  - wie url encoded man requests? 
    - -d 'name=Manchester United FC' ...// macht curl automatisch
  - wie fügt man verschachtelte Objekte bei?
    - schwierig, da sie erst angelegt und referenziert werden müssten. Deshalb muss der request body im json format vorgelegt werden. 
    - verschachtelte Strukturen lassen sich mit json darstellen
  - wie validiert man Objekte? [Validierung]
    - bean validation
  - wie mappt man Objekte? [Mapping]
    - manuelle converter schreiben oder jackson objectMapper
    - bei servlets und HttpServletRequest werden die Parameter als key, value Paare übertragen, wenn der content-type x-www-url/form-encoded ist
  - wie lädt und installiert man neue Komponenten [Loading & Deployment]
  - wie macht man multithreading [Multithreading]
  - wie injeziert man Konfigurationen [Konfiguration]
    - bzw. wie integriert sich spring in die servlet Spezifikation
  