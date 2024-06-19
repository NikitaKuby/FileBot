package ru.test.demobot.modelDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReplyMarkupDTO {
    private Boolean resize_keyboard;
    private Boolean one_time_keyboard;
    private Boolean remove_keyboard;
    private String input_field_placeholder;
    private List<List<KeyDTO>> keyboard;
}