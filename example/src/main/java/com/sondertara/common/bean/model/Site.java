

package com.sondertara.common.bean.model;

import java.util.Map;

public class Site {
  private String name;
  private User admin;
  private Map<String, User> users;

  Site() {}

  public Site(String name, Map<String, User> users) {
    this.name = name;
    this.users = users;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User getAdmin() {
    return admin;
  }

  public void setAdmin(User admin) {
    this.admin = admin;
  }

  public Map<String, User> getUsers() {
    return users;
  }

  public void setUsers(Map<String, User> users) {
    this.users = users;
  }

  @Override
  public String toString() {
    return "Site{" +
        "name='" + name + '\'' +
        ", admin=" + admin +
        ", users=" + users +
        '}';
  }
}
