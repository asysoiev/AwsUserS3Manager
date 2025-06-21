package com.sandbox.commands.kinesis.stream;

import picocli.CommandLine;

/**
 * Example of Kinesis Stream data producer
 */
@CommandLine.Command(name = "putRecordsToKinesisStream", description = "Send data to Kinesis Stream")
public class PutRecordsToKinesisStreamCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Sending data to Kinesis Stream");
        //https://docs.aws.amazon.com/streams/latest/dev/developing-producers-with-sdk.html
    }
}
