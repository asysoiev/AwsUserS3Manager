package com.sandbox.commands.kinesis.stream;

import picocli.CommandLine;

/**
 * Example of Kinesis Stream data subscriber
 */
@CommandLine.Command(name = "subscribeToKinesisStream", description = "Receive data from Kinesis Stream")
public class SubscribeToKinesisStreamCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Receiving data from Kinesis Stream");
        //https://docs.aws.amazon.com/streams/latest/dev/develop-consumers-sdk.html
    }
}
