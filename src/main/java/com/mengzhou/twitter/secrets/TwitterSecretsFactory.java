package com.mengzhou.twitter.secrets;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mengzhou.aws.client.factory.SecretsManagerClientFactory;

import java.io.IOException;

public class TwitterSecretsFactory {

    private static final String DEFAULT_SECRETS_NAME = "XiaowuLotteryBotSecrets";
    private static final String DEFAULT_SECRETS_REGION = "ca-central-1";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static TwitterSecrets defaultSecrets() {
        return of(DEFAULT_SECRETS_REGION, DEFAULT_SECRETS_NAME);
    }

    public static TwitterSecrets of(String awsRegion) {
        return of(awsRegion, DEFAULT_SECRETS_NAME);
    }

    public static TwitterSecrets of(String awsRegion, String secretsName) {
        AWSSecretsManager client = SecretsManagerClientFactory.of(awsRegion);

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretsName);

        GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        try {
            return MAPPER.readValue(getSecretValueResult.getSecretString(), TwitterSecrets.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
