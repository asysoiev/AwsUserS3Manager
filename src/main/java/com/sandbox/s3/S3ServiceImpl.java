package com.sandbox.s3;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

public class S3ServiceImpl implements S3Service {

    private final S3Client s3;

    public S3ServiceImpl() {
        s3 = S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public String createBucket(Region region, String prefix) {
        try {
            String bucketName = prefix + "-" + UUID.randomUUID();//Bucket name should not contain '_'

            CreateBucketRequest.Builder reqBuilder = CreateBucketRequest.builder()
                    .bucket(bucketName);
            if (region != Region.US_EAST_1) {
                reqBuilder.createBucketConfiguration(
                        CreateBucketConfiguration.builder()
                                .locationConstraint(region.id())
                                .build());
            }
            CreateBucketRequest createBucketRequest = reqBuilder.build();

            s3.createBucket(createBucketRequest);
            s3.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                    .bucket(bucketName).build());

            System.out.println("Bucket created: " + bucketName);
            return bucketName;
        } catch (BucketAlreadyExistsException e) {
            throw new RuntimeException("Bucket name is already taken. Please choose a different name.");
        }
    }

    @Override
    public void createFolder(String bucketName, String folderName) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(folderName)
                .build();

        s3.putObject(request, RequestBody.empty());
        System.out.println("Created folder: " + folderName);
    }

    @Override
    public void close() throws Exception {
        s3.close();
    }
}
