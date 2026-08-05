package com.sojolrana.duetgig.models;

import com.google.firebase.Timestamp;

public class Message {
    private String messageId;
    private String senderId;
    private String content;
    private Timestamp timestamp;

    public Message() {
        // Required for Firebase
    }

    public Message(String messageId, String senderId, String content, Timestamp timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public Timestamp getTimestamp() { return timestamp; }
}