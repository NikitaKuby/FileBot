package ru.test.demobot.database.entites;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(schema = "data_sources", name = "numbers")
public class Numbers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String number;
}
