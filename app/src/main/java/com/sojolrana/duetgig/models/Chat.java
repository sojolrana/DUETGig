package com.sojolrana.duetgig.models;

import com.google.firebase.Timestamp;
import java.util.List;

public class Chat {
    private String chatId;
    private List<String> userIds;
    private String lastMessage;
    private Timestamp lastTimestamp;
    private String otherUserName; // For display in list

    public Chat() {
        // Required for Firebase
    }

    public Chat(String chatId, List<String> userIds, String lastMessage, Timestamp lastTimestamp, String otherUserName) {
        this.chatId = chatId;
        this.userIds = userIds;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
        this.otherUserName = otherUserName;
    }

    public String getChatId() { return chatId; }
    public List<String> getUserIds() { return userIds; }
    public String getLastMessage() { return lastMessage; }
    public Timestamp getLastTimestamp() { return lastTimestamp; }
    public String getOtherUserName() { return otherUserName; }
    public void setOtherUserName(String name) { this.otherUserName = name; }
}