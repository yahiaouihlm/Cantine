package fr.sqli.cantine.dto.out.food;

import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.entity.OrderEntity;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public class OrderDtOut {

    private  Integer id ;
    private Integer studentId;

    private List<MealDtOut> meals;

    private List<MenuDtOut> menus;

    private LocalDate creationDate;

    private Time creationTime;

    private  Integer status;
    private BigDecimal price;

    private StudentDtout studentOrder;

    private  boolean isCancelled;

    public OrderDtOut(OrderEntity orderEntity , String mealUrlImage , String menuUrlImage  , String  studentUrlImage){
        this.id =  orderEntity.getId();
        this.studentId = orderEntity.getStudent().getId();
        this.creationDate = orderEntity.getCreationDate();
        this.creationTime = orderEntity.getCreationTime();
        this.status = orderEntity.getStatus();
        this.isCancelled = orderEntity.isCancelled();
        this.studentOrder = new StudentDtout(orderEntity.getStudent() , studentUrlImage);
        this.meals = orderEntity.getMeals().stream().map( (mealEntity) -> new MealDtOut(mealEntity , mealUrlImage)).toList();
        this.menus=orderEntity.getMenus().stream().map(menuEntity -> new MenuDtOut(menuEntity, menuUrlImage , mealUrlImage)).toList();
         this.price = orderEntity.getPrice();
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

    public List<MealDtOut> getMeals() {
        return meals;
    }

    public void setMeals(List<MealDtOut> meals) {
        this.meals = meals;
    }

    public List<MenuDtOut> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuDtOut> menus) {
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

    public StudentDtout getStudentOrder() {
        return studentOrder;
    }

    public void setStudentOrder(StudentDtout studentOrder) {
        this.studentOrder = studentOrder;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
