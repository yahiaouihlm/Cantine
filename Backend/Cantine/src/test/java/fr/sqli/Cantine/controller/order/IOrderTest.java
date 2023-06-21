package fr.sqli.Cantine.controller.order;

import java.util.Map;

public interface IOrderTest {



    String ORDER_BASIC_URL = "/cantine/student/order";



    String  ADD_ORDER_URL =   ORDER_BASIC_URL +   "/add";



    String  ORDER_ADDED_SUCCESSFULLY = "ORDER ADDED SUCCESSFULLY";


    Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("StudentIsRequired",  "STUDENT ID IS  REQUIRED"),
            Map.entry("InvalidJsonFormat",  "INVALID JSON FORMAT"),
            Map.entry("studentNotFound",  "STUDENT NOT FOUND")
    );



}
