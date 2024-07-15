package fr.sqli.cantine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "luser", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
}
)

public class UserEntity extends AbstractEntity {


    @Column(name = "firstname", nullable = false, length = 99)
    private String firstname;


    @Column(name = "lastname", nullable = false, length = 99)
    private String lastname;


    @Column(name = "email", unique = true, nullable = false, length = 299)
    private String email;

    @Column(name = "password", nullable = false, length = 2999)
    private String password;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Column(name = "town", nullable = false, length = 1000)
    private String town;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;


    @Column(name = "status", nullable = false)
    @Check(constraints = "status IN (0,1)")
    private Integer status;


    @Column(name = "disable_date", nullable = true)
    private LocalDate disableDate;

    @Column(name = "address", length = 3000)
    private String address;

    @Column(name = "phone", nullable = false, length = 50)
    private String phone;

    @Column(name = "validation")
    private Integer validation;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", nullable = false)
    private ImageEntity image;

    @Column(name = "wallet")
    private BigDecimal wallet;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "class_id")
    private StudentClassEntity studentClass;


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "function_id")
    private FunctionEntity function;


    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<RoleEntity> roles;


    public UserEntity() {
        this.registrationDate = LocalDate.now();
    }

}
