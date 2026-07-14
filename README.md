# Bienestar Estudiantil - LLM Backend (Java)

Este proyecto es una aplicación de consola desarrollada en **Java 21**, **Spring Boot**, y **LangChain4j**, diseñada para dar soporte y bienestar a los estudiantes. Utiliza técnicas de *Retrieval-Augmented Generation* (RAG) integradas con un LLM (Groq/OpenAI) y una **Base de Datos Vectorial en Memoria** para responder preguntas, analizar emociones y sugerir contenido de entretenimiento.

## Requerimientos Implementados

- **RF-BNST-01:** Chatbot para Apoyo Estudiantil (RAG con embeddings locales).
- **RF-BNST-02:** Diario de Emociones (persistido por usuario en Postgres/Neon).
- **RF-BNST-03:** Recomendaciones de Desconexión (Basadas en Kaggle).
- **RF-BNST-04:** Catálogo Nativo de Sonidos de Relajación.
- **RF-BNST-05:** Ejercicios Guiados de Respiración.

---

## Cómo Ejecutar el Proyecto

### Requisitos Previos
1. **Java 21 (JDK 21)** instalado en el sistema y configurado en el PATH (o la variable `JAVA_HOME`).
2. Obtener una **API Key** de Groq o OpenAI.
3. Una base de datos Postgres alcanzable (recomendado: una rama de desarrollo en [Neon](https://neon.tech)). El servicio ya no arranca sin ella — Spring Boot falla rápido si el `DataSource`/Flyway no pueden conectar.

### Pasos
1. Abre una consola (PowerShell o CMD) en la carpeta raíz del proyecto (`C:\Users\Isabel\Downloads\LLM-Backend`).
2. Abre el archivo `.env` en la raíz del proyecto y agrega tu llave y las credenciales de la base de datos:
   ```env
   GROQ_API_KEY="tu_llave_real_aqui"
   SPRING_DATASOURCE_URL="jdbc:postgresql://<host>/<db>?sslmode=require"
   SPRING_DATASOURCE_USERNAME="tu_usuario"
   SPRING_DATASOURCE_PASSWORD="tu_password"
   ```
   Al arrancar, Flyway crea automáticamente las tablas `diary_entries` y `exercise_completions` (ver `src/main/resources/db/migration/`).
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
    User([Estudiante]) -->|Vía Gateway - X-User-Id| App[Bienestar Estudiantil App\nSpring Boot]
    App -->|Diario + ejercicios completados| PgDB[(Postgres - Neon)]
    App -->|Consultas de Chat/RAG| LLM[API de LLM - Groq/OpenAI]
    
    classDef sys fill:#1168bd,stroke:#0b4884,color:#ffffff;
    classDef ext fill:#999999,stroke:#666666,color:#ffffff;
    classDef db fill:#2f7823,stroke:#1f4e17,color:#ffffff;
    
    class App sys;
    class LLM ext;
    class PgDB db;
```

### Diagrama de Arquitectura (C4 - Nivel 2)
Este diagrama detalla los componentes internos de la aplicación y la inyección de dependencias con Spring.

```mermaid
graph TD
    subgraph "REST API (Spring Boot)"
        ChatCtrl["ChatController.java<br/>@RestController - /api/chat, /api/diary"]
        EjCtrl["EjerciciosController.java<br/>@RestController - /api/bienestar/ejercicios"]
        LlmSvc["LlmService.java<br/>@Service - Gestión RAG"]
        AudioSvc["AudioService.java<br/>@Service - Audios"]
        DiaryRepo["DiaryRepository.java<br/>@Repository - JPA"]
        ExSvc["ExerciseCompletionService.java<br/>@Service - JPA"]
        VectorDB[("InMemory<br/>EmbeddingStore")]
    end

    ChatCtrl --> LlmSvc
    ChatCtrl --> DiaryRepo
    EjCtrl --> ExSvc

    LlmSvc -->|LangChain4j AiServices| Groq[Groq/OpenAI API]
    LlmSvc -->|Ingiere Documentos| VectorDB
    LlmSvc -->|Embeddings Locales| Model[All-MiniLM-L6-V2]
    DiaryRepo -->|diary_entries| PgDB[(Postgres - Neon)]
    ExSvc -->|exercise_completions| PgDB

    subgraph "Recursos Locales"
       DataFiles[(CSV Datasets)]
    end
    DataFiles -->|Lee al inicializar| LlmSvc
```

## Integración Continua (CI/CD) y Análisis de Código

### Pipeline de Integración Continua
Se ha implementado un pipeline de GitHub Actions (`.github/workflows/CI.yml`) que se ejecuta en cada Push o Pull Request a las ramas `develop` y `main`.
Este pipeline automatiza:
1. Configuración del entorno (Java 21, Maven).
2. Ejecución de pruebas unitarias y de integración (`mvn clean verify`).
3. Validación de umbrales de cobertura.

```mermaid
sequenceDiagram
    participant Dev as Desarrollador
    participant Git as GitHub (develop/main)
    participant CI as GitHub Actions
    
    Dev->>Git: Push / Pull Request
    Git->>CI: Trigger Workflow (CI.yml)
    CI->>CI: Setup Java 21 & Maven
    CI->>CI: mvn clean verify
    CI-->>Git: Reporte de éxito/fallo (Build & Tests)
```

### Cobertura de Código (JaCoCo)
El proyecto utiliza el **JaCoCo Maven Plugin** para medir la cobertura del código.
Durante la fase `verify` de Maven, se genera un reporte en `target/site/jacoco/index.html`. Además, se ha configurado un umbral que requiere un **80% mínimo de cobertura** de líneas para que el build se considere exitoso.

## Datasets Usados (Kaggle)
Este proyecto lee informacion en tiempo real de dos datasets alojados en `src/main/resources/data/` que son ingeridos en la memoria al iniciar el contexto de Spring:
1. `mental_health_faq.csv`: Base de datos para soporte estudiantil.
2. `netflix_titles.csv`: Base de datos para recomendaciones de entretenimiento.