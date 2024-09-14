# Untersuchung

    - ist jeder Endpunkt beim PlayerServlet sicher (multhithreeading)?
        - wie stellt man es nach? 
            > Transaktionen erzeugen mit player erstellen, player updaten, plaer holen und player löschen
            > Sequenz [ERSTELLEN > UPDATE > HOLEN > LÖSCHEN > HOLEN]
            > komplexe Transaktionen, die mehrere DB Ressourcen umspannen, erzeugen/nutzen (Player - Team | Player - Addresse)
        - zuerst anhand des PlayerServlet race conditions überprüfen
        - Vorgehen Transaktionen:
            - einzelne Tasks, die abgegrenzt und unabhängig ausgeführt werden können, identifizieren
                - werden in Transaktionen ausgeführt
            - tasks haben einen Vor- & Danachzustand (muss bekannt sein)
                - Zustand muss im Task abgeprüft werden
                - Abarbeitung der tasks darf keine Exception werfen
            - Szenarien bilden Kompositionen aus tasks
            - Szenarios, die mit Zwischenergebnissen (erstelltem player) arbeiten aus unabhängigen Tasks zusammenstellen
                - Szenarien arbeiten/lauschen auf exceptions
        - Testdatenquelle?!
            - statische dummy Werte
            - generierte Zufallswerte
            - Vergleich mit Soll-Zustand (Speicherung der Werte zum späteren Ableich im Test)
                - Testdatengenerierung und späterer Vergleich könnte Teil vom Szenario sein
                - Speicherung der Teilergebnisse im ResultStore notwendig
                    - muss concurrent sein
                    - muss auch eine Funktion sein, die vor- oder nach dem Task aufgerufen wird
            
    - gibt es Deadlock Potenzial?
    - gibt es race-conditions im Verbund mit Teams <--> Players Ressourcen
