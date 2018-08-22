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
package de.tschumacher.queueservice.message.coder;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.tschumacher.queueservice.DataCreater;
import de.tschumacher.queueservice.message.TestMessage;


public class GsonSQSCoderTest {
  private GsonSQSCoder<TestMessage> coder;
  private Gson gson;


  @Before
  public void setUp() {
    this.gson = new GsonBuilder().create();
    this.coder = new GsonSQSCoder<>(this.gson, TestMessage.class);
  }


  @Test
  public void decodeTest() {

    final TestMessage message = new TestMessage(DataCreater.createString());

    final String decodeMessage = this.coder.decode(message);

    assertEquals(this.gson.toJson(message), decodeMessage);

  }

  @Test
  public void encodeTest() {

    final TestMessage message = new TestMessage(DataCreater.createString());
    final String decodedMessage = this.gson.toJson(message);


    final TestMessage encodedMessage = this.coder.encode(decodedMessage);

    assertEquals(this.gson.fromJson(decodedMessage, TestMessage.class).getContent(),
        encodedMessage.getContent());

  }


}
