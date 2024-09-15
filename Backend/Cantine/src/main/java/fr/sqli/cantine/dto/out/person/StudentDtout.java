package fr.sqli.cantine.dto.out.person;

import fr.sqli.cantine.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class StudentDtout extends AbstractPersonDtout {

    private BigDecimal wallet;
    private String studentClass;


    public StudentDtout(UserEntity student, String studentUrlImage) {
        super.setId(student.getId());
        super.setFirstname(student.getFirstname());
        super.setLastname(student.getLastname());
        super.setEmail(student.getEmail());
        super.setBirthdate(student.getBirthdate());
        super.setTown(student.getTown());
        super.setPhone(student.getPhone());
        var path = student.getImage().getName();
        super.setImage(
                studentUrlImage + path
        );
        this.wallet = student.getWallet();
        this.setStudentClass(student.getStudentClass().getName());
    }

}
