ToDoApp
===

Einfache ToDo App als Übungsaufgabe für ein Schülerpraktikum (Klasse 10).

Geplanter Funktionsumfang

* Aufgabe erstellen
* offene Aufgaben auflisten
* Aufgabe als erledigt "abhaken"

Geplante Iterationen

* Standalone Java App (`main` Methode)
* HTTP (Web) API
* Web App

## Curl Statements

Offene Aufgabe abfragen

    curl -v http://localhost:8080/aufgaben

Offene Aufgaben im JSON Format

    curl -v -H "Accept: application/json" http://localhost:8080/aufgaben

Aufgabe hinzufügen

    curl -v -X POST -d "bezeichnung=Aufgabe 1" http://localhost:8080/aufgaben

Aufgabe erledigen

    curl -v -X POST -d "erledigt=true" http://localhost:8080/aufgaben/350506724

Undo

    curl -v -X POST -d "erledigt=false" http://localhost:8080/aufgaben/350506724