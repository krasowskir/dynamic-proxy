# Untersuchung

    - ist jeder Endpunkt beim PlayerServlet sicher (multhithreeading)?
        - wie stellt man es nach? 
            > Transaktionen erzeugen mit player erstellen, player updaten, plaer holen und player löschen
            > Sequenz [ERSTELLEN > UPDATE > HOLEN > LÖSCHEN > HOLEN]
            > komplexe Transaktionen, die mehrere DB Ressourcen umspannen, erzeugen/nutzen (Player - Team | Player - Addresse)
        - zuerst anhand des PlayerServlet race conditions überprüfen
    - gibt es Deadlock Potenzial?
    - gibt es race-conditions im Verbund mit Teams <--> Players Ressourcen