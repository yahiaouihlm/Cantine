package fr.sqli.cantine.dto.out.food;

import fr.sqli.cantine.entity.MealEntity;

import java.math.BigDecimal;

public class MealDtOut extends AbstractFoodDtOut {

    private final String category;
    private final String mealType;


    public MealDtOut(MealEntity meal, String mealUrlImage) {
        super(meal.getUuid(), meal.getLabel(), meal.getDescription(), meal.getPrice(), meal.getQuantity(), meal.getStatus(), meal.getImage(), mealUrlImage);
        this.category = meal.getCategory();
        this.mealType = meal.getMealType().name();
    }


    public String getCategory() {
        return category;
    }

    public String getMealType() {
        return mealType;
    }
}
