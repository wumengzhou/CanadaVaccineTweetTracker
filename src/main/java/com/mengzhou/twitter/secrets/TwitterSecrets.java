package com.mengzhou.twitter.secrets;

import lombok.Data;

@Data
public class TwitterSecrets {
    private String oAuthConsumerKey;
    private String oAuthConsumerSecret;
    private String oAuthAccessToken;
    private String oAuthAccessTokenSecret;
}