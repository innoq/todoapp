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

    public void offeneAufgaben() {
        for (int i=0; i<aufgaben.size(); i++) {
            Aufgabe aufgabe = aufgaben.get(i);
            System.out.println(aufgabe);
        }
    }
}

