package com.sojolrana.duetgig.models;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.Map;

public class Chat {
    private String chatId;
    private List<String> userIds;
    private String lastMessage;
    private Timestamp lastTimestamp;
    private Map<String, String> userNames;

    public Chat() {
        // Required for Firebase
    }

    public Chat(String chatId, List<String> userIds, String lastMessage, Timestamp lastTimestamp, Map<String, String> userNames) {
        this.chatId = chatId;
        this.userIds = userIds;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
        this.userNames = userNames;
    }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public List<String> getUserIds() { return userIds; }
    public void setUserIds(List<String> userIds) { this.userIds = userIds; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Timestamp getLastTimestamp() { return lastTimestamp; }
    public void setLastTimestamp(Timestamp lastTimestamp) { this.lastTimestamp = lastTimestamp; }

    public Map<String, String> getUserNames() { return userNames; }
    public void setUserNames(Map<String, String> userNames) { this.userNames = userNames; }

    public String getOtherUserName(String currentUserId) {
        if (userNames == null || userNames.isEmpty()) return "Unknown User";
        for (Map.Entry<String, String> entry : userNames.entrySet()) {
            if (!entry.getKey().equals(currentUserId)) {
                return entry.getValue();
            }
        }
        // If it\u0027s a chat with yourself or something is wrong
        if (userNames.containsKey(currentUserId)) {
            return userNames.get(currentUserId);
        }
        return "Unknown User";
    }
}