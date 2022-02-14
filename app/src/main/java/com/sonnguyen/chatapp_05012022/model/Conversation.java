package com.sonnguyen.chatapp_05012022.model;

public class Conversation {
    private ChatMessage lastMessage;
    private User otherUser;

    public Conversation(){

    }

    public Conversation(ChatMessage lastMessage, User otherUser) {
        this.lastMessage = lastMessage;
        this.otherUser = otherUser;
    }

    public ChatMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }
}
