package restping;

import restping.commands.CheckCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import restping.commands.RunCommand;
import restping.commands.PingCommand;
import restping.commands.InspectCommand;

@Command(
        name = "restping",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "A CLI tool to test REST API endpoints",
        subcommands = { CheckCommand.class , PingCommand.class , RunCommand.class , InspectCommand.class }
)
public class Main implements Runnable {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}