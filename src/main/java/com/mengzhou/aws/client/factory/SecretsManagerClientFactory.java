package com.mengzhou.aws.client.factory;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;

public class SecretsManagerClientFactory {

    public static AWSSecretsManager of(String region) {
        return AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
    }
}
