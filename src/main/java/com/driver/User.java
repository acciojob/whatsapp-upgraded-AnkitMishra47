package com.driver;

public class User {
    private String name;
    private String mobile;

    private Group group;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public User() {
    }

    public User(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
    }

}
