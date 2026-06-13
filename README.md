# Bienestar Estudiantil - LLM Backend (Java)

Este proyecto es una aplicacion de consola desarrollada en **Java 21** con **Maven** y **LangChain4j**, disenada para dar soporte y bienestar a los estudiantes. Utiliza tecnicas de *Retrieval-Augmented Generation* (RAG) integradas con un LLM (OpenAI) para responder preguntas, analizar emociones y sugerir contenido de entretenimiento.

## Requerimientos Implementados

- **RF-BNST-01:** Chatbot para Apoyo Estudiantil.
- **RF-BNST-02:** Diario de Emociones (con guardado local).
- **RF-BNST-03:** Recomendaciones de Desconexion (Basadas en Kaggle).
- **RF-BNST-04:** Catalogo Nativo de Sonidos de Relajacion.
- **RF-BNST-05:** Ejercicios Guiados de Respiracion.

---

## Cómo Ejecutar el Proyecto

### Requisitos Previos
1. **Java 21 (JDK 21)** instalado en el sistema y configurado en el PATH (o la variable `JAVA_HOME`).
2. Obtener una **API Key** de OpenAI.

### Pasos
1. Abre una consola (PowerShell o CMD) en la carpeta raíz del proyecto (`C:\Users\Isabel\Downloads\LLM-Backend`).
2. Abre el archivo `.env` en la raíz del proyecto y agrega tu llave:
   ```env
   OPENAI_API_KEY="tu_llave_real_aqui"
   ```
3. Compila y ejecuta la aplicación usando el Maven local que viene incluido en la carpeta `apache-maven-3.9.6`:

   **En PowerShell:**
   ```powershell
   .\apache-maven-3.9.6\bin\mvn clean compile exec:java
   ```

   **En CMD (Símbolo del sistema):**
   ```cmd
   apache-maven-3.9.6\bin\mvn clean compile exec:java
   ```

4. ¡Disfruta usando el menú desde tu terminal!

## Datasets Usados (Kaggle)
Este proyecto lee informacion en tiempo real de dos datasets alojados en `src/main/resources/data/`:
1. `mental_health_faq.csv`: Base de datos para soporte estudiantil.
2. `netflix_titles.csv`: Base de datos para recomendaciones de entretenimiento.