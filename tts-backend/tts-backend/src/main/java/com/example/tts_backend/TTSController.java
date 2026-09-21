package com.example.tts_backend.controller;

import com.example.tts_backend.service.TTSService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TTSController {

    private final TTSService ttsService;

    public TTSController(TTSService ttsService) {
        this.ttsService = ttsService;
    }

    // =========================
    // Generate Speech
    // =========================
    @PostMapping(
            value = "/tts",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> generateSpeech(@RequestBody TTSRequest request) {

        try {
            // Text validation
            if (request.text() == null || request.text().trim().isEmpty()) {
                return errorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Text is required."
                );
            }

            // Maximum 500 characters
            if (request.text().length() > 500) {
                return errorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Text cannot exceed 500 characters."
                );
            }

            // Language validation
            if (request.language() == null ||
                    request.language().trim().isEmpty()) {

                return errorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Language is required."
                );
            }

            // Voice validation
            if (request.voice() == null ||
                    request.voice().trim().isEmpty()) {

                return errorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Voice is required."
                );
            }

            // Format
            String format = request.format();

            if (format == null || format.trim().isEmpty()) {
                format = "mp3";
            }

            format = format.toLowerCase();

            if (!format.equals("mp3") &&
                    !format.equals("wav") &&
                    !format.equals("ogg")) {

                return errorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Supported formats are MP3, WAV and OGG."
                );
            }

            // Generate MP3 from ElevenLabs
            byte[] mp3Audio = ttsService.generateSpeech(
                    request.text(),
                    request.language(),
                    request.voice()
            );

            byte[] finalAudio;
            MediaType mediaType;
            String fileName;

            // =========================
            // MP3
            // =========================
            if (format.equals("mp3")) {

                finalAudio = mp3Audio;
                mediaType = MediaType.parseMediaType("audio/mpeg");
                fileName = "generated-speech.mp3";
            }

            // =========================
            // WAV
            // =========================
            else if (format.equals("wav")) {

                finalAudio = ttsService.convertToWav(mp3Audio);
                mediaType = MediaType.parseMediaType("audio/wav");
                fileName = "generated-speech.wav";
            }

            // =========================
            // OGG
            // =========================
            else {

                finalAudio = ttsService.convertToOgg(mp3Audio);
                mediaType = MediaType.parseMediaType("audio/ogg");
                fileName = "generated-speech.ogg";
            }

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(mediaType);
            headers.setContentLength(finalAudio.length);
            headers.set(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileName + "\""
            );

            return new ResponseEntity<>(
                    finalAudio,
                    headers,
                    HttpStatus.OK
            );

        } catch (Exception e) {

            e.printStackTrace();

            return errorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate speech: " + e.getMessage()
            );
        }
    }


    // =========================
    // Get Available Voices
    // =========================
    @GetMapping("/voices")
    public ResponseEntity<Map<String, Object>> getVoices() {

        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, String> english = new LinkedHashMap<>();
        english.put("Male", "CwhRBWXzGAHq8TQ4Fs17");
        english.put("Female", "hpp4J3VqNfWAUOO0d1Us");

        Map<String, String> hindi = new LinkedHashMap<>();
        hindi.put("Male", "ZHgnZdEq4O7h0ag188k4");
        hindi.put("Female", "lcmwQ2WEzsYG07P3wAMc");

        Map<String, String> gujarati = new LinkedHashMap<>();
        gujarati.put("Female", "m263W1kly0RUFc6HLEu2");
        gujarati.put("Male", "ZHgnZdEq4O7h0ag188k4");

        Map<String, String> marathi = new LinkedHashMap<>();
        marathi.put("Female", "m263W1kly0RUFc6HLEu2");
        marathi.put("Male", "ZHgnZdEq4O7h0ag188k4");

        Map<String, String> spanish = new LinkedHashMap<>();
        spanish.put("Male", "CwhRBWXzGAHq8TQ4Fs17");
        spanish.put("Female", "hpp4J3VqNfWAUOO0d1Us");

        Map<String, String> german = new LinkedHashMap<>();
        german.put("Male", "FTNCalFNG5bRnkkaP5Ug");
        german.put("Female", "dCnu06FiOZma2KVNUoPZ");

        response.put("English", english);
        response.put("Hindi", hindi);
        response.put("Gujarati", gujarati);
        response.put("Marathi", marathi);
        response.put("Spanish", spanish);
        response.put("German", german);

        return ResponseEntity.ok(response);
    }


    // =========================
    // Error Response
    // =========================
    private ResponseEntity<Map<String, Object>> errorResponse(
            HttpStatus status,
            String message
    ) {

        Map<String, Object> error = new LinkedHashMap<>();

        error.put("success", false);
        error.put("error", message);

        return ResponseEntity
                .status(status)
                .body(error);
    }


    // =========================
    // Request Record
    // =========================
    public record TTSRequest(
            String text,
            String language,
            String voice,
            String format
    ) {
    }
}