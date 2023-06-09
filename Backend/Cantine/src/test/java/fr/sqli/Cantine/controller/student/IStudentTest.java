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
            Map.entry("LongFirstName", "FIRSTNAME MUST BE LESS THAN 90 CHARACTERS")

            );
}
