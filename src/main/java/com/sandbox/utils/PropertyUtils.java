package com.sandbox.utils;

import software.amazon.awssdk.utils.StringUtils;

import java.util.Properties;

public final class PropertyUtils {

    public static int getIntValue(Properties properties, String propName, int defaultValue) {
        String propertyValue = properties.getProperty(propName);
        if (StringUtils.isEmpty(propertyValue)) {
            return defaultValue;
        }
        return Integer.parseInt(propertyValue);
    }

}
