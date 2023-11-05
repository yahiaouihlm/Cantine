package fr.sqli.cantine.dto.out.food;

import fr.sqli.cantine.entity.MenuEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MenuDtOut  extends  AbstractFoodDtOut{


    private  final List<MealDtOut> meals;



    public MenuDtOut(MenuEntity menu , String menuUrlImage, String mealUrlImage) {

        super(menu.getUuid(),menu.getLabel(),menu.getDescription(),menu.getPrice(),menu.getQuantity(),menu.getStatus(),menu.getImage(),   menuUrlImage);
        this.meals = menu.getMeals().stream().map(meal -> new MealDtOut(meal,mealUrlImage)).toList();
    }

    public List<MealDtOut> getMeals() {
        return meals;
    }
}
