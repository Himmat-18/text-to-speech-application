package com.example.tts_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TTSService {

    @Value("${elevenlabs.api.key}")
    private String apiKey;

    // IMPORTANT:
    // FFmpeg executable ka exact Windows path
    private final String ffmpegPath =
            "C:\\Users\\Himmat Singh Yadav\\Downloads\\ffmpeg-9.0.1-essentials_build\\ffmpeg-9.0.1-essentials_build\\bin\\ffmpeg.exe";


    // ==========================================
    // NORMAL TEXT TO SPEECH - MP3
    // ==========================================

    public byte[] generateSpeech(
            String text,
            String language,
            String voice
    ) throws Exception {

        System.out.println(
                "========== ELEVENLABS TTS REQUEST =========="
        );

        System.out.println("Text: " + text);
        System.out.println("Language: " + language);
        System.out.println("Voice: " + voice);

        String voiceId = voice;

        String url =
                "https://api.elevenlabs.io/v1/text-to-speech/"
                        + voiceId;

        String jsonBody =
                "{"
                        + "\"text\":\""
                        + text.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        + "\","
                        + "\"model_id\":\"eleven_multilingual_v2\","
                        + "\"output_format\":\"mp3_44100_128\""
                        + "}";

        HttpClient client =
                HttpClient.newHttpClient();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "xi-api-key",
                                apiKey
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        jsonBody
                                )
                        )
                        .build();

        HttpResponse<byte[]> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofByteArray()
                );

        System.out.println(
                "HTTP Status: "
                        + response.statusCode()
        );

        System.out.println(
                "Response Size: "
                        + response.body().length
        );

        if (response.statusCode() != 200) {

            String error =
                    new String(
                            response.body(),
                            java.nio.charset.StandardCharsets.UTF_8
                    );

            System.out.println(
                    "ElevenLabs Error: "
                            + error
            );

            throw new RuntimeException(
                    "ElevenLabs HTTP Error: "
                            + response.statusCode()
                            + " - "
                            + error
            );
        }

        System.out.println(
                "Audio generated successfully!"
        );

        System.out.println(
                "============================================"
        );

        return response.body();
    }


    // ==========================================
    // CONVERT MP3 TO WAV
    // ==========================================

    public byte[] convertToWav(
            byte[] mp3Audio
    ) throws Exception {

        Path tempDirectory =
                Files.createTempDirectory("tts-");

        Path inputMp3 =
                tempDirectory.resolve("input.mp3");

        Path outputWav =
                tempDirectory.resolve("output.wav");

        try {

            Files.write(
                    inputMp3,
                    mp3Audio
            );

            ProcessBuilder processBuilder =
                    new ProcessBuilder(
                            ffmpegPath,
                            "-y",
                            "-i",
                            inputMp3.toString(),
                            "-ar",
                            "44100",
                            "-ac",
                            "2",
                            outputWav.toString()
                    );

            processBuilder.redirectErrorStream(true);

            Process process =
                    processBuilder.start();

            String ffmpegOutput =
                    new String(
                            process.getInputStream().readAllBytes(),
                            java.nio.charset.StandardCharsets.UTF_8
                    );

            int exitCode =
                    process.waitFor();

            System.out.println(
                    "========== MP3 TO WAV =========="
            );

            System.out.println(ffmpegOutput);

            System.out.println(
                    "FFmpeg Exit Code: "
                            + exitCode
            );

            if (exitCode != 0 || !Files.exists(outputWav)) {

                throw new RuntimeException(
                        "FFmpeg WAV conversion failed."
                );
            }

            return Files.readAllBytes(outputWav);

        } finally {

            deleteFile(inputMp3);
            deleteFile(outputWav);
            deleteDirectory(tempDirectory);
        }
    }


    // ==========================================
    // CONVERT MP3 TO OGG
    // ==========================================

    public byte[] convertToOgg(
            byte[] mp3Audio
    ) throws Exception {

        Path tempDirectory =
                Files.createTempDirectory("tts-");

        Path inputMp3 =
                tempDirectory.resolve("input.mp3");

        Path outputOgg =
                tempDirectory.resolve("output.ogg");

        try {

            Files.write(
                    inputMp3,
                    mp3Audio
            );

            ProcessBuilder processBuilder =
                    new ProcessBuilder(
                            ffmpegPath,
                            "-y",
                            "-i",
                            inputMp3.toString(),
                            "-c:a",
                            "libvorbis",
                            "-q:a",
                            "5",
                            outputOgg.toString()
                    );

            processBuilder.redirectErrorStream(true);

            Process process =
                    processBuilder.start();

            String ffmpegOutput =
                    new String(
                            process.getInputStream().readAllBytes(),
                            java.nio.charset.StandardCharsets.UTF_8
                    );

            int exitCode =
                    process.waitFor();

            System.out.println(
                    "========== MP3 TO OGG =========="
            );

            System.out.println(ffmpegOutput);

            System.out.println(
                    "FFmpeg Exit Code: "
                            + exitCode
            );

            if (exitCode != 0 || !Files.exists(outputOgg)) {

                throw new RuntimeException(
                        "FFmpeg OGG conversion failed."
                );
            }

            return Files.readAllBytes(outputOgg);

        } finally {

            deleteFile(inputMp3);
            deleteFile(outputOgg);
            deleteDirectory(tempDirectory);
        }
    }


    // ==========================================
    // DELETE TEMP FILE
    // ==========================================

    private void deleteFile(
            Path path
    ) {

        try {

            if (path != null) {
                Files.deleteIfExists(path);
            }

        } catch (IOException ignored) {

        }
    }


    // ==========================================
    // DELETE TEMP DIRECTORY
    // ==========================================

    private void deleteDirectory(
            Path directory
    ) {

        try {

            if (directory != null) {
                Files.deleteIfExists(directory);
            }

        } catch (IOException ignored) {

        }
    }


    // ==========================================
    // VOICE INFORMATION CHECK
    // ==========================================

    public void checkVoice(
            String voiceId
    ) throws Exception {

        String url =
                "https://api.elevenlabs.io/v1/voices/"
                        + voiceId;

        HttpClient client =
                HttpClient.newHttpClient();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "xi-api-key",
                                apiKey
                        )
                        .GET()
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println(
                "========== VOICE CHECK =========="
        );

        System.out.println(
                "Voice ID: "
                        + voiceId
        );

        System.out.println(
                "HTTP Status: "
                        + response.statusCode()
        );

        System.out.println("Response:");

        System.out.println(
                response.body()
        );

        System.out.println(
                "================================="
        );
    }


    // ==========================================
    // FIXED ROGER TEST
    // ==========================================

    public byte[] generateRogerTest()
            throws Exception {

        String voiceId =
                "CwhRBWXzGAHq8TQ4Fs17";

        String text =
                "Hello Himmat. This is a test of the Roger voice.";

        System.out.println(
                "========== FIXED ROGER TEST =========="
        );

        System.out.println(
                "Voice ID: "
                        + voiceId
        );

        System.out.println(
                "Text: "
                        + text
        );

        String url =
                "https://api.elevenlabs.io/v1/text-to-speech/"
                        + voiceId;

        String jsonBody =
                "{"
                        + "\"text\":\""
                        + text
                        + "\","
                        + "\"model_id\":\"eleven_multilingual_v2\","
                        + "\"output_format\":\"mp3_44100_128\""
                        + "}";

        HttpClient client =
                HttpClient.newHttpClient();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "xi-api-key",
                                apiKey
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        jsonBody
                                )
                        )
                        .build();

        HttpResponse<byte[]> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofByteArray()
                );

        System.out.println(
                "HTTP Status: "
                        + response.statusCode()
        );

        System.out.println(
                "Audio Size: "
                        + response.body().length
        );

        if (response.statusCode() != 200) {

            String error =
                    new String(
                            response.body(),
                            java.nio.charset.StandardCharsets.UTF_8
                    );

            System.out.println(
                    "ElevenLabs Error: "
                            + error
            );

            throw new RuntimeException(
                    "Roger test failed: "
                            + response.statusCode()
                            + " - "
                            + error
            );
        }

        System.out.println(
                "Roger test audio generated successfully!"
        );

        System.out.println(
                "======================================"
        );

        return response.body();
    }
}