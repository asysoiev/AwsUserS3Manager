package com.sandbox.commands.kinesis.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.sandbox.commands.kinesis.stream.PutRecordsToKinesisStreamCommand.COMMAND_NAME;
import static com.sandbox.utils.PropertyUtils.getIntValue;
import static software.amazon.awssdk.utils.StringUtils.isEmpty;

/**
 * Example of Kinesis Stream data producer implemented by AWS SDK
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/developing-producers-with-sdk.html">Develop producers using the Amazon Kinesis Data Streams API with the AWS SDK for Java</a>
 */
@CommandLine.Command(name = COMMAND_NAME, description = "Send data to Kinesis Stream by AWS SDK")
public class PutRecordsToKinesisStreamCommand extends AbstractKinesisSDKCommand {

    public static final Logger logger = LoggerFactory.getLogger(PutRecordsToKinesisStreamCommand.class);

    public static final String COMMAND_NAME = "putRecordsToKinesisStream";
    private static final String RECORDS_COUNT_PROP = COMMAND_NAME + ".recordsCount";
    @CommandLine.Option(names = "--" + RECORDS_COUNT_PROP, description = "Number of records to put.")
    protected int recordsCount;

    public PutRecordsToKinesisStreamCommand(Properties props) {
        super(props);
    }

    @Override
    protected void mergeKinesisCommandProperties(Properties props) {
        if (recordsCount == 0) {
            recordsCount = getIntValue(props, RECORDS_COUNT_PROP, 10);
        }
    }

    @Override
    protected void executeKinesisCommand() {
        logger.info("Sending data to Kinesis Stream");
        if (recordsCount <= 10) {
            //put record
            String sequenceNumberOfPreviousRecord = null;
            for (int j = 0; j < recordsCount; j++) {
                SdkBytes data = SdkBytes.fromUtf8String(String.valueOf(j));
                String partitionKey = String.format("partitionKey-%d", j / 5);
                PutRecordRequest putRecordRequest = PutRecordRequest.builder()
                        .streamName(streamName)
                        .data(data)
                        .partitionKey(partitionKey)
                        .sequenceNumberForOrdering(sequenceNumberOfPreviousRecord)
                        .build();
                PutRecordResponse putRecordResponse = kinesisClient.putRecord(putRecordRequest);
                sequenceNumberOfPreviousRecord = putRecordResponse.sequenceNumber();
                logger.info("Put record result: ");
                logger.info("sequenceNumber: {}", sequenceNumberOfPreviousRecord);
                logger.info("shardId: {}", putRecordResponse.shardId());
            }
        } else {
            //put records
            List<PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<>();
            for (int i = 0; i < recordsCount; i++) {
                PutRecordsRequestEntry putRecordsRequestEntry = PutRecordsRequestEntry
                        .builder()
                        .data(SdkBytes.fromUtf8String(String.valueOf(i)))
                        .partitionKey(String.format("partitionKey-%d", i))
                        .build();
                putRecordsRequestEntryList.add(putRecordsRequestEntry);
            }
            PutRecordsRequest putRecordsRequest = PutRecordsRequest.builder()
                    .streamName(streamName)
                    .records(putRecordsRequestEntryList)
                    .build();

            PutRecordsResponse putRecordsResponse = kinesisClient.putRecords(putRecordsRequest);
            int resultCount = putRecordsResponse.records().size();
            logger.info("Put records count: {}", resultCount);
            logger.info("Failed: {}", putRecordsResponse.failedRecordCount());
            if (putRecordsResponse.failedRecordCount() != resultCount) {
                logger.info("Successful records: ");
                putRecordsResponse.records().stream()
                        .filter(r -> !isEmpty(r.sequenceNumber())).
                        forEach(record -> {
                            logger.info("Record #:{} Shard: {}", record.sequenceNumber(), record.shardId());
                        });
            }
            if (putRecordsResponse.failedRecordCount() > 0) {
                logger.info("Failed records: ");
                putRecordsResponse.records().stream()
                        .filter(r -> isEmpty(r.sequenceNumber())).
                        forEach(record -> {
                            logger.info("Error code:{}", record.errorCode());
                        });
            }
        }
    }
}
