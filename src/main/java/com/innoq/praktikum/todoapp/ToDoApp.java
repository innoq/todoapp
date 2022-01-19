package com.innoq.praktikum.todoapp;

import java.util.List;

public class ToDoApp {
    public static void main(String[] args) {
        AufgabenListe aufgabenListe = new AufgabenListe();
        aufgabenListe.neueAufgabe("Test 1");
        aufgabenListe.neueAufgabe("Test 2");
        aufgabenListe.neueAufgabe("lol");


        aufgabenListe.getAufgabe(1).aufgabeerledigen();

        List<Aufgabe> offeneAufgaben = aufgabenListe.offeneAufgaben();
        for (int i = 0; i < offeneAufgaben.size(); i++) {
            Aufgabe aufgabe = offeneAufgaben.get(i);

            System.out.println(aufgabe);

        }
    }
}