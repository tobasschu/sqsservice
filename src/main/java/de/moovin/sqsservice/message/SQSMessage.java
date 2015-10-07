package de.moovin.sqsservice.message;

public class SQSMessage<T> {

  private T content;
  private final SQSCoder<T> coder;



  public SQSMessage(final SQSCoder<T> coder, final String content) {
    super();
    this.coder = coder;
    this.content = this.coder.encode(content);
  }


  public SQSMessage(final SQSCoder<T> coder, final T content) {
    super();
    this.coder = coder;
    this.content = content;
  }



  public T getContent() {
    return this.content;
  }


  public String getContentAsString() {
    return this.coder.decode(this.content);
  }


  public void setContent(final T content) {
    this.content = content;
  }


  public void setContent(final String content) {
    this.content = this.coder.encode(content);
  }



}
