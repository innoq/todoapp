package com.innoq.praktikum.todoapp;

public class ToDoApp {
    public static void main(String[] args) {
        AufgabenListe aufgabenListe = new AufgabenListe();
        aufgabenListe.neueAufgabe("Test 1");
        aufgabenListe.neueAufgabe("Test 2");
        aufgabenListe.neueAufgabe("lol");
        aufgabenListe.offeneAufgaben();
    }
}