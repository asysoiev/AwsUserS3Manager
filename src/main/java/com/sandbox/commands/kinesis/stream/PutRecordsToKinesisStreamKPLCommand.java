package com.sandbox.commands.kinesis.stream;

import com.sandbox.commands.BaseCommand;
import picocli.CommandLine;

import java.util.Properties;

/**
 * Example of Kinesis Stream data producer implemented by KPL
 * </br>
 * <a href="https://docs.aws.amazon.com/streams/latest/dev/developing-producers-with-kpl.html">Develop producers using the Amazon Kinesis Producer Library (KPL)</a>
 */
@CommandLine.Command(name = "putRecordsToKinesisStreamKPL", description = "Send data to Kinesis Stream by KPL")
public class PutRecordsToKinesisStreamKPLCommand extends BaseCommand {

    public PutRecordsToKinesisStreamKPLCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeCommand() {
        System.out.println("Sending data to Kinesis Stream");
        //https://docs.aws.amazon.com/streams/latest/dev/developing-producers-with-kpl.html
    }
}
