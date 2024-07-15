package fr.sqli.cantine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Entity

@Table(name="image")
public class ImageEntity extends AbstractEntity  implements Serializable {


    @Column(name = "name" , nullable=false,length = 399)
    private String  name ;
    //@Column(nullable=false)


    //bi-directional many-to-one association to MenuEntity
    //@OneToMany(mappedBy="image")
   // private List<MenuEntity> menus;

    //bi-directional many-to-one association to PlatEntity
    /*@OneToMany(mappedBy="image")
    private List<MealEntity> plats;*/

    //bi-directional many-to-one association to UserEntity
  //  @OneToMany(mappedBy="image")
   // private List<UserEntity> users;


  /*  public List<MealEntity> getPlats() {
        return plats;
    }

    public void setPlats(List<MealEntity> plats) {
        this.plats = plats;
    }
*/
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
