package fr.sqli.cantine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lorder", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"qr_code"})
})
public class OrderEntity extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "creation_time", nullable = false)
    private Time creationTime;


    @Column(name = "qr_code", nullable = true, length = 1000)
    private String QRCode;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    @Check(constraints = "status IN (0,1,2)")
    private Integer status;


    @Column(name = "iscancelled", nullable = false)
    private boolean isCancelled;


    // bi-directional many-to-one association to UserEntity
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    // bi-directional many-to-many association to MenuEntity

    @ManyToMany
    @JoinTable(
            name = "lorder_has_menu",
            joinColumns = {@JoinColumn(name = "order_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "menu_id", nullable = false)}
    )
    private List<MenuEntity> menus;

    // bi-directional many-to-many association to QuantiteEntity
/*    @ManyToMany(mappedBy = "orders")
    private List<QuantiteEntity> quantites;*/


    //bi-directional many-to-many association to PlatEntity
    @ManyToMany
    @JoinTable(
            name = "lorder_has_meal"
            , joinColumns = {
            @JoinColumn(name = "order_id", nullable = false)
    }
            , inverseJoinColumns = {
            @JoinColumn(name = "meal_id", nullable = false)
    }
    )
    private List<MealEntity> meals;


}