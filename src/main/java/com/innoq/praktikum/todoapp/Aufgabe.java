package com.innoq.praktikum.todoapp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Aufgabe {
    private final int id;
    private final String besitzer;
    private final String bezeichnung;
    private final LocalDateTime erstellzeit;
    private LocalDateTime endzeit;
    private LocalDate deadline;
    private boolean erledigt;

    public Aufgabe(int id, String besitzer, String bezeichnung, LocalDate deadline) {
        this.id = id;
        this.besitzer = besitzer;
        this.bezeichnung = bezeichnung;
        this.erstellzeit = LocalDateTime.now();
        this.erledigt = false;
        this.deadline = deadline;
        this.endzeit = null;
    }

    public Aufgabe(int id, String besitzer, String bezeichnung, LocalDateTime erstellzeit, LocalDate deadline, boolean erledigt, LocalDateTime endzeit) {
        this.id = id;
        this.besitzer = besitzer;
        this.bezeichnung = bezeichnung;
        this.erstellzeit = erstellzeit;
        this.deadline = deadline;
        this.erledigt = erledigt;
        this.endzeit = endzeit;
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

    public String getFormattedDeadline() {
        return deadline.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public int getId() {
        return id;
    }

    public String getBesitzer() {
        return besitzer;
    }

    public void aufgabeerledigen() {
        this.erledigt = true;
        this.endzeit = LocalDateTime.now();
    }

    public LocalDateTime getEndzeit() {
        return endzeit;
    }

    public void aufgabeundo() {
        this.erledigt = false;
        this.endzeit = null;
    }

    public boolean isErledigt() {
        return this.erledigt;
    }

    public boolean isUeberfaellig() {
        if (deadline == null){
            return false;
        }
        return LocalDate.now().isAfter(deadline);
    }

    public boolean isHeuteFaellig() {
        if (deadline == null) {
            return false;
        }
        return LocalDate.now().isEqual(deadline);

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " '" + bezeichnung + "' (" + getId() + ")" + erledigt;
    }
}
