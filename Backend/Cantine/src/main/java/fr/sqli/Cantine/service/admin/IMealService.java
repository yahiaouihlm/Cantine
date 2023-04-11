package fr.sqli.Cantine.service.admin;


import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.dto.out.MealDtout;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.admin.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;

import java.io.IOException;
import java.util.List;

public interface IMealService {


    public MealEntity addMeal(MealDtoIn mealEntity) throws InvalidMealInformationAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException;

    /**
     * Get all the meals from the database and return them as a list of MealDTO
     * @return  The list of all the meals found in the database or an empty list if no meal is found
     */
    public List<MealDtout> getAllMeals();


    /**
     * Get a meal by its id from the database and return it as a MealDTO throw an exception if the meal is not found
     * @param id the meal id to search for
     * @return  The meal found with the given id or throw an exception if the meal is not found
     * @throws InvalidMealInformationAdminException if the meal id is null or less than 0
     * @throws MealNotFoundAdminException if the meal is not found
     */
    public MealDtout getMealByID (Integer id) throws InvalidMealInformationAdminException, MealNotFoundAdminException;


    /**
     * Check if the meal information is valid or not and throw an exception if it is not valid ( if one of the arguments is null or empty or less than 0)
     * @param args the meal information to check
     * @throws InvalidMealInformationAdminException if the meal information is not valid
     */
    default  void verifyMealInformation (  String  meassageException ,  Object  ... args ) throws InvalidMealInformationAdminException {
        for ( Object arg : args) {
            if ( arg==null  ||  (arg instanceof String && ((String) arg).isEmpty()) )
                throw new InvalidMealInformationAdminException(meassageException);
            if (arg==null   || (arg instanceof Integer && (Integer) arg < 0))
                throw new InvalidMealInformationAdminException(meassageException);

        }
    }


}
