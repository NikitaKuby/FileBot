package ru.test.demobot.modelDTO;

import lombok.Data;

@Data
public class MessageSendDTO {
    private final Long chat_id;
    private final String text;
    private  ReplyMarkupDTO reply_markup;
}
