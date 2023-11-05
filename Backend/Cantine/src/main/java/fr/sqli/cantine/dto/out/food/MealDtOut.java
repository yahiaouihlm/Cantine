package fr.sqli.cantine.dto.out.food;

import fr.sqli.cantine.entity.MealEntity;

import java.math.BigDecimal;

public class MealDtOut extends AbstractFoodDtOut {

    private final String category;


    public MealDtOut(MealEntity meal, String mealUrlImage) {
        super(meal.getUuid(), meal.getLabel(), meal.getDescription(), meal.getPrice(), meal.getQuantity(), meal.getStatus(), meal.getImage(), mealUrlImage);
        this.category = meal.getCategory();
    }


    public String getCategory() {
        return category;
    }
}
