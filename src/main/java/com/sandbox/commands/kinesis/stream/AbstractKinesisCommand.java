package com.sandbox.commands.kinesis.stream;

import com.sandbox.commands.BaseCommand;
import picocli.CommandLine;
import software.amazon.awssdk.utils.StringUtils;

import java.util.Properties;

public abstract class AbstractKinesisCommand extends BaseCommand {

    public static final String STREAM_NAME = "kinesis.streamName";

    @CommandLine.Option(names = "--" + STREAM_NAME, description = "Stream name")
    protected String streamName;

    public AbstractKinesisCommand(Properties props) {
        super(props);
    }

    @Override
    protected final void mergeFieldsWithProperties(Properties props) {
        if (StringUtils.isEmpty(streamName)) {
            streamName = props.getProperty(STREAM_NAME, "Kinesis Stream");
        }
        mergeKinesisCommandProperties(props);
    }

    protected void mergeKinesisCommandProperties(Properties props) {
    }
}
