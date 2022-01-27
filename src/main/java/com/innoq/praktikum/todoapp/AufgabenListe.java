package com.innoq.praktikum.todoapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AufgabenListe {

    private final Connection dbConnection;

    public AufgabenListe() {
        this.dbConnection = initDbConnection();
    }

    public void neueAufgabeForUser(String user, String bezeichnung) {
        int id = findNextId();
        Aufgabe aufgabe = new Aufgabe(id, user, bezeichnung);
        insertIntoDatabase(aufgabe);
        System.out.println("Neue Aufgaben '" + bezeichnung + "' (id " + id + ") hinzugefügt");
    }

    public List<Aufgabe> findOffeneAufgabenForUser(String user) {
        return readOffeneAufgabenFromDatabase(user);
    }

    public Aufgabe findAufgabeByUserAndId(String user, int id) {
        return readAufgabeFromDatabase(user, id);
    }

    public void alsErledigtAbhaken(Aufgabe aufgabe) {
        aufgabe.aufgabeerledigen();
        updateAufgabeInDatabase(aufgabe);
        System.out.println("Aufgabe '" + aufgabe.getBezeichnung() + "' als erledigt abgehakt");
    }

    public void aufNichtErledigtZuruecksetzen(Aufgabe aufgabe) {
        aufgabe.aufgabeundo();
        updateAufgabeInDatabase(aufgabe);
        System.out.println("Aufgabe '" + aufgabe.getBezeichnung() + "' auf nicht-erledigt zurückgesetzt");
    }

    // Database access

    private Connection initDbConnection() {
        String url = Objects.requireNonNull(System.getenv("DATABASE_URL"), "Environment Variable DATABASE_URL nicht gesetzt");
        String username = Objects.requireNonNull(System.getenv("DATABASE_USERNAME"), "Environment Variable DATABASE_USERNAME nicht gesetzt");
        String password = Objects.requireNonNull(System.getenv("DATABASE_PASSWORD"), "Environment Variable DATABASE_PASSWORD nicht gesetzt");

        try {
            var connection = DriverManager.getConnection(url, username, password);
            System.out.println("Verbindung zu DB '" + url + "' hergestellt");
            return connection;
        } catch (SQLException exc) {
            throw new RuntimeException("Verbindung zu DB '" + url + "' fehlgeschlagen", exc);
        }
    }

    private int findNextId() {
        try (var statement = this.dbConnection.prepareStatement(
                "SELECT MAX(id) + 1 FROM aufgaben")) {
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 1;
        } catch (SQLException exc) {
            throw new RuntimeException("Lesen der nächsten Aufgaben ID aus DB fehlgeschlagen", exc);
        }
    }

    private void insertIntoDatabase(Aufgabe aufgabe) {
        try (var statement = this.dbConnection.prepareStatement(
                "INSERT INTO aufgaben (id, besitzer, bezeichnung, erstellzeit, erledigt) VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, aufgabe.getId());
            statement.setString(2, aufgabe.getBesitzer());
            statement.setString(3, aufgabe.getBezeichnung());
            statement.setTimestamp(4, Timestamp.valueOf(aufgabe.getErstellzeit()));
            statement.setBoolean(5, aufgabe.isErledigt());
            statement.executeUpdate();
        } catch (SQLException exc) {
            throw new RuntimeException("Schreiben der Aufgabe " + aufgabe.getId() + " in DB fehlgeschlagen", exc);
        }
    }

    private ArrayList<Aufgabe> readOffeneAufgabenFromDatabase(String user) {
        try (var statement = this.dbConnection.prepareStatement(
                "SELECT id, besitzer, bezeichnung, erstellzeit, erledigt FROM aufgaben WHERE besitzer = ? AND erledigt = false")) {
            statement.setString(1, user);
            var resultSet = statement.executeQuery();
            var aufgaben = new ArrayList<Aufgabe>();
            while (resultSet.next()) {
                var id = resultSet.getInt("id");
                var besitzer = resultSet.getString("besitzer");
                var bezeichnung = resultSet.getString("bezeichnung");
                var erstellzeit = resultSet.getTimestamp("erstellzeit").toLocalDateTime();
                var erledigt = resultSet.getBoolean("erledigt");
                aufgaben.add(new Aufgabe(id, besitzer, bezeichnung, erstellzeit, erledigt));
            }
            return aufgaben;
        } catch (SQLException exc) {
            throw new RuntimeException("Lesen der offenen Aufgaben aus DB fehlgeschlagen", exc);
        }
    }

    private Aufgabe readAufgabeFromDatabase(String user, int id) {
        try (var statement = this.dbConnection.prepareStatement(
                "SELECT id, besitzer, bezeichnung, erstellzeit, erledigt FROM aufgaben WHERE id = ? AND besitzer = ?")) {
            statement.setInt(1, id);
            statement.setString(2, user);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var idFromDB = resultSet.getInt("id");
                var besitzer = resultSet.getString("besitzer");
                var bezeichnung = resultSet.getString("bezeichnung");
                var erstellzeit = resultSet.getTimestamp("erstellzeit").toLocalDateTime();
                var erledigt = resultSet.getBoolean("erledigt");
                return new Aufgabe(idFromDB, besitzer, bezeichnung, erstellzeit, erledigt);
            }
            return null;
        } catch (SQLException exc) {
            throw new RuntimeException("Lesen der Aufgabe " + id + " aus DB fehlgeschlagen", exc);
        }
    }

    private void updateAufgabeInDatabase(Aufgabe aufgabe) {
        try (var statement = this.dbConnection.prepareStatement(
                "UPDATE aufgaben SET erledigt = ? WHERE id = ?")) {
            statement.setBoolean(1, aufgabe.isErledigt());
            statement.setInt(2, aufgabe.getId());
            statement.executeUpdate();
        } catch (SQLException exc) {
            throw new RuntimeException("Abhaken der Aufgabe " + aufgabe.getId() + " in DB fehlgeschlagen", exc);
        }
    }
}

