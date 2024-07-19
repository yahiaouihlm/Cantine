package fr.sqli.cantine.controller.order;

import fr.sqli.cantine.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IOrderTest {


    String BASIC_ORDER_URL = "http://localhost:8080/";
    String ORDER_BASIC_URL = BASIC_ORDER_URL + "order/";
    String CANCEL_ORDER_BY_ADMIN_URL = ORDER_BASIC_URL + "admin/cancelOrder";
    String CANCEL_ORDER_BY_STUDENT_URL = ORDER_BASIC_URL + "student/cancel";
    String ADD_ORDER_URL = ORDER_BASIC_URL + "student/add";


    String ORDER_CANCELLED_SUCCESSFULLY = "ORDER CANCELLED SUCCESSFULLY";


    Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("StudentIsRequired", "STUDENT ID IS  REQUIRED"),
            Map.entry("InvalidJsonFormat", "INVALID JSON FORMAT"),
            Map.entry("studentNotFound", "STUDENT NOT FOUND"),
            Map.entry("MealsOrMenusAreRequired", "MEALS OR MENUS ARE REQUIRED"),
            Map.entry("InvalidMealId", "INVALID MEAL ID"),
            Map.entry("InvalidMenuId", "INVALID MENU ID"),
            Map.entry("OrderLimit", "ORDER LIMIT EXCEEDED"),
            Map.entry("MealNotFound", "MEAL NOT FOUND"),
            Map.entry("MenuNotFound", "MENU NOT FOUND"),
            Map.entry("TaxNotFound", "TAX NOT FOUND"),
            Map.entry("InsufficientBalance", "YOU  DON'T HAVE ENOUGH MONEY TO PAY FOR THE ORDER"),
            Map.entry("ArgumentNotValid", "ARGUMENT NOT VALID"),
            Map.entry("MissingArgument", "MISSING PARAMETER"),
            Map.entry("InvalidOrderId", "INVALID ORDER ID"),
            Map.entry("OrderNotFound", "ORDER NOT FOUND"),
            Map.entry("OrderAlreadyValidated", "ORDER IS ALREADY VALIDATED"),
            Map.entry("OrderAlreadyCanceled", "ORDER IS ALREADY CANCELED"),
            Map.entry("EmptyMealsAndMenus" , "INVALID ORDER  THERE  IS NO  MEALS  OR  MENUS")

    );

    Map <String, String> responseMap = Map.ofEntries(
            Map.entry("OrderAddedSuccessfully", "ORDER ADDED SUCCESSFULLY"),
            Map.entry("OrderCancelledSuccessfully", "ORDER CANCELLED SUCCESSFULLY")
    );



    static MealEntity createMeal() {
        MealEntity mealEntity = new MealEntity();
        mealEntity.setLabel("meal");
        mealEntity.setDescription("description");
        mealEntity.setPrice(BigDecimal.valueOf(5.0));
        mealEntity.setQuantity(10);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName("image");
        mealEntity.setImage(imageEntity);
        mealEntity.setStatus(1);
        mealEntity.setCategory("category");
        mealEntity.setQuantity(10);
        return mealEntity;
    }

    static MenuEntity createMenu(MealEntity mealEntity) {
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setLabel("menu");
        menuEntity.setDescription("description");
        menuEntity.setPrice(BigDecimal.valueOf(5.0));
        menuEntity.setQuantity(10);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName("image");
        menuEntity.setCreatedDate(LocalDate.now());
        menuEntity.setImage(imageEntity);
        menuEntity.setStatus(1);
        menuEntity.setMeals(List.of(mealEntity));
        menuEntity.setQuantity(10);

        return menuEntity;
    }

    static UserEntity createStudent(String email, StudentClassEntity studentClassEntity) {
        UserEntity studentEntity = new UserEntity();
        studentEntity.setStudentClass(studentClassEntity);
        studentEntity.setFirstname("firstname");
        studentEntity.setLastname("lastname");
        studentEntity.setEmail(email);
        studentEntity.setWallet(BigDecimal.valueOf(500));
        studentEntity.setStatus(1);
        studentEntity.setTown("town");
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName("image");
        studentEntity.setImage(imageEntity);
        studentEntity.setBirthdate(LocalDate.now());
        studentEntity.setRegistrationDate(LocalDate.now());
        studentEntity.setPassword("password");
        return studentEntity;

    }

}
