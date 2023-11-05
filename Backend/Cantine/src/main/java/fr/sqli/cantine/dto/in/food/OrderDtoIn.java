package fr.sqli.cantine.dto.in.food;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.order.exception.InvalidOrderException;

import java.util.List;

public class OrderDtoIn {

    private Integer studentId;

    private List<Integer> mealsId;

    private List<Integer> menusId;

    @JsonIgnore
    public void checkOrderIDsValidity() throws InvalidUserInformationException, InvalidOrderException, InvalidFoodInformationException {
        if (this.studentId == null || this.studentId < 0) {
            throw new InvalidUserInformationException("STUDENT ID IS  REQUIRED");
        }

        if (this.mealsId == null && this.menusId == null) {
            throw new InvalidOrderException("MEALS OR MENUS ARE REQUIRED");
        }

        if (this.mealsId != null) {
            for (Integer mealId : mealsId) {
                if (mealId == null || mealId < 0) {
                    throw new InvalidFoodInformationException("INVALID MEAL ID");
                }
            }
        }
        if (this.menusId != null) {
            for (Integer menuId : menusId) {
                if (menuId == null || menuId < 0) {
                    throw new InvalidFoodInformationException("INVALID MENU ID");
                }
            }
        }

    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public List<Integer> getMealsId() {
        return mealsId;
    }

    public void setMealsId(List<Integer> mealsId) {
        this.mealsId = mealsId;
    }

    public List<Integer> getMenusId() {
        return menusId;
    }

    public void setMenusId(List<Integer> menusId) {
        this.menusId = menusId;
    }
}
