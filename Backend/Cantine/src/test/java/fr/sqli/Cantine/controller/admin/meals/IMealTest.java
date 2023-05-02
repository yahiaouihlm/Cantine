package fr.sqli.Cantine.controller.admin.meals;

import java.util.Map;

public interface IMealTest {

    //  Meal Tests messages  Errors and Success
    final String BASE_MEAL_URL = "/cantine/api/admin/meals";
    final String GET_ALL_MEALS_URL = BASE_MEAL_URL + "/getAll";

    final  String GET_ONE_MEAL_URL = BASE_MEAL_URL + "/get";
    final String ADD_MEAL_URL = BASE_MEAL_URL + "/add";

    final String DELETE_MEAL_URL = BASE_MEAL_URL + "/delete";

    final String UPDATE_MEAL_URL = BASE_MEAL_URL + "/update";

    String  IMAGE_MEAL_FOR_TEST_NAME = "ImageMealForTest.jpg";
    String SECOND_IMAGE_MEAL_FOR_TEST_NAME="ImageMealForTest1.jpg";
    String IMAGE_MEAL_FORMAT_FOR_TEST="image/jpg";
    String IMAGE_MEAL_DIRECTORY_PATH = "images/meals/";
    String   IMAGE_MEAL_FOR_TEST_PATH = IMAGE_MEAL_DIRECTORY_PATH+IMAGE_MEAL_FOR_TEST_NAME;
    final Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("Label", "LABEL_IS_MANDATORY"),
            Map.entry("Category", "CATEGORY_IS_MANDATORY"),
            Map.entry("Description", "DESCRIPTION_IS_MANDATORY"),
            Map.entry("Price", "PRICE_IS_MANDATORY"),
            Map.entry("Quantity", "QUANTITY_IS_MANDATORY"),
            Map.entry("Status", "STATUS_IS_MANDATORY"),
            Map.entry("Image", "IMAGE_IS_MANDATORY"),
            Map.entry("ShortLabelLength", "LABEL_IS_TOO_SHORT"),
            Map.entry("LongLabelLength", "LABEL_IS_TOO_LONG"),
            Map.entry("ShortDescriptionLength", "DESCRIPTION_IS_TOO_SHORT"),
            Map.entry("LongDescriptionLength", "DESCRIPTION_IS_TOO_LONG"),
            Map.entry("ShortCategoryLength", "CATEGORY_IS_TOO_SHORT"),
            Map.entry("LongCategoryLength", "CATEGORY_IS_TOO_LONG"),
            Map.entry("InvalidArgument", "ARGUMENT NOT VALID"),
            Map.entry("OutSideStatusValue", "STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE"),
            Map.entry("HighPrice", "PRICE MUST BE LESS THAN 1000"),
            Map.entry("HighQuantity", "QUANTITY_IS_TOO_HIGH"),
            Map.entry("NegativePrice", "PRICE MUST BE GREATER THAN 0"),
            Map.entry("NegativeQuantity", "QUANTITY MUST BE GREATER THAN 0"),
            Map.entry("InvalidImageFormat", "INVALID IMAGE TYPE ONLY PNG , JPG , JPEG   ARE ACCEPTED"),
            Map.entry("MealAddedSuccessfully", "MEAL ADDED SUCCESSFULLY"),
            Map.entry("InvalidParameter", "THE ID CAN NOT BE NULL OR LESS THAN 0"),
            Map.entry("missingParam", "MISSING PARAMETER"),
            Map.entry("mealNotFound", "NO MEAL WAS FOUND WITH THIS ID"),
            Map.entry("InvalidID", "THE ID  CAN NOT BE NULL OR LESS THAN 0")

    );


}
