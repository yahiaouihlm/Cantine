package fr.sqli.cantine.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import  jakarta.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "st_order", uniqueConstraints={
        @UniqueConstraint(columnNames={"qr_code"})
})
public class OrderEntity extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "creation_date", nullable=false)
    private LocalDate creationDate;

    @Column(name = "creation_time", nullable=false)
    private Time creationTime;


    @Column(name = "qr_code", nullable=false, length = 1000)
    private  String QRCode;
    @Column(nullable = false )
    private BigDecimal price;
    @Column(nullable = false)
    @Check(constraints = "status IN (0,1)")
    private Integer status;


    @Column(name = "iscancelled", nullable = false)
    private  boolean   isCancelled ;


    // bi-directional many-to-one association to UserEntity
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    // bi-directional many-to-many association to MenuEntity

    @ManyToMany
    @JoinTable(
            name="st_order_has_menu",
            joinColumns={@JoinColumn(name="order_idorder", nullable=false)},
            inverseJoinColumns={@JoinColumn(name="menu_idmenu", nullable=false)}
    )
    private List<MenuEntity> menus;

    // bi-directional many-to-many association to QuantiteEntity
/*    @ManyToMany(mappedBy = "orders")
    private List<QuantiteEntity> quantites;*/


    //bi-directional many-to-many association to PlatEntity
    @ManyToMany
    @JoinTable(
            name="st_order_has_meal"
            , joinColumns={
            @JoinColumn(name="order_idorder", nullable=false)
    }
            , inverseJoinColumns={
            @JoinColumn(name="meal_idmeal", nullable=false)
    }
    )
    private List<MealEntity> meals;

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Time getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Time creationTime) {
        this.creationTime = creationTime;
    }


    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
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


    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public List<MenuEntity> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuEntity> menus) {
        this.menus = menus;
    }

    public List<MealEntity> getMeals() {
        return meals;
    }

    public void setMeals(List<MealEntity> meals) {
        this.meals = meals;
    }
}