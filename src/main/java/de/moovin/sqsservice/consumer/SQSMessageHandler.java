package de.moovin.sqsservice.consumer;

import de.moovin.sqsservice.SQSQueue;
import de.moovin.sqsservice.message.SQSMessage;

public interface SQSMessageHandler<T> {

  void receivedMessage(SQSQueue queue, SQSMessage<T> receiveMessage);

}
