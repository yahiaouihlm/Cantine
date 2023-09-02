package fr.sqli.cantine.dto.out.superAdmin;

import fr.sqli.cantine.entity.FunctionEntity;

public class FunctionDtout {

    private String name;


    public FunctionDtout(String name) {
        this.name = name;

    }
    public FunctionDtout(FunctionEntity functionEntity) {
        this.name = functionEntity.getName();


    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }





}
