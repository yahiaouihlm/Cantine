package fr.sqli.Cantine.entity;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "admin")
public class AdminEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(unique=true, nullable=false )
    private Integer id;
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

    @Column(name = "town" , nullable=false , length = 399)
    private String town;


    @Column(name = "address" , nullable=false , length = 999)
    private String address;

    @Column(name = "phone" , nullable=false , length = 99)
    private String phone ;

    @Column(name=  "status" , nullable=false , length = 99)
    private Integer status ;


    @Column(name = "registration_date" , nullable=false )
    private LocalDate registrationDate ;

    @ManyToOne(cascade =  CascadeType.ALL)
    @JoinColumn(name="image_idimage", nullable=false)
    private ImageEntity image;


    @ManyToOne(cascade =  CascadeType.ALL)
    @JoinColumn(name="function_id", nullable=false)
    private FunctionEntity function;


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

    public String getAddress() {
        return address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public FunctionEntity getFunction() {
        return function;
    }

    public void setFunction(FunctionEntity function) {
        this.function = function;
    }
}
