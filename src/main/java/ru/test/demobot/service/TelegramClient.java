package ru.test.demobot.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.test.demobot.model.OffsetStore;
import ru.test.demobot.modelDTO.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Slf4j
@Component
@ConfigurationProperties(prefix = "springbot.telegram-client")
@Setter
public class TelegramClient {
    private String botToken;
    private String telegramUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    public int updateIdMessage;

    @Autowired
    private OffsetStore offsetStore;

    public List<UpdateDTO> getUpdates(){
        Optional<Long> maybeOffset = offsetStore.tryReadOffset();
            return maybeOffset.map(this::getUpdates).orElseGet(Collections::emptyList);
    }

    private List<UpdateDTO> getUpdates(Long offset) {

        String url = String.format("%s/bot%s/getUpdates?timeout=90&offset=%s", telegramUrl, botToken, offset);
        UpdatesDTO response;
        try {
            response = restTemplate.getForObject(new URI(url), UpdatesDTO.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return response != null ? response.getResult() : null;
    }

    public void sendMessage(MessageSendDTO message){

        String url = String.format("%s/bot%s/sendMessage", telegramUrl, botToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Gson gson = new GsonBuilder().create();
        HttpEntity<String> request = new HttpEntity<>(gson.toJson(message), headers);
        restTemplate.postForEntity(url, request, String.class);
    }




    @SneakyThrows
    public String translateRuInEn(String ruText){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://google-translation-unlimited.p.rapidapi.com/translate"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("X-RapidAPI-Key", "d8bc52304amshe62fd31d989b70cp1ece26jsnfe9c053a08b0")
                .header("X-RapidAPI-Host", "google-translation-unlimited.p.rapidapi.com")
                .method("POST", HttpRequest.BodyPublishers
                        .ofString("texte="+ruText+"&to_lang=en"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObject = new JsonParser().parse(response.body()).getAsJsonObject();
        return jsonObject.get("translation_data").getAsJsonObject().get("translation").getAsString();
    }



    public void nonUnderstandCommand(Long chatID){
        MessageSendDTO warnMessage = new MessageSendDTO(chatID, "Sorry, я не понимая вашу команду");
        sendMessage(warnMessage);
    }


    public void sharePhone(Long chatId) {
        KeyDTO sharedKey = new KeyDTO("Поделиться номером", true);

        List<KeyDTO> inerList = new ArrayList<>();
        inerList.add(sharedKey);
        List<List<KeyDTO>> listKey = new ArrayList<>();
        listKey.add(inerList);

        ReplyMarkupDTO replyMarkupDto = new ReplyMarkupDTO();
        replyMarkupDto.setKeyboard(listKey);
        replyMarkupDto.setOne_time_keyboard(true);
        replyMarkupDto.setResize_keyboard(true);
        replyMarkupDto.setRemove_keyboard(false);

        MessageSendDTO authMessage = new MessageSendDTO(chatId, "Авторизуйся что бы использовать функции бота");
        authMessage.setReply_markup(replyMarkupDto);
        sendMessage(authMessage);
    }

    public void commandDone(Long chatId, String text) {
        MessageSendDTO infoMessage = new MessageSendDTO(chatId, text);
        ReplyMarkupDTO deleteKeyBoard = new ReplyMarkupDTO();
        deleteKeyBoard.setRemove_keyboard(true);
        infoMessage.setReply_markup(deleteKeyBoard);
        sendMessage(infoMessage);
    }



    public void incorrectNumber(Long userID) {
        MessageSendDTO warnMessage = new MessageSendDTO(userID, "Неккоректный номер телефона");
        sendMessage(warnMessage);
    }
    public void unAuth(Long chatId) {
        MessageSendDTO warnMessage = new MessageSendDTO(chatId, "Вы не смогли пройти авторизацию " +
                "поробуйте снова");
        sendMessage(warnMessage);
    }


}
