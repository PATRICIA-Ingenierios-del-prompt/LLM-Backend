package com.bienestar.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LlmService {

    private ChatLanguageModel model;
    private String faqContext = "";
    private String moviesContext = "";

    public LlmService() {
        initModel();
        loadDatasets();
    }

    private void initModel() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String groqApiKey = dotenv.get("GROQ_API_KEY");
        String openAiApiKey = dotenv.get("OPENAI_API_KEY");
        
        if (groqApiKey != null && !groqApiKey.isEmpty() && !groqApiKey.equals("TU_GROQ_API_KEY_AQUI")) {
            try {
                this.model = OpenAiChatModel.builder()
                        .baseUrl("https://api.groq.com/openai/v1")
                        .apiKey(groqApiKey)
                        .modelName("llama-3.1-8b-instant")
                        .build();
                System.out.println("[INFO] LLM configurado usando Groq API (Llama 3.1 8B)");
            } catch (Exception e) {
                System.err.println("Error al inicializar Groq: " + e.getMessage());
            }
        } else if (openAiApiKey != null && !openAiApiKey.isEmpty() && !openAiApiKey.equals("TU_API_KEY_AQUI")) {
            try {
                this.model = OpenAiChatModel.builder()
                        .apiKey(openAiApiKey)
                        .modelName("gpt-3.5-turbo")
                        .build();
                System.out.println("[INFO] LLM configurado usando OpenAI API (GPT-3.5)");
            } catch (Exception e) {
                System.err.println("Error al inicializar OpenAI: " + e.getMessage());
            }
        } else {
            System.err.println("ADVERTENCIA: Ni GROQ_API_KEY ni OPENAI_API_KEY estan configuradas. El LLM no funcionara correctamente.");
        }
    }

    private void loadDatasets() {
        // Load FAQ
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/data/mental_health_faq.csv")));
             CSVReader csvReader = new CSVReader(reader)) {
            String[] line;
            StringBuilder sb = new StringBuilder();
            csvReader.readNext(); // skip header
            while ((line = csvReader.readNext()) != null) {
                if(line.length >= 2) {
                    sb.append("Q: ").append(line[0]).append("\nA: ").append(line[1]).append("\n");
                }
            }
            faqContext = sb.toString();
        } catch (Exception e) {
            System.err.println("No se pudo cargar el dataset de FAQ: " + e.getMessage());
        }

        // Load Movies
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/data/netflix_titles.csv")));
             CSVReader csvReader = new CSVReader(reader)) {
            String[] line;
            StringBuilder sb = new StringBuilder();
            csvReader.readNext(); // skip header
            int count = 0;
            while ((line = csvReader.readNext()) != null && count < 10) {
                // Title is usually index 2, listed_in index 10, description index 11
                if(line.length >= 12) {
                    String listedIn = line[10];
                    if (listedIn.toLowerCase().contains("comedies") || listedIn.toLowerCase().contains("documentaries")) {
                        sb.append("- ").append(line[2]).append(" (").append(listedIn).append("): ").append(line[11]).append("\n");
                        count++;
                    }
                }
            }
            moviesContext = sb.toString();
        } catch (Exception e) {
            System.err.println("No se pudo cargar el dataset de peliculas: " + e.getMessage());
        }
    }

    public String getChatbotResponse(String userMessage) {
        if (model == null) return "El servicio LLM no esta configurado (Falta API Key).";
        
        String prompt = "Eres un asistente de apoyo estudiantil amigable y empatico.\n" +
                "Tu objetivo es dar soporte de primer nivel y responder preguntas sobre salud estudiantil.\n" +
                "Aqui hay algunas preguntas frecuentes y respuestas que puedes usar:\n" +
                faqContext + "\n\n" +
                "Mensaje del estudiante: " + userMessage + "\n" +
                "Respuesta:";
        
        try {
            return model.generate(prompt);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("insufficient_quota")) {
                return "Error: Tu clave de OpenAI no tiene saldo disponible (insufficient_quota).\n     Por favor, recarga saldo en https://platform.openai.com/settings/billing";
            }
            return "Error al comunicarse con el LLM: " + e.getMessage();
        }
    }

    public String getDiaryAdvice(String mood, String content) {
        if (model == null) return "Diario guardado, pero el servicio LLM no esta configurado para dar consejos.";
        
        String prompt = "Eres un consejero empatico. Un estudiante acaba de escribir en su diario de emociones.\n" +
                "Estado de animo detectado/indicado: " + mood + "\n" +
                "Contenido del diario: \"" + content + "\"\n\n" +
                "Escribe un breve y reconfortante consejo basado en lo que escribio. Manten un tono comprensivo.\n" +
                "Consejo:";
        
        try {
            return model.generate(prompt);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("insufficient_quota")) {
                return "Error: Tu clave de OpenAI no tiene saldo disponible (insufficient_quota) para generar consejos.";
            }
            return "Error al comunicarse con el LLM: " + e.getMessage();
        }
    }

    public String getDisconnectionRecommendation(String preferences) {
        if (model == null) return "Servicio LLM no configurado. Te recomendamos salir a caminar 10 minutos.";
        
        String prompt = "Eres un recomendador de entretenimiento para ayudar a los estudiantes a desconectarse.\n" +
                "El estudiante tiene estas preferencias: \"" + preferences + "\"\n\n" +
                "Catalogo disponible:\n" +
                moviesContext + "\n\n" +
                "Recomienda 1 o 2 opciones del catalogo y explica por que le ayudaran a relajarse. Tambien puedes sugerir algo fuera de pantalla.\n" +
                "Recomendacion:";
        
        try {
            return model.generate(prompt);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("insufficient_quota")) {
                return "Error: Tu clave de OpenAI no tiene saldo disponible (insufficient_quota).\n     Sugerencia alternativa nativa: Puedes ver una comedia o documental relajante o tomar una taza de té.";
            }
            return "Error al comunicarse con el LLM: " + e.getMessage();
        }
    }
}
