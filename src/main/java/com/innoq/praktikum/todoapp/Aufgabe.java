package com.innoq.praktikum.todoapp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Aufgabe {
    private final int id;
    private final String besitzer;
    private final String bezeichnung;
    private final LocalDateTime erstellzeit;
    private boolean erledigt;

    public Aufgabe(int id, String besitzer, String bezeichnung) {
        this.id = id;
        this.besitzer = besitzer;
        this.bezeichnung = bezeichnung;
        this.erstellzeit = LocalDateTime.now();
        this.erledigt = false;
    }

    public Aufgabe(int id, String besitzer, String bezeichnung, LocalDateTime erstellzeit, boolean erledigt) {
        this.id = id;
        this.besitzer = besitzer;
        this.bezeichnung = bezeichnung;
        this.erstellzeit = erstellzeit;
        this.erledigt = erledigt;
    }

    public LocalDateTime getErstellzeit() {
        return erstellzeit;
    }

    @SuppressWarnings("unused")
    public String getFormattedErstellzeit() {
        return erstellzeit.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public int getId() {
        return id;
    }

    public String getBesitzer() {
        return besitzer;
    }

    public void aufgabeerledigen() {
        this.erledigt = true;
    }

    public void aufgabeundo() {
        this.erledigt = false;
    }

    public boolean isErledigt() {
        return this.erledigt;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " '" + bezeichnung + "' (" + getId() + ")" +erledigt;
    }
}
