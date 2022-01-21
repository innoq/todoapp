package com.innoq.praktikum.todoapp;

import java.util.ArrayList;
import java.util.List;

public class AufgabenListe {
    private  List<Aufgabe> aufgaben;

    public AufgabenListe() {
        this.aufgaben = new ArrayList<>();
    }

    public void neueAufgabe(String bezeichnung) {
        Aufgabe aufgabe = new Aufgabe(bezeichnung, false);
        aufgaben.add(aufgabe);
    }

    public List<Aufgabe> offeneAufgaben() {
        List<Aufgabe> offeneAufgaben = new ArrayList<>();
        for (Aufgabe aufgabe : aufgaben) {
            if (!aufgabe.isterledigt()) {
                offeneAufgaben.add(aufgabe);
            }
        }
        return offeneAufgaben;
    }

    public Aufgabe findAufgabeById(int id) {
        for (Aufgabe aufgabe : aufgaben) {
            if (aufgabe.getId() == id) {
                return aufgabe;
            }
        }
        return null;
    }
}

