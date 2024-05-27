package ru.test.demobot.modelDTO;

import lombok.Data;
import ru.test.demobot.enums.TypeUpdate;

@Data
public class UpdateDTO {
    private int update_id;
    private MessageDTO message;

    public TypeUpdate getType() {
        if (message.getText() != null)
            return TypeUpdate.TEXT;
        if (message.getDocument() != null)
            return TypeUpdate.FILE;
        return TypeUpdate.OTHER;
    }
}
