package fr.sqli.cantine.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "student", uniqueConstraints={
        @UniqueConstraint(columnNames={"email"})
}
)

public class StudentEntity  implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;


    @Column(name = "uuid" , nullable=false , length = 254)
    private String  uuid;
    @Column(name = "firstname" , nullable=false , length = 99)
    private String firstname  ;

    @Column(name = "lastname" , nullable=false , length = 99)
    private String lastname  ;

    @Column(name = "email" , unique = true  , nullable=false , length = 299)
    private String email ;

    @Column(name = "password" , nullable=false , length = 2999)
    private String password  ;

    @Column(name = "birthdate" , nullable=false )
    private LocalDate birthdate ;

    @Column(name = "town" , nullable=false , length = 1000)
    private String town;

    @Column(name = "wallet" , nullable=false )
    @Check(constraints = "wallet >= 0")
    private BigDecimal wallet;
    @Column(name = "phone" , nullable=true , length = 99)
    private String phone ;

    @Column(name = "disable_date" , nullable=true )
    private LocalDate disableDate ;


    @Column(name = "registration_date" , nullable=false )
    private LocalDate registrationDate ;

    @ManyToOne(cascade =  CascadeType.ALL)
    @JoinColumn(name="image_idimage", nullable=false)
    private ImageEntity image;


    @ManyToOne(cascade =  CascadeType.MERGE)
    @JoinColumn(name="class_id", nullable=false)
    private StudentClassEntity studentClass;


    @Column(name=  "status" , nullable=false)
    @Check(constraints = "status IN (0,1)")
    private Integer status ;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDisableDate() {
        return disableDate;
    }

    public void setDisableDate(LocalDate disableDate) {
        this.disableDate = disableDate;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public ImageEntity getImage() {
        return image;
    }

    public void setImage(ImageEntity image) {
        this.image = image;
    }

    public StudentClassEntity getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(StudentClassEntity studentClass) {
        this.studentClass = studentClass;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getWallet() {
        return wallet;
    }
    public void setWallet(BigDecimal wallet) {
        this.wallet = wallet;
    }
}
