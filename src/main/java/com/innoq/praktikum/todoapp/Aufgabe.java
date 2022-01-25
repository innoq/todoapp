package com.innoq.praktikum.todoapp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Aufgabe {
    private int id;
    private String bezeichnung;
    private boolean erledigt;
    private  LocalDateTime  erstellzeit;

    public Aufgabe(int id, String bezeichnung, boolean erledigt) {
        this.id = id;
        this.bezeichnung = bezeichnung;
        this.erledigt = erledigt;
        this.erstellzeit = LocalDateTime.now();
    }

    public Aufgabe(int id, String bezeichnung, boolean erledigt, LocalDateTime erstellzeit) {
        this.id = id;
        this.bezeichnung = bezeichnung;
        this.erledigt = erledigt;
        this.erstellzeit = erstellzeit;
    }

    public LocalDateTime getErstellzeit() {
        return erstellzeit;
    }

    public String getFormattedErstellzeit() {
        return erstellzeit.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }


    public String getBezeichnung() {
        return bezeichnung;
    }

    public int getId() {
        return id;
    }

    public void aufgabeerledigen() {
        this.erledigt = true;
    }
    public void aufgabeundo() {
        this.erledigt = false;
    }
    public boolean isterledigt(){
        return this.erledigt;
    }
    @Override
    public String toString() {
        return getClass().getSimpleName() + " '" + bezeichnung + "' (" + getId() + ")" +erledigt;
    }
}
