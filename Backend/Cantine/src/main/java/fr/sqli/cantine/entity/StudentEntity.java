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

public class StudentEntity  extends UserEntity implements Serializable {

    @Column(name = "wallet" , nullable=false )
    @Check(constraints = "wallet >= 0")
    private BigDecimal wallet;
    @Column(name = "phone" , nullable=true , length = 99)
    private String phone ;
    @ManyToOne(cascade =  CascadeType.MERGE)
    @JoinColumn(name="class_id", nullable=false)
    private StudentClassEntity studentClass;




    public  StudentEntity (String firstname , String lastname , String email , String password , LocalDate birthdate , String town , String phone , LocalDate disableDate  , StudentClassEntity studentClass , Integer status , BigDecimal wallet,  ImageEntity image) {
        super();
        super.setFirstname(firstname.trim());
        super.setLastname(lastname.trim());
        super.setEmail(email.trim());
        super.setPassword(password);
        super.setBirthdate(birthdate);
        super.setTown(town);
        super.setDisableDate(disableDate);
        super.setImage(image);
        super.setStatus(status);
        this.phone = phone;
        this.studentClass = studentClass;
        this.wallet = wallet;
    }

    public StudentEntity() {}

    public BigDecimal getWallet() {
        return wallet;
    }

    public void setWallet(BigDecimal wallet) {
        this.wallet = wallet;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public StudentClassEntity getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(StudentClassEntity studentClass) {
        this.studentClass = studentClass;
    }
}
