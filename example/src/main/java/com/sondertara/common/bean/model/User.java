

package com.sondertara.common.bean.model;

import java.util.List;

public class User {
  private String name;
  private List<User> underHands;

  User() {}

  public User(String name, List<User> underHands) {
    this.name = name;
    this.underHands = underHands;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<User> getUnderHands() {
    return underHands;
  }

  public void setUnderHands(List<User> underHands) {
    this.underHands = underHands;
  }

  @Override
  public String toString() {
    return "User{" +
        "name='" + name + '\'' +
        ", underHands=" + underHands +
        '}';
  }
}
