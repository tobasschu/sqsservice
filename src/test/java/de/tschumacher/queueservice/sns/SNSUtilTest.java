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
package de.tschumacher.queueservice.sns;

import static de.tschumacher.queueservice.DataCreater.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicResult;

import de.tschumacher.queueservice.DataCreater;



public class SNSUtilTest {

  private AmazonSNS sns;

  @Before
  public void setUp() {
    this.sns = Mockito.mock(AmazonSNS.class);
  }

  @After
  public void shutDown() {
    Mockito.verifyNoMoreInteractions(this.sns);
  }

  @Test
  public void createTest() {
    final CreateTopicResult createTopicResult = DataCreater.createCreateTopicResult();
    final String snsName = createString();

    Mockito.when(this.sns.createTopic(snsName)).thenReturn(createTopicResult);

    final String createdTopicArn = SNSUtil.createTopic(this.sns, snsName);

    assertEquals(createTopicResult.getTopicArn(), createdTopicArn);

    Mockito.verify(this.sns).createTopic(snsName);
  }
}
