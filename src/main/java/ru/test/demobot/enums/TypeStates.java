package ru.test.demobot.enums;

import lombok.Getter;

@Getter
public enum TypeStates {
        DONE("done"),
        PROCESSING("processing");

        private final String title;
        TypeStates(String title) {
            this.title=title;
        }

}
