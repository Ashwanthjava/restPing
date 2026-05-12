package restping.commands;

import restping.http.ApiClient;
import restping.model.ApiResponse;
import restping.printer.ResponsePrinter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = "check",
        description = "Check if an API endpoint is alive and show its status"
)
public class CheckCommand implements Runnable {

    @Parameters(index = "0", description = "The URL to check")
    private String url;

    @Override
    public void run() {
        ApiClient client = new ApiClient();
        ResponsePrinter printer = new ResponsePrinter();
        System.out.println("Checking: " + url);
        ApiResponse response = client.get(url);
        printer.print(response);
    }
}