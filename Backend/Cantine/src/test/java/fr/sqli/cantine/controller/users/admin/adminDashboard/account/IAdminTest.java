package fr.sqli.cantine.controller.users.admin.adminDashboard.account;

import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.FunctionEntity;
import fr.sqli.cantine.entity.ImageEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Map;

public interface IAdminTest {
    String BASIC_CANTINE_ROOT_URL = "http://localhost:8080/";
    String ADMIN_EMAIL_EXAMPLE = "mockey.d.luffy@admin.fr";
    String ADMIN_PASSWORD_EXAMPLE = "password";
    String ADMIN_SIGN_IN_URL = BASIC_CANTINE_ROOT_URL + "user/login";

    String ADMIN_FUNCTION = "Manager";

    String ADMIN_SIGN_UP = BASIC_CANTINE_ROOT_URL + "cantine/admin/register";


    String ADMIN_DASH_BOARD_BASIC_URL =BASIC_CANTINE_ROOT_URL +  "cantine/admin";
    String ADMIN_IMAGE_PATH = "images/persons/admin/";

    String ADMIN_SEND_TOKEN_URL = ADMIN_DASH_BOARD_BASIC_URL + "/sendToken";
    String ADMIN_DISABLE_ACCOUNT = ADMIN_DASH_BOARD_BASIC_URL + "/disableAdmin";

    String GET_ADMIN_BY_ID = ADMIN_DASH_BOARD_BASIC_URL + "/getAdmin";
    String ADMIN_UPDATE_INFO = ADMIN_DASH_BOARD_BASIC_URL + "/updateAdmin/info";
    String IMAGE_NAME = "imageForTest.jpg";
    String IMAGE_FOR_TEST_PATH = "imagesTests/" + IMAGE_NAME;
    String ADMIN_INFO_UPDATED_SUCCESSFULLY = "ADMIN UPDATED SUCCESSFULLY";
    String TOKEN_SENDED_SUCCESSFULLY = "TOKEN SENDED SUCCESSFULLY";
    String IMAGE_MEAL_TEST_DIRECTORY_PATH = "imagesTests/";
    String IMAGE_ADMIN_FOR_TEST_NAME = "ImageForTest.jpg";

    Map <String, String> responseMap = Map.ofEntries(
            Map.entry("AdminAddedSuccessfully", "ADMIN ADDED SUCCESSFULLY")
            );





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
            Map.entry("InvalidEmailFormat", "INVALID EMAIL"),
            Map.entry("PasswordRequire", "PASSWORD IS  REQUIRED"),
            Map.entry("ShortPassword", "PASSWORD MUST BE AT LEAST 6 CHARACTERS"),
            Map.entry("LongPassword", "PASSWORD MUST BE LESS THAN 20 CHARACTERS"),
            Map.entry("FunctionRequire", "FUNCTION IS  REQUIRED"),
            Map.entry("FunctionNotFound", "YOUR FUNCTIONALITY IS NOT FOUND"),
            Map.entry("InvalidImageFormat", "INVALID IMAGE TYPE ONLY PNG , JPG , JPEG   ARE ACCEPTED"),
            Map.entry("ExistingAdmin", "EMAIL IS ALREADY EXISTS"),
            Map.entry("InvalidParam", "ARGUMENT NOT VALID"),
            Map.entry("InvalidInfo", "INVALID INFORMATION REQUEST THE  EMAIL AND  PASSWORD  MUST BE  EXCLUDED"),
            Map.entry("InvalidUuId", "INVALID UUID"),
            Map.entry("AdminNotFound", "ADMIN NOT FOUND"),
            Map.entry("MissingPram", "MISSING PARAMETER"),
            Map.entry("InvalidEmail", "YOUR EMAIL IS NOT VALID"),
            Map.entry("EnableAccount", "YOUR ACCOUNT IS ALREADY ENABLED")
    );

    static AdminEntity createAdminWith(String email, FunctionEntity functionEntity, ImageEntity imageEntity) {

        AdminEntity adminEntity = new AdminEntity();
        adminEntity.setEmail(email);
        adminEntity.setFunction(functionEntity);
        adminEntity.setFirstname("firstName");
        adminEntity.setLastname("lastName");
        adminEntity.setAddress("address");
        adminEntity.setTown("town");
        adminEntity.setPhone("0631990180");
        adminEntity.setBirthdate(LocalDate.now());
        adminEntity.setPassword(new BCryptPasswordEncoder().encode(ADMIN_PASSWORD_EXAMPLE));
        adminEntity.setValidation(1);
        adminEntity.setRegistrationDate(LocalDate.now());
        adminEntity.setImage(imageEntity);

        adminEntity.setStatus(1);

        return adminEntity;

    }

    static ImageEntity createImageEntity() {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(IMAGE_NAME);
        return imageEntity;
    }

    static FunctionEntity createFunctionEntity() {
        FunctionEntity functionEntity = new FunctionEntity();
        functionEntity.setName("MANAGER");
        return functionEntity;
    }
}
