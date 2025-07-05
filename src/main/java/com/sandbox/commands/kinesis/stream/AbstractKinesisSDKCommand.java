package com.sandbox.commands.kinesis.stream;

import com.sandbox.commands.BaseCommand;
import picocli.CommandLine;
import software.amazon.awssdk.services.kinesis.KinesisClient;

import java.util.Properties;

/**
 * Base class for Kinesis SDK commands.
 * <br>Contains common properties.
 * <br>Initialises kinesis client.
 */
public abstract class AbstractKinesisSDKCommand extends BaseCommand {

    public static final String STREAM_NAME = "streamName";

    @CommandLine.Option(names = "--" + STREAM_NAME, description = "Stream name")
    protected String streamName;

    protected KinesisClient kinesisClient;

    public AbstractKinesisSDKCommand(Properties props) {
        super(props);
    }

    /**
     * Extenders must override {@link #executeKinesisCommand()} to implement logic.
     */
    @Override
    protected final void executeCommand() {
        kinesisClient = KinesisClient.builder()
                .region(baseAWSConfig.getRegion())
                .build();
        executeKinesisCommand();
        kinesisClient.close();
    }

    protected abstract void executeKinesisCommand();
}
