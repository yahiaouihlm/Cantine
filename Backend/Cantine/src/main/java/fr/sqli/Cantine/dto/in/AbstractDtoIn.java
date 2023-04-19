package fr.sqli.Cantine.dto.in;

import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public abstract class AbstractDtoIn {


    /**
     *  check  if the   validity of the  parameters  passed  to the  method  ( the information shared  by the  meal and the menu)
     *
     * @param type the type of the object  ( meal or menu) to  check  the validity of the  parameters
     * @param label  the label of the  meal or the menu
     * @param description the description of the  meal or the menu
     * @param price the price of the  meal or the menu
     * @param status the status of the  meal or the menu
     * @param quantity the quantity of the  meal or the menu
     * @param category the category of the  meal or the menu
     * @throws InvalidMealInformationException if the meal information is not valid if  type is a meal
     * @throws InvalidMenuInformationException if the menu information is not valid if  type is a menu
     */
    public void checkValidity( Object type ,  String label,  String  description ,  BigDecimal price, Integer status,  Integer quantity ,  String category ) throws InvalidMealInformationException, InvalidMenuInformationException {


        if (label == null || label.trim().isEmpty()) {
            throwRightException(type ,  "LABEL_IS_MANDATORY");
        }
        if (description == null || description.trim().isEmpty()) {
            throwRightException(type ,"DESCRIPTION_IS_MANDATORY");
        }

        if (price == null) {
            throwRightException(type ,"PRICE_IS_MANDATORY");
        }
        if (status == null) {
            throwRightException(type ,"STATUS_IS_MANDATORY");
        }
        if  ( type instanceof MealEntity && category == null || category.trim().isEmpty()) {
            throwRightException(type ,"CATEGORIES_IS_MANDATORY");
        }
        if (type instanceof MealEntity &&  category.length() > 45) {
            throwRightException(type ,"CATEGORIES_IS_TOO_LONG");
        }

        if (type instanceof MealEntity && this.removeSpaces(category).length() < 3) {
            throwRightException(type ,"CATEGORIES_IS_TOO_SHORT");
        }


        if (this.removeSpaces(label).length() < 3) {
            throwRightException(type ,"LABEL_IS_TOO_SHORT");
        }
        if (label.length() > 100) {
            throwRightException(type ,"LABEL_IS_TOO_LONG");
        }

        if (this.removeSpaces(description).length() < 5) {
            throwRightException(type ,"DESCRIPTION_IS_TOO_SHORT");
        }

        if  ( type instanceof MealEntity && description.length() > 600) {
            throwRightException(type ,"DESCRIPTION_IS_TOO_LONG");
        }

        if  ( type instanceof MenuEntity && description.length() > 700) {
            throwRightException(type ,"DESCRIPTION_IS_TOO_LONG");
        }


        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throwRightException(type ,"PRICE MUST BE GREATER THAN 0");
        }

        if (quantity == null || quantity < 0) {
            throwRightException(type ,"QUANTITY_IS_MANDATORY");
        }


        if (status != 0 && status != 1) {
            throwRightException(type ,"STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE ");
        }
    }


    public String removeSpaces(String str) {
        return str.replaceAll("\\s+", "");
    }

    public void  checImageValididty ( Object  type,  MultipartFile image) throws InvalidMealInformationException, InvalidMenuInformationException {

        if (image == null || image.isEmpty()) {
            throwRightException ( type , "IMAGE_IS_MANDATORY");
           }
         }


    private  void throwRightException (Object type ,  String messageException) throws InvalidMealInformationException, InvalidMenuInformationException {
        if  ( type instanceof MealEntity){
            throw new InvalidMealInformationException(messageException);
        }
        if  ( type instanceof MenuEntity){
            throw new InvalidMenuInformationException(messageException);
        }
    }

}
