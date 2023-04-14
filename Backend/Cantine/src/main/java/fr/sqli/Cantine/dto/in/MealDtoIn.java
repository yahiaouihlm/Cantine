package fr.sqli.Cantine.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;

public class MealDtoIn implements Serializable {

    private String category;
    private String description;
    private String label;
    private BigDecimal price;
    private Integer quantity;
    private Integer status;

    private MultipartFile image;

    /**
     * Convert the MealDtoIn to a MealEntity object and return it  after checking if the meal information is valid
     *
     * @return the MealEntity object created from the MealDtoIn object or throw an exception if the meal information is not valid
     * @throws InvalidMealInformationAdminException if the meal information is not valid
     */

    @JsonIgnore
    public MealEntity toMealEntity() throws InvalidMealInformationAdminException {
        this.checkMealInformationValidity(); // check if the meal information is valid
        return this.createMealEntity(); // create the MealEntity object
    }

    /**
     * Check if the meal information is valid or not and throw an exception if it is not valid ( if one of the arguments is null or empty or less than 0)
     *
     * @throws InvalidMealInformationAdminException if the meal information is not valid (if one of the arguments is null or empty or less than 0)
     */
    @JsonIgnore
    public MealEntity toMealEntityWithoutImage() throws InvalidMealInformationAdminException {
        this.checkValidityExceptImage(); // check if the meal information is valid except the image
        return this.createMealEntity(); // create the MealEntity object
    }

    /**
     * Convert the MealDtoIn to a MealEntity object and return it  after checking if the meal information is valid except the image
     *
     * @return the MealEntity object created from the MealDtoIn object or throw an exception if the meal information is not valid
     * @throws InvalidMealInformationAdminException if the meal information is not valid
     */
    @JsonIgnore
    public MealEntity createMealEntity() throws InvalidMealInformationAdminException {
        MealEntity mealEntity = new MealEntity();
        mealEntity.setCategory(this.category);
        mealEntity.setDescription(this.description);
        mealEntity.setLabel(this.label);
        mealEntity.setPrice(this.price);
        mealEntity.setQuantity(quantity);
        mealEntity.setStatus(this.status);
        return mealEntity;
    }

    /**
     * Check if the meal information is valid or not and throw an exception
     * if it is not valid ( if one of the arguments is null or empty or less than 0)
     *
     * @throws InvalidMealInformationAdminException if the meal information is not valid
     */
    @JsonIgnore
    public void checkMealInformationValidity() throws InvalidMealInformationAdminException {
        this.checkValidityExceptImage();
        if (this.image == null || this.image.isEmpty()) {
            throw new InvalidMealInformationAdminException("IMAGE_IS_MANDATORY");
        }
    }

    /**
     * Check if the meal information is valid or not and throw an exception if it is not valid
     * ( if one of the arguments is null or empty or less than 0) except the image
     *
     * @throws InvalidMealInformationAdminException if the meal information is not valid except the image
     */
    @JsonIgnore
    public void checkValidityExceptImage() throws InvalidMealInformationAdminException {
        if (this.label == null || this.label.trim().isEmpty()) {
            throw new InvalidMealInformationAdminException("LABEL_IS_MANDATORY");
        }
        if (this.description == null || this.description.trim().isEmpty()) {
            throw new InvalidMealInformationAdminException("DESCRIPTION_IS_MANDATORY");
        }
        if (this.category == null || this.category.trim().isEmpty()) {
            throw new InvalidMealInformationAdminException("CATEGORIES_IS_MANDATORY");
        }
        if (this.price == null) {
            throw new InvalidMealInformationAdminException("PRICE_IS_MANDATORY");
        }
        if (this.status == null ) {
            throw new InvalidMealInformationAdminException("STATUS_IS_MANDATORY");
        }

        if  (this.label.trim().length() <  3 ) {
            throw new InvalidMealInformationAdminException("LABEL_IS_TOO_SHORT");
        }
        if (this.label.length() > 100) {
            throw new InvalidMealInformationAdminException("LABEL_IS_TOO_LONG");
        }

        if  (this.description.trim().length() <  3 ) {
            throw new InvalidMealInformationAdminException("DESCRIPTION_IS_TOO_SHORT");
        }

        if (this.description.length() > 600) {
            throw new InvalidMealInformationAdminException("DESCRIPTION_IS_TOO_LONG");
        }

        if (this.category.length() > 45) {
            throw new InvalidMealInformationAdminException("CATEGORIES_IS_TOO_LONG");
        }

        if  (this.category.trim().length() <  3  ) {
            throw new InvalidMealInformationAdminException("CATEGORIES_IS_TOO_SHORT");
        }

        if  ( this.price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMealInformationAdminException("PRICE MUST BE GREATER THAN 0");
        }

        if (this.quantity == null || this.quantity < 0) {
            throw new InvalidMealInformationAdminException("QUANTITY_IS_MANDATORY");
        }


        if (this.status != 0 && this.status != 1) {
            throw new InvalidMealInformationAdminException("STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE ");
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
