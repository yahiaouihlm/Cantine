package fr.sqli.Cantine.entity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.*;
@Entity
@Table(name="plat")
public class MealEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer idplat;

    @Column(nullable=false, length=45)
    private String categorie;

    @Column(nullable=false, length=2147483647)
    private String description;

    @Column(nullable=false, length=100)
    private String label;

    @Column(nullable=false, precision=5, scale=2)
    private BigDecimal prixht;


    private Integer quantite;

    @Column(nullable=false)
    private Integer status;

    //bi-directional many-to-many association to MenuEntity
    @ManyToMany()
    @JoinTable(
            name="menu_has_plat"
            , joinColumns={
            @JoinColumn(name="plat_idplat", nullable=false)
    }
            , inverseJoinColumns={
            @JoinColumn(name="menu_idmenu", nullable=false)
    }
    )
    private List<MenuEntity> menus;

    //bi-directional many-to-one association to ImageEntity
    @ManyToOne(cascade =  CascadeType.ALL)
    @JoinColumn(name="image_idimage", nullable=false)
    private ImageEntity image;

    //bi-directional many-to-many association to CommandeEntity
    @ManyToMany
    @JoinTable(
            name="plat_has_commande"
            , joinColumns={
            @JoinColumn(name="plat_idplat", nullable=false)
    }
            , inverseJoinColumns={
            @JoinColumn(name="commande_idcommande", nullable=false)
    }
    )
    private List<OrderEntity> commandes;

    //bi-directional many-to-one association to QuantiteEntity
    @OneToMany(mappedBy="plat")
    private List<QuantiteEntity> quantites;

    public MealEntity() {
    }

    public Integer getIdplat() {
        return this.idplat;
    }

    public void setIdplat(Integer idplat) {
        this.idplat = idplat;
    }

    public String getCategorie() {
        return this.categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getQuantite() {
        return this.quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<MenuEntity> getMenus() {
        return this.menus;
    }

    public void setMenus(List<MenuEntity> menus) {
        this.menus = menus;
    }

    public ImageEntity getImage() {
        return this.image;
    }

    public void setImage(ImageEntity image) {
        this.image = image;
    }

    public List<OrderEntity> getCommandes() {
        return this.commandes;
    }

    public void setCommandes(List<OrderEntity> commandes) {
        this.commandes = commandes;
    }

    public List<QuantiteEntity> getQuantites() {
        return this.quantites;
    }

    public void setQuantites(List<QuantiteEntity> quantites) {
        this.quantites = quantites;
    }

    public QuantiteEntity addQuantite(QuantiteEntity quantite) {
        getQuantites().add(quantite);
        quantite.setPlat(this);

        return quantite;
    }

    public QuantiteEntity removeQuantite(QuantiteEntity quantite) {
        getQuantites().remove(quantite);
        quantite.setPlat(null);

        return quantite;
    }

}