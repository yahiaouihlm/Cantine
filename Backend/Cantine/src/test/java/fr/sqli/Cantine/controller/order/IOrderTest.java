package fr.sqli.Cantine.controller.order;

import java.util.Map;

public interface IOrderTest {



    String ORDER_BASIC_URL = "/cantine/student/order";



    String  ADD_ORDER_URL =   ORDER_BASIC_URL +   "/add";



    String  ORDER_ADDED_SUCCESSFULLY = "ORDER ADDED SUCCESSFULLY";


    Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("StudentIsRequired",  "STUDENT ID IS  REQUIRED"),
            Map.entry("InvalidJsonFormat",  "INVALID JSON FORMAT"),
            Map.entry("studentNotFound",  "STUDENT NOT FOUND"),
            Map.entry("MealsOrMenusAreRequired",  "MEALS OR MENUS ARE REQUIRED"),
            Map.entry("InvalidMealId",  "INVALID MEAL ID"),
            Map.entry("InvalidMenuId",  "INVALID MENU ID"),
            Map.entry("OrderLimit",  "ORDER LIMIT EXCEEDED"),
            Map.entry("MealNotFound",  "MEAL NOT FOUND"),
            Map.entry("MenuNotFound",  "MENU NOT FOUND") ,
            Map.entry("InsufficientBalance",  "YOU  DON'T HAVE ENOUGH MONEY TO PAY FOR THE ORDER")
    );

    Map<String, String> discontinuedexceptionsMap = Map.ofEntries(
            Map.entry("MenuFor",  "MENU WITH  ID: " ),
            Map.entry("NealFor",  "MEAL WITH  ID : "),
            Map.entry("NotFound",  " NOT FOUND") ,
            Map.entry("MealForUnavailable",  "MEAL  : ") ,
            Map.entry("MenuForUnavailable",  "MENU  : "),
            Map.entry("Unavailable",  " IS UNAVAILABLE")

    );



}
