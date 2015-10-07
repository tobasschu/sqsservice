package de.moovin.sqsservice.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSQSCoder<B> implements SQSCoder<B> {

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private final Class<B> clazz;



  public GsonSQSCoder(final Class<B> clazz) {
    super();
    this.clazz = clazz;
  }


  @Override
  public B encode(final String content) {
    return this.gson.fromJson(content, this.clazz);
  }


  @Override
  public String decode(final B content) {
    return this.gson.toJson(content);
  }


}
