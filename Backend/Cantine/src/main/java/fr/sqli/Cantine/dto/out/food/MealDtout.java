package fr.sqli.Cantine.dto.out.food;

import fr.sqli.Cantine.entity.MealEntity;

import java.math.BigDecimal;

public class MealDtout {

    private  final   Integer id ;
    private  final String label ;
    private final String description;
    private  final  String category;
    private  final   BigDecimal price ;
    private final  Integer quantity;
    private final Integer status;

    private final String image ;






    public MealDtout(MealEntity meal ,  String mealUrlImage) {
        this.id = meal.getId();
        this.description = meal.getDescription();
        this.category = meal.getCategory();
        this.price = meal.getPrice();
        this.quantity = meal.getQuantity();
        this.status = meal.getStatus();
        this.label = meal.getLabel();
        var  path  = meal.getImage().getImagename();
        this.image =mealUrlImage + path;
    }

    public Integer getId() {
        return id;
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
