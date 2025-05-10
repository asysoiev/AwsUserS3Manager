## Java sandbox for AWS services: IAM, S3

### Logic
- Connect to AWS by default profile from ~/.aws/credentials
- Creates "aws-users-*" S3 bucket in the us-east-1 region
- Creates User/Public folder in the bucket
- Creates User/{user_name} folder for each AWS user in the bucket

### Technologies
- Java 21
- AWS: S3, IAM
- Gradle