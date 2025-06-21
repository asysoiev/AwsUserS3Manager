package com.sandbox.commands.kinesis.stream;

import com.sandbox.commands.BaseCommand;
import picocli.CommandLine;

import java.util.Properties;

/**
 * Example of Kinesis Stream data subscriber
 */
@CommandLine.Command(name = "subscribeToKinesisStream", description = "Receive data from Kinesis Stream")
public class SubscribeToKinesisStreamCommand extends BaseCommand {

    public SubscribeToKinesisStreamCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeCommand() {
        System.out.println("Receiving data from Kinesis Stream");
        //https://docs.aws.amazon.com/streams/latest/dev/develop-consumers-sdk.html
    }
}
