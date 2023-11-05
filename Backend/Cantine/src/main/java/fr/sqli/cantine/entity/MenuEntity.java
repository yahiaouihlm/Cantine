package fr.sqli.cantine.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

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

    @Column(name = "uuid" , nullable=false , length = 254)
    private String  uuid;
    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, length = 3002)
    private String description;
   @Column(name ="creation_date",  nullable = false )
   private LocalDate createdDate;


    @Column(nullable = false, precision = 5, scale = 2)
    @Check(constraints = "price > 0")
    private BigDecimal price;

    @Column(nullable = false)
    @Check(constraints = "status IN (0,1,2)")
    private Integer status;

    //bi-directional many-to-many association to CommandeEntity
    @ManyToMany(fetch =  FetchType.LAZY)
    @JoinTable(
            name="st_order_has_menu",
            joinColumns={@JoinColumn(name="menu_idmenu", nullable=false)},
            inverseJoinColumns={@JoinColumn(name="order_idorder", nullable=false)}
    )

    private List<OrderEntity> orders;

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

    private List<MealEntity> meals;
    //bi-directional many-to-one association to QuantiteEntity
   /* @OneToMany(mappedBy="menu")
    private List<QuantiteEntity> quantites;*/


    @Column(name = "quantity")
    @Check(constraints = "quantity > 0")
    private Integer quantity;

    public MenuEntity(String label, String description, BigDecimal price, Integer status, Integer quantity, ImageEntity image , Set<MealEntity> meals) {
        this.uuid = java.util.UUID.randomUUID().toString();
        this.label = label;
        this.description = description;
        this.createdDate = LocalDate.now();
        this.price = price;
        this.status = status;
        this.quantity = quantity;
        this.image = image;
        this.meals = new ArrayList<>(meals);
    }

    public MenuEntity() {}
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

    public ImageEntity getImage() {
        return image;
    }

    public void setImage(ImageEntity image) {
        this.image = image;
    }

    public List<MealEntity> getMeals() {
        return meals;
    }

    public void setMeals(List<MealEntity> meals) {
        this.meals = meals;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
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