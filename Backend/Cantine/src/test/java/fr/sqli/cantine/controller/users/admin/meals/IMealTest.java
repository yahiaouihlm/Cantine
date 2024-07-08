package fr.sqli.cantine.controller.users.admin.meals;

import java.util.Map;

public interface IMealTest {

    String BASIC_CANTINE_ROOT_URL = "http://localhost:8080/";

    String BASIC_MEAL_URL = BASIC_CANTINE_ROOT_URL + "cantine/admin/api/meals";


    final  String GET_ONE_MEAL_URL = BASIC_MEAL_URL + "/get";
    String GET_ALL_MEALS_URL = BASIC_MEAL_URL + "/getAll";

    String GET_UNAVAILABLE_MEALS_URL = BASIC_MEAL_URL + "/getUnavailableMeals";


    final String ADD_MEAL_URL = BASIC_MEAL_URL + "/add";

    final String DELETE_MEAL_URL = BASIC_MEAL_URL + "/delete";

    final String UPDATE_MEAL_URL = BASIC_MEAL_URL + "/update";

    String IMAGE_MEAL_TEST_DIRECTORY_PATH = "imagesTests/";


    String  IMAGE_MEAL_FOR_TEST_NAME = "ImageForTest.jpg";
    String SECOND_IMAGE_MEAL_FOR_TEST_NAME="ImageMealForTest1.jpg";
    String IMAGE_MEAL_FORMAT_FOR_TEST="image/jpg";

    String IMAGE_MEAL_DIRECTORY_PATH = "images/meals/";
    String   IMAGE_MEAL_FOR_TEST_PATH = IMAGE_MEAL_TEST_DIRECTORY_PATH +IMAGE_MEAL_FOR_TEST_NAME;


    Map <String, String> responseMap = Map.ofEntries(
            Map.entry("MealAddedSuccessfully", "MEAL ADDED SUCCESSFULLY"),
            Map.entry("MealDeletedSuccessfully", "MEAL DELETED SUCCESSFULLY"),
            Map.entry("MealUpdatedSuccessfully", "MEAL UPDATED SUCCESSFULLY")
    );






    final Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("Label", "LABEL_IS_MANDATORY"),
            Map.entry("Category", "CATEGORY_IS_MANDATORY"),
            Map.entry("Description", "DESCRIPTION_IS_MANDATORY"),
            Map.entry("Price", "PRICE_IS_MANDATORY"),
            Map.entry("Quantity", "QUANTITY_IS_MANDATORY"),
            Map.entry("Status", "STATUS_IS_MANDATORY"),
            Map.entry("Image", "IMAGE_IS_MANDATORY"),
            Map.entry("mealType", "MEAL_TYPE_IS_MANDATORY"),
            Map.entry("InvalidMealType", "MEAL_TYPE_IS_INVALID"),
            Map.entry("ShortLabelLength", "LABEL_IS_TOO_SHORT"),
            Map.entry("LongLabelLength", "LABEL_IS_TOO_LONG"),
            Map.entry("ShortDescriptionLength", "DESCRIPTION_IS_TOO_SHORT"),
            Map.entry("LongDescriptionLength", "DESCRIPTION_IS_TOO_LONG"),
            Map.entry("ShortCategoryLength", "CATEGORY_IS_TOO_SHORT"),
            Map.entry("LongCategoryLength", "CATEGORY_IS_TOO_LONG"),
            Map.entry("InvalidValue", "INVALID VALUE"),
            Map.entry("OutSideStatusValue", "STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE"),
            Map.entry("HighPrice", "PRICE MUST BE LESS THAN 1000"),
            Map.entry("HighQuantity", "QUANTITY_IS_TOO_HIGH"),
            Map.entry("NegativePrice", "PRICE MUST BE GREATER THAN 0"),
            Map.entry("NegativeQuantity", "QUANTITY MUST BE GREATER THAN 0"),
            Map.entry("InvalidImageFormat", "INVALID IMAGE TYPE ONLY PNG , JPG , JPEG   ARE ACCEPTED"),
            Map.entry("MealAddedSuccessfully", "MEAL ADDED SUCCESSFULLY"),
            Map.entry("InvalidParameter", "THE ID CAN NOT BE NULL OR LESS THAN 0"),
            Map.entry("InvalidMealUuid", "INVALID MEAL UUID"),
            Map.entry("mealNotFound", "NO MEAL WAS FOUND"),
            Map.entry("mealCanNotBeDeletedOrder", "THE MENU CAN NOT BE DELETED BECAUSE IT IS PRESENT IN AN ORDER(S)"
                    +"PS -> THE  MEAL WILL  BE  AUTOMATICALLY  REMOVED IN  BATCH  TRAITEMENT"),
            Map.entry("mealCanNotBeDeletedMenu", "THE MEAL CAN NOT BE DELETED BECAUSE IT IS PRESENT IN AN OTHER MENU(S) " +
                    "PS -> THE  MEAL WILL  BE  AUTOMATICALLY  REMOVED IN  BATCH  TRAITEMENT")

    );




}
