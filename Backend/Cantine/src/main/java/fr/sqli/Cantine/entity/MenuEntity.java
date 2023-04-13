package fr.sqli.Cantine.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.*;
/*
@Entity
@Table(name="menu")
public class MenuEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer idmenu;

    @Column(nullable=false, length=2147483647)
    private String description;

    @Column(length=45)
    private String jourassocier;

    @Column(nullable=false, length=100)
    private String label;

    @Column(nullable=false, precision=5, scale=2)
    private BigDecimal prixht;

    @Column(nullable=false)
    private Integer status;

    //bi-directional many-to-many association to CommandeEntity
    @ManyToMany
    @JoinTable(
            name="commande_has_menu"
            , joinColumns={
            @JoinColumn(name="menu_idmenu", nullable=false)
    }
            , inverseJoinColumns={
            @JoinColumn(name="commande_idcommande", nullable=false)
    }
    )
    private List<OrderEntity> commandes;

    //bi-directional many-to-one association to ImageEntity
    @ManyToOne( cascade = CascadeType.ALL)
    @JoinColumn(name="image_idimage", nullable=false)
    private ImageEntity image;

    //bi-directional many-to-many association to PlatEntity

    @ManyToMany()
    @JoinTable(
            name=" menu_has_plat",
            joinColumns={ @JoinColumn(name="menu_idmenu", nullable=false)},
            inverseJoinColumns={@JoinColumn(name=" plat_idplat", nullable=false)}
    )
    private List<MealEntity> plats;
    //bi-directional many-to-one association to QuantiteEntity
    @OneToMany(mappedBy="menu")
    private List<QuantiteEntity> quantites;



    @Column(name = "quantite")
    private  Integer quantite;
    public MenuEntity() {
    }

    public Integer getIdmenu() {
        return this.idmenu;
    }
    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
    public void setIdmenu(Integer idmenu) {
        this.idmenu = idmenu;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJourassocier() {
        return this.jourassocier;
    }

    public void setJourassocier(String jourassocier) {
        this.jourassocier = jourassocier;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getPrixht() {
        return this.prixht;
    }

    public void setPrixht(BigDecimal prixht) {
        this.prixht = prixht;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<OrderEntity> getCommandes() {
        return this.commandes;
    }

    public void setCommandes(List<OrderEntity> commandes) {
        this.commandes = commandes;
    }

    public ImageEntity getImage() {
        return this.image;
    }

    public void setImage(ImageEntity image) {
        this.image = image;
    }

    public List<MealEntity> getPlats() {
        return this.plats;
    }

    public void setPlats(List<MealEntity> plats) {
        this.plats = plats;
    }

    public List<QuantiteEntity> getQuantites() {
        return this.quantites;
    }

    public void setQuantites(List<QuantiteEntity> quantites) {
        this.quantites = quantites;
    }

    public QuantiteEntity addQuantite(QuantiteEntity quantite) {
        getQuantites().add(quantite);
        quantite.setMenu(this);

        return quantite;
    }

    public QuantiteEntity removeQuantite(QuantiteEntity quantite) {
        getQuantites().remove(quantite);
        quantite.setMenu(null);

        return quantite;
    }

}*/