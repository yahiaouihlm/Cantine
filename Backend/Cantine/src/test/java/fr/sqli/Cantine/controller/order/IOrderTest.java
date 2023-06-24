package fr.sqli.Cantine.controller.order;

import fr.sqli.Cantine.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
            Map.entry("TaxNotFound",  "TAX NOT FOUND"),
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

    static  MealEntity  createMeal  ()   {
        MealEntity  mealEntity = new MealEntity();
             mealEntity.setLabel("meal");
            mealEntity.setDescription("description");
             mealEntity.setPrice(BigDecimal.valueOf(5.0));
            mealEntity.setQuantity(10);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("image");
             mealEntity.setImage(imageEntity);
            mealEntity.setStatus(1);
             mealEntity.setCategory("category");
             mealEntity.setQuantity(10);
            return mealEntity;
    }

    static   MenuEntity createMenu () {
        MenuEntity menuEntity = new MenuEntity();
            menuEntity.setLabel("menu");
           menuEntity.setDescription("description");
          menuEntity.setPrice(BigDecimal.valueOf(5.0));
          menuEntity.setQuantity(10);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("image");
        menuEntity.setCreatedDate(LocalDate.now());
        menuEntity.setImage(imageEntity);
        menuEntity.setStatus(1);
        menuEntity.setMeals(List.of(.mealEntity));
        menuEntity.setQuantity(10);

        return menuEntity;
    }

    static StudentEntity  createStudent( String  email , StudentClassEntity studentClassEntityEntity ) {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setStudentClass(studentClassEntityEntity);
        studentEntity.setFirstname("firstname");
        studentEntity.setLastname("lastname");
        studentEntity.setEmail(email);
        studentEntity.setWallet(BigDecimal.valueOf(500));
        studentEntity.setStatus(1);
        studentEntity.setTown("town");
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("image");
        studentEntity.setImage(imageEntity);
        studentEntity.setBirthdate(LocalDate.now());
        studentEntity.setRegistrationDate(LocalDate.now());
        studentEntity.setPassword("password");
        return  studentEntity;

    }

}
