package com.doogie.liquido.mongoServices;

public class DuplicateKeyException extends Exception {
  public DuplicateKeyException(String msg) {
    super(msg);
  }
}
