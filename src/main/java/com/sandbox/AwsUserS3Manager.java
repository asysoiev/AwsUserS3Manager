package com.sandbox;

import com.sandbox.iam.IamService;
import com.sandbox.iam.IamServiceImpl;
import com.sandbox.s3.S3Service;
import com.sandbox.s3.S3ServiceImpl;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.model.User;

import java.util.List;

public class AwsUserS3Manager {

    public static void main(String[] args) {
        try (IamService iamService = new IamServiceImpl();
             S3Service s3Service = new S3ServiceImpl()) {

            String bucket = s3Service.createBucket(Region.US_EAST_1, "aws-users");
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
