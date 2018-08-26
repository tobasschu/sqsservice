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
package de.tschumacher.queueservice.sns.distributor;

import static de.tschumacher.queueservice.DataCreater.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tschumacher.queueservice.message.SQSMessageFactory;
import de.tschumacher.queueservice.message.TestMessage;
import de.tschumacher.queueservice.message.coder.GsonSQSCoder;
import de.tschumacher.queueservice.sns.SNSQueue;


public class SNSMessageDistributorTest {

  private SNSMessageDistributor<TestMessage> snsMessageDistributor;
  private SQSMessageFactory<TestMessage> factory;
  private SNSQueue snsQueue;


  @Before
  public void setUp() {
    this.snsQueue = Mockito.mock(SNSQueue.class);
    this.factory = new SQSMessageFactory<>(new GsonSQSCoder<>(TestMessage.class));
    this.snsMessageDistributor = new SNSMessageDistributorImpl<>(this.snsQueue, this.factory);
  }

  @After
  public void shutDown() {
    Mockito.verifyNoMoreInteractions(this.snsQueue);
  }

  @Test
  public void distributeTest() {
    final TestMessage message = createTestMessage();

    this.snsMessageDistributor.distribute(message);

    Mockito.verify(this.snsQueue)
        .sendMessage(this.factory.createMessage(message).getContentAsString());
  }
}
