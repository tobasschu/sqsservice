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
package de.tschumacher.queueservice.sns.consumer;

import de.tschumacher.queueservice.sns.SNSQueue;
import de.tschumacher.queueservice.sqs.SQSQueue;
import de.tschumacher.queueservice.sqs.consumer.SQSMessageReceiver;
import de.tschumacher.queueservice.sqs.consumer.SQSMessageReceiverService;

public class SNSMessageReceiverService<F> extends SQSMessageReceiverService<F> {


  public SNSMessageReceiverService(SNSQueue snsQueue, SQSQueue sqsQueue,
      SQSMessageReceiver<F> messageReceiver) {
    super(sqsQueue, messageReceiver);
    snsQueue.subscribeSQSQueue(sqsQueue.getQueueUrl());
  }

}
