package fr.sqli.cantine.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "payment")
public class PaymentEntity  extends AbstractEntity{


    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private AdminEntity admin;
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @Column(name = "amount" , nullable=false )
    private BigDecimal amount;

    @Column(name = "payment_date" , nullable=false )
    private LocalDate paymentDate;

    @Column(name = "payment_time", nullable=false)
    private Time paymentTime;


    public PaymentEntity(AdminEntity admin, StudentEntity student, BigDecimal amount) {
        this.admin = admin;
        this.student = student;
        this.amount = amount;
        this.paymentDate = LocalDate.now();
        this.paymentTime = Time.valueOf(LocalTime.now());
    }
    public  PaymentEntity(){}
    public AdminEntity getAdmin() {
        return admin;
    }

    public void setAdmin(AdminEntity admin) {
        this.admin = admin;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Time getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Time paymentTime) {
        this.paymentTime = paymentTime;
    }

}
