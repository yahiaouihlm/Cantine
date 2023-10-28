package fr.sqli.cantine.service.food.meals;


import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.dto.out.food.MealDtout;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.meals.exceptions.ExistingMealException;
import fr.sqli.cantine.service.food.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.food.meals.exceptions.RemoveMealAdminException;
import fr.sqli.cantine.service.food.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IMealService {

     static final Logger LOG = LogManager.getLogger();



    static void checkUuidValidity(String  uuid) throws InvalidFoodInformationException {

        if (uuid == null || uuid.isEmpty() || uuid.isBlank() || uuid.length() < 20) {
            IMealService.LOG.debug("THE MEAL UUID CAN NOT BE NULL OR EMPTY OR LESS THAN 20 CHARACTERS ");
            throw new InvalidFoodInformationException("INVALID MEAL UUID");
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

     * @throws MealNotFoundException           if the meal is not found in the database
     * @throws InvalidFormatImageException            if the image type is not valid (if the image type is not jpg or png)
     * @throws InvalidImageException                if the image is not valid (if the image is null or empty)
     * @throws ImagePathException                   if the path or imageName is not valid ( null or empty) or  image is not found in 'images/meals' directory
     * @throws IOException                          if the image is not found or  the jvm cannot create the file
     */

    MealEntity updateMeal(MealDtoIn mealDtoIn) throws  MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException, InvalidFoodInformationException;


    /**
     * this method is used to remove a meal from the database and delete the image from the (images/meals) directory if the meal is not present in any menu
     *
     * @param uuid as Integer the id of the meal to remove
     * @return MealEntity the meal removed from the database
     * @throws MealNotFoundException           if the meal is not found in the database
     * @throws RemoveMealAdminException             if the meal is present in a menu(s)
     * @throws ImagePathException                   if the path or imageName is not valid ( null or empty) or  image is not found in 'images/meals' directory
     */



    MealEntity removeMeal(String uuid) throws MealNotFoundException, RemoveMealAdminException, ImagePathException, InvalidFoodInformationException;

    /**
     * this method is used to add a meal to  database and save the image in the (images/meals) directory
     *
     * @param mealDtoIn as  MealDtoIn the meal to add to the database
     * @return MealEntity the meal added to the database
     * @throws InvalidFormatImageException            if the image type is not valid (if the image type is not jpg or png)
     * @throws InvalidImageException                if the image is not valid (if the image is null or empty)
     * @throws ImagePathException                   if the path is not valid (if the path is null or empty)
     * @throws IOException                          if the image is not found
     */

    MealEntity addMeal(MealDtoIn mealDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException, InvalidFoodInformationException;

    /**
     * Get all the meals from the database and return them as a list of MealDTO
     *
     * @return The list of all the meals found in the database or an empty list if no meal is found
     */
    List<MealDtout> getAllMeals();

    /**
     * Get a meal by its id from the database and return it as a MealDTO throw an exception if the meal is not found
     *
     * @param uuid the meal  uuid to search for
     * @return The meal found with the given id or throw an exception if the meal is not found
     * @throws MealNotFoundException           if the meal is not found
     */
    MealDtout getMealByUUID(String uuid) throws MealNotFoundException, InvalidFoodInformationException;


    MealEntity getMealEntityByUUID(String uuid) throws MealNotFoundException, InvalidFoodInformationException;


}
