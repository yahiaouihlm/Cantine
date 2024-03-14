package fr.sqli.cantine.dto.out.person;

import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.StudentEntity;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;

public class TransactionDtout {

    private  AdminDtout admin;

    private  StudentDtout student;

    private BigDecimal amount;

    private LocalDate paymentDate;

    private Time paymentTime ;



    public TransactionDtout(AdminEntity admin, StudentEntity student, BigDecimal amount, LocalDate paymentDate , Time paymentTime) {
        this.admin = new AdminDtout(admin ,  "IMAGE_IS_NOT_NEEDED");
        this.student = new StudentDtout(student , "IMAGE_IS_NOT_NEEDED");
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentTime =  paymentTime;
    }


    public AdminDtout getAdmin() {
        return admin;
    }

    public void setAdmin(AdminDtout admin) {
        this.admin = admin;
    }

    public StudentDtout getStudent() {
        return student;
    }

    public void setStudent(StudentDtout student) {
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
