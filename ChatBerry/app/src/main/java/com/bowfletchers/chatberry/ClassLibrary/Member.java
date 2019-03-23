package com.bowfletchers.chatberry.ClassLibrary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Member implements Serializable {
    private String id;
    private String email;
    private String name;
    private int profilePicture;
    private List<Member> friendList;
    private List<Chat> chatList;
    private List<Story> stories;

    public Member() {

    }

    public Member (String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePicture = 0;
        friendList = new ArrayList<>();
        chatList = new ArrayList<>();
        stories = new ArrayList<>();
    }

    public Member(String name){
        this.id = "hbhbjnknk";
        this.name = name;
        this.email = "test@gmail.com";
        this.profilePicture = 0;
        friendList = new ArrayList<>();
        chatList = new ArrayList<>();
        stories = new ArrayList<>();
    }


    public String  getId() {
        return id;
    }

    public void setId(String  id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public int getProfilePicture() {
        return profilePicture;
    }

    public List<Member> getFriendList() {
        return friendList;
    }

    public List<Chat> getChatList() {
        return chatList;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void addFriend(Member friend) {
        this.friendList.add(friend);
    }

    public void removeFriend(Member friend) {
        this.friendList.remove(friend);
    }

    public void addStory(Story story) {
        this.stories.add(story);
    }

    public void removeStory(Story story) {
        this.stories.remove(story);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePicture(int newAvarta) {
        this.profilePicture = newAvarta;
    }

    public void addChat(Chat chat) {
        this.chatList.add(chat);
    }

    public void removeChat(Chat chat) {
        this.chatList.remove(chat);
    }
}
