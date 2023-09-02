package fr.sqli.Cantine.dto.out.person;

import fr.sqli.Cantine.entity.StudentEntity;

public class StudentDtout    extends AbstractPersonDtout{


    private String studentClass;

    public StudentDtout( StudentEntity  student){
        super.setId(student.getId());
        super.setFirstname(student.getFirstname());
        super.setLastname(student.getLastname());
        super.setEmail(student.getEmail());
        super.setBirthdate(student.getBirthdate());
        super.setTown(student.getTown());
        super.setPhone(student.getPhone());
        super.setImage(student.getImage().getImagename());
        this.setStudentClass(student.getStudentClass().getName());
    }


     public  void setStudentClass(String studentClass) {
         this.studentClass = studentClass ;
     };

        public  String getStudentClass() {
            return this.studentClass;
        };
}
