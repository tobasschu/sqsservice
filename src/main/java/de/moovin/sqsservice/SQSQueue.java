package de.moovin.sqsservice;

import com.amazonaws.services.sqs.model.Message;


public interface SQSQueue {

  Message receiveMessage();


  void deleteMessage(String receiptHandle);


  void changeMessageVisibility(String receiptHandle, int retrySeconds);


  void sendMessage(String messageBody);

}
