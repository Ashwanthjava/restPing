package restping.http;

import restping.model.ApiResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

public class ApiClient {

    private final HttpClient client;

    public ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public ApiResponse get(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            Instant start = Instant.now();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            long ms = Duration.between(start, Instant.now()).toMillis();

            return new ApiResponse(response.statusCode(), ms, response.body(), url);

        } catch (Exception e) {
            System.out.println("❌ Failed to reach: " + url);
            System.out.println("   Reason: " + e.getMessage());
            return null;
        }
    }
}