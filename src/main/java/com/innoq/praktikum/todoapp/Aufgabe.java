package com.innoq.praktikum.todoapp;

public class Aufgabe {
    private String bezeichnung;
    private boolean erledigt;
    public Aufgabe(String bezeichnung, boolean erledigt) {
        this.bezeichnung = bezeichnung;
        this.erledigt = erledigt;
    }

    public int getId() {
        return hashCode();
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
