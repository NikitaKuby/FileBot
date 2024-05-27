package ru.test.demobot.modelDTO;

import lombok.Data;

@Data
public class MessageDTO {
    private int message_id;
    private int date;
    private ChatDTO chat;
    private String text;
    private DocumentDTO document;
    private ContactDTO contact;
    private UserDTO from;
    private MessageDTO reply_to_message;
}