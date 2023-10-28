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

    public void CheckNullabilityAndEmptiness() throws InvalidFoodInformationException {

                  //------------- Label ----------------//
        if (this.label == null ||  label.isBlank()) {
            throw  new InvalidFoodInformationException("LABEL_IS_MANDATORY");
        }
        else if (this.label.length() > 100) {
            throw  new InvalidFoodInformationException("LABEL_IS_TOO_LONG");
        }
        else if (this.removeSpaces(this.label).length() < 3) {
            throw  new InvalidFoodInformationException("LABEL_IS_TOO_SHORT");
        }

        //------------- Description ----------------//
        else if (this.description == null || this.description.isBlank()) {
            throw  new InvalidFoodInformationException("DESCRIPTION_IS_MANDATORY");
        }
        else if  (this.removeSpaces(this.description).length() < 5) {
            throw  new InvalidFoodInformationException( "DESCRIPTION_IS_TOO_SHORT");
        }
        else if (this.description.length() > 3000) {
           throw new InvalidFoodInformationException("DESCRIPTION_IS_TOO_LONG");
        }


        //------------- Price ----------------//
        else if (this.price == null) {
            throw  new InvalidFoodInformationException("PRICE_IS_MANDATORY");
        }
        else if  (this.price.compareTo(BigDecimal.ZERO) <= 0) {
            throw  new InvalidFoodInformationException( "PRICE MUST BE GREATER THAN 0");
        }
        else if  (this.price.compareTo(BigDecimal.valueOf(1000)) > 0) {
            throw  new InvalidFoodInformationException ( "PRICE MUST BE LESS THAN 1000");
        }



        //------------- status  ----------------//
        else if (this.status == null) {
            throw  new InvalidFoodInformationException("STATUS_IS_MANDATORY");
        }
        else if  ( this.status != 0 && this.status != 1) {
            throw  new InvalidFoodInformationException("STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE");
        }






        //------------- quantity ----------------//
        else if (this.quantity == null) {
            throw  new InvalidFoodInformationException("QUANTITY_IS_MANDATORY");
        }
        else if (this.quantity < 0) {
            throw  new InvalidFoodInformationException( "QUANTITY MUST BE GREATER THAN 0");
        }

        else if (quantity > 10000 ) {
            throw  new InvalidFoodInformationException ("QUANTITY_IS_TOO_HIGH");
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

