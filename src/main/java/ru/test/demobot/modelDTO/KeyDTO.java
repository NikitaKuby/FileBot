package ru.test.demobot.modelDTO;

import lombok.Data;

@Data
public class KeyDTO {
    private String text;
    private Boolean request_contact;

    public KeyDTO(String text) {
        this.text = text;
    }

    public KeyDTO(String text, boolean request_contact) {
        this.text = text;
        this.request_contact = request_contact;
    }
}
