# syntax=docker/dockerfile:1
# ---------------------------------------------------------------------------
# PATRICIA wellbeing (LLM) MS — multi-stage build.
# Stage 1 builds the fat jar with JDK 21 (Temurin). Stage 2 runs it on a JRE 21.
# --platform=linux/amd64 is pinned so an image built on Apple Silicon still runs
# on EKS Fargate (amd64) instead of dying with "exec format error".
# ---------------------------------------------------------------------------

# --- build stage -----------------------------------------------------------
FROM --platform=linux/amd64 maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
# Tests already run in the CI build-and-test gate; skip them here for a fast,
# deterministic image build. Dependencies resolve on first build and are cached
# in the BuildKit mount across builds.
RUN --mount=type=cache,target=/root/.m2 mvn -B -q clean package -DskipTests

# --- runtime stage ---------------------------------------------------------
FROM --platform=linux/amd64 eclipse-temurin:21-jre
WORKDIR /app

# Non-root runtime user.
RUN groupadd -r app && useradd -r -g app app

COPY --from=build /app/target/llm-backend-console-1.0-SNAPSHOT.jar app.jar
RUN chown -R app:app /app
USER app

EXPOSE 8086

# MaxRAMPercentage=50 leaves headroom for the ONNX Runtime OFF-HEAP allocation
# (AllMiniLmL6V2 native memory is NOT counted in -Xmx). Overridable via the
# JAVA_TOOL_OPTIONS env set in the Helm values.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=50.0 -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m"

ENTRYPOINT ["java", "-jar", "app.jar"]
