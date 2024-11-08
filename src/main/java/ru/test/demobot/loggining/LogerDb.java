package ru.test.demobot.loggining;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.test.demobot.database.repository.UserRepository;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty("db-show.enabled")
public class LogerDb implements ApplicationRunner {
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Runner");
        log.info("Data from user: {}", userRepository.findAll());
    }
}