package fr.azrotho.aibuildplugin.openrouter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

public class ModelsUtility {
    private static final Gson GSON = new Gson();
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private record ModelsResponse(List<Model> data) {}
    private record Model(String id, String name, Architecture architecture) {}
    private record Architecture(String modality) {}

    public static List<String> getTextModelNames(String apiKey) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/models"))
                .header("User-Agent", "AIBuildPlugin/1.0")
                .timeout(Duration.ofSeconds(15))
                .GET();

        // OpenRouter's /models endpoint is public. Only attach token if a real key is present.
        if (apiKey != null && !apiKey.isBlank() && !apiKey.equalsIgnoreCase("example_api_key")) {
            builder.header("Authorization", "Bearer " + apiKey);
        }

        HttpRequest request = builder.build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("OpenRouter API error HTTP " + response.statusCode() + ": " + response.body());
                return Collections.emptyList();
            }

            ModelsResponse payload = GSON.fromJson(response.body(), ModelsResponse.class);

            if (payload == null || payload.data() == null) {
                return Collections.emptyList();
            }

            return payload.data().stream()
                    .filter(m -> m.id() != null && !m.id().startsWith("~")
                              && (m.architecture() == null || m.architecture().modality() == null 
                                  || m.architecture().modality().endsWith("->text")
                                  || m.architecture().modality().contains("text")))
                    .map(Model::id)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            System.err.println("Exception while querying OpenRouter models: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
