package com.sandbox.commands.s3;

import com.sandbox.commands.BaseCommand;
import com.sandbox.s3.S3Service;
import com.sandbox.s3.S3ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.StringUtils;

import java.util.Properties;

import static com.sandbox.sts.StsAssumeRoleWrapper.assumeRoleWrapperBuilder;

@Command(name = DeleteS3BucketCommand.COMMAND, description = "Delete S3 bucket")
public class DeleteS3BucketCommand extends BaseCommand {

    public static final Logger logger = LoggerFactory.getLogger(DeleteS3BucketCommand.class);

    public static final String COMMAND = "deleteS3Bucket";
    public static final String BUCKET_NAME_PARAM = "bucketName";

    @Option(names = "--" + COMMAND + "." + BUCKET_NAME_PARAM, description = "Bucket name")
    private String bucketName;

    public DeleteS3BucketCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeCommand() {
        logger.info("Deleting S3 Bucket: {}", bucketName);

        String baseProfile = baseAWSConfig.getIamProfile();
        String roleArn = baseAWSConfig.getIamRoleArn();
        Region region = baseAWSConfig.getRegion();
        AwsCredentialsProvider assumedCredentials = assumeRoleWrapperBuilder(baseProfile, region, roleArn).build()
                .assumeRole();
        try (S3Service s3Service = new S3ServiceImpl(assumedCredentials)) {
            s3Service.deleteBucket(bucketName);
        } catch (Exception e) {
            logger.error("Failed to Delete S3 Bucket", e);
        }
    }

    @Override
    protected void mergeFieldsWithProperties(Properties props) {
        if (StringUtils.isEmpty(bucketName)) {
            bucketName = props.getProperty(BUCKET_NAME_PARAM);
        }
    }
}
