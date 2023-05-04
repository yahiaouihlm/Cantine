package fr.sqli.Cantine.dto.in.superAdmin;

import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;

public class FunctionDtoIn {


       private String name  ;


         public String getName() throws InvalidPersonInformationException {
               if  (name == null || name.isEmpty() ){
                    throw new InvalidPersonInformationException(" NAME ID  REQUIRED");
               }


             return name;
         }


}
