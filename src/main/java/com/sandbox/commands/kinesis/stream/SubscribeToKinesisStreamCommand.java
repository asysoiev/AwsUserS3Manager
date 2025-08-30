package com.sandbox.commands.kinesis.stream;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Example of Kinesis Stream data subscriber
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/develop-consumers-sdk.html">Develop consumers with the AWS SDK for Java</a>
 */
@CommandLine.Command(name = "subscribeToKinesisStream", description = "Receive data from Kinesis Stream")
public class SubscribeToKinesisStreamCommand extends AbstractKinesisSDKCommand {

    public static final Logger logger = LoggerFactory.getLogger(SubscribeToKinesisStreamCommand.class);

    public SubscribeToKinesisStreamCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeKinesisCommand() {
        logger.info("Receiving data from Kinesis Stream");
        List<Shard> shards = getShards();
        logger.info("Number of shards: {}", shards.size());
        while (true) {
            shards.forEach(shard -> {
                String shardIterator;
                GetShardIteratorRequest getShardIteratorRequest = GetShardIteratorRequest
                        .builder()
                        .streamName(streamName)
                        .shardId(shard.shardId())
                        .shardIteratorType(ShardIteratorType.TRIM_HORIZON)
                        .build();

                GetShardIteratorResponse shardIteratorResponse = kinesisClient.getShardIterator(getShardIteratorRequest);
                shardIterator = shardIteratorResponse.shardIterator();
                logger.info("Get records from shard iterator: {}", shardIterator);
                while (shardIterator != null) {
                    GetRecordsRequest getRecordsRequest = GetRecordsRequest
                            .builder()
                            .shardIterator(shardIterator)
                            .limit(25)
                            .build();

                    GetRecordsResponse getRecordsResult = kinesisClient.getRecords(getRecordsRequest);
                    List<Record> records = getRecordsResult.records();
                    logger.info("Retrieved records: {}", records.size());
                    records.forEach(record -> {
                        logger.info("Record seq: \"{}\", partitionKey: \"{}\", data: \"{}\"",
                                record.sequenceNumber(), record.partitionKey(), record.data().asUtf8String());
                    });
                    shardIterator = getRecordsResult.nextShardIterator();
                }
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private List<Shard> getShards() {
        RetryConfig config = RetryConfig.<List<Shard>>custom()
                .maxAttempts(Integer.MAX_VALUE)
                .waitDuration(Duration.ofSeconds(30))
                .retryExceptions(ResourceNotFoundException.class)
                .build();

        Retry retry = Retry.of("getShards-retry", config);
        retry.getEventPublisher()
                .onRetry(event -> logger.warn(
                        "Retry attempt #{}, waiting {}ms. Cause: {}",
                        event.getNumberOfRetryAttempts(),
                        event.getWaitInterval().toMillis(),
                        event.getLastThrowable() != null ? event.getLastThrowable().toString() : "n/a"));

        ListShardsRequest request = ListShardsRequest
                .builder().streamName(streamName)
                .build();

        Supplier<List<Shard>> supplier = Retry.decorateSupplier(retry, () -> kinesisClient.listShards(request).shards());

        List<Shard> shards;
        do {
            shards = supplier.get();
        } while (CollectionUtils.isEmpty(shards));

        return shards;
    }
}
