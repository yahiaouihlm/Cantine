package fr.sqli.Cantine.service.admin;


import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.dto.out.MealDtout;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.admin.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.exceptions.RemoveMealAdminException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;

import java.io.IOException;
import java.util.List;

public interface IMealService {


    /**
     * Check if the meal information is valid or not and throw an exception if it is not valid ( if one of the arguments is null or empty or less than 0)
     *
     * @param args the meal information to check
     * @throws InvalidMealInformationAdminException if the meal information is not valid
     */
    static void verifyMealInformation(String meassageException, Object... args) throws InvalidMealInformationAdminException {
        for (Object arg : args) {
            if (arg == null || (arg instanceof String && ((String) arg).isEmpty()))
                throw new InvalidMealInformationAdminException(meassageException);
            if (arg == null || (arg instanceof Integer && (Integer) arg < 0))
                throw new InvalidMealInformationAdminException(meassageException);

        }
    }

    /**
     * this method is used to remove a meal from the database and delete the image from the (images/meals) directory if the meal is not present in any menu
     *
     * @param id as Integer the id of the meal to remove
     * @return MealEntity the meal removed from the database
     * @throws InvalidMealInformationAdminException if the meal  id is not valid (if the id is null or less than 0)
     * @throws MealNotFoundAdminException           if the meal is not found in the database
     * @throws RemoveMealAdminException             if the meal is present in a menu(s)
     * @throws ImagePathException                   if the path or imageName is not valid ( null or empty) or  image is not found in 'images/meals' directory
     */

    MealEntity removeMeal(Integer id) throws InvalidMealInformationAdminException, MealNotFoundAdminException, RemoveMealAdminException, ImagePathException;

    /**
     * this method is used to add a meal to  database and save the image in the (images/meals) directory
     *
     * @param mealDtoIn as  MealDtoIn the meal to add to the database
     * @return MealEntity the meal added to the database
     * @throws InvalidMealInformationAdminException if the meal information is not valid (if one of the arguments is null or empty or less than 0)
     * @throws InvalidTypeImageException            if the image type is not valid (if the image type is not jpg or png)
     * @throws InvalidImageException                if the image is not valid (if the image is null or empty)
     * @throws ImagePathException                   if the path is not valid (if the path is null or empty)
     * @throws IOException                          if the image is not found
     */

    MealEntity addMeal(MealDtoIn mealDtoIn) throws InvalidMealInformationAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException;

    /**
     * Get all the meals from the database and return them as a list of MealDTO
     *
     * @return The list of all the meals found in the database or an empty list if no meal is found
     */
    List<MealDtout> getAllMeals();

    /**
     * Get a meal by its id from the database and return it as a MealDTO throw an exception if the meal is not found
     *
     * @param id the meal id to search for
     * @return The meal found with the given id or throw an exception if the meal is not found
     * @throws InvalidMealInformationAdminException if the meal id is null or less than 0
     * @throws MealNotFoundAdminException           if the meal is not found
     */
    MealDtout getMealByID(Integer id) throws InvalidMealInformationAdminException, MealNotFoundAdminException;


}
