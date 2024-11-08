package ru.test.demobot.database.repository;

import ru.test.demobot.database.entites.Document;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository  extends JpaRepository<Document, String>{
    @Query(nativeQuery = true, value = "SELECT * FROM data_sources.document" +
            " WHERE user_id = :userId and name = :fileName limit 1")
    Optional<Document> findByUserIdAndFileName(@Param("userId") Long id, @Param("fileName") String name);

    @Query(nativeQuery = true, value = "SELECT * FROM data_sources.document WHERE user_id = :userId")
    List<Document> findByUserId(@Param("userId") Long id);
    @Transactional
    void deleteByUserId(Long id);
}
