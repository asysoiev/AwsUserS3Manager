package com.sandbox.commands.kinesis.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsResponse;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;

import java.util.List;
import java.util.Properties;

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
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private List<Shard> getShards() {
        ListShardsRequest request = ListShardsRequest
                .builder().streamName(streamName)
                .build();

        ListShardsResponse listShardsResponse = kinesisClient.listShards(request);
        return listShardsResponse.shards();
    }
}
