package ru.test.demobot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class    SheduledGerpdates {

    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private TelegramService telegramService;

    @Scheduled(fixedRate = 1000)
    public void getUpdatesScheduled()
    {
        telegramService.processing(telegramClient.getUpdates());
    }
}
