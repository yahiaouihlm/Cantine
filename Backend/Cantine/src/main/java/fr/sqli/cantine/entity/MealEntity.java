package fr.sqli.cantine.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "meal", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"label", "description", "category"})

})
public class MealEntity extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    @Column(nullable = false, length = 100)
    private String label;


    @Column(nullable = false, length = 101)
    private String category;

    @Column(nullable = false, length = 3002)
    private String description;

    @Column(nullable = false, precision = 5, scale = 2)
    @Check(constraints = "price > 0")
    private BigDecimal price;


    @Column(nullable = false)
    @Check(constraints = "quantity > 0")
    private Integer quantity;

    @Column(nullable = false)
    @Check(constraints = "status IN (0,1,2)")
    private Integer status;

    //bi-directional many-to-many association to MenuEntity
    @ManyToMany()
    @JoinTable(
            name = "menu_has_meal"
            , joinColumns = {
            @JoinColumn(name = "meal_id", nullable = false)
    }
            , inverseJoinColumns = {
            @JoinColumn(name = "menu_id", nullable = false)
    }
    )
    private List<MenuEntity> menus;

    //bi-directional many-to-one association to ImageEntity
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", nullable = false)
    private ImageEntity image;

    @Enumerated(EnumType.STRING)
    private MealTypeEnum meal_type;

    //bi-directional many-to-many association to CommandeEntity
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "lorder_has_meal"
            , joinColumns = {
            @JoinColumn(name = "meal_id", nullable = false)
    }
            , inverseJoinColumns = {
            @JoinColumn(name = "order_id", nullable = false)
    }
    )
    private List<OrderEntity> orders;
 /*
    //bi-directional many-to-one association to QuantiteEntity
    @OneToMany(mappedBy="plat")
    private List<QuantiteEntity> quantites;*/

    public MealEntity(String label, String category, String description, BigDecimal price, Integer quantity, Integer status, MealTypeEnum mealTypeEnum, ImageEntity image) {
        ;
        this.label = label.trim();
        this.category = category.trim();
        this.description = description.trim();
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.meal_type = mealTypeEnum;
        this.image = image;
    }


    public MealEntity() {

    }
}