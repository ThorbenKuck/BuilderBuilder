package com.github.thorbenkuck.builder.annotations.processor;

import java.util.Objects;

public class Validate {

  public static void notNull(Object object) {
    notNull(object, null);
  }

  public static void notNull(Object object, String message) {
    if(Objects.isNull(object)) {
      throw new NullPointerException(message);
    }
  }

}
