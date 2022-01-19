package com.innoq.praktikum.todoapp;

import java.util.ArrayList;
import java.util.List;

public class AufgabenListe {
    private List<Aufgabe> aufgaben;

    public AufgabenListe() {
        this.aufgaben = new ArrayList<>();
    }

    public void neueAufgabe(String bezeichnung) {
        Aufgabe aufgabe = new Aufgabe(bezeichnung, false);
        aufgaben.add(aufgabe);
    }

    public List<Aufgabe> offeneAufgaben() {
        return aufgaben;
    }
}

