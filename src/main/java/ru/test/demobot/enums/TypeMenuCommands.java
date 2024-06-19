package ru.test.demobot.enums;

import lombok.Getter;

@Getter
public enum TypeMenuCommands {
    TRANSLATE("/translate"),
    DELETEUSER("/deleteuser"),
    OTHER(""),
    FILEBUFFER("/document");

    private final String title;

    TypeMenuCommands(String title) {
        this.title = title;
    }
}
