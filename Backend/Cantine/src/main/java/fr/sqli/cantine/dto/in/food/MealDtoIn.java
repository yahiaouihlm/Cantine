package fr.sqli.cantine.dto.in.food;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.entity.MealTypeEnum;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;

import java.io.Serializable;

public class MealDtoIn extends AbstractFoodDtoIn implements Serializable {

    private String category;
    private String mealType;

    private MealTypeEnum mealTypeEnum;

    @JsonIgnore
    public void checkMealInformation() throws InvalidFoodInformationException {
        super.CheckNullabilityAndEmptiness();
        checkMealSpecificArguments();
        super.checkImageValidity();
    }


    @JsonIgnore
    public void checkMealInfoValidityWithoutImage() throws InvalidFoodInformationException {
        super.CheckNullabilityAndEmptiness(); // check if the meal information is valid except the image and  category
        this.checkMealSpecificArguments();
        super.setLabel(super.getLabel().trim());
    }


    public void checkMealSpecificArguments() throws InvalidFoodInformationException {
        if (category == null || category.isBlank()) {
            throw new InvalidFoodInformationException("CATEGORY_IS_MANDATORY");
        }

        if (this.removeSpaces(category).length() < 3)
            throw new InvalidFoodInformationException("CATEGORY_IS_TOO_SHORT");

        if (category.length() > 100) {
            throw new InvalidFoodInformationException("CATEGORY_IS_TOO_LONG");
        }
        if (mealType == null || mealType.isBlank()) {
            throw new InvalidFoodInformationException("MEAL_TYPE_IS_MANDATORY");
        }
        if (!MealTypeEnum.contains(mealType)) {
            throw new InvalidFoodInformationException("MEAL_TYPE_IS_INVALID");
        }

        this.mealTypeEnum = MealTypeEnum.getMealTypeEnum(mealType);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public MealTypeEnum getMealTypeEnum() {
        return mealTypeEnum;
    }

    public void setMealTypeEnum(MealTypeEnum mealTypeEnum) {
        this.mealTypeEnum = mealTypeEnum;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}