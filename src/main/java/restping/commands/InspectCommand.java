package restping.commands;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import restping.analyzer.JsonAnalyzer;
import restping.http.ApiClient;
import restping.model.ApiResponse;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

@Command(
        name = "inspect",
        description = "Fetch a URL and analyze its JSON structure with suggested Java class"
)
public class InspectCommand implements Runnable {

    @Parameters(index = "0", description = "The URL to inspect")
    private String url;

    @Option(names = {"--class-name", "-n"}, description = "Java class name to suggest", defaultValue = "Root")
    private String className;

    @Override
    public void run() {
        ApiClient client = new ApiClient();
        JsonAnalyzer analyzer = new JsonAnalyzer();

        System.out.println("Inspecting: " + url);
        System.out.println("------------------------------------------");

        // Step 1 — Fetch the URL
        ApiResponse response = client.get(url);

        if (response == null || !response.isSuccess()) {
            System.out.println("❌ Failed to fetch URL");
            return;
        }

        // Step 2 — Parse JSON dynamically
        JsonElement element;
        try {
            element = JsonParser.parseString(response.body);
        } catch (Exception e) {
            System.out.println("❌ Response is not valid JSON");
            System.out.println("   Reason: " + e.getMessage());
            return;
        }

        // Step 3 — Print tree structure
        System.out.println("📋 JSON Structure");
        System.out.println("Root: Object");
        analyzer.printTree(element, "");
        System.out.println();

        // Step 4 — Print suggested Java class
        System.out.println("💡 Suggested Java Class:");
        System.out.println("------------------------------------------");
        System.out.println(analyzer.generateClass(className, element));
        System.out.println("------------------------------------------");
        System.out.println("✅ Copy this class into your project and use with Gson");
    }
}