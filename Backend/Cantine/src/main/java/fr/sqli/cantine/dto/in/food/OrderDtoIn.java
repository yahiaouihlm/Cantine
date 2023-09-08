package fr.sqli.cantine.dto.in.food;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.order.exception.InvalidOrderException;

import java.util.List;

public class OrderDtoIn {

    private Integer studentId;

    private List<Integer> mealsId;

        private List<Integer> menusId;

    @JsonIgnore
    public void checkOrderIDsValidity() throws InvalidPersonInformationException, InvalidMealInformationException, InvalidMenuInformationException, InvalidOrderException {
        if (this.studentId == null || this.studentId < 0) {
            throw new InvalidPersonInformationException("STUDENT ID IS  REQUIRED");
        }

        if (this.mealsId == null && this.menusId == null) {
            throw new InvalidOrderException("MEALS OR MENUS ARE REQUIRED");
        }

        if (this.mealsId != null) {
            for (Integer mealId : mealsId) {
                if (mealId == null || mealId < 0) {
                    throw new InvalidMealInformationException("INVALID MEAL ID");
                }
            }
        }
        if (this.menusId != null) {
            for (Integer menuId : menusId) {
                if (menuId == null || menuId < 0) {
                    throw new InvalidMenuInformationException("INVALID MENU ID");
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
