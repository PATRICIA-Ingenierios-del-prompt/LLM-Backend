package com.bienestar.service;

import com.opencsv.CSVReader;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmService.class);
    
    private ChatLanguageModel chatModel;
    private WellbeingAssistant assistant;
    private EmbeddingModel embeddingModel;
    private EmbeddingStore<TextSegment> embeddingStore;

    interface WellbeingAssistant {
        @SystemMessage({
            "Eres un asistente de apoyo estudiantil amigable y empatico.",
            "Tu objetivo es dar soporte de primer nivel y responder preguntas sobre salud estudiantil y recomendar actividades."
        })
        String chat(@UserMessage String userMessage);
    }

    public LlmService() {
        initModels();
        if (chatModel != null) {
            setupRag();
        }
    }

    private void initModels() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String groqApiKey = dotenv.get("GROQ_API_KEY");
        String openAiApiKey = dotenv.get("OPENAI_API_KEY");
        
        if (groqApiKey != null && !groqApiKey.isEmpty() && !groqApiKey.equals("TU_GROQ_API_KEY_AQUI")) {
            try {
                this.chatModel = OpenAiChatModel.builder()
                        .baseUrl("https://api.groq.com/openai/v1")
                        .apiKey(groqApiKey)
                        .modelName("llama-3.1-8b-instant")
                        .build();
                log.info("LLM configurado usando Groq API (Llama 3.1 8B)");
            } catch (Exception e) {
                log.error("Error al inicializar Groq", e);
            }
        } else if (openAiApiKey != null && !openAiApiKey.isEmpty() && !openAiApiKey.equals("TU_API_KEY_AQUI")) {
            try {
                this.chatModel = OpenAiChatModel.builder()
                        .apiKey(openAiApiKey)
                        .modelName("gpt-3.5-turbo")
                        .build();
                log.info("LLM configurado usando OpenAI API (GPT-3.5)");
            } catch (Exception e) {
                log.error("Error al inicializar OpenAI", e);
            }
        } else {
            log.warn("Ni GROQ_API_KEY ni OPENAI_API_KEY estan configuradas. El LLM no funcionara correctamente.");
        }

        // Initialize local embedding model
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        this.embeddingStore = new InMemoryEmbeddingStore<>();
    }

    private void setupRag() {
        List<Document> documents = new ArrayList<>();

        // Load FAQ
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/data/mental_health_faq.csv")));
             CSVReader csvReader = new CSVReader(reader)) {
            String[] line;
            csvReader.readNext(); // skip header
            while ((line = csvReader.readNext()) != null) {
                if(line.length >= 2) {
                    documents.add(Document.from("Pregunta: " + line[0] + "\nRespuesta: " + line[1], Metadata.from("type", "faq")));
                }
            }
        } catch (Exception e) {
            log.error("No se pudo cargar el dataset de FAQ", e);
        }

        // Load Movies
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/data/netflix_titles.csv")));
             CSVReader csvReader = new CSVReader(reader)) {
            String[] line;
            csvReader.readNext(); // skip header
            int count = 0;
            while ((line = csvReader.readNext()) != null && count < 50) { // Limit to 50 for local ingestion speed
                if(line.length >= 12) {
                    String listedIn = line[10];
                    if (listedIn.toLowerCase().contains("comedies") || listedIn.toLowerCase().contains("documentaries")) {
                        documents.add(Document.from("Pelicula/Serie: " + line[2] + " (" + listedIn + "). Sinopsis: " + line[11], Metadata.from("type", "movie")));
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            log.error("No se pudo cargar el dataset de peliculas", e);
        }

        // Ingest documents into store
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        
        ingestor.ingest(documents);
        log.info("RAG configurado: Se ingresaron " + documents.size() + " documentos en el EmbeddingStore local.");

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3) // Fetch top 3 relevant chunks
                .minScore(0.5)
                .build();

        this.assistant = AiServices.builder(WellbeingAssistant.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(contentRetriever)
                .build();
    }

    /**
     * True once the RAG assistant is wired (chat model + embeddings ready).
     * Used by WellbeingHealthIndicator to gate the readiness probe, so a pod
     * with a missing/invalid GROQ_API_KEY fails the rollout instead of serving
     * degraded "no configurado" responses with HTTP 200.
     */
    public boolean isReady() {
        return assistant != null;
    }

    public String getChatbotResponse(String userMessage) {
        if (assistant == null) return "El servicio LLM no esta configurado (Falta API Key).";
        try {
            return assistant.chat(userMessage);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public String getDiaryAdvice(String mood, String content) {
        if (assistant == null) return "Diario guardado, pero el servicio LLM no esta configurado para dar consejos.";
        String prompt = "Un estudiante acaba de escribir en su diario de emociones.\n" +
                "Estado de animo: " + mood + "\n" +
                "Contenido: \"" + content + "\"\n\n" +
                "Dame un breve consejo reconfortante y comprensivo.";
        try {
            return assistant.chat(prompt);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public String getDisconnectionRecommendation(String preferences) {
        if (assistant == null) return "Servicio LLM no configurado. Te recomendamos salir a caminar 10 minutos.";
        String prompt = "El estudiante quiere desconectarse. Preferencias: \"" + preferences + "\"\n" +
                "Busca en el catalogo de peliculas disponibles o sugiere otra actividad. Explica por que le ayudara.";
        try {
            return assistant.chat(prompt);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    private String handleError(Exception e) {
        log.error("Error comunicandose con el LLM", e);
        if (e.getMessage() != null && e.getMessage().contains("insufficient_quota")) {
            return "Error: Tu clave de API no tiene saldo disponible (insufficient_quota).";
        }
        return "Error al comunicarse con el LLM: " + e.getMessage();
    }
}
