package com.sandbox;

import com.sandbox.commands.kinesis.stream.CreateKinesisStreamCommand;
import com.sandbox.commands.kinesis.stream.DeleteKinesisStreamCommand;
import com.sandbox.commands.kinesis.stream.PutRecordsToKinesisStreamCommand;
import com.sandbox.commands.kinesis.stream.PutRecordsToKinesisStreamKPLCommand;
import com.sandbox.commands.kinesis.stream.SubscribeToKinesisStreamCommand;
import com.sandbox.commands.kinesis.stream.SubscribeToKinesisStreamKCLCommand;
import com.sandbox.commands.polly.PollyDemoCommand;
import com.sandbox.commands.s3.CreateS3BucketCommand;
import com.sandbox.commands.s3.DeleteS3BucketCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@CommandLine.Command(name = "cli", mixinStandardHelpOptions = true,
        subcommands = {
                CommandLine.HelpCommand.class
        })
public class AwsSdkSandbox implements Runnable {

    private static Logger logger;

    public static void main(String[] args) {
        if (args.length > 0) {
            // create separate log file for each command
            System.setProperty("LOG_FILE", args[0]);
        }
        logger = LoggerFactory.getLogger(AwsSdkSandbox.class);//should be called after LOG_FILE var definition

        // Load from properties if exists
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config/application.properties")) {
            props.load(input);
        } catch (IOException e) {
            logger.info("No config.properties file found. Using defaults or command-line args.");
        }

        CommandLine commandLine = new CommandLine(new AwsSdkSandbox())
                .addSubcommand(new CreateS3BucketCommand(props))
                .addSubcommand(new DeleteS3BucketCommand(props))
                .addSubcommand(new CreateKinesisStreamCommand(props))
                .addSubcommand(new DeleteKinesisStreamCommand(props))
                .addSubcommand(new PutRecordsToKinesisStreamCommand(props))
                .addSubcommand(new SubscribeToKinesisStreamCommand(props))
                .addSubcommand(new PutRecordsToKinesisStreamKPLCommand(props))
                .addSubcommand(new SubscribeToKinesisStreamKCLCommand(props))
                .addSubcommand(new PollyDemoCommand(props));
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        CommandLine cli = new CommandLine(this);
        logger.info("Specify a subcommand: {}", cli.getSubcommands().keySet());
    }
}
