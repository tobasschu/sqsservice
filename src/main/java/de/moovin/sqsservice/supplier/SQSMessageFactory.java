package de.moovin.sqsservice.supplier;

import de.moovin.sqsservice.message.SQSCoder;
import de.moovin.sqsservice.message.SQSMessage;

public class SQSMessageFactory<F> {

  private final SQSCoder<F> coder;


  public SQSMessageFactory(final SQSCoder<F> coder) {
    super();
    this.coder = coder;
  }


  public SQSMessage<F> createMessage(final String body) {
    return new SQSMessage<F>(this.coder, body);
  }


  public SQSMessage<F> createMessage(final F body) {
    return new SQSMessage<F>(this.coder, body);
  }



}
