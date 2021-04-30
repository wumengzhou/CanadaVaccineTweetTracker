package com.mengzhou;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.google.common.base.Strings;
import com.mengzhou.aws.AwsUtils;
import com.mengzhou.aws.client.factory.SnsClientFactory;
import com.mengzhou.twitter.TwitterClientFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Handler implements RequestHandler<Map<String,String>, String> {

    private static final String VAX_HUNTERS_CAN_ID = "1373531468744552448";
    private static final String KEY_WORDS = "KeyWords";
    private static final int NUMBER_OF_TWEETS_TO_FETCH = 20;
    private static final int INTERVAL_BETWEEN_EXECUTIONS = 20;
    private static final String SNS_TOPIC = "arn:aws:sns:%s:%s:%s";

    @Override
    public String handleRequest(Map<String, String> stringMap, Context context) {
        String awsRegion = AwsUtils.getAwsRegion();
        String awsAccountId = AwsUtils.getAwsAccountIdFromArn(context.getInvokedFunctionArn());
        LambdaLogger logger = context.getLogger();

        TwitterClient twitter = TwitterClientFactory.of(awsRegion);
        AmazonSNS sns = SnsClientFactory.createSnsClient(awsRegion);
        List<String> keyWords = getKeyWords(stringMap);

        if (keyWords.isEmpty()) {
            logger.log("No key words were given\n");
            return "200 OK";
        } else {
            logger.log("Targeting key words " + keyWords);
        }

        logger.log("Starting polling tweets\n");
        List<Tweet> tweets = getTweets(twitter);
        for (Tweet tweet : tweets) {
            logger.log("Start processing: " + tweet.getId() + " created at " + tweet.getCreatedAt() + "\n");
            if (isStaleTweet(tweet)) {
                logger.log("Job done. Stale tweet " + tweet.getId() + " create at " + tweet.getCreatedAt() + "\n");
                return "200 OK";
            }

            keyWords.forEach(keyWord -> {
                if (isTargetTweet(tweet, keyWord)) {
                    String topicArn = String.format(SNS_TOPIC, awsRegion, awsAccountId, keyWord);
                    logger.log("Target tweet found, publish to " + topicArn + "\n");
                    publish(sns, topicArn, tweet);
                }
            });
        }

        return "200 OK";
    }

    private List<String> getKeyWords(Map<String, String> stringMap) {
        return Arrays.stream(stringMap.getOrDefault(KEY_WORDS, "").split(","))
                .map(String::trim)
                .filter(word -> !Strings.isNullOrEmpty(word))
                .collect(Collectors.toList());
    }

    private List<Tweet> getTweets(TwitterClient twitter) {
        return twitter.getUserTimeline(VAX_HUNTERS_CAN_ID, NUMBER_OF_TWEETS_TO_FETCH);
    }

    private boolean isStaleTweet(Tweet tweet) {
        return tweet.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(INTERVAL_BETWEEN_EXECUTIONS));
    }

    public void publish(AmazonSNS sns, String topic, Tweet tweet) {
        PublishRequest request = new PublishRequest();
        request.setTopicArn(topic);
        request.setMessage(tweet.getText());
        sns.publish(request);
    }

    private boolean isTargetTweet(Tweet tweet, String keyWord) {
        String content = tweet.getText();
        return content.contains(keyWord);
    }
}
