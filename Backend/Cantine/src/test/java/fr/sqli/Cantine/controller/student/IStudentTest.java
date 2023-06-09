package fr.sqli.Cantine.controller.student;

import java.util.Map;

public interface IStudentTest {

    String STUDENT_BASIC_URL = "/cantine/student";

    String  STUDENT_SIGN_UP = STUDENT_BASIC_URL +  "/signUp";
    String  IMAGE_NAME = "imageForTest.jpg";
    String IMAGE_FOR_TEST_PATH= "imagesTests/"+IMAGE_NAME;


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
            Map.entry("InvalidPhoneFormat", "INVALID PHONE FORMAT"),
            Map.entry("EmailRequire", "EMAIL IS  REQUIRED"),
            Map.entry("ShortEmail", "EMAIL MUST BE AT LEAST 5 CHARACTERS"),
            Map.entry("LongEmail", "EMAIL MUST BE LESS THAN 1000 CHARACTERS"),
            Map.entry("InvalidEmailFormat", "YOUR EMAIL IS NOT VALID")

            );
}
