package com.sandbox.commands.kinesis.stream;

import picocli.CommandLine;
import software.amazon.awssdk.services.kinesis.model.DeleteStreamRequest;

import java.util.Properties;

/**
 * Deletes Kinesis Stream
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/kinesis-using-sdk-java-delete-stream.html">Delete a stream</a>
 */
@CommandLine.Command(name = DeleteKinesisStreamCommand.COMMAND, description = "Deletes Kinesis Stream")
public class DeleteKinesisStreamCommand extends AbstractKinesisSDKCommand {

    public static final String COMMAND = "deleteKinesisStream";

    public DeleteKinesisStreamCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeKinesisCommand() {
        System.out.printf("Deleting Kinesis Stream: %s%n", streamName);
        DeleteStreamRequest deleteStreamRequest = DeleteStreamRequest.builder()
                .streamName(streamName)
                .build();
        kinesisClient.deleteStream(deleteStreamRequest);
        System.out.printf("The Kinesis Stream: %s was deleted %n", streamName);
    }
}
