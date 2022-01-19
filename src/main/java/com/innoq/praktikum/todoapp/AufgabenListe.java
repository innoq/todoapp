package com.innoq.praktikum.todoapp;

import java.util.ArrayList;
import java.util.List;

public class AufgabenListe {
    private List aufgaben;

    public AufgabenListe() {
        this.aufgaben = new ArrayList();
    }

    public void neueAufgabe(String bezeichnung) {
        Object Aufgabe = new Aufgabe(bezeichnung, false);
        aufgaben.add(bezeichnung);
    }

    public void offeneAufgaben() {
        for (int i=0; i<aufgaben.size(); i++) {
            Object aufgabe = aufgaben.get(i);
            System.out.println(aufgabe);
        }
    }
}

