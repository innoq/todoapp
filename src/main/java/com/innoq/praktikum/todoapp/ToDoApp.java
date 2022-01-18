package com.innoq.praktikum.todoapp;

public class ToDoApp {
    public static void main(String[] args) {
        Liste liste = new Liste();
        liste.NeueAufgabe("Test 1");
        liste.NeueAufgabe("Test 2");
        liste.NeueAufgabe("lol");
        liste.offenneAufgaben();
    }
}