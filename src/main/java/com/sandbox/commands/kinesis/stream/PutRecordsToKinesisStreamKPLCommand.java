package com.sandbox.commands.kinesis.stream;

import com.amazonaws.services.kinesis.producer.Attempt;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.UserRecordResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import software.amazon.awssdk.core.SdkBytes;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.sandbox.commands.kinesis.stream.PutRecordsToKinesisStreamKPLCommand.COMMAND_NAME;
import static com.sandbox.utils.PropertyUtils.getIntValue;

/**
 * Example of Kinesis Stream data producer implemented by KPL
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/developing-producers-with-kpl.html">Develop producers using the Amazon Kinesis Producer Library (KPL)</a>
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/kinesis-kpl-writing.html">Write to your Kinesis data stream using the KPL</a>
 */
@CommandLine.Command(name = COMMAND_NAME, description = "Send data to Kinesis Stream by KPL")
public class PutRecordsToKinesisStreamKPLCommand extends AbstractKinesisCommand {

    public static final Logger logger = LoggerFactory.getLogger(PutRecordsToKinesisStreamKPLCommand.class);

    public static final String COMMAND_NAME = "putRecordsToKinesisStreamKPL";
    private static final String RECORDS_COUNT_PROP = COMMAND_NAME + ".recordsCount";
    @CommandLine.Option(names = "--" + RECORDS_COUNT_PROP, description = "Number of records to put.")
    protected int recordsCount;

    public PutRecordsToKinesisStreamKPLCommand(Properties props) {
        super(props);
    }

    @Override
    protected void mergeKinesisCommandProperties(Properties props) {
        if (recordsCount == 0) {
            recordsCount = getIntValue(props, RECORDS_COUNT_PROP, 10);
        }
    }

    @Override
    protected void executeCommand() {
        logger.info("Sending data to Kinesis Stream");
        //https://docs.aws.amazon.com/streams/latest/dev/kinesis-kpl-writing.html
        // KinesisProducer gets credentials automatically like
        // DefaultAWSCredentialsProviderChain.
        // It also gets region automatically from the EC2 metadata service.
        KinesisProducer kinesis = new KinesisProducer();
        List<Future<UserRecordResult>> putFutures = new LinkedList<Future<UserRecordResult>>();
        // Put some records
        for (int i = 0; i < recordsCount; ++i) {
            ByteBuffer data = SdkBytes.fromUtf8String(String.valueOf(i)).asByteBuffer();
            String partitionKey = String.format("partitionKey-%d", i / 5);
            // doesn't block
            putFutures.add(kinesis.addUserRecord(streamName, partitionKey, data));
        }
        // Wait for puts to finish and check the results
        for (Future<UserRecordResult> f : putFutures) {
            UserRecordResult result; // this does block
            try {
                result = f.get();
                if (result.isSuccessful()) {
                    logger.info("Put record into shard {}", result.getShardId());
                } else {
                    for (Attempt attempt : result.getAttempts()) {
                        // Analyze and respond to the failure
                        logger.info("Failed, SequenceNumber:{}", result.getSequenceNumber());
                        logger.info("Error code:{}", attempt.getErrorCode());
                        logger.info("Error message:{}", attempt.getErrorMessage());
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
