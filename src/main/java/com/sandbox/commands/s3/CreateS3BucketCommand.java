package com.sandbox.commands.s3;

import com.sandbox.commands.BaseCommand;
import com.sandbox.config.converters.StringToAWSRegion;
import com.sandbox.iam.IamService;
import com.sandbox.iam.IamServiceImpl;
import com.sandbox.s3.S3Service;
import com.sandbox.s3.S3ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.model.User;

import java.util.List;
import java.util.Properties;

import static com.sandbox.sts.StsAssumeRoleWrapper.assumeRoleWrapperBuilder;

@Command(name = CreateS3BucketCommand.COMMAND_NAME, description = "Create S3 bucket")
public class CreateS3BucketCommand extends BaseCommand {

    public static final Logger logger = LoggerFactory.getLogger(CreateS3BucketCommand.class);

    public static final String COMMAND_NAME = "createS3Bucket";
    public static final String REGION = COMMAND_NAME + ".region";

    @Option(names = "--" + REGION, description = "Region where create S3 bucket",
            converter = StringToAWSRegion.class)
    private Region region;

    public CreateS3BucketCommand(Properties props) {
        super(props);
    }

    @Override
    public void executeCommand() {
        logger.info("Creating S3 Bucket");

        String baseProfile = baseAWSConfig.getIamProfile();
        String roleArn = baseAWSConfig.getIamRoleArn();
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

            logger.info("All operations completed successfully.");
        } catch (Exception e) {
            logger.error("Failed to Create S3 Bucket", e);
        }
    }

    @Override
    protected void mergeFieldsWithProperties(Properties props) {
        if (region == null) {
            region = Region.of(props.getProperty(REGION, baseAWSConfig.getRegion().id()));
        }
    }

    private static String getUserFolderName(String userName) {
        return "Users/%s".formatted(userName);
    }
}
