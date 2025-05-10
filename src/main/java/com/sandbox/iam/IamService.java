package com.sandbox.iam;

import software.amazon.awssdk.services.iam.model.User;

import java.util.List;

/**
 * Wrapper service for AWS IAM client
 */
public interface IamService extends AutoCloseable{

    /**
     * Returns all users
     * @return
     */
    List<User> getAllUsers();

}
