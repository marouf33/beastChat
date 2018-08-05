package com.maroufb.beastchat.Entities;

public class Message {

    private String messageText;
    private String messageSenderEmail;
    private String messageSenderPicture;

    public Message() {
    }

    public Message(String messageText, String messageSenderEmail, String messageSenderPicture) {
        this.messageText = messageText;
        this.messageSenderEmail = messageSenderEmail;
        this.messageSenderPicture = messageSenderPicture;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageSenderEmail() {
        return messageSenderEmail;
    }

    public String getMessageSenderPicture() {
        return messageSenderPicture;
    }
}
