package fr.sqli.Cantine.entity;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import  jakarta.persistence.*;
@Entity

@Table(name="commande")
public class OrderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer idcommande;


    @Column(nullable=false)

    private LocalDate datecreation;

    private Time heurecreation;

    @Column(nullable = false)
    private Integer statut;

    // bi-directional many-to-one association to UserEntity
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // bi-directional many-to-many association to MenuEntity
    @ManyToMany(mappedBy = "commandes")
    private List<MenuEntity> menus;

    // bi-directional many-to-many association to QuantiteEntity
    @ManyToMany(mappedBy = "commandes")
    private List<QuantiteEntity> quantites;


    //bi-directional many-to-many association to PlatEntity
    @ManyToMany(mappedBy="commandes")
    private List<MealEntity> plats;


    public OrderEntity() {
    }

    public  void addMeal (MealEntity meal){
        this.plats.add(meal);
    }

    public  void addMenu (MenuEntity menu){
        this.menus.add(menu);
    }
    public Integer getIdcommande() {
        return this.idcommande;
    }

    public void setIdcommande(Integer idcommande) {
        this.idcommande = idcommande;
    }

    public LocalDate getDatecreation() {
        return this.datecreation;
    }

    public void setDatecreation(LocalDate datecreation) {
        this.datecreation = datecreation;
    }

    public Time getHeurecreation() {
        return this.heurecreation;
    }

    public void setHeurecreation(Time heurecreation) {
        this.heurecreation = heurecreation;
    }

    public Integer getStatut() {
        return this.statut;
    }

    public void setStatut(Integer statut) {
        this.statut = statut;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<MenuEntity> getMenus() {
        return this.menus;
    }

    public void setMenus(List<MenuEntity> menus) {
        this.menus = menus;
    }

    public List<QuantiteEntity> getQuantites() {
        return this.quantites;
    }

    public void setQuantites(List<QuantiteEntity> quantites) {
        this.quantites = quantites;
    }

    public List<MealEntity> getPlats() {
        return this.plats;
    }

    public void setPlats(List<MealEntity> plats) {
        this.plats = plats;
    }

}