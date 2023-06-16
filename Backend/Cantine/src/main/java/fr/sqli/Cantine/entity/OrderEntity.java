package fr.sqli.Cantine.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import  jakarta.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "order", uniqueConstraints={
        @UniqueConstraint(columnNames={"qr_code"})
})
public class OrderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;


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





    // bi-directional many-to-one association to UserEntity
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    // bi-directional many-to-many association to MenuEntity
    @ManyToMany(mappedBy = "orders")
    private List<MenuEntity> menus;

    // bi-directional many-to-many association to QuantiteEntity
/*    @ManyToMany(mappedBy = "orders")
    private List<QuantiteEntity> quantites;*/


    //bi-directional many-to-many association to PlatEntity
    @ManyToMany(mappedBy="orders")
    private List<MealEntity> meals;



}