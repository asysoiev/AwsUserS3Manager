## Java sandbox for AWS services: IAM, S3

### Logic

- Connects to AWS by profile from ~/.aws/credentials
- Assumes IAM role
- Creates "aws-users-*" S3 bucket in the us-east-1 region
- Creates User/Public folder in the bucket
- Creates User/{user_name} folder for each AWS user in the bucket

### Technologies
- Java 21
- AWS: S3, IAM, STS
- Gradle

### TODO
1. Add delete bucket feature
2. Use logger instead of
   1. System.out.println 
   2. e.printStackTrace(); 
   3. fix SLF4J warning
3. Upload some files to the S3 bucket.
   Try using multipart upload API.
4. Play with prefixes and keys in S3.
   Get list of files by prefix, full key, etc.
5. Delete all S3 bucket from ALL regions
6. Configure S3 resource based access policies.
   User can do anything only in hist folder.