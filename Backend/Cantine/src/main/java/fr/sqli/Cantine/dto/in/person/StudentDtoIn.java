package fr.sqli.Cantine.dto.in.person;

public class StudentDtoIn  extends  AbstractPersonDtoIn{

    private String studentClass;



    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }
}
