package fr.sqli.cantine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public class AbstractEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id = java.util.UUID.randomUUID().toString();
}
