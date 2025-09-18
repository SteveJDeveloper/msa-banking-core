package com.pichincha.dm.exception;

public class DisabledClientException extends RuntimeException {

  public DisabledClientException(String message) {
    super(message);
  }

}