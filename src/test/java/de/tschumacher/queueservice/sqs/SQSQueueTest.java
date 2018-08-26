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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import de.tschumacher.queueservice.DataCreater;


public class SQSQueueTest {

  private SQSQueue sqsQueue;
  private AmazonSQS sqs;
  private String queueName;
  private String queueUrl;

  @Before
  public void setUp() {
    this.queueName = DataCreater.createString();
    this.sqs = Mockito.mock(AmazonSQS.class);

    final GetQueueUrlResult getQueueUrlResult = DataCreater.createGetQueueUrlResult();
    this.queueUrl = getQueueUrlResult.getQueueUrl();
    Mockito.when(this.sqs.getQueueUrl(this.queueName)).thenReturn(getQueueUrlResult);

    this.sqsQueue = new SQSQueueImpl(this.sqs, this.queueName);

  }

  @After
  public void shutDown() {
    Mockito.verify(this.sqs).getQueueUrl(this.queueName);
    Mockito.verifyNoMoreInteractions(this.sqs);
  }

  @Test
  public void sendMessageTest() {
    final String messageBody = DataCreater.createString();

    this.sqsQueue.sendMessage(messageBody);

    verifySendMessageRequest(messageBody, null);
  }


  @Test
  public void sendMessageWithDelaySecondsTest() {
    final String messageBody = DataCreater.createString();
    final Integer delaySeconds = DataCreater.createInteger();

    this.sqsQueue.sendMessage(messageBody, delaySeconds);

    verifySendMessageRequest(messageBody, delaySeconds);
  }

  @Test
  public void changeMessageVisibilityTest() {
    final String receiptHandle = DataCreater.createString();
    final Integer retrySeconds = DataCreater.createInteger();

    this.sqsQueue.changeMessageVisibility(receiptHandle, retrySeconds);

    Mockito.verify(this.sqs).changeMessageVisibility(this.queueUrl, receiptHandle, retrySeconds);
  }

  @Test
  public void deleteMessageTest() {
    final String receiptHandle = DataCreater.createString();

    this.sqsQueue.deleteMessage(receiptHandle);

    Mockito.verify(this.sqs).deleteMessage(this.queueUrl, receiptHandle);
  }


  @Test
  public void getQueueUrlTest() {
    final String queueUrl = this.sqsQueue.getQueueUrl();
    assertEquals(this.queueUrl, queueUrl);
  }

  @Test
  public void receiveMessageTest() {
    final ReceiveMessageResult receiveMessageResult = DataCreater.createReceiveMessageResult();

    Mockito.when(this.sqs.receiveMessage(Matchers.any(ReceiveMessageRequest.class)))
        .thenReturn(receiveMessageResult);

    final Message resultReceiveMessage = this.sqsQueue.receiveMessage();

    assertEquals(receiveMessageResult.getMessages().get(0), resultReceiveMessage);

    Mockito.verify(this.sqs).receiveMessage(Matchers.any(ReceiveMessageRequest.class));
  }


  @Test
  public void receiveEmptyMessageTest() {

    Mockito.when(this.sqs.receiveMessage(Matchers.any(ReceiveMessageRequest.class)))
        .thenReturn(new ReceiveMessageResult());

    final Message resultReceiveMessage = this.sqsQueue.receiveMessage();

    assertNull(resultReceiveMessage);

    Mockito.verify(this.sqs).receiveMessage(Matchers.any(ReceiveMessageRequest.class));
  }

  private void verifySendMessageRequest(final String messageBody, Integer delaySeconds) {
    final ArgumentCaptor<SendMessageRequest> sendMessageRequestCaptor =
        ArgumentCaptor.forClass(SendMessageRequest.class);
    Mockito.verify(this.sqs).sendMessage(sendMessageRequestCaptor.capture());

    assertEquals(messageBody, sendMessageRequestCaptor.getValue().getMessageBody());
    assertEquals(this.queueUrl, sendMessageRequestCaptor.getValue().getQueueUrl());
    assertEquals(delaySeconds, sendMessageRequestCaptor.getValue().getDelaySeconds());
  }


}
