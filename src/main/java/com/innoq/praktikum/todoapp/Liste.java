package com.innoq.praktikum.todoapp;

import java.util.ArrayList;
import java.util.List;

public class Liste {
    private List aufgaben;

    public Liste() {
        this.aufgaben = new ArrayList();
    }

    public void NeueAufgabe(String bezeichnung) {
        Object Aufgabe = new Aufgabe(bezeichnung, false);
        aufgaben.add(bezeichnung);
    }

    public void offenneAufgaben() {
        for (int i=0; i<aufgaben.size(); i++) {
            Object aufgabe = aufgaben.get(i);
            System.out.println(aufgabe);
        }
    }
}

