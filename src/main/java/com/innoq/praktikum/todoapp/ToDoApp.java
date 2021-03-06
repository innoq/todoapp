package com.innoq.praktikum.todoapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToDoApp {

    private final AufgabenListe aufgabenListe;
    private final Gson gson;
    private final TemplateEngine templateEngine;

    public ToDoApp() {
        aufgabenListe = new AufgabenListe();

        gson = new GsonBuilder().setPrettyPrinting().create();

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(new ClassLoaderTemplateResolver());
    }

    public static void main(String[] args) throws IOException {
        ToDoApp toDoApp = new ToDoApp();
        toDoApp.start();
    }

    public void start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/login", this::handleLoginRequest);
        httpServer.createContext("/logout", this::handleLogoutRequest);
        httpServer.createContext("/aufgaben", this::handleAufgabenRequest);
        httpServer.createContext("/health", this::handleHealthRequest);
        httpServer.createContext("/erledigt", this::handleGetErledigteAufgabenRequest);
        httpServer.createContext("/static", this::handleStaticRequest);
        httpServer.createContext("/", this::handleRootRequest);
        httpServer.start();
        System.out.println("HTTP Server auf Port 8080 gestartet");
    }

    private void handleStaticRequest(HttpExchange exchange) throws IOException {
        var uri = exchange.getRequestURI().getPath();
        var filename = uri.substring(uri.lastIndexOf("/") + 1);
        System.out.println(filename);

        try (var fileInputStream = ToDoApp.class.getResourceAsStream("/static/" + filename)) {
            var bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);

            if (filename.endsWith(".css")) {
                sendResponse(exchange, 200, "text/css", new String(bytes));
            } else {
                sendEmptyResponse(exchange, 404);
            }
        } catch (Exception exc) {
            exc.printStackTrace(System.err);
            sendEmptyResponse(exchange, 404);
        }
    }

    private void handleLoginRequest(HttpExchange exchange) throws IOException {
        System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI());

        if (!exchange.getRequestURI().toString().equals("/login")) {
            sendEmptyResponse(exchange, 404);
            return;
        }

        if (exchange.getRequestMethod().equals("GET")) {
            handleGetLoginFormRequest(exchange);
        } else if (exchange.getRequestMethod().equals("POST")) {
            handlePostLoginFormRequest(exchange);
        } else {
            sendEmptyResponse(exchange, 405);
        }
    }

    private void handleGetLoginFormRequest(HttpExchange exchange) throws IOException {
        String body = templateEngine.process("templates/login.html", new Context(Locale.getDefault()));
        sendResponse(exchange, 200, "text/html", body);
    }

    private void handleGetErledigteAufgabenRequest(HttpExchange exchange) throws IOException {
        if (notContainsValidCookie(exchange)) {
            redirectToLogin(exchange);
            return;
        }
        String user = readUserFromCookie(exchange);

        List<Aufgabe> erledigteAufgaben = aufgabenListe.findErledigteAufgabenForUser(user);
        System.out.println(erledigteAufgaben.size() + " erledigte Aufgaben gefunden");

        if (exchange.getRequestHeaders().getFirst("Accept").contains("text/html")) {
            IContext context = new Context(Locale.GERMAN, Map.of(
                    "user", user,
                    "alleErledigtenAufgaben", erledigteAufgaben));
            String data = templateEngine.process("templates/ErledigteAufgaben.html", context);
            sendResponse(exchange, 200, "text/html", data);

        } else if (exchange.getRequestHeaders().getFirst("Accept").contains("application/json")) {
            String data = gson.toJson(erledigteAufgaben);

            sendResponse(exchange, 200, "application/json", data);

        } else {
            String data = createPlainTextData(erledigteAufgaben);

            sendResponse(exchange, 200, "text/plain", data);
        }
    }
    private void handlePostLoginFormRequest(HttpExchange exchange) throws IOException {
        Map<String, String> formData = readFormData(exchange);
        String name = formData.get("name");
        if (name == null || name.isEmpty()) {
            Context context = new Context(Locale.getDefault(),
                    Map.of("error", "Du musst einen Namen eingeben"));
            String body = templateEngine.process("templates/login.html", context);
            sendResponse(exchange, 400, "text/html", body);
            return;
        }
        if (name.length() < 3 || name.length() > 20) {
            Context context = new Context(Locale.getDefault(),
                    Map.of("error", "Der Name muss mindestens 3 und maximal 20 Zeichen lang sein"));
            String body = templateEngine.process("templates/login.html", context);
            sendResponse(exchange, 400, "text/html", body);
            return;
        }
        if (!name.matches("[A-Za-z0-9_]*")) {
            Context context = new Context(Locale.getDefault(),
                    Map.of("error", "Der Name darf nur Buchstaben (A-Z, gro?? oder klein), Ziffern (0-9) sowie den Unterstrich (_) enthalten"));
            String body = templateEngine.process("templates/login.html", context);
            sendResponse(exchange, 400, "text/html", body);
            return;
        }

        exchange.getResponseHeaders().add("Set-Cookie", "user=" + name);

        redirectToAufgaben(exchange);
    }

    private void handleLogoutRequest(HttpExchange exchange) throws IOException {
        System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI());

        if (!exchange.getRequestURI().toString().equals("/logout")) {
            sendEmptyResponse(exchange, 404);
            return;
        }
        if (!exchange.getRequestMethod().equals("POST")) {
            sendEmptyResponse(exchange, 405);
            return;
        }

        expireCookie(exchange);

        redirectToLogin(exchange);
    }

    private void handleHealthRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 200, "text/plain", "I'm fine");
    }

    private void handleRootRequest(HttpExchange exchange) throws IOException {
        System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI());

        if (!exchange.getRequestURI().toString().equals("/")) {
            sendEmptyResponse(exchange, 404);
            return;
        }

        redirectToAufgaben(exchange);
    }

    private void handleAufgabenRequest(HttpExchange exchange) throws IOException {
        System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI());

        if (notContainsValidCookie(exchange)) {
            redirectToLogin(exchange);
            return;
        }

        try {
            if (exchange.getRequestURI().toString().equals("/aufgaben")) {
                handleAufgabenListeRequest(exchange);
            } else if (exchange.getRequestURI().toString().matches("/aufgaben/\\d+")) {
                handleEinzelneAufgabeRequest(exchange);
            } else {
                sendEmptyResponse(exchange, 404);
            }
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
            sendEmptyResponse(exchange, 500);
        }
    }

    private void handleEinzelneAufgabeRequest(HttpExchange exchange) throws IOException {
        int id = parseId(exchange.getRequestURI());
        String user = readUserFromCookie(exchange);
        Map<String, String> formData = readFormData(exchange);

        if (!formData.containsKey("erledigt")) {
            sendEmptyResponse(exchange, 400);
            return;
        }

        Aufgabe zuAenderndeAufgabe = aufgabenListe.findAufgabeByUserAndId(user, id);
        if (zuAenderndeAufgabe == null) {
            sendEmptyResponse(exchange, 404);
            return;
        }

        if (formData.get("erledigt").equals("true")) {
            aufgabenListe.alsErledigtAbhaken(zuAenderndeAufgabe);
        } else if (formData.get("erledigt").equals("false")) {
            aufgabenListe.aufNichtErledigtZuruecksetzen(zuAenderndeAufgabe);
        } else {
            sendEmptyResponse(exchange, 400);
            return;
        }

        if (formData.containsKey("forwardUrl")) {
            String referrer = formData.get("forwardUrl");
            redirectTo(exchange, referrer);
        } else {
            redirectToAufgaben(exchange);
        }
    }

    private void handleAufgabenListeRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            handleGetOffeneAufgaben(exchange);
        } else if (exchange.getRequestMethod().equals("POST")) {
            handlePostNeueAufgabe(exchange);
        }
    }

    private void handlePostNeueAufgabe(HttpExchange exchange) throws IOException {
        String user = readUserFromCookie(exchange);

        Map<String, String> formData = readFormData(exchange);
        if (formData.containsKey("bezeichnung")) {
            if(formData.get("bezeichnung").equalsIgnoreCase("weekend")){
                System.out.println("It's Weekend time");
                for (Aufgabe aufgabe : aufgabenListe.findOffeneAufgabenForUser(user)) {
                    aufgabenListe.alsErledigtAbhaken(aufgabe);
                    //easter egg
                }
            }
            else if (formData.get("bezeichnung").length() > 0
                && formData.get("bezeichnung").length() <= 20) {

                String deadline = formData.get("deadline");
                if (!deadline.isEmpty()) {
                    LocalDate localDate = LocalDate.parse(deadline);
                    aufgabenListe.neueAufgabeForUser(user, formData.get("bezeichnung"), localDate);
                    System.out.println(localDate);

                }
                else{
                    aufgabenListe.neueAufgabeForUser(user, formData.get("bezeichnung"), null);
                }
            } else {
                IContext context = new Context(Locale.GERMAN, Map.of(
                        "alleOffenenAufgaben", this.aufgabenListe.findOffeneAufgabenForUser(user),
                        "bezeichnung", formData.get("bezeichnung"),
                        "fehlermeldung", "Du musst eine Aufgabenbezeichnung zwischen 1 und 20 Zeichen angeben"
                ));
                String data = templateEngine.process("templates/aufgabenliste.html", context);
                sendResponse(exchange, 400, "text/html", data);

            }

        } else {
            sendEmptyResponse(exchange, 400);
        }
        redirectToAufgaben(exchange);
    }

    private void handleGetOffeneAufgaben(HttpExchange exchange) throws IOException {
        String user = readUserFromCookie(exchange);

        List<Aufgabe> offeneAufgaben = aufgabenListe.findOffeneAufgabenForUser(user);
        System.out.println(offeneAufgaben.size() + " offene Aufgaben gefunden");

        if (exchange.getRequestHeaders().getFirst("Accept").contains("text/html")) {
            IContext context = new Context(Locale.GERMAN, Map.of(
                    "user", user,
                    "alleOffenenAufgaben", offeneAufgaben));
            String data = templateEngine.process("templates/aufgabenliste.html", context);
            sendResponse(exchange, 200, "text/html", data);

        } else if (exchange.getRequestHeaders().getFirst("Accept").contains("application/json")) {
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

    private void redirectToLogin(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Location", "/login");
        exchange.sendResponseHeaders(302, -1);
    }

    private void redirectToAufgaben(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Location", "/aufgaben");
        exchange.sendResponseHeaders(302, -1);
    }

    private void redirectTo(HttpExchange exchange, String url) throws IOException {
        exchange.getResponseHeaders().set("Location", url);
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

    private boolean notContainsValidCookie(HttpExchange exchange) {
        return readUserFromCookie(exchange) == null;
    }

    private String readUserFromCookie(HttpExchange exchange) {
        return readUserFromCookie(exchange.getRequestHeaders().getFirst("Cookie"));
    }

    static String readUserFromCookie(String cookie) {
        if (cookie == null || cookie.isEmpty()) {
            System.out.println("keinen Cookie gefunden");
            return null;
        }

        Pattern pattern = Pattern.compile(".*user=([A-Za-z0-9_]{3,20}).*");
        Matcher matcher = pattern.matcher(cookie);
        if (matcher.matches()) {
            System.out.println("Validen Cookie gefunden");
            return matcher.group(1);
        }

        System.out.println("keinen validen Cookie gefunden");
        return null;
    }

    private void expireCookie(HttpExchange exchange) {
        var user = readUserFromCookie(exchange);
        exchange.getResponseHeaders().add("Set-Cookie", "user=" + user + "; Max-Age=0");
    }

}