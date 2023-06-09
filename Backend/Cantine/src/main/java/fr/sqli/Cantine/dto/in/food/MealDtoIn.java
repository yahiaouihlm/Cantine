package fr.sqli.Cantine.dto.in.food;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.Cantine.dto.in.food.AbstractDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;

public class MealDtoIn  extends AbstractDtoIn implements Serializable {

    private  Integer id ; // id of the meal only used in the update method
    private String label;
    private String category;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Integer status;

    private MultipartFile image;

    /**
     * Convert the MealDtoIn to a MealEntity object and return it  after checking if the meal information is valid
     *
     * @return the MealEntity object created from the MealDtoIn object or throw an exception if the meal information is not valid
     * @throws InvalidMealInformationException if the meal information is not valid
     * @throws InvalidMenuInformationException if the menu information is not valid but it will never be thrown in MealDtoIn
     */

    @JsonIgnore
    public MealEntity toMealEntity() throws InvalidMealInformationException, InvalidMenuInformationException {
        this.checkMealInformationValidity(); // check if the meal information is valid
        return this.createMealEntity(); // create the MealEntity object
    }

    /**
     * Check if the meal information is valid or not and throw an exception if it is not valid ( if one of the arguments is null or empty or less than 0)
     *
     * @throws InvalidMealInformationException if the meal information is not valid (if one of the arguments is null or empty or less than 0)
     */
    @JsonIgnore
    public MealEntity toMealEntityWithoutImage() throws InvalidMealInformationException, InvalidMenuInformationException {
        super.checkValidity( MealEntity.class, this.label,  this.description, this.price, this.status ,  this.quantity, this.category); // check if the meal information is valid except the image
        return this.createMealEntity(); // create the MealEntity object
    }

    /**
     * Convert the MealDtoIn to a MealEntity object and return it  after checking if the meal information is valid except the image
     * the method also make a  trim() on the label, description and category before creating the MealEntity object
     *
     * @return the MealEntity object created from the MealDtoIn object or throw an exception if the meal information is not valid
     * @throws InvalidMealInformationException if the meal information is not valid
     */
    @JsonIgnore
    private MealEntity createMealEntity() throws InvalidMealInformationException {
        MealEntity mealEntity = new MealEntity();
        mealEntity.setCategory(this.category.trim());
        mealEntity.setDescription(this.description.trim());
        mealEntity.setLabel(this.label.trim());
        mealEntity.setPrice(this.price);
        mealEntity.setQuantity(quantity);
        mealEntity.setStatus(this.status);
        return mealEntity;

    }

    /**
     * Check if the meal information is valid or not and throw an exception
     * if it is not valid ( if one of the arguments is null or empty or less than 0)
     *
     * @throws InvalidMealInformationException if the meal information is not valid
     * @throws InvalidMenuInformationException if the menu information is not valid but  in  this  cas  it will never be thrown
     *      *                                        because the method is called only in the MealDtoIn class with  type  MealEntity
     */
    @JsonIgnore
    private void checkMealInformationValidity() throws InvalidMealInformationException, InvalidMenuInformationException {
        super.checkValidity( MealEntity.class, this.label,  this.description, this.price, this.status ,  this.quantity, this.category);
        super.checkImageValidity( MealEntity.class ,   this.image);
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
