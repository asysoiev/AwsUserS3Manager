## Java sandbox for AWS services

### Logic

- Connects to AWS by profile from ~/.aws/credentials
- Assumes IAM role
- Creates "aws-users-*" S3 bucket in the us-east-1 region
- Creates User/Public folder in the bucket
- Creates User/{user_name} folder for each AWS user in the bucket

### Technologies
- AWS: S3, IAM, STS, Kinesis
- Java 21
- [Picocli](https://picocli.info/)
- Gradle

### TODO

1. Use logger instead of
   1. System.out.println 
   2. e.printStackTrace(); 
   3. fix SLF4J warning
2. Upload some files to the S3 bucket.
   Try using multipart upload API.
3. Play with prefixes and keys in S3.
   Get list of files by prefix, full key, etc.
4. Delete all S3 bucket from ALL regions
5. Configure S3 resource based access policies.
   User can do anything only in hist folder.
6. Add validation of S3 bucket name
   Must NOT start with the prefix xn--
   Must NOT end with the suffix -s3alias
7. Create folder each group of user
8. Create access points for group of
   users https://docs.aws.amazon.com/AmazonS3/latest/userguide/creating-access-points.html
9. Implement PutRecordsToKinesisStreamCommand, SubscribeToKinesisStreamCommand