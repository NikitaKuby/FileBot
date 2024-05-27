package ru.test.demobot.modelDTO;

import lombok.Data;

import java.util.List;
@Data
public class UpdatesDTO {
    private Boolean ok;
    private List<UpdateDTO> result;

}