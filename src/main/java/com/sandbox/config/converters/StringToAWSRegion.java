package com.sandbox.config.converters;

import picocli.CommandLine.ITypeConverter;
import software.amazon.awssdk.regions.Region;

public class StringToAWSRegion implements ITypeConverter<Region> {
    @Override
    public Region convert(String value) {
        return Region.of(value);
    }
}
