package com.sandbox.commands.kinesis.stream;

import com.sandbox.commands.BaseCommand;
import picocli.CommandLine;

import java.util.Properties;

/**
 * Example of Kinesis Stream data producer
 */
@CommandLine.Command(name = "putRecordsToKinesisStream", description = "Send data to Kinesis Stream")
public class PutRecordsToKinesisStreamCommand extends BaseCommand {

    public PutRecordsToKinesisStreamCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeCommand() {
        System.out.println("Sending data to Kinesis Stream");
        //https://docs.aws.amazon.com/streams/latest/dev/developing-producers-with-sdk.html
    }
}
