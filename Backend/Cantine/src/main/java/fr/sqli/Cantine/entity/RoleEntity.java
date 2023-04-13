package fr.sqli.Cantine.entity;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;


/*@Entity
@Table(name="role")

public class RoleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer idrole;

    @Column(length=2147483647)
    private String description;

    @Column(nullable=false, length=45)
    private String libelle;

    //bi-directional many-to-many association to UserEntity
    @ManyToMany(mappedBy="roles")
    private List<UserEntity> users;

    public RoleEntity() {
    }

    public Integer getIdrole() {
        return this.idrole;
    }

    public void setIdrole(Integer idrole) {
        this.idrole = idrole;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public List<UserEntity> getUsers() {
        return this.users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

}*/