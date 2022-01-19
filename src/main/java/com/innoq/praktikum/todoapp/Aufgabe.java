package com.innoq.praktikum.todoapp;

public class Aufgabe {
    private String bezeichnung;

    public Aufgabe(String bezeichnung, boolean erledigt) {
        this.bezeichnung = bezeichnung;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " '" + bezeichnung + "' (" + hashCode() + ")";
    }
}

