package ru.test.demobot.modelDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentSendDTO {
    Long chat_id;
    String caption;
    String document;
}
