package restping.model;

public class ApiResponse {
    public int statusCode;
    public long responseTimeMs;
    public String body;
    public String url;

    public ApiResponse(int statusCode, long responseTimeMs, String body, String url) {
        this.statusCode = statusCode;
        this.responseTimeMs = responseTimeMs;
        this.body = body;
        this.url = url;
    }

    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }
}