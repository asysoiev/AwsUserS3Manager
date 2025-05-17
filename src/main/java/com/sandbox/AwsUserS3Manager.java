package com.sandbox;

import com.sandbox.config.Config;
import com.sandbox.iam.IamService;
import com.sandbox.iam.IamServiceImpl;
import com.sandbox.s3.S3Service;
import com.sandbox.s3.S3ServiceImpl;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.model.User;

import java.util.List;

import static com.sandbox.config.Config.loadConfig;
import static com.sandbox.sts.StsAssumeRoleWrapper.assumeRoleWrapperBuilder;

public class AwsUserS3Manager {

    public static void main(String[] args) {
        Config config = loadConfig(args);
        String baseProfile = config.getProfile();
        String roleArn = config.getRoleArn();
        Region region = config.getRegion();

        AwsCredentialsProvider assumedCredentials = assumeRoleWrapperBuilder(baseProfile, region, roleArn).build()
                .assumeRole();
        try (IamService iamService = new IamServiceImpl(assumedCredentials);
             S3Service s3Service = new S3ServiceImpl(assumedCredentials)) {

            String bucket = s3Service.createBucket(region, "aws-users");
            s3Service.createFolder(bucket, getUserFolderName("Public"));
            List<User> awsUsers = iamService.getAllUsers();
            // 4. Create folder for each user
            for (User user : awsUsers) {
                String userFolderName = getUserFolderName(user.userName());
                s3Service.createFolder(bucket, userFolderName);
            }

            System.out.println("All operations completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUserFolderName(String userName) {
        return "Users/%s".formatted(userName);
    }
}
