package fr.sqli.cantine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public class AbstractEntity {
    @Id
    @Column(name = "id" , nullable=false)
    private String  id  = java.util.UUID.randomUUID().toString();
}
