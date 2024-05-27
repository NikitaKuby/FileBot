package ru.test.demobot.enums;

public enum TypeUserCommands {
    START("/start"),
    AUTH("auth"),
    HELP("/help"),
    OTHER("");


    private final String title;
    TypeUserCommands(String title) {
        this.title=title;
    }

    public String getTitle() {
        return title;
    }
}
