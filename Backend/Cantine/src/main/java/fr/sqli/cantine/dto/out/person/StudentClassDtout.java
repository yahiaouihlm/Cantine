package fr.sqli.cantine.dto.out.person;

public class StudentClassDtout  {

    private  Integer  id ;
    private  String  name  ;


    public StudentClassDtout(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
