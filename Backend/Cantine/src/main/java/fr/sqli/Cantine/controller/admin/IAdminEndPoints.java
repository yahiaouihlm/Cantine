package fr.sqli.Cantine.controller.admin;

import java.io.File;

public interface IAdminEndPoints {
    String ENDPOINT_ADD_MEAL_URL = "cantine/admin/meals/add";
    String ENDPOINT_DELETE_MEAL_URL = "cantine/admin/meals/delete";

    String ENDPOINT_UPDATE_MEAL_URL = "cantine/admin/meals/update";
    String ENDPOINT_GET_ONE_MEAL_URL = "cantine/admin/meals/getOne";
    String ENDPOINT_GET_ALL_MEALS_URL = "cantine/admin/meals/getAll";





    String MEAL_ADDED_SUCCESSFULLY = "MEAL ADDED SUCCESSFULLY";
    String MEAL_DELETED_SUCCESSFULLY = "MEAL DELETED SUCCESSFULLY";
    String MEAL_UPDATED_SUCCESSFULLY = "MEAL UPDATED SUCCESSFULLY";
}
