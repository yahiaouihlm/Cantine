package fr.sqli.cantine.dto.out.food;

import fr.sqli.cantine.entity.MealEntity;

import java.math.BigDecimal;

public class MealDtOut {

    private  final   String uuid ;
    private  final String label ;
    private final String description;
    private  final  String category;
    private  final   BigDecimal price ;
    private final  Integer quantity;
    private final Integer status;

    private final String image ;






    public MealDtOut(MealEntity meal , String mealUrlImage) {
        this.uuid = meal.getUuid();
        this.description = meal.getDescription();
        this.category = meal.getCategory();
        this.price = meal.getPrice();
        this.quantity = meal.getQuantity();
        this.status = meal.getStatus();
        this.label = meal.getLabel();
        var  path  = meal.getImage().getImagename();
        this.image =mealUrlImage + path;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }
}
