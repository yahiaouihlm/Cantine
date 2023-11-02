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
        super.checkImageValidity();
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
}
