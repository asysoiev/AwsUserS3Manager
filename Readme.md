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

### TODO
1. Add delete bucket feature
2. Add parameters:
   1. profile for the authentication 
   2. region
3. Use logger instead of 
   1. System.out.println 
   2. e.printStackTrace(); 
   3. fix SLF4J warning