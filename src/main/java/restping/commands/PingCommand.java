package restping.commands;

import restping.http.ApiClient;
import restping.model.ApiResponse;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

@Command(
        name = "ping",
        description = "Ping an API endpoint multiple times and show statistics"
)
public class PingCommand implements Runnable {

    @Parameters(index = "0", description = "The URL to ping")
    private String url;

    @Option(names = {"--count", "-c"}, description = "Number of times to ping", defaultValue = "5")
    private int count;

    @Override
    public void run() {
        ApiClient client = new ApiClient();

        System.out.println("Pinging " + url + " " + count + " times...");
        System.out.println("------------------------------------------");

        long totalMs = 0;
        long minMs = Long.MAX_VALUE;
        long maxMs = Long.MIN_VALUE;
        int successCount = 0;

        for (int i = 1; i <= count; i++) {
            try {
                ApiResponse response = client.get(url);

                if (response != null) {
                    String symbol = response.isSuccess() ? "✅" : "❌";
                    System.out.printf("[%d] %s Status: %d | Time: %dms%n",
                            i, symbol, response.statusCode, response.responseTimeMs);

                    totalMs += response.responseTimeMs;
                    minMs = Math.min(minMs, response.responseTimeMs);
                    maxMs = Math.max(maxMs, response.responseTimeMs);
                    if (response.isSuccess()) successCount++;
                } else {
                    System.out.printf("[%d] ❌ Failed to reach server%n", i);
                }

                // 0.5 sec delay between pings — avoids hammering the server
                Thread.sleep(500);

            } catch (Exception e) {
                System.out.printf("[%d] ❌ Error: %s%n", i, e.getMessage());
            }
        }

        System.out.println("------------------------------------------");
        System.out.printf("✅ Success : %d/%d%n", successCount, count);

        // Fix 1 — avg only if at least one success
        if (successCount > 0) {
            System.out.printf("⏱  Avg     : %dms%n", totalMs / successCount);
        } else {
            System.out.println("⏱  Avg     : N/A");
        }

        // Fix 2 — min/max only if at least one success
        if (successCount > 0) {
            System.out.printf("⬇️  Min     : %dms%n", minMs);
            System.out.printf("⬆️  Max     : %dms%n", maxMs);
        } else {
            System.out.println("⬇️  Min     : N/A");
            System.out.println("⬆️  Max     : N/A");
        }

        System.out.println("------------------------------------------");
    }
}