package fr.sqli.Cantine.dto.in.food;

import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public abstract class AbstractDtoIn {


    /**
     * check  if the   validity of the  parameters  passed  to the  method  ( the information shared  by the  meal and the menu)
     *
     * @param type        the type of the object  ( meal or menu) to  check  the validity of the  parameters
     * @param label       the label of the  meal or the menu
     * @param description the description of the  meal or the menu
     * @param price       the price of the  meal or the menu
     * @param status      the status of the  meal or the menu
     * @param quantity    the quantity of the  meal or the menu
     * @param category    the category of the  meal or the menu
     * @throws InvalidMealInformationException if the meal information is not valid if  type is a meal
     * @throws InvalidMenuInformationException if the menu information is not valid if  type is a menu
     */
    public void checkValidity(Class type, String label, String description, BigDecimal price, Integer status, Integer quantity, String category) throws InvalidMealInformationException, InvalidMenuInformationException {


        if (label == null || label.trim().isEmpty()) {
            throwRightException(type, "LABEL_IS_MANDATORY");
        }

        if (description == null || description.trim().isEmpty()) {
            throwRightException(type, "DESCRIPTION_IS_MANDATORY");
        }

        if (price == null) {
            throwRightException(type, "PRICE_IS_MANDATORY");
        }
        if (status == null) {
            throwRightException(type, "STATUS_IS_MANDATORY");
        }

        if (MealEntity.class.isAssignableFrom(type)) { // if the type is a meal
            if (category == null || category.trim().isEmpty()) {
                throwRightException(type, "CATEGORY_IS_MANDATORY");
            }

            if (this.removeSpaces(category).length() < 3)
                throwRightException(type, "CATEGORY_IS_TOO_SHORT");

            if (category.length() > 44) {
                throwRightException(type, "CATEGORY_IS_TOO_LONG");
            }

            if (description.length() > 1600) {
                throwRightException(type, "DESCRIPTION_IS_TOO_LONG");
            }


        }

        if (MenuEntity.class.isAssignableFrom(type)) {
            if (description.length() > 1700) {
                throwRightException(type, "DESCRIPTION_IS_TOO_LONG");
            }

        }

        if (quantity == null) {
            throwRightException(type, "QUANTITY_IS_MANDATORY");
        }
        if (quantity < 0) {
            throwRightException(type, "QUANTITY MUST BE GREATER THAN 0");
        }

        if (quantity > Integer.MAX_VALUE-100 ) {
            throwRightException(type, "QUANTITY_IS_TOO_HIGH");
        }

        if (this.removeSpaces(label).length() < 3) {

            throwRightException(type, "LABEL_IS_TOO_SHORT");
        }
        if (label.length() > 100) {
            throwRightException(type, "LABEL_IS_TOO_LONG");
        }

        if (this.removeSpaces(description).length() < 5) {
            throwRightException(type, "DESCRIPTION_IS_TOO_SHORT");
        }


        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throwRightException(type, "PRICE MUST BE GREATER THAN 0");
        }
        if (price.compareTo(BigDecimal.valueOf(1000)) > 0) {
            throwRightException(type, "PRICE MUST BE LESS THAN 1000");
        }

        if (status != 0 && status != 1) {
            throwRightException(type, "STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE");
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

    public void checkImageValidity(Class type, MultipartFile image) throws InvalidMealInformationException, InvalidMenuInformationException {

        if (image == null || image.isEmpty()) {
            throwRightException(type, "IMAGE_IS_MANDATORY");
        }
    }


    private void throwRightException(Class type, String messageException) throws InvalidMealInformationException, InvalidMenuInformationException {
        if (MealEntity.class.isAssignableFrom(type)) {
            throw new InvalidMealInformationException(messageException);
        }
        if (MenuEntity.class.isAssignableFrom(type)) {
            throw new InvalidMenuInformationException(messageException);
        }
    }


}

