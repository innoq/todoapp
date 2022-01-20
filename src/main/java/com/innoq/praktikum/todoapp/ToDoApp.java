package com.innoq.praktikum.todoapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        if (exchange.getRequestMethod().equals("GET")) {
            System.out.println("GET " + exchange.getRequestURI());

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

        } else if (exchange.getRequestMethod().equals("POST")) {
            System.out.println("POST " + exchange.getRequestURI());

            InputStream inputStream = exchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String daten = reader.readLine();
            Map<String, String> formData = parseFormData(daten);
            aufgabenListe.neueAufgabe(formData.get("bezeichnung"));

            exchange.getResponseHeaders().set("Location", "/aufgaben");
            exchange.sendResponseHeaders(201, -1);
        }

    }

    private Map<String, String> parseFormData(String body) {
        if (body == null || body.isBlank()) {
            return Collections.emptyMap();
        }

        // body: startdatum=2022-01-01&enddatum=2022-01-22&bezeichnung=Urlaub

        Map<String, String> map = new HashMap<>();
        var paramTokenizer = new StringTokenizer(body, "&");
        // startdatum=2022-01-01
        // enddatum=2022-01-22
        // bezeichnung=Urlaub

        while (paramTokenizer.hasMoreTokens()) {
            var nextParam = paramTokenizer.nextToken();
            var keyValue = nextParam.split("=");
            // bezeichnung
            // Urlaub

            var key = keyValue[0];
            if (keyValue.length > 1) {
                var value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                map.put(key, value);
            } else {
                map.put(key, "");
            }
        }
        return map;
    }


}