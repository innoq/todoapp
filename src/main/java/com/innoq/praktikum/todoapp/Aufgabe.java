package com.innoq.praktikum.todoapp;

public class Aufgabe {
    private String bezeichnung;
    private boolean erledigt;
    public Aufgabe(String bezeichnung, boolean erledigt) {
        this.bezeichnung = bezeichnung;
        this.erledigt = erledigt;
    }

    public void aufgabeerledigen() {
        this.erledigt = true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " '" + bezeichnung + "' (" + hashCode() + ")" +erledigt;
    }
}

