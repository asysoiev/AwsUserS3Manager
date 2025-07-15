package com.sandbox.commands.kinesis.stream;

import com.sandbox.commands.BaseCommand;
import picocli.CommandLine;

import java.util.Properties;

/**
 * Example of Kinesis Stream data subscriber implemented by KCL
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/kcl.html">Use Kinesis Client Library</a>
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/shared-throughput-kcl-consumers.html">Shared throughput KCL consumers</a>
 */
@CommandLine.Command(name = "subscribeToKinesisStreamKCL", description = "Receive data from Kinesis Stream by KCL")
public class SubscribeToKinesisStreamKCLCommand extends BaseCommand {

    public SubscribeToKinesisStreamKCLCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeCommand() {
        System.out.println("Receiving data from Kinesis Stream");
    }
}
