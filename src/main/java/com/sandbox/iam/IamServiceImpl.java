package com.sandbox.iam;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.ListUsersRequest;
import software.amazon.awssdk.services.iam.model.ListUsersResponse;
import software.amazon.awssdk.services.iam.model.User;

import java.util.List;

public class IamServiceImpl implements IamService {

    private final IamClient iam;

    public IamServiceImpl() {
        iam = IamClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public List<User> getAllUsers() {
        try {
            ListUsersRequest request = ListUsersRequest.builder().build();
            ListUsersResponse response = iam.listUsers(request);
            return response.users();
        } catch (IamException e) {
            throw new RuntimeException("Failed to list IAM users: " + e.awsErrorDetails().errorMessage());
        }
    }

    @Override
    public void close() throws Exception {
        iam.close();
    }
}
