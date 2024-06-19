package ru.test.demobot.model;

import ru.test.demobot.enums.TypeMenuCommands;
import ru.test.demobot.enums.TypeUserCommands;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.test.demobot.modelDTO.MessageDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "springdemobot.bot-property")
public class CommandBuilder {
    private String adminPostfix;

    public Command parseToCommand(MessageDTO message) {

        TypeUserCommands typeUserCommands = TypeUserCommands.OTHER;
        List<String> args = new ArrayList<>();
        if (Objects.equals(message.getText(), TypeUserCommands.VIEW.getTitle()) ||
                message.getText().contains(TypeUserCommands.VIEW.getTitle() + adminPostfix)) {
            typeUserCommands = TypeUserCommands.VIEW;
            args = Arrays.stream(
                    message.getText()
                            .replace(TypeUserCommands.VIEW.getTitle() + adminPostfix , "")
                            .strip()
                            .split(" ")).collect(Collectors.toList());
        }

        if (Objects.equals(message.getText(), TypeUserCommands.DELETE.getTitle()) ||
                message.getText().contains(TypeUserCommands.DELETE.getTitle() + adminPostfix)) {
            typeUserCommands = TypeUserCommands.DELETE;
            args = Arrays.stream(
                    message.getText()
                            .replace(TypeUserCommands.DELETE.getTitle() + adminPostfix , "")
                            .strip()
                            .split(" ")).collect(Collectors.toList());
        }
        if (message.getText().contains(TypeUserCommands.CREATE.getTitle() + adminPostfix)) {
            typeUserCommands = TypeUserCommands.CREATE;
            args = Arrays.stream(
                    message.getText()
                            .replace(TypeUserCommands.CREATE.getTitle() + adminPostfix, "")
                            .strip()
                            .split(" ")).collect(Collectors.toList());
        }
        if (Objects.equals(message.getText(), TypeUserCommands.UPLOAD.getTitle()))
            typeUserCommands = TypeUserCommands.UPLOAD;

        TypeMenuCommands typeMenuCommands = TypeMenuCommands.OTHER;
        if (Objects.equals(message.getText(), TypeMenuCommands.TRANSLATE.getTitle()) ||
                message.getText().contains(TypeMenuCommands.TRANSLATE.getTitle() + adminPostfix)) {
            typeMenuCommands = TypeMenuCommands.TRANSLATE;
            args = Arrays.stream(
                    message.getText()
                            .replace(TypeMenuCommands.TRANSLATE.getTitle() + adminPostfix, "")
                            .strip()
                            .split(" ")).collect(Collectors.toList());
        }

        if (Objects.equals(message.getText(), TypeMenuCommands.FILEBUFFER.getTitle()) ||
                message.getText().contains(TypeMenuCommands.FILEBUFFER.getTitle() + adminPostfix)) {
            typeMenuCommands = TypeMenuCommands.FILEBUFFER;
            args = Arrays.stream(
                    message.getText()
                            .replace(TypeMenuCommands.FILEBUFFER.getTitle() + adminPostfix, "")
                            .strip()
                            .split(" ")).collect(Collectors.toList());
        }

        if (Objects.equals(message.getText(), TypeMenuCommands.DELETEUSER.getTitle()) ||
                message.getText().contains(TypeMenuCommands.DELETEUSER.getTitle() + adminPostfix)) {
            typeMenuCommands = TypeMenuCommands.DELETEUSER;
            args = Arrays.stream(
                    message.getText()
                            .replace(TypeMenuCommands.DELETEUSER.getTitle() + adminPostfix, "")
                            .strip()
                            .split(" ")).collect(Collectors.toList());
        }


        return new Command(typeUserCommands, typeMenuCommands,  args);
    }
}