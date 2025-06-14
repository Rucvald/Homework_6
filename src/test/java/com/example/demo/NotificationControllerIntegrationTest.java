package com.example.demo;

import com.example.demo.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // ✅ правильный импорт
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    @MockitoBean // ✅ заменяет бин в контексте
    private EmailService emailService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSendEmail() throws Exception {
        mockMvc.perform(post("/api/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\":\"test@example.com\", \"subject\":\"Test\", \"text\":\"Hello\"}"))
                .andExpect(status().isOk());

        verify(emailService, times(1)).sendEmail(
                eq("test@example.com"),
                eq("Test"),
                eq("Hello")
        );
    }
}
