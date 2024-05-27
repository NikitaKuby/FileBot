package ru.test.demobot.modelDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContactDTO {
    @JsonProperty("phone_number")
    private String PhoneNumber;
}
