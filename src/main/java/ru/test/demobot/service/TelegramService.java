package ru.test.demobot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.test.demobot.database.entites.User;
import ru.test.demobot.database.repository.NumberRepository;
import ru.test.demobot.database.repository.UserRepository;
import ru.test.demobot.enums.TypeStates;
import ru.test.demobot.enums.TypeUserCommands;
import ru.test.demobot.model.OffsetStore;
import ru.test.demobot.modelDTO.MessageDTO;
import ru.test.demobot.modelDTO.MessageSendDTO;
import ru.test.demobot.modelDTO.UpdateDTO;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class TelegramService {
    private OffsetStore offsetStore;
    TelegramClient telegramClient;
    private UserRepository userRepository;
    private NumberRepository numberRepository;

    public void processing(List<UpdateDTO> updates) {

        try {
            for (UpdateDTO update : updates) {
                processingUser(update);
            }
        }catch (Exception e){
            log.info(e.toString());
        }


        if (!updates.isEmpty()) {
            int lastOffset = updates.get(updates.size() - 1).getUpdate_id();
            offsetStore.setOffset((long) (lastOffset + 1));
        }
    }


    private void processingUser(UpdateDTO update){
        Long chatId = update.getMessage().getChat().getId();


        //check on Start and Authorization
       if(isStart(update.getMessage())) return;
       if(isAuth(update.getMessage())) return;

       switch (update.getType()){
            case TEXT:
                //processingText(update.getMessage());
                telegramClient.sendMessage(new MessageSendDTO(chatId, telegramClient.translateRuInEn(update.getMessage().getText())));
                break;
            case FILE:
                //processingFile(update.getMessage());
                telegramClient.sendMessage(new MessageSendDTO(chatId, "Это файл"));
                break;
            case OTHER:
                telegramClient.nonUnderstandCommand(chatId);
        }
    }



    private boolean isStart(MessageDTO message){
        Long userId = message.getChat().getId();
        String messageText = message.getText();
        if (userRepository.findById(userId).isEmpty()) {
          return !Objects.equals(messageText, TypeUserCommands.START.getTitle());
        }
        //Уже стартанул->Идём дальше
        return false;
    }

    private boolean isAuth(MessageDTO message) {

        Long userID = message.getChat().getId();
        if(userRepository.findById(userID).isEmpty()){
            sharedNumber(message);
            return true;
        } else if ((userRepository.findById(userID).get().getAuth()) == null
                || !userRepository.findById(userID).get().getAuth()) {
                attemptAuth(message);
                return true;
        }
        //Авторизован->Идём дальше
        return false;
    }

    private void sharedNumber(MessageDTO message){
        Long userID = message.getFrom().getId();
        String userName = message.getFrom().getFirstName()+' '
                +message.getFrom().getLastName()
                + '@' + message.getFrom().getUsername();
        telegramClient.sharePhone(message.getChat().getId());

        User user = new User(userID,userName);
        user.setCommand(TypeUserCommands.AUTH.getTitle());
        user.setState(TypeStates.PROCESSING.getTitle());
        userRepository.save(user);
    }

    private void attemptAuth(MessageDTO message){
        Long userID = message.getFrom().getId();
        String authDone = "Вы авторизовались";
        if(notPhoneNumber(message)) return;

        String userName = message.getFrom().getFirstName()+' '
                +message.getFrom().getLastName()
                + '@' + message.getFrom().getUsername();
        String phoneNumber = message.getContact().getPhoneNumber();

        User user = userRepository.findById(userID).orElse(new User(userID, userName, phoneNumber));
        user.setNumber(phoneNumber);
        user.setName(userName);
        if (numberRepository.findByNumber(phoneNumber).isPresent()) {
            user.setAuth(true);
            saveDoneState(message, user, authDone);
            return;
        }
        userRepository.save(user);
        telegramClient.unAuth(userID);
    }

    private boolean notPhoneNumber(MessageDTO message){
        Long userID = message.getFrom().getId();
        if (message.getContact()==null) {
            telegramClient.incorrectNumber(userID);
            return true;
        }
        return false;
    }

    private void saveDoneState(MessageDTO message, User user, String commandDone) {
        telegramClient.commandDone(message.getChat().getId(), commandDone);
        user.setState(TypeStates.DONE.getTitle());
        userRepository.save(user);
    }

}


