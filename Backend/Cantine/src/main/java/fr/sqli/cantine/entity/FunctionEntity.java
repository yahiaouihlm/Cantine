package fr.sqli.cantine.entity;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="function")
public class FunctionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Column(name = "name", unique = true,   nullable=false, length=299)
    private  String name  ;

    @OneToMany(mappedBy="function")
    private List<AdminEntity> admins;


    public List<AdminEntity> getAdmins() {
        return admins;
    }

    public void setAdmins(List<AdminEntity> admins) {
        this.admins = admins;
    }

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
