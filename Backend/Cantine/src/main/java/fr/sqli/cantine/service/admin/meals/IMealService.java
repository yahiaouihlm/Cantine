package fr.sqli.cantine.service.admin.meals;


import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.dto.out.food.MealDtout;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.service.admin.meals.exceptions.ExistingMealException;
import fr.sqli.cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.admin.meals.exceptions.RemoveMealAdminException;
import fr.sqli.cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IMealService {


    /**
     * Check if the meal information is valid or not and throw an exception if it is not valid ( if one of the arguments is null or empty or less than 0)
     *
     * @param args the meal information to check
     * @throws InvalidMealInformationException if  one of the arguments is null or empty or less than 0
     */
    static void verifyMealInformation(String messageException, Object... args) throws InvalidMealInformationException {
        for (Object arg : args) {
            if (arg == null || (arg instanceof String && ((String) arg).isEmpty()))
                throw new InvalidMealInformationException(messageException);
            if (arg == null || (arg instanceof Integer && (Integer) arg < 0))
                throw new InvalidMealInformationException(messageException);

        }
    }






    /**
     * this methode is  used  to  check if the meal is already present in the database or not with the same label, description and category
     * if the meal is present in the database it will throw an ExistingMeal exception
     *
     * @param label       the label of the meal
     * @param description the description of the meal
     * @param category    the category of the meal
     * @throws ExistingMealException if the meal is already present in the database with the same label, description and category
     */
    Optional<MealEntity> checkExistMeal (String label, String description, String category) throws ExistingMealException;


    /**
     * this method is used to update a meal in the database and save the image in the (images/meals)
     * directory if the meal is not present in any menu all the feilds must be filled in the mealDtoIn
     * if  there is no  new image to update  the old image will be kept
     *
     * @param mealDtoIn the  meal with the updated information
     * @return MealEntity the meal updated in the database
     * @throws InvalidMealInformationException if the meal information is not valid (if one of the arguments is null or empty or less than 0)
     * @throws MealNotFoundException           if the meal is not found in the database
     * @throws InvalidFormatImageException            if the image type is not valid (if the image type is not jpg or png)
     * @throws InvalidImageException                if the image is not valid (if the image is null or empty)
     * @throws ImagePathException                   if the path or imageName is not valid ( null or empty) or  image is not found in 'images/meals' directory
     * @throws IOException                          if the image is not found or  the jvm cannot create the file
     */

    MealEntity updateMeal(MealDtoIn mealDtoIn) throws InvalidMealInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException;


    /**
     * this method is used to remove a meal from the database and delete the image from the (images/meals) directory if the meal is not present in any menu
     *
     * @param id as Integer the id of the meal to remove
     * @return MealEntity the meal removed from the database
     * @throws InvalidMealInformationException if the meal  id is not valid (if the id is null or less than 0)
     * @throws MealNotFoundException           if the meal is not found in the database
     * @throws RemoveMealAdminException             if the meal is present in a menu(s)
     * @throws ImagePathException                   if the path or imageName is not valid ( null or empty) or  image is not found in 'images/meals' directory
     */

    MealEntity removeMeal(Integer id) throws InvalidMealInformationException, MealNotFoundException, RemoveMealAdminException, ImagePathException;

    /**
     * this method is used to add a meal to  database and save the image in the (images/meals) directory
     *
     * @param mealDtoIn as  MealDtoIn the meal to add to the database
     * @return MealEntity the meal added to the database
     * @throws InvalidMealInformationException if the meal information is not valid (if one of the arguments is null or empty or less than 0)
     * @throws InvalidFormatImageException            if the image type is not valid (if the image type is not jpg or png)
     * @throws InvalidImageException                if the image is not valid (if the image is null or empty)
     * @throws ImagePathException                   if the path is not valid (if the path is null or empty)
     * @throws IOException                          if the image is not found
     */

    MealEntity addMeal(MealDtoIn mealDtoIn) throws InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException;

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
     * @throws InvalidMealInformationException if the meal id is null or less than 0
     * @throws MealNotFoundException           if the meal is not found
     */
    MealDtout getMealByID(Integer id) throws InvalidMealInformationException, MealNotFoundException;


    /**
     *  Get a meal by its id from the database and return it as a MealEntity throw an exception if the meal is not found
     * @param id the meal id to search for
     * @return The meal found with the given id or throw an exception if the meal is not found
     * @throws InvalidMealInformationException if the meal id is null or less than 0
     * @throws MealNotFoundException          if the meal is not found
     */
    MealEntity  getMealEntityByID (Integer id) throws InvalidMealInformationException, MealNotFoundException;


}
