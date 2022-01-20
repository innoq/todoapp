package com.innoq.praktikum.todoapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.List;

public class ToDoApp {

    private AufgabenListe aufgabenListe;

    public ToDoApp() {
        aufgabenListe = new AufgabenListe();
        aufgabenListe.neueAufgabe("Aufgabe 1");
        aufgabenListe.neueAufgabe("Aufgabe 2");
        aufgabenListe.neueAufgabe("lol");
        aufgabenListe.getAufgabe(1).aufgabeerledigen();
    }

    public static void main(String[] args) throws IOException {
        ToDoApp toDoApp = new ToDoApp();
        toDoApp.start();
    }

    public void start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/aufgaben", this::handleAufgabenRequest);
        httpServer.createContext("/", this::handleRootRequest);
        httpServer.start();
        System.out.println("HTTP Server auf Port 8080 gestartet");
    }

    private void handleRootRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestURI().toString().equals("/")) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        System.out.println(exchange.getRequestURI() + " wurde angefragt");
        exchange.getResponseHeaders().set("Location", "/aufgaben");
        exchange.sendResponseHeaders(302, -1);
    }

    private void handleAufgabenRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestURI().toString().equals("/aufgaben")) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        System.out.println(exchange.getRequestURI() + " wurde angefragt");

        List<Aufgabe> offeneAufgaben = aufgabenListe.offeneAufgaben();
        System.out.println(offeneAufgaben.size() + " offene Aufgaben gefunden");

        exchange.getResponseHeaders().set("Content-type", "text/plain");
        exchange.sendResponseHeaders(200, 0);

        OutputStream outputStream = exchange.getResponseBody();
        PrintWriter writer = new PrintWriter(outputStream);

        for (Aufgabe aufgabe : offeneAufgaben) {
            writer.println(aufgabe);
        }
        writer.close();
    }
}