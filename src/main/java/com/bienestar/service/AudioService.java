package com.bienestar.service;

import java.util.Arrays;
import java.util.List;

public class AudioService {

    public List<String> getSoundsCatalog() {
        return Arrays.asList(
                "1. Ruido Blanco (White Noise): Ideal para bloquear distracciones.",
                "2. Ruido Marrón: Excelente para relajación profunda y sueño.",
                "3. Lluvia Suave: Sonido continuo para calmar la ansiedad.",
                "4. Bosque y Pájaros: Sonidos ambientales del bosque."
        );
    }

    public List<String> getBreathingExercises() {
        return Arrays.asList(
                "1. Tecnica 4-7-8: Inhala por 4s, sosten por 7s, exhala por 8s. Ayuda a conciliar el sueno.",
                "2. Respiracion de Caja (Box Breathing): Inhala 4s, sosten 4s, exhala 4s, sosten 4s. Ideal para recuperar la calma."
        );
    }
}
