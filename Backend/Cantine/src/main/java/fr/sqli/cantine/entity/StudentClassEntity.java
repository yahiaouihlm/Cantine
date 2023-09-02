package fr.sqli.cantine.entity;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="studentclass")
public class StudentClassEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Column(name = "name", unique = true,   nullable=false, length=99)
    private   String name  ;



    @OneToMany(mappedBy = "studentClass")
    private List<StudentEntity> students;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
