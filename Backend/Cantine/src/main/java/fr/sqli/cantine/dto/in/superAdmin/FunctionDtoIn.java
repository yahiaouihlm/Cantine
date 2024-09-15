package fr.sqli.cantine.dto.in.superAdmin;

import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;

public class FunctionDtoIn {


    private String name;


    public String getName() throws InvalidUserInformationException {
        if (name == null || name.isEmpty()) {
            throw new InvalidUserInformationException(" NAME ID  REQUIRED");
        }


        return name;
    }


}
