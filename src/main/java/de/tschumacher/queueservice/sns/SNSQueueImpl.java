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

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

public class SNSQueueImpl implements SNSQueue {
  private static final String DEFAULT_REGION = Regions.EU_CENTRAL_1.name();
  private final AmazonSNS sns;
  private final String topicArn;


  public SNSQueueImpl(AmazonSNS sns, final String snsName) {
    super();
    this.sns = sns;
    this.topicArn = SNSUtil.createTopic(this.sns, snsName);
  }

  public SNSQueueImpl(final String accessKey, final String secretKey, final String snsName) {
    this(accessKey, secretKey, DEFAULT_REGION, snsName);
  }

  public SNSQueueImpl(final String accessKey, final String secretKey, String regionName,
      final String snsName) {
    this(createAmazonSQS(accessKey, secretKey, regionName), snsName);

  }

  private static AmazonSNS createAmazonSQS(final String accessKey, final String secretKey,
      String regionName) {
    final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    return AmazonSNSClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(Regions.fromName(regionName)).build();
  }


  @Override
  public void sendMessage(String message) {
    this.sns.publish(this.topicArn, message);
  }

  @Override
  public void subscribeSQSQueue(String queueUrl) {
    this.sns.subscribe(this.topicArn, "sqs", queueUrl);
  }


}
