package com.sandbox.config.converters;

import picocli.CommandLine.ITypeConverter;
import software.amazon.awssdk.services.polly.model.Engine;

public class StringToPollyEngine implements ITypeConverter<Engine> {
    @Override
    public Engine convert(String value) {
        return Engine.fromValue(value);
    }
}
