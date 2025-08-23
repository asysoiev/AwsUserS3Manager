package com.sandbox.commands.kinesis.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static final Logger logger = LoggerFactory.getLogger(DeleteKinesisStreamCommand.class);

    public static final String COMMAND = "deleteKinesisStream";

    public DeleteKinesisStreamCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeKinesisCommand() {
        logger.info("Deleting Kinesis Stream: {}", streamName);
        DeleteStreamRequest deleteStreamRequest = DeleteStreamRequest.builder()
                .streamName(streamName)
                .build();
        kinesisClient.deleteStream(deleteStreamRequest);
        logger.info("The Kinesis Stream: {} was deleted", streamName);
    }
}
