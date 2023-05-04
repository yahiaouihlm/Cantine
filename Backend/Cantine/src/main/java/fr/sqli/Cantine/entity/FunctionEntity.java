package fr.sqli.Cantine.entity;


import jakarta.persistence.*;

@Entity
@Table(name="function")
public class FunctionEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Column(name = "name",  nullable=false, length=299)
    private  String name  ;


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
