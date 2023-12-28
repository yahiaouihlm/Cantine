package fr.sqli.cantine.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "admin", uniqueConstraints={
        @UniqueConstraint(columnNames={"email"})
}
)

public class AdminEntity  extends  UserEntity implements Serializable {

    @Column(name = "address" , nullable=false , length = 3000)
    private String address;

    @Column(name = "phone" , nullable=false , length = 99)
    private String phone ;



    @Column(name = "validation" , nullable=true )
    private  Integer validation ;


    @ManyToOne(cascade =  CascadeType.MERGE)
    @JoinColumn(name="function_id", nullable=false)
    private FunctionEntity function;


/*    public AdminEntity( String firstname ,  String lastname ,  String email ,  String password ,  LocalDate birthdate ,  String town ,  String address ,  String phone ,  Integer status  ,  ImageEntity image ,  LocalDate disableDate ,  Integer validation ,  FunctionEntity function ) {
        this.firstname = firstname ;
        this.lastname = lastname ;
        this.email = email ;
        this.password = password ;
        this.birthdate = birthdate ;
        this.town = town ;
        this.address = address ;
        this.phone = phone ;
        this.status = status ;
        this.registrationDate = LocalDate.now() ;
        this.image = image ;
        this.disableDate = disableDate ;
        this.validation = validation ;
        this.function = function ;

    }*/

    public AdminEntity() {}


    public String getAddress() {
        return address;
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

    public FunctionEntity getFunction() {
        return function;
    }

    public void setFunction(FunctionEntity function) {
        this.function = function;
    }

    public Integer getValidation() {
        return validation;
    }

   public void setValidation(Integer validation) {
            this.validation = validation;
        }
}
