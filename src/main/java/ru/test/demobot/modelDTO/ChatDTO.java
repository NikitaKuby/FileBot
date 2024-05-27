package ru.test.demobot.modelDTO;

import lombok.Data;

@Data
public class ChatDTO {
    private Long id;
    private String first_name;
    private String last_name;
    private String username;
    private String type;

}
