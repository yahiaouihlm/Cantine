package fr.sqli.cantine.dto.out.food;

import fr.sqli.cantine.entity.OrderEntity;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public class OrderDtout  {

    private  Integer id ;
    private Integer studentId;

    private List<MealDtout> meals;

    private List<MenuDtout> menus;

    private LocalDate creationDate;

    private Time creationTime;

    private  Integer status;

    private  boolean isCanceled;

    public OrderDtout (OrderEntity orderEntity , String mealUrlImage ,  String menuUrlImage ){
        this.id =  orderEntity.getId();
        this.studentId = orderEntity.getStudent().getId();
        this.creationDate = orderEntity.getCreationDate();
        this.creationTime = orderEntity.getCreationTime();
        this.status = orderEntity.getStatus();
        this.isCanceled = orderEntity.isCancelled();
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Time getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Time creationTime) {
        this.creationTime = creationTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

}
