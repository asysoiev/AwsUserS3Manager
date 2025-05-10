package com.sandbox.s3;

import software.amazon.awssdk.regions.Region;

/**
 * Wrapper service for AWS S3 client
 */
public interface S3Service extends AutoCloseable {

    /**
     * Creates S3 bucket
     * @param region region where the bucket will be created
     * @param prefix prefix for name of the bucket.
     * @return name of the created bucket: {@param prefix}-GUID
     */
    String createBucket(Region region, String prefix);

    /**
     * Creates a folder in the bucket
     * @param bucketName
     * @param folderName
     */
    void createFolder(String bucketName, String folderName);

}
