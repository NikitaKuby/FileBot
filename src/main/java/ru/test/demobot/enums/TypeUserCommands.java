package ru.test.demobot.enums;

public enum TypeUserCommands {

    UPLOAD("/upload"),
    DOCUMENT("/document"),
    VIEW("/view"),
    DELETE("/delete"),
    HELP("/help"),
    START("/start"),
    AUTH("/auth"),
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
