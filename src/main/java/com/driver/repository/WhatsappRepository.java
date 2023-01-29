package com.driver.repository;

import com.driver.Group;
import com.driver.Message;
import com.driver.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class WhatsappRepository {

    HashMap<String, User>           whatsappUsers = new HashMap<>();
    HashMap<Group , List <User>>    allActualGroups     = new HashMap<>();
    HashMap<Group , List <User>>    allGroups     = new HashMap<>();
    TreeMap<Date , Message>         allMessages   = new TreeMap<>();

    // constants
    private final String SUCCESS = "SUCCESS";

    public String createUser(String name , String mobile) throws Exception {
        User user = new User(name , mobile);

        if (whatsappUsers.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        whatsappUsers.put(mobile , user);
        return SUCCESS;
    }

    public Group createGroup(List<User> users){
        int groupUserCount  = users.size();
        String groupName;

        if (groupUserCount == 2){
            groupName = users.get(1).getName();
        }
        else{
            groupName = "Group " + (allActualGroups.size() + 1);
        }

        Group group = new Group(groupName , groupUserCount);
        group.setUsers(users);

        for (User user : users){
            user.setGroup(group);
        }

        if (groupUserCount > 2){
            allActualGroups.put(group , users);
        }

        allGroups.put(group, users);
        
        return group;
    }

    public int createMessage(String content) {
        int     id      = allMessages.size() + 1; 
        Message message = new Message(id , content );
        allMessages.put(new Date(), message);
        
        return id;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {

        if (!isGroupFound(group)){
            throw new Exception("Group does not exist");
        }

        if (!isMemberOfTheGroup(sender , group)){
            throw new Exception("You are not allowed to send message");
        }

        List<Message> messages = group.getMessages();
        message.setUser(sender);
        message.setTimestamp(new Date());
        messages.add(message);
        group.setMessages(messages);

        return messages.size();
    }

    private boolean isMemberOfTheGroup(User sender, Group group) {
        User user = group.getUsers().stream().filter(val -> val == sender).findFirst().orElse(null);
        return  user != null;
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if (!isGroupFound(group)){
            throw new Exception("Group does not exist");
        }

        if (!isAdmin(group , approver)){
            throw new Exception("Approver does not have rights");
        }

        if (!isMemberOfTheGroup(user , group)){
            throw new Exception("User is not a participant");
        }

        List<User> users = new ArrayList<>();
        for (User newUser : group.getUsers()){
            if (newUser == user){
                users.add(approver);
            }
            else if (newUser == approver){
                users.add(user);
            }
            else{
                users.add(newUser);
            }
        }

        group.setUsers(users);
        allActualGroups.put(group , users);

        return SUCCESS;
    }

    private boolean isAdmin(Group group, User approver) {
        return group.getUsers().get(0) == approver;
    }

    private boolean isGroupFound(Group group){
        Group newGroup = allGroups.keySet().stream().filter(val -> val == group).findFirst().orElse(null);
        return newGroup != null;
    }

    public int removeUser(User user) throws Exception {
        if (user.getGroup() == null){
            throw new Exception("User not found");
        }

        if (isAdmin(user.getGroup() , user)){
            throw new Exception("Cannot remove admin");
        }

        List<Message> totalGroupMessages    = user.getGroup().getMessages();
        List<Message> userMessages          = getUserMessages(user ,  totalGroupMessages);

        for (Map.Entry<Date , Message> mapEntry : allMessages.entrySet()){
                if (userMessages.contains(mapEntry.getValue())){
                    allMessages.remove(mapEntry.getKey());
                }
        }

        whatsappUsers.remove(user.getMobile());
        user.getGroup().removeUser(user);
        user.setGroup(null);

        return user.getGroup().getUsers().size() + (totalGroupMessages.size() - userMessages.size()) + allMessages.size();
    }

    private List<Message> getUserMessages(User user, List<Message> messages) {
        return messages.stream().filter(val -> val.getUser() == user).collect(Collectors.toList());
    }
}
