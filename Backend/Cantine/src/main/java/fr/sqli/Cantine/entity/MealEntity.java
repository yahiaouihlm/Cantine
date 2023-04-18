package fr.sqli.Cantine.entity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;


@Entity
@Table(name="meal", uniqueConstraints={
        @UniqueConstraint(columnNames={"label", "description", "category"})

})
public class MealEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Column(nullable=false, length=45)
    private String category ;

    @Column(nullable=false, length=590)
    private String description;

    @Column(nullable=false, length=100)
    private String label;

    @Column(nullable=false, precision=5, scale=2)
    @Check(constraints = "price > 0")
    private BigDecimal price ;


    @Column(nullable=false )
    @Check(constraints = "quantity > 0")
    private Integer quantity ;

    @Column(nullable=false)
    @Check(constraints = "status IN (0,1)")
    private Integer status;

    //bi-directional many-to-many association to MenuEntity
   @ManyToMany()
    @JoinTable(
            name="menu_has_meal"
            , joinColumns={
            @JoinColumn(name="meal_idmeal", nullable=false)
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
  /*  @ManyToMany
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
    private List<QuantiteEntity> quantites;*/

    public MealEntity(String category, String description, String label, BigDecimal price, Integer quantity, Integer status, ImageEntity image) {
        this.category = category;
        this.description = description;
        this.label = label;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.image = image;
    }

    public MealEntity() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

/* public List<OrderEntity> getCommandes() {
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

    public List<MenuEntity> getMenus() {
        return this.menus;
    }

    public void setMenus(List<MenuEntity> menus) {
        this.menus = menus;
    }
*/

}