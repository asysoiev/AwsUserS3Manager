package com.sandbox.commands.kinesis.stream;

import picocli.CommandLine;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsResponse;
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

    public SubscribeToKinesisStreamCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeKinesisCommand() {
        System.out.println("Receiving data from Kinesis Stream");
        List<Shard> shards = getShards();
        System.out.println("Number of shards: " + shards.size());
        while (true) {
            shards.forEach(shard -> {
                String shardIterator;
                GetShardIteratorRequest getShardIteratorRequest = GetShardIteratorRequest
                        .builder()
                        .streamName(streamName)
                        .streamName(shard.shardId())
                        .shardIteratorType(ShardIteratorType.TRIM_HORIZON)
                        .build();

                GetShardIteratorResponse shardIteratorResponse = kinesisClient.getShardIterator(getShardIteratorRequest);
                shardIterator = shardIteratorResponse.shardIterator();
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
