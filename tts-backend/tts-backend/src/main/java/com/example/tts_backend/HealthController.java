package com.example.tts_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthController {
    @GetMapping ("/api/health")
    public String health() {
        return "TTS Backend is running!";
    }
}

