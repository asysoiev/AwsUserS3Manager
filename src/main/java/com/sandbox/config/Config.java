package com.sandbox.config;

import software.amazon.awssdk.regions.Region;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private String profile;
    private Region region;
    private String roleArn;

    public Config() {
        profile = "default";
        region = Region.US_EAST_1;
        roleArn = "";
    }

    public String getProfile() {
        return profile;
    }

    public Region getRegion() {
        return region;
    }

    public String getRoleArn() {
        return roleArn;
    }

    /**
     * Loads config properties from command line ars or config.properties file.
     *
     * @param args - command line arguments
     * @return
     */
    public static Config loadConfig(String[] args) {
        Config config = new Config();

        // Load from config.properties if exists
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            System.out.println("No config.properties file found. Using defaults or command-line args.");
        }

        // Apply properties file values
        config.profile = props.getProperty("iam.profile", config.profile);
        config.region = Region.of(props.getProperty("region", config.region.id()));
        config.roleArn = props.getProperty("iam.roleArn", config.roleArn);

        // Override with CLI args
        for (String arg : args) {
            if (arg.startsWith("--iam.profile=")) {
                config.profile = arg.substring("--iam.profile=".length());
            } else if (arg.startsWith("--region=")) {
                config.region = Region.of(arg.substring("--region=".length()));
            } else if (arg.startsWith("--iam.roleArn=")) {
                config.roleArn = arg.substring("--iam.roleArn=".length());
            }
        }

        if (config.roleArn == null || config.roleArn.isEmpty()) {
            System.out.printf("\"roleArn\" property is not defined. Profile \"%s\" credentials will be used\n",
                    config.profile);
        }

        System.out.printf("Using profile: %s, region: %s, role: \"%s\"%n",
                config.profile, config.region.id(), config.roleArn);
        return config;
    }

}
