package com.diamonddogs.huddle;

import java.util.Date;

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private String groupId;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser, String id) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.groupId = id;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(String messageText, String messageUser, String id, long messageTime) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.groupId = id;
        this.messageTime = messageTime;
    }


    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getGroupId() { return groupId; }

    public void setGroupId(String groupId) { this.groupId = groupId; }
}
