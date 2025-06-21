package com.sandbox.config;

import com.sandbox.config.converters.StringToAWSRegion;
import picocli.CommandLine.Option;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BaseAWSConfig {

    @Option(names = "--iam.profile", description = "IAM profile")
    private String iamProfile;
    @Option(names = "--region", description = "Region", converter = StringToAWSRegion.class)
    private Region region;
    @Option(names = "--iam.roleArn", description = "IAM role ARN")
    private String iamRoleArn;

    public String getIamProfile() {
        return iamProfile;
    }

    public Region getRegion() {
        return region;
    }

    public String getIamRoleArn() {
        return iamRoleArn;
    }

    public void merge(Properties props) {
        // Apply properties file values
        if (StringUtils.isEmpty(iamProfile)) {
            iamProfile = props.getProperty("iam.profile", "default");
        }
        if (region == null) {
            region = Region.of(props.getProperty("region", Region.US_EAST_1.id()));
        }
        if (StringUtils.isEmpty(iamRoleArn)) {
            iamRoleArn = props.getProperty("iam.roleArn");
        }

        if (iamRoleArn == null || iamRoleArn.isEmpty()) {
            System.out.printf("\"roleArn\" property is not defined. Profile \"%s\" credentials will be used\n",
                    iamProfile);
        }

        System.out.printf("Using profile: %s, region: %s, role: \"%s\"%n",
                iamProfile, region.id(), iamRoleArn);
    }

}
