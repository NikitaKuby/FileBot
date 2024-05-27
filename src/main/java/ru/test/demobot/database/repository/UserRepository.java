
package ru.test.demobot.database.repository;


import ru.test.demobot.database.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM data_sources.users " +
            "WHERE id = :userId and command = :command and state = :state")
    Optional<User> findByIdAndCommandAndState(@Param("userId") Long userId,
                                              @Param("command") String command,
                                              @Param("state") String state);


    Optional<User> findByNumber(String number);
}
