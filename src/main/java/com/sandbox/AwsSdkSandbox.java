package com.sandbox;

import com.sandbox.commands.kinesis.stream.CreateKinesisStreamCommand;
import com.sandbox.commands.kinesis.stream.DeleteKinesisStreamCommand;
import com.sandbox.commands.kinesis.stream.PutRecordsToKinesisStreamCommand;
import com.sandbox.commands.kinesis.stream.PutRecordsToKinesisStreamKPLCommand;
import com.sandbox.commands.kinesis.stream.SubscribeToKinesisStreamCommand;
import com.sandbox.commands.kinesis.stream.SubscribeToKinesisStreamKCLCommand;
import com.sandbox.commands.s3.CreateS3BucketCommand;
import com.sandbox.commands.s3.DeleteS3BucketCommand;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@CommandLine.Command(name = "cli", mixinStandardHelpOptions = true,
        subcommands = {
                CommandLine.HelpCommand.class
        })
public class AwsSdkSandbox implements Runnable {

    public static void main(String[] args) {
        // Load from properties if exists
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config/application.properties")) {
            props.load(input);
        } catch (IOException e) {
            System.out.println("No config.properties file found. Using defaults or command-line args.");
        }

        CommandLine commandLine = new CommandLine(new AwsSdkSandbox())
                .addSubcommand(new CreateS3BucketCommand(props))
                .addSubcommand(new DeleteS3BucketCommand(props))
                .addSubcommand(new CreateKinesisStreamCommand(props))
                .addSubcommand(new DeleteKinesisStreamCommand(props))
                .addSubcommand(new PutRecordsToKinesisStreamCommand(props))
                .addSubcommand(new SubscribeToKinesisStreamCommand(props))
                .addSubcommand(new PutRecordsToKinesisStreamKPLCommand(props))
                .addSubcommand(new SubscribeToKinesisStreamKCLCommand(props));
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        CommandLine cli = new CommandLine(this);
        System.out.printf("Specify a subcommand: %s%n", cli.getSubcommands().keySet());
    }
}
