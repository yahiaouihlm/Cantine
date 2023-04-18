package fr.sqli.Cantine.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name="menu", uniqueConstraints={
        @UniqueConstraint(columnNames={"label", "description", "price"})

})
public class MenuEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;
    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, length = 700)
    private String description;
   @Column(name ="creation_date",  nullable = false )
   private LocalDate createdDate;


    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer status;

    //bi-directional many-to-many association to CommandeEntity
    /*@ManyToMany
    @JoinTable(
            name="commande_has_menu"
            , joinColumns={
            @JoinColumn(name="menu_idmenu", nullable=false)
    }
            , inverseJoinColumns={
            @JoinColumn(name="commande_idcommande", nullable=false)
    }
    )
    private List<OrderEntity> commandes;*/

    //bi-directional many-to-one association to ImageEntity
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_idimage", nullable = false)
    private ImageEntity image;

    //bi-directional many-to-many association to PlatEntity

    @ManyToMany()
    @JoinTable(
            name = " menu_has_meal",
            joinColumns = {@JoinColumn(name = "menu_idmenu", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "meal_idmeal", nullable = false)}
    )
    private List<MealEntity> plats;
    //bi-directional many-to-one association to QuantiteEntity
   /* @OneToMany(mappedBy="menu")
    private List<QuantiteEntity> quantites;*/


    @Column(name = "quantity")
    private Integer quantity;

    public MenuEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /* public List<OrderEntity> getCommandes() {
        return this.commandes;
    }

    public void setCommandes(List<OrderEntity> commandes) {
        this.commandes = commandes;
    }*/

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

}

  /*  public List<QuantiteEntity> getQuantites() {
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