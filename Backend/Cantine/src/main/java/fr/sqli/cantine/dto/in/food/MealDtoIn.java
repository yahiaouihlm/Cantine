package fr.sqli.cantine.dto.in.food;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import org.springframework.web.multipart.MultipartFile;
import java.io.Serializable;
import java.math.BigDecimal;

public class MealDtoIn extends AbstractFoodDtoIn implements Serializable {

    private String category;



    @JsonIgnore
    public void checkMealInformation() throws InvalidFoodInformationException {
        super.CheckNullabilityAndEmptiness();
        checkCategoryValidity();
        super.checkImageValidity(this.image);
    }


    @JsonIgnore
    public void toMealEntityWithoutImage() throws InvalidFoodInformationException {
        super.CheckNullabilityAndEmptiness(); // check if the meal information is valid except the image and  category
        this.checkCategoryValidity();
    }


    public void checkCategoryValidity() throws InvalidFoodInformationException {
        if (category == null || category.isBlank()) {
            throw new InvalidFoodInformationException("CATEGORY_IS_MANDATORY");
        }

        if (this.removeSpaces(category).length() < 3)
            throw new InvalidFoodInformationException("CATEGORY_IS_TOO_SHORT");

        if (category.length() > 100) {
            throw new InvalidFoodInformationException("CATEGORY_IS_TOO_LONG");
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
