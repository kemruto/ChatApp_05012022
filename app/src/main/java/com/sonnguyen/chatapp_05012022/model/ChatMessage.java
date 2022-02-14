package com.sonnguyen.chatapp_05012022.model;

import java.util.Date;

public class ChatMessage {
    private String messageID;
    private String senderID;
    private String receiverID;
    private String message;
    private String timeStamp;
    private Date dateObject;
    private int feeling = -1;
    private String image;

    public ChatMessage() {

    }

    public ChatMessage(String senderID, String receiverID, String message,String image, String timeStamp, Date dateObject) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.image = image;
        this.timeStamp = timeStamp;
        this.dateObject = dateObject;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }
}
