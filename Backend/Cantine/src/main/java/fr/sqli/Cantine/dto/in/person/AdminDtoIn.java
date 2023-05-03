package fr.sqli.Cantine.dto.in.person;

import org.springframework.web.multipart.MultipartFile;

public class AdminDtoIn  extends  AbstractPersonDtoIn{


    private String function;






    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
