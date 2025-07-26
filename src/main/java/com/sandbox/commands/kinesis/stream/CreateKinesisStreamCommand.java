package com.sandbox.commands.kinesis.stream;

import picocli.CommandLine;
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest;
import software.amazon.awssdk.services.kinesis.model.CreateStreamResponse;
import software.amazon.awssdk.services.kinesis.model.DecreaseStreamRetentionPeriodRequest;

import java.util.Properties;

import static com.sandbox.utils.PropertyUtils.getIntValue;

/**
 * Creates Kinesis Stream
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/kinesis-using-sdk-java-create-stream.html">Create a stream using the APIs</a>
 */
@CommandLine.Command(name = CreateKinesisStreamCommand.COMMAND_NAME, description = "Creates Kinesis Stream")
public class CreateKinesisStreamCommand extends AbstractKinesisSDKCommand {

    public static final String COMMAND_NAME = "createKinesisStream";
    private static final String SHARDS_COUNT_PROP = COMMAND_NAME + ".shardsCount";
    @CommandLine.Option(names = "--" + SHARDS_COUNT_PROP, description = "Number of shards")
    protected int shardsCount;

    public CreateKinesisStreamCommand(Properties props) {
        super(props);
    }

    @Override
    protected void mergeKinesisCommandProperties(Properties props) {
        if (shardsCount == 0) {
            shardsCount = getIntValue(props, SHARDS_COUNT_PROP, 1);
        }
    }

    @Override
    protected void executeKinesisCommand() {
        System.out.printf("Creating Kinesis Stream: %s%n", streamName);
        if (shardsCount <= 0) {
            System.out.printf("Shards count: %d must be greater than 0%n", shardsCount);
            return;
        }
        CreateStreamRequest streamReq = CreateStreamRequest.builder()
                .streamName(streamName)
                .shardCount(shardsCount)
                .build();
        CreateStreamResponse createStreamResponse = kinesisClient.createStream(streamReq);
        System.out.printf("The Kinesis Stream: %s was created%n", streamName);

        //https://docs.aws.amazon.com/streams/latest/dev/kinesis-extended-retention.html
        int retentionPeriod = 24;
        DecreaseStreamRetentionPeriodRequest decreaseStreamRetentionPeriodRequest =
                DecreaseStreamRetentionPeriodRequest.builder()
                        .streamName(streamName)
                        .retentionPeriodHours(retentionPeriod)
                        .build();
        kinesisClient.decreaseStreamRetentionPeriod(decreaseStreamRetentionPeriodRequest);
        System.out.printf("Retention period was decreased to %d(hrs)%n", retentionPeriod);
    }
}
