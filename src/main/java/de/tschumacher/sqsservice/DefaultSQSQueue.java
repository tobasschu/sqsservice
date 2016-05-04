package de.tschumacher.sqsservice;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class DefaultSQSQueue implements SQSQueue {
  private static final int VISIBILITY_TIMEOUT = 60 * 2;
  private static final int WAIT_TIME_SECONDS = 20;
  private static final int MAX_NUMBER_OF_MESSAGES = 1;
  private final AmazonSQS sqs;
  private final String queueUrl;

  public DefaultSQSQueue(final String accessKey, final String secretKey, final String queueName) {
    super();
    final AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    this.sqs = new AmazonSQSClient(credentials);
    this.sqs.setRegion(com.amazonaws.regions.Region.getRegion(Regions.EU_CENTRAL_1));
    this.queueUrl = SQSUtil.createIfNotExists(this.sqs, queueName);
  }

  @Override
  public synchronized Message receiveMessage() {
    final ReceiveMessageRequest receiveMessageRequest =
        new ReceiveMessageRequest(this.queueUrl).withWaitTimeSeconds(WAIT_TIME_SECONDS)
            .withMaxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)
            .withVisibilityTimeout(VISIBILITY_TIMEOUT);
    final ReceiveMessageResult receiveMessage = this.sqs.receiveMessage(receiveMessageRequest);
    if (receiveMessage.getMessages().size() > 0)
      return receiveMessage.getMessages().get(0);
    return null;
  }

  @Override
  public void deleteMessage(final String receiptHandle) {
    final DeleteMessageRequest deleteMessageRequest =
        new DeleteMessageRequest(this.queueUrl, receiptHandle);
    this.sqs.deleteMessage(deleteMessageRequest);

  }

  @Override
  public void changeMessageVisibility(final String receiptHandle, final int retrySeconds) {
    final ChangeMessageVisibilityRequest changeMessageVisibilityRequest =
        new ChangeMessageVisibilityRequest(this.queueUrl, receiptHandle, retrySeconds);
    this.sqs.changeMessageVisibility(changeMessageVisibilityRequest);
  }

  @Override
  public void sendMessage(final String messageBody) {
    sendMessage(messageBody, null);
  }


  @Override
  public void sendMessage(final String messageBody, Integer delaySeconds) {
    final SendMessageRequest sendMessageRequest =
        new SendMessageRequest(this.queueUrl, messageBody);
    sendMessageRequest.setDelaySeconds(delaySeconds);
    this.sqs.sendMessage(sendMessageRequest);
  }

}
