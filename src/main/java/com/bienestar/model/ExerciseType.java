package com.bienestar.model;

/**
 * Mirrors the breathing techniques already catalogued in
 * {@link com.bienestar.service.AudioService#getBreathingExercises()}, giving
 * each one a stable code to validate against in the exercises API.
 */
public enum ExerciseType {
    RESPIRACION_478("Tecnica 4-7-8: Inhala por 4s, sosten por 7s, exhala por 8s."),
    BOX_BREATHING("Respiracion de Caja: Inhala 4s, sosten 4s, exhala 4s, sosten 4s.");

    private final String descripcion;

    ExerciseType(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
