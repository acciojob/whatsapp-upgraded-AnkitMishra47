package com.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Group {
    private String name;
    private int numberOfParticipants;
    private List<Message> messages = new ArrayList<>();

    private List<User> users = new ArrayList<>();

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Group(String name, int numberOfParticipants ) {
        this.name = name;
        this.numberOfParticipants = numberOfParticipants;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public Group() {
    }

    public void removeUser(User user){
        List<Message>   messages    = getMessages().stream().filter(message -> message.getUser() != user)
                                                            .collect(Collectors.toList());
        List<User>      users       = getUsers();

        users.remove(user);
        setMessages(messages);
        setUsers(users);
        user.setGroup(null);
    }
}
