

package com.sondertara.common.bean.model;

import java.util.Map;

public class SiteView {
  private String name;
  private UserView admin;
  private Map<String, UserView> users;

  SiteView() {}

  public SiteView(String name, Map<String, UserView> users) {
    this.name = name;
    this.users = users;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UserView getAdmin() {
    return admin;
  }

  public void setAdmin(UserView admin) {
    this.admin = admin;
  }

  public Map<String, UserView> getUsers() {
    return users;
  }

  public void setUsers(Map<String, UserView> users) {
    this.users = users;
  }

  @Override
  public String toString() {
    return "SiteView{" +
        "name='" + name + '\'' +
        ", admin=" + admin +
        ", users=" + users +
        '}';
  }
}
