package fr.azrotho.aibuildplugin.openrouter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

public class ModelsUtility {
private static final Gson GSON = new Gson();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private record ModelsResponse(List<Model> data) {}
    private record Model(String id, String name, Architecture architecture) {}
    private record Architecture(String modality) {}

    public static List<String> getTextModelNames(String apiKey) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/models"))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Erreur API : " + response.statusCode());
                return Collections.emptyList();
            }

            ModelsResponse payload = GSON.fromJson(response.body(), ModelsResponse.class);

            if (payload == null || payload.data() == null) {
                return Collections.emptyList();
            }

            return payload.data().stream()
                    .filter(m -> m.architecture() != null 
                              && m.architecture().modality() != null 
                              && m.architecture().modality().endsWith("->text")
                              && !m.id.startsWith("~"))
                    .map(Model::id)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
