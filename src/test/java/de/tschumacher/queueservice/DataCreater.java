package de.tschumacher.queueservice;

import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import de.tschumacher.queueservice.message.SQSMessage;
import de.tschumacher.queueservice.message.TestMessage;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class DataCreater {

  protected static PodamFactory factory = createFactory();

  private static PodamFactoryImpl createFactory() {
    final PodamFactoryImpl podamFactory = new PodamFactoryImpl();
    return podamFactory;
  }


  public static String createString() {
    return factory.manufacturePojo(String.class);
  }


  public static CreateQueueResult createCreateQueueResult() {
    final CreateQueueResult createQueueResult = new CreateQueueResult();
    createQueueResult.setQueueUrl(createString());
    return createQueueResult;
  }


  public static GetQueueUrlResult createGetQueueUrlResult() {
    return new GetQueueUrlResult().withQueueUrl(createString());
  }


  public static Integer createInteger() {
    return factory.manufacturePojo(Integer.class);
  }


  public static ReceiveMessageResult createReceiveMessageResult() {
    return new ReceiveMessageResult().withMessages(createMessage());
  }


  public static Message createMessage() {
    return new Message().withReceiptHandle(createString()).withBody(createString());
  }


  public static SQSMessage<TestMessage> createSQSMessage() {
    return new SQSMessage<>(null, createTestMessage());
  }


  public static TestMessage createTestMessage() {
    return new TestMessage(createString());
  }


  public static CreateTopicResult createCreateTopicResult() {
    return new CreateTopicResult().withTopicArn(createString());
  }

  public static GetQueueAttributesResult createGetQueueAttributesResult() {
    final GetQueueAttributesResult getQueueAttributesResult = new GetQueueAttributesResult();
    getQueueAttributesResult.addAttributesEntry("QueueArn", createString());
    return getQueueAttributesResult;
  }
}
