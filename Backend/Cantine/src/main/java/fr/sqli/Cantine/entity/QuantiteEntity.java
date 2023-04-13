package fr.sqli.Cantine.entity;

import java.io.Serializable;
import java.util.List;
import  jakarta.persistence.*;

/*@Entity
@Table(name="quantite")
public class QuantiteEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer idquantite;

    @Column(nullable=false)
    private Integer quantite;

    //bi-directional many-to-many association to CommandeEntity
    @ManyToMany
    @JoinTable(
            name="commande_has_quantite"
            , joinColumns={
            @JoinColumn(name="quantite_idquantite", nullable=false)
    }
            , inverseJoinColumns={
            @JoinColumn(name="commande_idcommande", nullable=false)
    }
    )
    private List<OrderEntity> commandes;

    //bi-directional many-to-one association to MenuEntity
    @ManyToOne
    @JoinColumn(name="menu_idmenu")
    private MenuEntity menu;

    //bi-directional many-to-one association to PlatEntity
    @ManyToOne
    @JoinColumn(name="plat_idplat")
    private MealEntity plat;

    public QuantiteEntity() {
    }

    public Integer getIdquantite() {
        return this.idquantite;
    }

    public void setIdquantite(Integer idquantite) {
        this.idquantite = idquantite;
    }

    public Integer getQuantite() {
        return this.quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public List<OrderEntity> getCommandes() {
        return this.commandes;
    }

    public void setCommandes(List<OrderEntity> commandes) {
        this.commandes = commandes;
    }

    public MenuEntity getMenu() {
        return this.menu;
    }

    public void setMenu(MenuEntity menu) {
        this.menu = menu;
    }

    public MealEntity getPlat() {
        return this.plat;
    }

    public void setPlat(MealEntity plat) {
        this.plat = plat;
    }

}*/