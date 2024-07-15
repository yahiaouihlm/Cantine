package fr.sqli.cantine.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "adminFunction")
@Getter
@Setter
public class FunctionEntity extends AbstractEntity implements Serializable {

    @Column(name = "name", unique = true, nullable = false, length = 299)
    private String name;

    @OneToMany(mappedBy = "function")
    private List<UserEntity> admins;


}
