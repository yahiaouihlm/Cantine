package fr.sqli.Cantine.dto.in;

import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public abstract class AbstractDtoIn {


    /**
     *  check  if the   validity of the  parameters  passed  to the  method  ( the information shared  by the  meal and the menu)
     * @param label the label of the meal or the menu
     * @param description the description of the meal or the menu
     * @param price the price of the meal or the menu
     * @param status the status of the meal or the menu
     * @param quantity  the quantity of the meal or the menu
     * @throws InvalidMealInformationException
     */

    public void checkValidityExceptImageAndCategory( Object type ,  String label,  String  description ,  BigDecimal price, Integer status,  Integer quantity) throws InvalidMealInformationException {
        if (label == null || label.trim().isEmpty()) {
            throw new InvalidMealInformationException("LABEL_IS_MANDATORY");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidMealInformationException("DESCRIPTION_IS_MANDATORY");
        }

        if (price == null) {
            throw new InvalidMealInformationException("PRICE_IS_MANDATORY");
        }
        if (status == null) {
            throw new InvalidMealInformationException("STATUS_IS_MANDATORY");
        }

        if (this.removeSpaces(label).length() < 3) {
            throw new InvalidMealInformationException("LABEL_IS_TOO_SHORT");
        }
        if (label.length() > 100) {
            throw new InvalidMealInformationException("LABEL_IS_TOO_LONG");
        }

        if (this.removeSpaces(description).length() < 5) {
            throw new InvalidMealInformationException("DESCRIPTION_IS_TOO_SHORT");
        }

        if (description.length() > 600) {
            throw new InvalidMealInformationException("DESCRIPTION_IS_TOO_LONG");
        }



        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMealInformationException("PRICE MUST BE GREATER THAN 0");
        }

        if (quantity == null || quantity < 0) {
            throw new InvalidMealInformationException("QUANTITY_IS_MANDATORY");
        }


        if (status != 0 && status != 1) {
            throw new InvalidMealInformationException("STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE ");
        }
    }


    public String removeSpaces(String str) {
        return str.replaceAll("\\s+", "");
    }

    public void  checImageValididty (MultipartFile image) throws InvalidMealInformationException {

        if (image == null || image.isEmpty()) {
            throw new InvalidMealInformationException("IMAGE_IS_MANDATORY");
        }
         }


}
