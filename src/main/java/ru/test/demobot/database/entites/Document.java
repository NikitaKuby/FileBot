package ru.test.demobot.database.entites;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(schema = "data_sources", name = "document")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    @Id
    private String id;
    private String name;
    @JsonProperty("unique_id")
    private String uniqueId;
    private String size;
    @ManyToOne()
    private User user;
}