package com.sandbox.sts;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.utils.StringUtils;

/**
 * Simplifies assume role.
 */
public class StsAssumeRoleWrapper {

    private final String profile;
    private final Region region;
    private final String roleArn;
    private final String sessionName;
    private final int durationSeconds;

    private StsAssumeRoleWrapper(String profile, Region region, String roleArn, String sessionName, int durationSeconds) {
        this.profile = profile;
        this.region = region;
        this.roleArn = roleArn;
        this.sessionName = sessionName;
        this.durationSeconds = durationSeconds;
    }

    /**
     * Assumes role or returns profile credentials if the roleArn is empty.
     *
     * @return credentials
     */
    public AwsCredentialsProvider assumeRole() {
        AwsCredentialsProvider baseCredentialsProvider = DefaultCredentialsProvider.builder()
                .profileName(profile)
                .build();
        if (StringUtils.isEmpty(roleArn)) {
            return baseCredentialsProvider;
        }

        // Create STS client
        try (StsClient stsClient = StsClient.builder()
                .region(region)
                .credentialsProvider(baseCredentialsProvider)
                .build()) {

            // Assume Role
            AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                    .roleArn(roleArn)
                    .roleSessionName(sessionName)
                    .durationSeconds(durationSeconds)
                    .build();

            AssumeRoleResponse response = stsClient.assumeRole(assumeRoleRequest);
            Credentials tempCredentials = response.credentials();

            return StaticCredentialsProvider.create(
                    AwsSessionCredentials.create(
                            tempCredentials.accessKeyId(),
                            tempCredentials.secretAccessKey(),
                            tempCredentials.sessionToken()
                    )
            );
        }
    }

    public static Builder assumeRoleWrapperBuilder(String profile, Region region, String roleArn) {
        return new Builder(profile, region, roleArn);
    }

    public static class Builder {
        private final String profile;
        private final Region region;
        private final String roleArn;
        private String sessionName;
        private int durationSeconds;

        private Builder(String profile, Region region, String roleArn) {
            this.profile = profile;
            this.region = region;
            this.roleArn = roleArn;
            sessionName = "AssumeRoleSession";
            durationSeconds = 3600;
        }

        public Builder setSessionName(String sessionName) {
            this.sessionName = sessionName;
            return this;
        }

        public Builder setDurationSeconds(int durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        public StsAssumeRoleWrapper build() {
            return new StsAssumeRoleWrapper(profile,
                    region,
                    roleArn,
                    sessionName,
                    durationSeconds);
        }
    }
}
