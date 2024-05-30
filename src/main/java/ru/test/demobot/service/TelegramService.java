package ru.test.demobot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.test.demobot.database.entites.Document;
import ru.test.demobot.database.entites.User;
import ru.test.demobot.database.repository.DocumentRepository;
import ru.test.demobot.database.repository.NumberRepository;
import ru.test.demobot.database.repository.UserRepository;
import ru.test.demobot.enums.TypeStates;
import ru.test.demobot.enums.TypeUserCommands;
import ru.test.demobot.model.Command;
import ru.test.demobot.model.CommandBuilder;
import ru.test.demobot.model.OffsetStore;
import ru.test.demobot.modelDTO.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class TelegramService {
    private OffsetStore offsetStore;
    private TelegramClient telegramClient;
    private UserRepository userRepository;
    private DocumentRepository documentRepository;
    private NumberRepository numberRepository;
    private CommandBuilder commandBuilder;

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
                processingText(update.getMessage());
                break;
            case FILE:
                processingFile(update.getMessage());
                break;
            case OTHER:
                telegramClient.nonUnderstandCommand(chatId);
        }
    }

    private void processingText(MessageDTO message) {
        Long chatId = message.getChat().getId();
        Long userId = message.getFrom().getId();
        boolean delete = userRepository.findByIdAndCommandAndState(
                userId,
                TypeUserCommands.DELETE.getTitle(),
                TypeStates.PROCESSING.getTitle()).isPresent();
        if (isUploadDone(message)) {
            return;
        }
        if (isDeleteDone(message)) {
            return;
        }
        if (delete) {
            deleteFiles(message);
            return;
        }
        Command command = commandBuilder.parseToCommand(message);
        switch (command.getTypeCommand()) {
            case VIEW:
                viewFiles(message, true);
                break;
            case UPLOAD:
                uploadFilesStart(message);
                break;
            case DELETE:
                deleteFilesStart(message);
                break;
            default:
                telegramClient.nonUnderstandCommand(chatId);

        }
    }
    private void processingFile(MessageDTO message) {
        Long userId = message.getFrom().getId();
        boolean upload = userRepository.findByIdAndCommandAndState(
                userId,
                TypeUserCommands.UPLOAD.getTitle(),
                TypeStates.PROCESSING.getTitle()).isPresent();
        if (true) {
            uploadFiles(message);
        }
    }

    private void uploadFiles(MessageDTO message) {
        DocumentDTO document = message.getDocument();
        Document documentToSave = new Document(document.getFileId(), document.getFileName(), document.getFileUniqueId(),
                document.getFileSize(), new User(message.getFrom().getId()));
        documentRepository.save(documentToSave);
    }


    private void uploadFilesStart(MessageDTO message) {
        telegramClient.uploadCommand(message.getChat().getId());
        User userUploadProcessing = userRepository.findById(message.getFrom().getId())
                .orElse(new User(message.getFrom().getId()));
        userUploadProcessing.setCommand(TypeUserCommands.UPLOAD.getTitle());
        userUploadProcessing.setState(TypeStates.PROCESSING.getTitle());
        userRepository.save(userUploadProcessing);
    }

    private Boolean isUploadDone(MessageDTO message) {
        Long userId = message.getFrom().getId();
        String uploadDone = "Файлы загруженны!";
        Optional<User> processingUploadUser = userRepository.findByIdAndCommandAndState(userId,
                TypeUserCommands.UPLOAD.getTitle(),
                TypeStates.PROCESSING.getTitle());
        if (processingUploadUser.isPresent() && !Objects.equals(message.getText(), "Готово")) {
            telegramClient.incorrectFile(message.getChat().getId());
            return true;
        }
        if (processingUploadUser.isPresent() && Objects.equals(message.getText(), "Готово")) {
            saveDoneState(message, processingUploadUser.get(), uploadDone);
            return true;
        }
        return false;

    }


    private void deleteFiles(MessageDTO message) {
        if (documentRepository.findByUserIdAndFileName(
                message.getFrom().getId(), message.getText()).isPresent()) {
            Document documentForDelete = documentRepository.findByUserIdAndFileName(
                    message.getFrom().getId(), message.getText()).get();
            documentRepository.delete(documentForDelete);
        } else {
            telegramClient.nonExistentFile(message.getChat().getId());
        }

    }

    private void deleteFilesStart(MessageDTO message) {
        List<String> documentNameList = documentRepository.findByUserId(message.getFrom().getId()).stream()
                .map(Document::getName)
                .collect(Collectors.toList());
        if (documentNameList.isEmpty()) {
            telegramClient.emptyFile(message.getChat().getId());
            return;
        }
        telegramClient.deleteCommand(message.getChat().getId(), documentNameList, "Какие файлы вы хотите удалить?");
        viewFiles(message, false);
        User userDeleteProcessing = userRepository.findById(message.getFrom().getId())
                .orElse(new User(message.getFrom().getId()));
        userDeleteProcessing.setCommand(TypeUserCommands.DELETE.getTitle());
        userDeleteProcessing.setState("processing");
        userRepository.save(userDeleteProcessing);
    }

    private boolean isDeleteDone(MessageDTO message) {
        Long userId = message.getFrom().getId();
        String deleteDone = "Файлы удалены!";
        Optional<User> userDeleteProcessing = userRepository.
                findByIdAndCommandAndState(userId,
                        TypeUserCommands.DELETE.getTitle(),
                        TypeStates.PROCESSING.getTitle());
        if (userDeleteProcessing.isPresent() && documentRepository.findByUserIdAndFileName(
                userId,
                message.getText()).isPresent()) {
            return false;
        }
        if (documentRepository.findByUserIdAndFileName(
                userId,
                message.getText()).isEmpty() &&
                !(Objects.equals(message.getText(), "Готово"))) {
            return false;
        }
        if (userDeleteProcessing.isPresent() && Objects.equals(message.getText(), "Готово")) {
            saveDoneState(message, userDeleteProcessing.get(), deleteDone);
            return true;
        }
        return false;
    }



    private void viewFiles(MessageDTO message, Boolean all) {
        List<Document> documentList;
        if (all) {
            documentList = documentRepository.findAll();
        } else {
            documentList = documentRepository.findByUserId(message.getFrom().getId());
        }
        List<DocumentSendDTO> documentSendDtoList = documentList.stream()
                .map(document -> new DocumentSendDTO(
                        message.getChat().getId(),
                        document.getUser().getName(),
                        document.getId())
                )
                .collect(Collectors.toList());
        if (documentSendDtoList.size() == 0) {
            telegramClient.emptyFile(message.getChat().getId());
        }
        for (DocumentSendDTO document : documentSendDtoList) {
            telegramClient.sendDocument(document);
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


