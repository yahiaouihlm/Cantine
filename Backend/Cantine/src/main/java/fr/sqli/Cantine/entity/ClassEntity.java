package fr.sqli.Cantine.entity;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name="class")
public class ClassEntity {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Column(name = "name",  nullable=false, length=99)
    private  String name  ;


    @OneToMany(mappedBy="class")
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

    public List<StudentEntity> getStudents() {
        return students;
    }

    public void setStudents(List<StudentEntity> students) {
        this.students = students;
    }
}
