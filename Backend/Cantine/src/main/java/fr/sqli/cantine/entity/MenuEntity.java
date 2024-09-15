package fr.sqli.cantine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "menu", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"label", "description", "price"})

})
public class MenuEntity extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, length = 3002)
    private String description;
    @Column(name = "creation_date", nullable = false)
    private LocalDate createdDate;


    @Column(nullable = false, precision = 5, scale = 2)
    @Check(constraints = "price > 0")
    private BigDecimal price;

    @Column(nullable = false)
    @Check(constraints = "status IN (0,1,2)")
    private Integer status;

    //bi-directional many-to-many association to CommandeEntity
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "lorder_has_menu",
            joinColumns = {@JoinColumn(name = "menu_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "order_id", nullable = false)}
    )

    private List<OrderEntity> orders;

    //bi-directional many-to-one association to ImageEntity
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", nullable = false)
    private ImageEntity image;

    //bi-directional many-to-many association to PlatEntity

    @ManyToMany()
    @JoinTable(
            name = "menu_has_meal",
            joinColumns = {@JoinColumn(name = "menu_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "meal_id", nullable = false)}
    )

    private List<MealEntity> meals;
    //bi-directional many-to-one association to QuantiteEntity
   /* @OneToMany(mappedBy="menu")
    private List<QuantiteEntity> quantites;*/


    @Column(name = "quantity")
    @Check(constraints = "quantity > 0")
    private Integer quantity;

    public MenuEntity(String label, String description, BigDecimal price, Integer status, Integer quantity, ImageEntity image, Set<MealEntity> meals) {
        super();
        this.label = label;
        this.description = description;
        this.createdDate = LocalDate.now();
        this.price = price;
        this.status = status;
        this.quantity = quantity;
        this.image = image;
        this.meals = new ArrayList<>(meals);
    }

    public MenuEntity() {
    }

}

