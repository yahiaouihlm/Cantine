package fr.sqli.cantine.dto.out.person;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentClassDtout  {

    private  String  id ;
    private  String  name  ;


    public StudentClassDtout(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
