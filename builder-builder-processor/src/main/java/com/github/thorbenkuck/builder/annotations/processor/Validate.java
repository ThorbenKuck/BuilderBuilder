package com.github.thorbenkuck.builder.annotations.processor;

import java.util.Objects;

public class Validate {

  public static Object notNull(Object object) {
    return notNull(object, null);
  }

  public static Object notNull(Object object, String message) {
    if(Objects.isNull(object)) {
      throw new NullPointerException(message);
    }

    return object;
  }

}
