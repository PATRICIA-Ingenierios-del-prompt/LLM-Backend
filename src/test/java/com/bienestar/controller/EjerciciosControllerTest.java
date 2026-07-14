package com.bienestar.controller;

import com.bienestar.entity.ExerciseCompletion;
import com.bienestar.model.ExerciseType;
import com.bienestar.service.ExerciseCompletionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.main.banner-mode=off"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EjerciciosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExerciseCompletionService exerciseCompletionService;

    @Test
    void completarEjercicioReturnsUnauthorizedWhenUserIdHeaderMissing() throws Exception {
        mockMvc.perform(post("/api/bienestar/ejercicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\": \"RESPIRACION_478\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void completarEjercicioReturnsBadRequestWhenTipoIsInvalid() throws Exception {
        mockMvc.perform(post("/api/bienestar/ejercicios")
                .header("X-User-Id", "user-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\": \"YOGA\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completarEjercicioReturnsCreatedOnValidRequest() throws Exception {
        ExerciseCompletion saved = new ExerciseCompletion("user-1", "RESPIRACION_478", "RELAJACION");
        when(exerciseCompletionService.registerCompletion(eq("user-1"), eq(ExerciseType.RESPIRACION_478)))
                .thenReturn(saved);

        mockMvc.perform(post("/api/bienestar/ejercicios")
                .header("X-User-Id", "user-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\": \"RESPIRACION_478\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.tipo").value("RESPIRACION_478"));

        verify(exerciseCompletionService).registerCompletion("user-1", ExerciseType.RESPIRACION_478);
    }

    @Test
    void contarEjerciciosReturnsTotalForUser() throws Exception {
        when(exerciseCompletionService.countCompletions("user-1")).thenReturn(3L);

        mockMvc.perform(get("/api/bienestar/usuarios/user-1/ejercicios/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.total").value(3));
    }
}
