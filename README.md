# Bienestar Estudiantil - LLM Backend (Java)

Este proyecto es una aplicación de consola desarrollada en **Java 21**, **Spring Boot**, y **LangChain4j**, diseñada para dar soporte y bienestar a los estudiantes. Utiliza técnicas de *Retrieval-Augmented Generation* (RAG) integradas con un LLM (Groq/OpenAI) y una **Base de Datos Vectorial en Memoria** para responder preguntas, analizar emociones y sugerir contenido de entretenimiento.

## Requerimientos Implementados

- **RF-BNST-01:** Chatbot para Apoyo Estudiantil (RAG con embeddings locales).
- **RF-BNST-02:** Diario de Emociones (con guardado local vía Jackson ObjectMapper).
- **RF-BNST-03:** Recomendaciones de Desconexión (Basadas en Kaggle).
- **RF-BNST-04:** Catálogo Nativo de Sonidos de Relajación.
- **RF-BNST-05:** Ejercicios Guiados de Respiración.

---

## Cómo Ejecutar el Proyecto

### Requisitos Previos
1. **Java 21 (JDK 21)** instalado en el sistema y configurado en el PATH (o la variable `JAVA_HOME`).
2. Obtener una **API Key** de Groq o OpenAI.

### Pasos
1. Abre una consola (PowerShell o CMD) en la carpeta raíz del proyecto (`C:\Users\Isabel\Downloads\LLM-Backend`).
2. Abre el archivo `.env` en la raíz del proyecto y agrega tu llave:
   ```env
   GROQ_API_KEY="tu_llave_real_aqui"
   ```
3. Compila y ejecuta la aplicación (Spring Boot) usando el Maven local que viene incluido en la carpeta `apache-maven-3.9.6`:

   **En PowerShell:**
   ```powershell
   .\apache-maven-3.9.6\bin\mvn spring-boot:run
   ```

   **En CMD (Símbolo del sistema):**
   ```cmd
   apache-maven-3.9.6\bin\mvn spring-boot:run
   ```

4. ¡Disfruta usando el menú desde tu terminal!

### Cómo Ejecutar las Pruebas Unitarias
El proyecto cuenta con pruebas unitarias usando JUnit 5 y Spring Boot Test. Para ejecutarlas:
```powershell
.\apache-maven-3.9.6\bin\mvn clean test
```

## Arquitectura y Contexto (Diagramas)

### Diagrama de Contexto (C4 - Nivel 1)
Este diagrama muestra cómo interactúa el estudiante (Usuario) con el sistema LLM-Backend y los servicios externos (Groq/OpenAI).

```mermaid
graph TD
    User([Estudiante]) -->|Interactúa vía consola| App[Bienestar Estudiantil App\nSpring Boot]
    App -->|Lee/Escribe JSON| LocalDB[(diary_entries.json)]
    App -->|Consultas de Chat/RAG| LLM[API de LLM - Groq/OpenAI]
    
    classDef sys fill:#1168bd,stroke:#0b4884,color:#ffffff;
    classDef ext fill:#999999,stroke:#666666,color:#ffffff;
    classDef db fill:#2f7823,stroke:#1f4e17,color:#ffffff;
    
    class App sys;
    class LLM ext;
    class LocalDB db;
```

### Diagrama de Arquitectura (C4 - Nivel 2)
Este diagrama detalla los componentes internos de la aplicación y la inyección de dependencias con Spring.

```mermaid
graph TD
    subgraph "Console Application (Spring Boot)"
        UI["ConsoleUI.java<br/>CommandLineRunner"]
        LlmSvc["LlmService.java<br/>@Service - Gestión RAG"]
        AudioSvc["AudioService.java<br/>@Service - Audios"]
        DiaryRepo["DiaryRepository.java<br/>@Repository - Almacenamiento Local"]
        VectorDB[("InMemory<br/>EmbeddingStore")]
    end

    UI --> LlmSvc
    UI --> AudioSvc
    UI --> DiaryRepo
    
    LlmSvc -->|LangChain4j AiServices| Groq[Groq/OpenAI API]
    LlmSvc -->|Ingiere Documentos| VectorDB
    LlmSvc -->|Embeddings Locales| Model[All-MiniLM-L6-V2]
    DiaryRepo -->|Escribe entradas| JsonDB[(diary_entries.json)]
    
    subgraph "Recursos Locales"
       DataFiles[(CSV Datasets)]
    end
    DataFiles -->|Lee al inicializar| LlmSvc
```

## Datasets Usados (Kaggle)
Este proyecto lee informacion en tiempo real de dos datasets alojados en `src/main/resources/data/` que son ingeridos en la memoria al iniciar el contexto de Spring:
1. `mental_health_faq.csv`: Base de datos para soporte estudiantil.
2. `netflix_titles.csv`: Base de datos para recomendaciones de entretenimiento.