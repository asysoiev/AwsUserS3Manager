package com.sandbox.commands.kinesis.stream;

import picocli.CommandLine;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.sandbox.commands.kinesis.stream.PutRecordsToKinesisStreamCommand.COMMAND_NAME;
import static software.amazon.awssdk.utils.StringUtils.isEmpty;

/**
 * Example of Kinesis Stream data producer implemented by AWS SDK
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/developing-producers-with-sdk.html">Develop producers using the Amazon Kinesis Data Streams API with the AWS SDK for Java</a>
 */
@CommandLine.Command(name = COMMAND_NAME, description = "Send data to Kinesis Stream by AWS SDK")
public class PutRecordsToKinesisStreamCommand extends AbstractKinesisSDKCommand {


    public static final String COMMAND_NAME = "putRecordsToKinesisStream";
    private static final String RECORDS_COUNT_PROP = COMMAND_NAME + ".recordsCount";
    @CommandLine.Option(names = "--" + RECORDS_COUNT_PROP, description = "Number of records to put.")
    protected int recordsCount;

    public PutRecordsToKinesisStreamCommand(Properties props) {
        super(props);
    }

    @Override
    protected void mergeFieldsWithProperties(Properties props) {
        if (recordsCount == 0) {
            recordsCount = (int) props.getOrDefault(RECORDS_COUNT_PROP, 10);
        }
    }

    @Override
    protected void executeKinesisCommand() {
        System.out.println("Sending data to Kinesis Stream");
        //stoped at https://docs.aws.amazon.com/streams/latest/dev/developing-producers-with-sdk.html#kinesis-using-sdk-java-add-data-to-stream
        if (recordsCount <= 10) {
            //put record
        } else {
            //put records
            List<PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<>();
            for (int i = 0; i < recordsCount; i++) {
                PutRecordsRequestEntry putRecordsRequestEntry = PutRecordsRequestEntry
                        .builder()
                        .data(SdkBytes.fromString(String.valueOf(i), Charset.defaultCharset()))
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
            System.out.println("Put records count: " + resultCount);
            System.out.println("Failed: " + putRecordsResponse.failedRecordCount());
            if (putRecordsResponse.failedRecordCount() != resultCount) {
                System.out.println("Successful records: ");
                putRecordsResponse.records().stream()
                        .filter(r -> !isEmpty(r.sequenceNumber())).
                        forEach(record -> {
                            System.out.printf("Record #:%s Shard: %s%n", record.sequenceNumber(), record.shardId());
                        });
            }
            if (putRecordsResponse.failedRecordCount() > 0) {
                System.out.println("Failed records: ");
                putRecordsResponse.records().stream()
                        .filter(r -> isEmpty(r.sequenceNumber())).
                        forEach(record -> {
                            System.out.printf("Error code:%s%n", record.errorCode());
                        });
            }
        }
    }
}
