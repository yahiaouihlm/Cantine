package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.entity.FunctionEntity;
import fr.sqli.Cantine.entity.ImageEntity;

import java.time.LocalDate;
import java.util.Map;

public interface IAdminTest {
    String ADMIN_DASH_BOARD_BASIC_URL = "/cantine/admin/adminDashboard";

    String ADMIN_IMAGE_PATH = "images/persons/admin/";
    String ADMIN_SIGN_UP = ADMIN_DASH_BOARD_BASIC_URL +  "/signUp";
    String  ADMIN_UPDATE_INFO = ADMIN_DASH_BOARD_BASIC_URL +  "/updateAdmin/info";
    String  IMAGE_NAME = "imageForTest.jpg";
    String IMAGE_FOR_TEST_PATH= "imagesTests/"+IMAGE_NAME;
    String ADMIN_ADDED_SUCCESSFULLY = "ADMIN ADDED SUCCESSFULLY";




    //   exceptions messages
     Map<String, String> exceptionsMap = Map.ofEntries(
      Map.entry("FirstNameRequire", "FIRSTNAME IS  REQUIRED"),
     Map.entry("ShortFirstName", "FIRSTNAME MUST BE AT LEAST 3 CHARACTERS"),
     Map.entry("LongFirstName", "FIRSTNAME MUST BE LESS THAN 90 CHARACTERS"),
        Map.entry("LastNameRequire", "LASTNAME IS  REQUIRED"),
        Map.entry("ShortLastName", "LASTNAME MUST BE AT LEAST 3 CHARACTERS"),
        Map.entry("LongLastName", "LASTNAME MUST BE LESS THAN 90 CHARACTERS"),
        Map.entry("BirthdateRequire", "BIRTHDATE IS  REQUIRED"),
        Map.entry("InvalidBirthdateFormat", "INVALID BIRTHDATE FORMAT"),
        Map.entry("TownRequire", "TOWN IS  REQUIRED"),
        Map.entry("ShortTown", "TOWN  MUST BE AT LEAST 2 CHARACTERS"),
        Map.entry("LongTown", "TOWN MUST BE LESS THAN 1000 CHARACTERS"),
        Map.entry("AddressRequire", "ADDRESS IS  REQUIRED"),
        Map.entry("ShortAddress", "ADDRESS  MUST BE AT LEAST 10 CHARACTERS"),
        Map.entry("LongAddress", "ADDRESS MUST BE LESS THAN 3000 CHARACTERS"),
            Map.entry("PhoneRequire", "PHONE IS  REQUIRED"),
            Map.entry("InvalidPhoneFormat", "INVALID PHONE FORMAT"),
            Map.entry("EmailRequire", "EMAIL IS  REQUIRED"),
            Map.entry("ShortEmail", "EMAIL MUST BE AT LEAST 5 CHARACTERS"),
            Map.entry("LongEmail", "EMAIL MUST BE LESS THAN 1000 CHARACTERS"),
            Map.entry("InvalidEmailFormat", "YOUR EMAIL IS NOT VALID"),
            Map.entry("PasswordRequire", "PASSWORD IS  REQUIRED"),
            Map.entry("ShortPassword", "PASSWORD MUST BE AT LEAST 6 CHARACTERS"),
            Map.entry("LongPassword", "PASSWORD MUST BE LESS THAN 20 CHARACTERS"),
            Map.entry("FunctionRequire", "FUNCTION IS  REQUIRED"),
            Map.entry("FunctionNotFound", "YOUR FUNCTIONALITY IS NOT VALID"),
            Map.entry("InvalidImageFormat", "INVALID IMAGE TYPE ONLY PNG , JPG , JPEG   ARE ACCEPTED"),
            Map.entry("ExistingAdmin", "THIS ADMIN IS ALREADY EXISTS"),
            Map.entry("MissingParam", "MISSING PARAMETER")
    );

     static  AdminEntity createAdminWith(String email, FunctionEntity functionEntity){

         AdminEntity adminEntity = new AdminEntity();
            adminEntity.setEmail(email);
            adminEntity.setFunction(functionEntity);
            adminEntity.setFirstname("firstName");
            adminEntity.setLastname("lastName");
            adminEntity.setAddress("address");
            adminEntity.setTown("town");
            adminEntity.setPhone("0631990180");
            adminEntity.setBirthdate(LocalDate.now());
            adminEntity.setPassword("password");
            adminEntity.setRegistrationDate(LocalDate.now());
            ImageEntity imageEntity = new ImageEntity();
               imageEntity.setImagename("ImageForTest");
               adminEntity.setImage(imageEntity);

            adminEntity.setStatus(0);

            return adminEntity;

     }
}
