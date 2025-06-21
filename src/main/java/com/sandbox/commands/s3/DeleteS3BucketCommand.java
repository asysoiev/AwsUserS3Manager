package com.sandbox.commands.s3;

import picocli.CommandLine.Command;

@Command(name = "deleteS3Bucket", description = "Delete S3 bucket")
public class DeleteS3BucketCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Deleting S3 Bucket");
    }
}
