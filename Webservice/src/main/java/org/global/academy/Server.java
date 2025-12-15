package org.global.academy;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class Server {
    public static void main(String[] args) {
        port(8080);
        staticFiles.location("/public"); // Files must be in src/main/resources/public

        // Allow browser connections
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
            res.header("Access-Control-Allow-Headers", "*");
        });

        Gson gson = new Gson();

        // --- 1. LOGIN ---
        post("/login", (req, res) -> {
            return gson.toJson(new LoginResponse("token-123", "alice"));
        });

        // --- 2. PROGRESS ---
        get("/progress", (req, res) -> {
            UserProgress p = new UserProgress();
            p.units.add(new Unit("Intro", "🥚", "active")); // Active unit
            p.units.add(new Unit("Travel", "✈️", "locked"));
            return gson.toJson(p);
        });

        // --- 3. LESSON DATA (Crucial Part) ---
        get("/lesson/1", (req, res) -> {
            res.type("application/json");
            
            LessonData lesson = new LessonData();
            lesson.question = "Which one of these is \"one\"?";
            
            // We set 'isCorrect' to true ONLY for option 3 ("un")
            lesson.options.add(new Option(1, "l'homme", "👨🏾", false)); 
            lesson.options.add(new Option(2, "le garçon", "👦🏼", false));
            lesson.options.add(new Option(3, "un", "1️⃣", true)); 
            
            return gson.toJson(lesson);
        });
    }

    // --- DATA CLASSES ---
    static class LoginResponse { String token; String username; LoginResponse(String t, String u){token=t; username=u;} }
    
    static class UserProgress { List<Unit> units = new ArrayList<>(); }
    static class Unit { String title; String icon; String status; Unit(String t, String i, String s){title=t; icon=i; status=s;} }

    static class LessonData { String question; List<Option> options = new ArrayList<>(); }
    static class Option {
        int id; String text; String icon; boolean isCorrect;
        Option(int id, String t, String i, boolean c) {
            this.id=id; this.text=t; this.icon=i; this.isCorrect=c;
        }
    }
}