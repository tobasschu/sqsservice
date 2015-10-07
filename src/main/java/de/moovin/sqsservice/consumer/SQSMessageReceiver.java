package de.moovin.sqsservice.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sqs.model.Message;

import de.moovin.sqsservice.SQSQueue;
import de.moovin.sqsservice.supplier.SQSMessageFactory;

public class SQSMessageReceiver<F> {


  private static final Logger logger = LoggerFactory.getLogger(SQSMessageReceiver.class);
  private static final int WORKER_COUNT = 5;
  protected static final int MAX_RETRYS = 5;
  protected static final int RETRY_SECONDS = 20;

  private final SQSMessageHandler<F> handler;
  private final ExecutorService executorService;
  private boolean running = false;
  private final Runnable worker;
  final SQSMessageFactory<F> factory;


  public SQSMessageReceiver(final SQSQueue queue, final SQSMessageHandler<F> handler,
      final SQSMessageFactory<F> factory) {
    super();
    this.handler = handler;
    this.worker = newWorker(queue);
    this.executorService = Executors.newFixedThreadPool(WORKER_COUNT);
    this.factory = factory;
    start();
  }



  public void start() {
    this.running = true;
    for (int i = 0; i < WORKER_COUNT; i++) {
      this.executorService.submit(this.worker);
    }
  }


  public void stop() throws InterruptedException {
    this.running = false;
  }



  private Runnable newWorker(final SQSQueue queue) {
    return new Runnable() {

      @Override
      public void run() {
        while (SQSMessageReceiver.this.running) {
          try {
            final Message receiveMessage = queue.receiveMessage();
            if (receiveMessage != null) {
              try {
                SQSMessageReceiver.this.handler.receivedMessage(queue,
                    SQSMessageReceiver.this.factory.createMessage(receiveMessage.getBody()));
                queue.deleteMessage(receiveMessage.getReceiptHandle());
              } catch (final Throwable e) {
                logger.error("could not process message", e);
                queue.changeMessageVisibility(receiveMessage.getReceiptHandle(), RETRY_SECONDS);
              }
            }
          } catch (final Throwable e) {
            logger.error("could not handle message", e);
          }
        }

      }
    };
  }



  @Override
  protected void finalize() throws Throwable {
    stop();
    super.finalize();
  }

}
