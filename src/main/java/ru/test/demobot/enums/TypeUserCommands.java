package ru.test.demobot.enums;

public enum TypeUserCommands {

    UPLOAD("/upload"),
    VIEW("/view"),
    DELETE("/delete"),
    START("/start"),
    AUTH("/auth"),
    HELP("/help"),
    CREATE("/create"),
    OTHER("");


    private final String title;
    TypeUserCommands(String title) {
        this.title=title;
    }

    public String getTitle() {
        return title;
    }
}
