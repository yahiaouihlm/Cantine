package fr.sqli.cantine.dto.out.person;


import fr.sqli.cantine.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;

@Setter
@Getter
public class TransactionDtout {

    private AdminDtout admin;

    private StudentDtout student;

    private BigDecimal amount;

    private LocalDate paymentDate;

    private Time paymentTime;


    public TransactionDtout(UserEntity admin, UserEntity student, BigDecimal amount, LocalDate paymentDate, Time paymentTime) {
        this.admin = new AdminDtout(admin, "IMAGE_IS_NOT_NEEDED");
        this.student = new StudentDtout(student, "IMAGE_IS_NOT_NEEDED");
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentTime = paymentTime;
    }


}
