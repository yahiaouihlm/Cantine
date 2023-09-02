package fr.sqli.cantine.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity

@Table(name="image")
public class ImageEntity  implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer idimage;

    @Column(name = "imagename")
    private String  imagename ;
    //@Column(nullable=false)


    //bi-directional many-to-one association to MenuEntity
    //@OneToMany(mappedBy="image")
   // private List<MenuEntity> menus;

    //bi-directional many-to-one association to PlatEntity
    @OneToMany(mappedBy="image")
    private List<MealEntity> plats;

    //bi-directional many-to-one association to UserEntity
  //  @OneToMany(mappedBy="image")
   // private List<UserEntity> users;



    public Integer getIdimage() {
        return idimage;
    }

    public void setIdimage(Integer idimage) {
        this.idimage = idimage;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }


    public List<MealEntity> getPlats() {
        return plats;
    }

    public void setPlats(List<MealEntity> plats) {
        this.plats = plats;
    }

   /* public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<MenuEntity> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuEntity> menus) {
        this.menus = menus;
    }
    */
}
