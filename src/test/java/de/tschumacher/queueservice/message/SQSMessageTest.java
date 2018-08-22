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
package de.tschumacher.queueservice.message;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tschumacher.queueservice.DataCreater;
import de.tschumacher.queueservice.message.coder.SQSCoder;


public class SQSMessageTest {
  private SQSMessageFactory<TestMessage> sqsMessageFactory;
  private SQSCoder<TestMessage> coder;



  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    this.coder = Mockito.mock(SQSCoder.class);
    this.sqsMessageFactory = new SQSMessageFactory<>(this.coder);
  }

  @After
  public void shutDown() {
    Mockito.verifyNoMoreInteractions(this.coder);
  }

  @Test
  public void encodeTest() {
    final String content = DataCreater.createString();
    final TestMessage message = new TestMessage(DataCreater.createString());

    Mockito.when(this.coder.encode(content)).thenReturn(message);

    final SQSMessage<TestMessage> createdMessage = this.sqsMessageFactory.createMessage(content);

    assertEquals(message.getContent(), createdMessage.getContent().getContent());

    Mockito.verify(this.coder).encode(content);

  }


  @Test
  public void decodeTest() {
    final TestMessage message = new TestMessage(DataCreater.createString());

    final SQSMessage<TestMessage> createdMessage = this.sqsMessageFactory.createMessage(message);

    assertEquals(message, createdMessage.getContent());

  }

}
