package de.moovin.sqsservice.message;

public interface SQSCoder<T> {

  public T encode(final String content);


  public String decode(final T content);

}
