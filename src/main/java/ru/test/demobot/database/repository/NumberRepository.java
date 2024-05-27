package ru.test.demobot.database.repository;

import ru.test.demobot.database.entites.User;
import ru.test.demobot.database.entites.Numbers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface
NumberRepository extends JpaRepository<Numbers, Long> {
    Optional<Numbers> findByNumber(String number);
}
