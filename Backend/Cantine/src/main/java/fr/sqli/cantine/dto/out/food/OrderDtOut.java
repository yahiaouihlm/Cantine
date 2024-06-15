package fr.sqli.cantine.dto.out.food;

import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.entity.OrderEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrderDtOut {


    private String uuid;

    private String studentUuid;


    private List<MealDtOut> meals;


    private List<MenuDtOut> menus;

    private LocalDate creationDate;

    private Time creationTime;

    private Integer status;
    private BigDecimal price;

    private StudentDtout studentOrder;

    private boolean isCancelled;

    public OrderDtOut(OrderEntity orderEntity, String mealUrlImage, String menuUrlImage, String studentUrlImage) {
        this.uuid = orderEntity.getUuid();
        this.studentUuid = orderEntity.getStudent().getUuid();
        this.creationDate = orderEntity.getCreationDate();
        this.creationTime = orderEntity.getCreationTime();
        this.status = orderEntity.getStatus();
        this.isCancelled = orderEntity.isCancelled();
        this.studentOrder = new StudentDtout(orderEntity.getStudent(), studentUrlImage);
        this.meals = orderEntity.getMeals().stream().map((mealEntity) -> new MealDtOut(mealEntity, mealUrlImage)).toList();
        this.menus = orderEntity.getMenus().stream().map(menuEntity -> new MenuDtOut(menuEntity, menuUrlImage, mealUrlImage)).toList();
        this.price = orderEntity.getPrice();
    }


}
