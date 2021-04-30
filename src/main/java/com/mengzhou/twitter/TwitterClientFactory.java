package com.mengzhou.twitter;

import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import com.mengzhou.twitter.secrets.TwitterSecrets;
import com.mengzhou.twitter.secrets.TwitterSecretsFactory;

public class TwitterClientFactory {

    public static TwitterClient of(String awsRegion) {
        TwitterSecrets twitterSecrets = TwitterSecretsFactory.of(awsRegion);

        TwitterCredentials twitterCredentials = TwitterCredentials.builder()
                .apiKey(twitterSecrets.getOAuthConsumerKey())
                .apiSecretKey(twitterSecrets.getOAuthConsumerSecret())
                .accessToken(twitterSecrets.getOAuthAccessToken())
                .accessTokenSecret(twitterSecrets.getOAuthAccessTokenSecret())
                .build();

        return new TwitterClient(twitterCredentials);
    }
}
