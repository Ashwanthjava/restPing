package restping.commands;

import com.google.gson.Gson;
import restping.http.ApiClient;
import restping.model.ApiResponse;
import restping.model.Endpoint;
import restping.model.EndpointConfig;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.FileReader;
import java.io.IOException;

@Command(
        name = "run",
        description = "Test multiple API endpoints from a JSON config file"
)
public class RunCommand implements Runnable {

    @Parameters(index = "0", description = "Path to endpoints JSON file")
    private String filePath;

    @Override
    public void run() {
        // Step 1 — Read and parse the JSON file
        Gson gson = new Gson();
        EndpointConfig config;

        try {
            FileReader reader = new FileReader(filePath);
            config = gson.fromJson(reader, EndpointConfig.class);
        } catch (IOException e) {
            System.out.println("❌ Could not read file: " + filePath);
            System.out.println("   Reason: " + e.getMessage());
            return;
        }

        // Step 2 — Validate the file
        if (config == null || config.endpoints == null || config.endpoints.isEmpty()) {
            System.out.println("❌ No endpoints found in: " + filePath);
            return;
        }

        // Step 3 — Test each endpoint
        ApiClient client = new ApiClient();
        int total = config.endpoints.size();
        int successCount = 0;

        System.out.println("Running " + total + " endpoints...");
        System.out.println("------------------------------------------");

        for (Endpoint endpoint : config.endpoints) {
            ApiResponse response = client.get(endpoint.url);

            if (response != null) {
                String symbol = response.isSuccess() ? "✅" : "❌";
                System.out.printf("%s %-20s | %d | %dms%n",
                        symbol, endpoint.name, response.statusCode, response.responseTimeMs);
                if (response.isSuccess()) successCount++;
            } else {
                System.out.printf("❌ %-20s | Failed%n", endpoint.name);
            }
        }

        // Step 4 — Print summary
        System.out.println("------------------------------------------");
        System.out.printf("Result: %d/%d endpoints healthy%n", successCount, total);
        System.out.println("------------------------------------------");
    }
}