package com.sandbox.commands.kinesis.stream;

import picocli.CommandLine;

import java.util.Properties;

/**
 * Example of Kinesis Stream data producer implemented by AWS SDK
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/developing-producers-with-sdk.html">Develop producers using the Amazon Kinesis Data Streams API with the AWS SDK for Java</a>
 */
@CommandLine.Command(name = "putRecordsToKinesisStream", description = "Send data to Kinesis Stream by AWS SDK")
public class PutRecordsToKinesisStreamCommand extends AbstractKinesisSDKCommand {

    public PutRecordsToKinesisStreamCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeKinesisCommand() {
        System.out.println("Sending data to Kinesis Stream");

    }
}
