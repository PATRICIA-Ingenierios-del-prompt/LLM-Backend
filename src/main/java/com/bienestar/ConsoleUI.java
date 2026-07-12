package com.bienestar;

import com.bienestar.service.AudioService;
import com.bienestar.service.DiaryRepository;
import com.bienestar.service.LlmService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Component
// Interactive console menu. ONLY runs when the "console" profile is explicitly
// active (local use: -Dspring.profiles.active=console). In the container there
// is no stdin, so it must NOT run — otherwise Scanner.nextLine() throws
// NoSuchElementException and crashes the whole app -> CrashLoopBackOff.
@Profile("console")
public class ConsoleUI implements CommandLineRunner {

    private final LlmService llmService;
    private final AudioService audioService;
    private final DiaryRepository diaryRepository;

    public ConsoleUI(LlmService llmService, AudioService audioService, DiaryRepository diaryRepository) {
        this.llmService = llmService;
        this.audioService = audioService;
        this.diaryRepository = diaryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("==================================================");
        System.out.println("   BIENVENIDO AL SISTEMA DE BIENESTAR ESTUDIANTIL ");
        System.out.println("==================================================");
        System.out.println("Cargando servicios y modelo LLM...");

        while (true) {
            System.out.println("\nPor favor selecciona una opcion:");
            System.out.println("1. Hablar con el Chatbot de Apoyo (RF-BNST-01)");
            System.out.println("2. Escribir en el Diario de Emociones (RF-BNST-02)");
            System.out.println("3. Pedir Recomendaciones de Desconexion (RF-BNST-03)");
            System.out.println("4. Ver Sonidos de Relajacion (RF-BNST-04)");
            System.out.println("5. Ver Ejercicios de Respiracion (RF-BNST-05)");
            System.out.println("6. Salir");
            System.out.print("Opcion: ");
            String option = scanner.nextLine().trim();
            if (option.isEmpty()) {
                continue;
            }

            switch (option) {
                case "1":
                    System.out.println("\n--- Chatbot de Apoyo Estudiantil ---");
                    System.out.println("Escribe tu pregunta o 'volver' para regresar al menu principal.");
                    while (true) {
                        System.out.print("Tu: ");
                        String msg = scanner.nextLine();
                        if (msg.equalsIgnoreCase("volver")) break;
                        
                        System.out.println("Pensando...");
                        String response = llmService.getChatbotResponse(msg);
                        System.out.println("Bot: " + response);
                    }
                    break;
                case "2":
                    System.out.println("\n--- Diario de Emociones ---");
                    System.out.print("Ingresa tu ID de estudiante (o nombre): ");
                    String userId = scanner.nextLine();
                    System.out.print("Como te sientes hoy? (ej. Feliz, Triste, Ansioso): ");
                    String mood = scanner.nextLine();
                    System.out.println("Escribe tu entrada de diario:");
                    String content = scanner.nextLine();
                    
                    diaryRepository.saveEntry(userId, content, mood);
                    System.out.println("\nGenerando un consejo para ti...");
                    String advice = llmService.getDiaryAdvice(mood, content);
                    System.out.println("Consejo del Agente: " + advice);
                    break;
                case "3":
                    System.out.println("\n--- Recomendaciones de Desconexion ---");
                    System.out.print("Dime que tipo de entretenimiento buscas hoy o como te sientes: ");
                    String prefs = scanner.nextLine();
                    System.out.println("Buscando en el catalogo de Kaggle...");
                    String recs = llmService.getDisconnectionRecommendation(prefs);
                    System.out.println("Recomendacion:\n" + recs);
                    break;
                case "4":
                    System.out.println("\n--- Sonidos de Relajacion ---");
                    audioService.getSoundsCatalog().forEach(System.out::println);
                    System.out.println("\nPresiona Enter para continuar...");
                    scanner.nextLine();
                    break;
                case "5":
                    System.out.println("\n--- Ejercicios de Respiracion ---");
                    audioService.getBreathingExercises().forEach(System.out::println);
                    System.out.println("\nPresiona Enter para continuar...");
                    scanner.nextLine();
                    break;
                case "6":
                    System.out.println("Saliendo del sistema. Que tengas un buen dia!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opcion no valida. Intentalo de nuevo.");
            }
        }
    }
}
