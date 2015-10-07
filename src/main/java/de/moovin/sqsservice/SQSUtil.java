package de.moovin.sqsservice;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;

public class SQSUtil {
  public static String getQueueUrl(final AmazonSQS sqs, final String queueName) {

    final GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
    return sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
  }


  public static String create(final AmazonSQS sqs, final String queueName) {
    final CreateQueueRequest createQueueRequest = new CreateQueueRequest().withQueueName(queueName);
    final String queueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
    return queueUrl;
  }


  public static String createIfNotExists(final AmazonSQS sqs, final String queueName) {
    String queueUrl;
    try {
      queueUrl = getQueueUrl(sqs, queueName);
    } catch (final QueueDoesNotExistException e) {
      queueUrl = create(sqs, queueName);
    }

    return queueUrl;
  }
}
