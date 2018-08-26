package de.tschumacher.queueservice.sqs.consumer;

import de.tschumacher.queueservice.sqs.SQSQueue;


public interface SQSMessageReceiver<F> {

  void receiveMessage(SQSQueue queue);

}
