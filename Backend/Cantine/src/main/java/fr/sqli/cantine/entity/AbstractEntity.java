package fr.sqli.cantine.entity;

import jakarta.persistence.*;

@MappedSuperclass
public class AbstractEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false )
    private Integer id;
    @Column(name = "uuid" , nullable=false , length = 254)
    private String  uuid  = java.util.UUID.randomUUID().toString();




    public Integer getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
