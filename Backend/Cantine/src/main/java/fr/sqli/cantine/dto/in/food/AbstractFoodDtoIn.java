package fr.sqli.cantine.dto.in.food;

import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public abstract class AbstractFoodDtoIn {

     protected  String  uuid;
     protected  String label;
     protected  String description;
     protected  BigDecimal price;
     protected  Integer status;
     protected  Integer quantity;
     protected  MultipartFile image;
    /**
     * check  if the   validity of the  parameters  passed  to the  method  ( the information shared  by the  meal and the menu)
     *

     * @param label       the label of the  meal or the menu
     * @param description the description of the  meal or the menu
     * @param price       the price of the  meal or the menu
     * @param status      the status of the  meal or the menu
     * @param quantity    the quantity of the  meal or the menu

     */
    public void CheckNullabilityAndEmptiness( String label, String description, BigDecimal price, Integer status, Integer quantity) throws InvalidFoodInformationException {


        if (label == null || label.trim().isEmpty()) {
            throw  new InvalidFoodInformationException("LABEL_IS_MANDATORY");
        }
        else if (label.length() > 100) {
            throw  new InvalidFoodInformationException("LABEL_IS_TOO_LONG");
        }
        else if (description == null || description.trim().isEmpty()) {
            throw  new InvalidFoodInformationException("DESCRIPTION_IS_MANDATORY");
        }

        else if (price == null) {
            throw  new InvalidFoodInformationException("PRICE_IS_MANDATORY");
        }
        else if (status == null) {
            throw  new InvalidFoodInformationException("STATUS_IS_MANDATORY");
        }
        else if  ( status != 0 && status != 1) {
            throw  new InvalidFoodInformationException("STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE");
        }

        else if (quantity == null) {
            throw  new InvalidFoodInformationException("QUANTITY_IS_MANDATORY");
        }
        else if (quantity < 0) {
            throw  new InvalidFoodInformationException( "QUANTITY MUST BE GREATER THAN 0");
        }

        else if (quantity > Integer.MAX_VALUE-100 ) {
            throw  new InvalidFoodInformationException ("QUANTITY_IS_TOO_HIGH");
        }

        else if (this.removeSpaces(label).length() < 3) {
            throw  new InvalidFoodInformationException("LABEL_IS_TOO_SHORT");
        }

        else if  (this.removeSpaces(description).length() < 5) {
            throw  new InvalidFoodInformationException( "DESCRIPTION_IS_TOO_SHORT");
        }


        else if  (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw  new InvalidFoodInformationException( "PRICE MUST BE GREATER THAN 0");
        }
        else if  (price.compareTo(BigDecimal.valueOf(1000)) > 0) {
            throw  new InvalidFoodInformationException ( "PRICE MUST BE LESS THAN 1000");
        }


    }

    /**
     * remove all the spaces in a string
     * @param str the string to remove the spaces
     * @return the string without spaces
     */
    public String removeSpaces(String str) {
        if (str == null) {
            return "";
        }
        return str.replaceAll("\\s+", "");
    }

    public void checkImageValidity(MultipartFile image) throws  InvalidFoodInformationException {

        if (image == null || image.isEmpty() || image.getSize()==0){
             throw   new  InvalidFoodInformationException("IMAGE_IS_MANDATORY");
        }
    }





}

