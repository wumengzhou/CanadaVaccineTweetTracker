package com.mengzhou.aws.client.factory;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;

public class SnsClientFactory {

    public static AmazonSNS createSnsClient(String region) {
        return AmazonSNSClient.builder().withRegion(region).build();
    }
}
