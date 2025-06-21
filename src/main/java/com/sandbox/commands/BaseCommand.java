package com.sandbox.commands;

import com.sandbox.config.BaseAWSConfig;
import picocli.CommandLine.Mixin;

import java.util.Properties;

/**
 * Contains common parameters and functionality for picocli commands.
 */
public abstract class BaseCommand implements Runnable {

    @Mixin
    protected BaseAWSConfig baseAWSConfig;

    private final Properties props;

    public BaseCommand(Properties props) {
        this.props = props;
    }

    @Override
    public final void run() {
        baseAWSConfig.merge(props);
        mergeFieldsWithProperties(props);
        executeCommand();
    }

    /**
     * Override to implement merge custom command parameters with properties.
     * @param props
     */
    protected void mergeFieldsWithProperties(Properties props) {

    }

    /**
     * Implement command logic
     */
    protected abstract void executeCommand();
}
