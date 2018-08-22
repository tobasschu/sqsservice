package de.tschumacher.queueservice;

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

}
