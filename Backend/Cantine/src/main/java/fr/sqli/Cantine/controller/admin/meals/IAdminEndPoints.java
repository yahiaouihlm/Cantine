package fr.sqli.Cantine.controller.admin.meals;

import java.io.File;

public interface IAdminEndPoints {

    String MEALS_URL_ADMIN = "/cantine/api/admin/meals";
    String ENDPOINT_ADD_MEAL_URL = "/add";
    String ENDPOINT_DELETE_MEAL_URL = "/delete";

    String ENDPOINT_UPDATE_MEAL_URL = "/update";
    String ENDPOINT_GET_ONE_MEAL_URL = "/get";
    String ENDPOINT_GET_ALL_MEALS_URL = "/getAll";





    String MEAL_ADDED_SUCCESSFULLY = "MEAL ADDED SUCCESSFULLY";
    String MEAL_DELETED_SUCCESSFULLY = "MEAL DELETED SUCCESSFULLY";
    String MEAL_UPDATED_SUCCESSFULLY = "MEAL UPDATED SUCCESSFULLY";
}
