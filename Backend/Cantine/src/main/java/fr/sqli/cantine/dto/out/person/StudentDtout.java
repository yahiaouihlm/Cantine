package fr.sqli.cantine.dto.out.person;

import fr.sqli.cantine.entity.StudentEntity;

import java.math.BigDecimal;

public class StudentDtout    extends AbstractPersonDtout{

    private BigDecimal wallet ;
    private String studentClass;


    public StudentDtout( StudentEntity  student ,  String studentUrlImage) {
        super.setId(student.getId());
        super.setFirstname(student.getFirstname());
        super.setLastname(student.getLastname());
        super.setEmail(student.getEmail());
        super.setBirthdate(student.getBirthdate());
        super.setTown(student.getTown());
        super.setPhone(student.getPhone());
        var  path  = student.getImage().getImagename();
        super.setImage(
                studentUrlImage + path
        );
        this.wallet = student.getWallet();
        this.setStudentClass(student.getStudentClass().getName());
    }


     public  void setStudentClass(String studentClass) {
         this.studentClass = studentClass ;
     };

        public  String getStudentClass() {
            return this.studentClass;
        };


    public BigDecimal getWallet() {
        return wallet;
    }

    public void setWallet(BigDecimal wallet) {
        this.wallet = wallet;
    }
}
