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

1. Change custom property by picocli:
   https://www.baeldung.com/java-picocli-create-command-line-program
2. Add delete bucket feature
3. Use logger instead of
   1. System.out.println 
   2. e.printStackTrace(); 
   3. fix SLF4J warning
4. Upload some files to the S3 bucket.
   Try using multipart upload API.
5. Play with prefixes and keys in S3.
   Get list of files by prefix, full key, etc.
6. Delete all S3 bucket from ALL regions
7. Configure S3 resource based access policies.
   User can do anything only in hist folder.
8. Add validation of S3 bucket name
   Must NOT start with the prefix xn--
   Must NOT end with the suffix -s3alias
9. Create folder each group of user
10. Create access points for group of
   users https://docs.aws.amazon.com/AmazonS3/latest/userguide/creating-access-points.html
11. Implement PutRecordsToKinesisStreamCommand, SubscribeToKinesisStreamCommand