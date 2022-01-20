package com.innoq.praktikum.todoapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ToDoApp {

    private AufgabenListe aufgabenListe;

    public ToDoApp() {
        aufgabenListe = new AufgabenListe();
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
        if (exchange.getRequestURI().toString().equals("/aufgaben")) {
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

                Map<String, String> formData = readFormData(exchange);
                aufgabenListe.neueAufgabe(formData.get("bezeichnung"));

                exchange.getResponseHeaders().set("Location", "/aufgaben");
                exchange.sendResponseHeaders(201, -1);
            }

        } else if (exchange.getRequestURI().toString().matches("/aufgaben/\\d+")) {
            System.out.println("POST " + exchange.getRequestURI());

            int id = parseId(exchange.getRequestURI());
            Map<String, String> formData = readFormData(exchange);

            if (!formData.containsKey("erledigt")) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            Aufgabe zuAenderndeAufgabe = null;
            for (Aufgabe aufgabe : aufgabenListe.alleAufgaben()) {
                if (aufgabe.hashCode() == id) {
                    zuAenderndeAufgabe = aufgabe;
                    break;
                }
            }

            if (zuAenderndeAufgabe == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            if (formData.get("erledigt").equals("true")) {
                zuAenderndeAufgabe.aufgabeerledigen();
            } else if (formData.get("erledigt").equals("false")) {
                zuAenderndeAufgabe.aufgabeundo();
            } else {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            exchange.getResponseHeaders().set("Location", "/aufgaben");
            exchange.sendResponseHeaders(302, -1);

        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private Map<String, String> readFormData(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String daten = reader.readLine();
        Map<String, String> formData = parseFormData(daten);
        return formData;
    }

    private int parseId(URI uri) {
        return Integer.parseInt(uri.toString().substring(uri.toString().lastIndexOf("/") + 1));
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