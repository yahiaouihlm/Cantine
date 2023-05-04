package fr.sqli.Cantine.dto.out.food;

import fr.sqli.Cantine.entity.MenuEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MenuDtout {

    private final Integer id;
    private final  String label;

    private final  String description;
    private final  LocalDate createdDate;

    private final  BigDecimal price;
    private final Integer quantity;

    private final  Integer status;

    private final String  image;

    private  final List<MealDtout> meals;



    public MenuDtout(MenuEntity menu , String menuUrlImage,  String mealUrlImage) {
        this.id = menu.getId();
        this.description = menu.getDescription();
        this.createdDate = menu.getCreatedDate();
        this.price = menu.getPrice();
        this.status = menu.getStatus();
        this.label = menu.getLabel();
        this.quantity = menu.getQuantity();

        var  path  = menu.getImage().getImagename();
        this.image =menuUrlImage + path;


        this.meals = menu.getMeals().stream().map(meal -> new MealDtout(meal,mealUrlImage)).toList();
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

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }


    public Integer getQuantity() {
        return quantity;
    }
    public List<MealDtout> getMeals() {
        return meals;
    }
}
