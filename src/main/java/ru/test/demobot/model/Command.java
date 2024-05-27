package ru.test.demobot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.test.demobot.enums.TypeUserCommands;

import java.util.List;

@Getter
@Setter
@Component
@AllArgsConstructor
@NoArgsConstructor
public class Command {
    private TypeUserCommands typeCommand;
    private List<String> args;
}