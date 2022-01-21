package com.innoq.praktikum.todoapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
            sendEmptyResponse(exchange, 404);
            return;
        }

        System.out.println(exchange.getRequestURI() + " wurde angefragt");
        redirectToAufgaben(exchange);
    }

    private void handleAufgabenRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().toString().equals("/aufgaben")) {
            handleAufgabenListeRequest(exchange);
        } else if (exchange.getRequestURI().toString().matches("/aufgaben/\\d+")) {
            handleEinzelneAufgabeRequest(exchange);
        } else {
            sendEmptyResponse(exchange, 404);
        }
    }

    private void handleEinzelneAufgabeRequest(HttpExchange exchange) throws IOException {
        System.out.println("POST " + exchange.getRequestURI());

        int id = parseId(exchange.getRequestURI());
        Map<String, String> formData = readFormData(exchange);

        if (!formData.containsKey("erledigt")) {
            sendEmptyResponse(exchange, 400);
            return;
        }

        Aufgabe zuAenderndeAufgabe = aufgabenListe.findAufgabeById(id);
        if (zuAenderndeAufgabe == null) {
            sendEmptyResponse(exchange, 404);
            return;
        }

        if (formData.get("erledigt").equals("true")) {
            zuAenderndeAufgabe.aufgabeerledigen();
        } else if (formData.get("erledigt").equals("false")) {
            zuAenderndeAufgabe.aufgabeundo();
        } else {
            sendEmptyResponse(exchange, 400);
            return;
        }

        redirectToAufgaben(exchange);
    }

    private void handleAufgabenListeRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            handleGetOffeneAufgaben(exchange);
        } else if (exchange.getRequestMethod().equals("POST")) {
            handlePostNeueAufgabe(exchange);
        }
    }

    private void handlePostNeueAufgabe(HttpExchange exchange) throws IOException {
        System.out.println("POST " + exchange.getRequestURI());

        Map<String, String> formData = readFormData(exchange);
        aufgabenListe.neueAufgabe(formData.get("bezeichnung"));

        redirectToAufgaben(exchange);
    }

    private void handleGetOffeneAufgaben(HttpExchange exchange) throws IOException {
        System.out.println("GET " + exchange.getRequestURI());

        List<Aufgabe> offeneAufgaben = aufgabenListe.offeneAufgaben();
        System.out.println(offeneAufgaben.size() + " offene Aufgaben gefunden");

        if (exchange.getRequestHeaders().getFirst("Accept").contains("application/json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String data = gson.toJson(offeneAufgaben);

            sendResponse(exchange, 200, "application/json", data);

        } else {
            String data = createPlainTextData(offeneAufgaben);

            sendResponse(exchange, 200, "text/plain", data);
        }
    }

    private void sendEmptyResponse(HttpExchange exchange, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, -1);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String contentType, String data) throws IOException {
        exchange.getResponseHeaders().set("Content-type", contentType);
        exchange.sendResponseHeaders(statusCode, 0);
        writeResponseBody(exchange, data);
    }

    private void redirectToAufgaben(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Location", "/aufgaben");
        exchange.sendResponseHeaders(302, -1);
    }

    private String createPlainTextData(List<Aufgabe> offeneAufgaben) {
        String data = "";
        for (Aufgabe aufgabe : offeneAufgaben) {
            //noinspection StringConcatenationInLoop
            data += aufgabe + "\n";
        }
        return data;
    }

    private void writeResponseBody(HttpExchange exchange, String data) {
        OutputStream outputStream = exchange.getResponseBody();
        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(data);
        writer.close();
    }

    private Map<String, String> readFormData(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String daten = reader.readLine();
        return parseFormData(daten);
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