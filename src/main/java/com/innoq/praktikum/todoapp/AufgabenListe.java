package com.innoq.praktikum.todoapp;

import java.util.ArrayList;
import java.util.List;

public class AufgabenListe {
    private  List<Aufgabe> aufgaben;

    public AufgabenListe() {
        this.aufgaben = new ArrayList<>();
    }


    public Aufgabe getAufgabe (int pos){
        return aufgaben.get(pos);
    }

    public void neueAufgabe(String bezeichnung) {
        Aufgabe aufgabe = new Aufgabe(bezeichnung, false);
        aufgaben.add(aufgabe);
    }

    public List<Aufgabe> offeneAufgaben() {
        List<Aufgabe> offeneAufgaben = new ArrayList<>();
        for (int i = 0; i < aufgaben.size(); i++) {
            Aufgabe aufgabe = aufgaben.get(i);
            if (!aufgabe.isterledigt()) {
                offeneAufgaben.add(aufgabe);
            }
        }
        return offeneAufgaben;
    }
}

