package fr.sqli.cantine.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "studentClass")
@Setter
@Getter
public class StudentClassEntity extends AbstractEntity {

    @Column(name = "name", unique = true, nullable = false, length = 99)
    private String name;


    @OneToMany(mappedBy = "studentClass")
    private List<UserEntity> students;

}
