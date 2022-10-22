

package com.sondertara.common.bean.model;

/*
 * An example of custom generic type
 */
public class Mono<T> {
  private T t;

  Mono() {}

  public Mono(T t) {
    this.t = t;
  }

  public T get() {
    return t;
  }

  @Override
  public String toString() {
    return "Mono{" +
        "t=" + t +
        '}';
  }
}
