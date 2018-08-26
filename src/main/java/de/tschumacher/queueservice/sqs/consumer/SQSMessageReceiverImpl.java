/*
 * Copyright 2015 Tobias Schumacher
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sqs.model.Message;

import de.tschumacher.queueservice.message.MessageHandler;
import de.tschumacher.queueservice.message.SQSMessageFactory;
import de.tschumacher.queueservice.sqs.SQSQueue;

public class SQSMessageReceiverImpl<F> implements SQSMessageReceiver<F> {
  protected static final int RETRY_SECONDS = 60 * 2;

  private static final Logger logger = LoggerFactory.getLogger(SQSMessageReceiverImpl.class);

  private final MessageHandler<F> handler;
  final SQSMessageFactory<F> factory;

  public SQSMessageReceiverImpl(final MessageHandler<F> handler, final SQSMessageFactory<F> factory) {
    super();
    this.handler = handler;
    this.factory = factory;
  }


  /* (non-Javadoc)
   * @see de.tschumacher.queueservice.sqs.consumer.SQSMessageReceiver#receiveMessage(de.tschumacher.queueservice.sqs.SQSQueue)
   */
  @Override
  public void receiveMessage(final SQSQueue queue) {
    final Message receiveMessage = queue.receiveMessage();
    if (receiveMessage != null) {
      try {
        handleMessage(queue, receiveMessage);
      } catch (final Throwable e) {
        logger.error("could not process message", e);
        changeVisibility(queue, receiveMessage);
      }
    }
  }

  private void handleMessage(final SQSQueue queue, final Message receiveMessage) {
    this.handler.receivedMessage(queue, this.factory.createMessage(receiveMessage.getBody()));
    queue.deleteMessage(receiveMessage.getReceiptHandle());
  }

  private void changeVisibility(final SQSQueue queue, final Message receiveMessage) {
    queue.changeMessageVisibility(receiveMessage.getReceiptHandle(), RETRY_SECONDS);
  }


}
