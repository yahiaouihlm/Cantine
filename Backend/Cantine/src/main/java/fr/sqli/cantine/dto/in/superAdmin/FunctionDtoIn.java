package fr.sqli.cantine.dto.in.superAdmin;

import fr.sqli.cantine.service.admin.exceptions.InvalidPersonInformationException;

public class FunctionDtoIn {


       private String name  ;


         public String getName() throws InvalidPersonInformationException {
               if  (name == null || name.isEmpty() ){
                    throw new InvalidPersonInformationException(" NAME ID  REQUIRED");
               }


             return name;
         }


}
