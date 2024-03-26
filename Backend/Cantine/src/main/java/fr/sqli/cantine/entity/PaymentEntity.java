package fr.sqli.cantine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
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

    @Enumerated(EnumType.STRING)
    @Column(name = "origin" ,  nullable = false)
    private TransactionType origin;

    public PaymentEntity(AdminEntity admin, StudentEntity student, BigDecimal amount , TransactionType origin) {
        this.admin = admin;
        this.student = student;
        this.amount = amount;
        this.paymentDate = LocalDate.now();
        this.paymentTime = Time.valueOf(LocalTime.now());
        this.origin= origin;
    }
    public  PaymentEntity(){}


}
