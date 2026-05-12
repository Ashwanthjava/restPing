package restping.printer;

import restping.model.ApiResponse;

public class ResponsePrinter {

    public void print(ApiResponse response) {
        if (response == null) return;

        String symbol = response.isSuccess() ? "✅" : "❌";

        System.out.println("------------------------------------------");
        System.out.println("🌐 URL    : " + response.url);
        System.out.println(symbol + " Status : " + response.statusCode);
        System.out.println("⏱  Time   : " + response.responseTimeMs + "ms");
        System.out.println("📦 Body   : " +
                response.body.substring(0, Math.min(200, response.body.length())) + "...");
        System.out.println("------------------------------------------");
    }
}