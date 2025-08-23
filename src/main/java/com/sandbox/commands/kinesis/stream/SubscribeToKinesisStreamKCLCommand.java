package com.sandbox.commands.kinesis.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import picocli.CommandLine;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.KinesisClientUtil;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;
import software.amazon.kinesis.retrieval.polling.PollingConfig;

import java.util.Properties;
import java.util.UUID;

/**
 * Example of Kinesis Stream data subscriber implemented by KCL
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/kcl.html">Use Kinesis Client Library</a>
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/develop-kcl-consumers-java.html">Develop consumers with KCL in Java</a>
 * </br>
 * Check DynamoDB console. KCL creates a table for each client, verify that it is deleted.
 * </br>
 * Must be run on Linux OS, see <a href="https://docs.aws.amazon.com/streams/latest/dev/develop-kcl-consumers-java.html#develop-kcl-consumers-java-prerequisites">Prerequisites</a>
 */
@CommandLine.Command(name = "subscribeToKinesisStreamKCL", description = "Receive data from Kinesis Stream by KCL")
public class SubscribeToKinesisStreamKCLCommand extends AbstractKinesisCommand {
    private static final Logger logger = LoggerFactory.getLogger(SubscribeToKinesisStreamKCLCommand.class);

    public SubscribeToKinesisStreamKCLCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeCommand() {
        logger.info("Receiving data from Kinesis Stream");
        SampleConsumer sampleConsumer = new SampleConsumer(streamName, baseAWSConfig.getRegion());
        sampleConsumer.run();
    }

    /**
     * <a href="https://docs.aws.amazon.com/streams/latest/dev/develop-kcl-consumers-java.html#implementation-main">Main Consumer Application</a>
     */
    static class SampleConsumer {
        private final String streamName;
        private final Region region;
        private final KinesisAsyncClient kinesisClient;

        public SampleConsumer(String streamName, Region region) {
            this.streamName = streamName;
            this.region = region;
            this.kinesisClient = KinesisClientUtil.createKinesisAsyncClient(KinesisAsyncClient.builder().region(this.region));
        }

        public void run() {
            DynamoDbAsyncClient dynamoDbAsyncClient = DynamoDbAsyncClient.builder().region(region).build();
            CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient.builder().region(region).build();

            ConfigsBuilder configsBuilder = new ConfigsBuilder(
                    streamName,
                    streamName,
                    kinesisClient,
                    dynamoDbAsyncClient,
                    cloudWatchClient,
                    UUID.randomUUID().toString(),
                    new SampleRecordProcessorFactory()
            );

            //https://docs.aws.amazon.com/streams/latest/dev/develop-kcl-consumers-java.html#implementation-scheduler
            Scheduler scheduler = new Scheduler(
                    configsBuilder.checkpointConfig(),
                    configsBuilder.coordinatorConfig(),
                    configsBuilder.leaseManagementConfig(),
                    configsBuilder.lifecycleConfig(),
                    configsBuilder.metricsConfig(),
                    configsBuilder.processorConfig(),
                    configsBuilder.retrievalConfig().retrievalSpecificConfig(new PollingConfig(streamName, kinesisClient))
            );

            Thread schedulerThread = new Thread(scheduler);
            schedulerThread.setDaemon(true);
            schedulerThread.start();
        }
    }

    /**
     * @see <a href="https://docs.aws.amazon.com/streams/latest/dev/develop-kcl-consumers-java.html#implementation-recordprocessorfactory">RecordProcessorFactory</a>
     */
    static class SampleRecordProcessorFactory implements ShardRecordProcessorFactory {
        @Override
        public ShardRecordProcessor shardRecordProcessor() {
            return new SampleRecordProcessor();
        }
    }

    /**
     * @see <a href="https://docs.aws.amazon.com/streams/latest/dev/develop-kcl-consumers-java.html#implementation-recordprocessor">RecordProcessor</a>
     */
    static class SampleRecordProcessor implements ShardRecordProcessor {
        private static final String SHARD_ID_MDC_KEY = "ShardId";
        private String shardId;

        @Override
        public void initialize(InitializationInput initializationInput) {
            shardId = initializationInput.shardId();
            MDC.put(SHARD_ID_MDC_KEY, shardId);
            try {
                logger.info("Initializing @ Sequence: {}", initializationInput.extendedSequenceNumber());
            } finally {
                MDC.remove(SHARD_ID_MDC_KEY);
            }
        }

        @Override
        public void processRecords(ProcessRecordsInput processRecordsInput) {
            MDC.put(SHARD_ID_MDC_KEY, shardId);
            try {
                logger.info("Processing {} record(s)", processRecordsInput.records().size());
                processRecordsInput.records().forEach(r ->
                        logger.info("Processing record pk: {} -- Seq: {}", r.partitionKey(), r.sequenceNumber())
                );

                // Checkpoint periodically
                processRecordsInput.checkpointer().checkpoint();
            } catch (Throwable t) {
                logger.error("Caught throwable while processing records. Aborting.", t);
            } finally {
                MDC.remove(SHARD_ID_MDC_KEY);
            }
        }

        @Override
        public void leaseLost(LeaseLostInput leaseLostInput) {
            MDC.put(SHARD_ID_MDC_KEY, shardId);
            try {
                logger.info("Lost lease, so terminating.");
            } finally {
                MDC.remove(SHARD_ID_MDC_KEY);
            }
        }

        @Override
        public void shardEnded(ShardEndedInput shardEndedInput) {
            MDC.put(SHARD_ID_MDC_KEY, shardId);
            try {
                logger.info("Reached shard end checkpointing.");
                shardEndedInput.checkpointer().checkpoint();
            } catch (ShutdownException | InvalidStateException e) {
                logger.error("Exception while checkpointing at shard end. Giving up.", e);
            } finally {
                MDC.remove(SHARD_ID_MDC_KEY);
            }
        }

        @Override
        public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
            MDC.put(SHARD_ID_MDC_KEY, shardId);
            try {
                logger.info("Scheduler is shutting down, checkpointing.");
                shutdownRequestedInput.checkpointer().checkpoint();
            } catch (ShutdownException | InvalidStateException e) {
                logger.error("Exception while checkpointing at requested shutdown. Giving up.", e);
            } finally {
                MDC.remove(SHARD_ID_MDC_KEY);
            }
        }
    }
}
