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
package de.tschumacher.queueservice.sqs.consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.sqs.model.Message;

import de.tschumacher.queueservice.AbstractMessageReceiver;
import de.tschumacher.queueservice.DataCreater;
import de.tschumacher.queueservice.message.MessageHandler;
import de.tschumacher.queueservice.message.SQSMessage;
import de.tschumacher.queueservice.message.SQSMessageFactory;
import de.tschumacher.queueservice.message.TestMessage;
import de.tschumacher.queueservice.sqs.SQSQueue;


public class SQSMessageReceiverTest {

  private SQSMessageFactory<TestMessage> factory;
  private MessageHandler<TestMessage> handler;
  private SQSMessageReceiver<TestMessage> sqsMessageReceiver;
  private SQSQueue queue;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    this.queue = Mockito.mock(SQSQueue.class);
    this.handler = Mockito.mock(MessageHandler.class);
    this.factory = Mockito.mock(SQSMessageFactory.class);
    this.sqsMessageReceiver = new SQSMessageReceiver<>(this.handler, this.factory);

  }

  @After
  public void shutDown() {
    Mockito.verifyNoMoreInteractions(this.queue);
    Mockito.verifyNoMoreInteractions(this.handler);
    Mockito.verifyNoMoreInteractions(this.factory);
  }

  @Test
  public void receiveMessageNoneTest() {
    Mockito.when(this.queue.receiveMessage()).thenReturn(null);

    this.sqsMessageReceiver.receiveMessage(this.queue);

    Mockito.verify(this.queue).receiveMessage();
  }

  @Test
  public void receiveMessageTest() {
    final Message message = DataCreater.createMessage();
    final SQSMessage<TestMessage> sqsMessage = DataCreater.createSQSMessage();

    Mockito.when(this.queue.receiveMessage()).thenReturn(message);
    Mockito.when(this.factory.createMessage(message.getBody())).thenReturn(sqsMessage);


    this.sqsMessageReceiver.receiveMessage(this.queue);

    Mockito.verify(this.queue).receiveMessage();
    Mockito.verify(this.factory).createMessage(message.getBody());
    Mockito.verify(this.handler).receivedMessage(this.queue, sqsMessage);
    Mockito.verify(this.queue).deleteMessage(message.getReceiptHandle());
  }


  @Test
  public void receiveMessageFailTest() {
    final Message message = DataCreater.createMessage();
    final SQSMessage<TestMessage> sqsMessage = DataCreater.createSQSMessage();

    Mockito.when(this.queue.receiveMessage()).thenReturn(message);
    Mockito.when(this.factory.createMessage(message.getBody())).thenReturn(sqsMessage);
    Mockito.doThrow(Throwable.class).when(this.handler).receivedMessage(this.queue, sqsMessage);

    this.sqsMessageReceiver.receiveMessage(this.queue);

    Mockito.verify(this.queue).receiveMessage();
    Mockito.verify(this.factory).createMessage(message.getBody());
    Mockito.verify(this.handler).receivedMessage(this.queue, sqsMessage);
    Mockito.verify(this.queue).changeMessageVisibility(message.getReceiptHandle(),
        AbstractMessageReceiver.RETRY_SECONDS);
  }
}
