package fr.sqli.cantine.dto.out.food;

import fr.sqli.cantine.entity.OrderEntity;

import java.util.List;

public class OrderDtout  {

    private  Integer id ;
    private Integer studentId;

    private List<MealDtout> meals;

    private List<MenuDtout> menus;

    public OrderDtout (OrderEntity orderEntity , String mealUrlImage ,  String menuUrlImage ){
        this.id =  orderEntity.getId();
        this.studentId = orderEntity.getStudent().getId();
        this.meals = orderEntity.getMeals().stream().map( (mealEntity) -> new MealDtout(mealEntity , mealUrlImage)).toList();
        this.menus=orderEntity.getMenus().stream().map(menuEntity -> new MenuDtout(menuEntity, menuUrlImage , mealUrlImage)).toList();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public List<MealDtout> getMeals() {
        return meals;
    }

    public void setMeals(List<MealDtout> meals) {
        this.meals = meals;
    }

    public List<MenuDtout> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuDtout> menus) {
        this.menus = menus;
    }

}
