

package com.sondertara.common.bean.model;

import java.util.Collection;

public class UserView {
  private String name;
  private Collection<UserView> underHands;

  UserView() {}

  public UserView(String name, Collection<UserView> underHands) {
    this.name = name;
    this.underHands = underHands;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Collection<UserView> getUnderHands() {
    return underHands;
  }

  public void setUnderHands(Collection<UserView> underHands) {
    this.underHands = underHands;
  }

  @Override
  public String toString() {
    return "UserView{" +
        "name='" + name + '\'' +
        ", underHands=" + underHands +
        '}';
  }
}
