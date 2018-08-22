/*
 * Copyright 2018 Tobias Schumacher
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.tschumacher.queueservice.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSQueueImpl implements SQSQueue {
  private static final int VISIBILITY_TIMEOUT = 60 * 2;
  private static final int WAIT_TIME_SECONDS = 20;
  private static final int MAX_NUMBER_OF_MESSAGES = 1;
  private static final String DEFAULT_REGION = Regions.EU_CENTRAL_1.name();

  private final AmazonSQS sqs;
  private final String queueUrl;

  public SQSQueueImpl(AmazonSQS sqs, final String queueName) {
    super();
    this.sqs = sqs;
    this.queueUrl = SQSUtil.createIfNotExists(this.sqs, queueName);
  }

  public SQSQueueImpl(final String accessKey, final String secretKey, final String queueName) {
    this(accessKey, secretKey, DEFAULT_REGION, queueName);
  }

  public SQSQueueImpl(final String accessKey, final String secretKey, String regionName,
      final String queueName) {
    this(createAmazonSQS(accessKey, secretKey, regionName), queueName);
  }

  private static AmazonSQS createAmazonSQS(final String accessKey, final String secretKey,
      String regionName) {
    final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    return AmazonSQSClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(Regions.fromName(regionName)).build();
  }

  @Override
  public synchronized Message receiveMessage() {
    final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(this.queueUrl)
        .withWaitTimeSeconds(WAIT_TIME_SECONDS).withMaxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)
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

  @Override
  public String getQueueUrl() {
    return this.queueUrl;
  }

}
