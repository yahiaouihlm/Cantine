package fr.sqli.cantine.dto.in.food;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.order.exception.InvalidOrderException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class OrderDtoIn {

    private static final Logger LOG = LogManager.getLogger();
    private String studentId;

    private List<String> mealsId;

    private List<String> menusId;

    @JsonIgnore
    public void checkOrderIDsValidity() throws InvalidUserInformationException, InvalidOrderException, InvalidFoodInformationException {
        if (this.studentId == null || this.studentId.trim().length() < 10 ) {
            OrderDtoIn.LOG.error("STUDENT ID IS  REQUIRED OR HAS LESS THAN 10 CHARACTERS");
            throw new InvalidUserInformationException("STUDENT ID IS  REQUIRED");
        }

        if (this.mealsId == null && this.menusId == null) {
            OrderDtoIn.LOG.error("MEALS OR MENUS ARE REQUIRED");
            throw new InvalidOrderException("MEALS OR MENUS ARE REQUIRED");
        }

        if (this.mealsId != null) {
            for (String mealId : mealsId) {
                if (mealId == null || mealId.trim().length() < 10 ) {
                    OrderDtoIn.LOG.error("INVALID MEAL ID OR HAS LESS THAN 10 CHARACTERS");
                    throw new InvalidFoodInformationException("INVALID MEAL ID");
                }
            }
        }
        if (this.menusId != null) {
            for (String menuId : menusId) {
                if (menuId == null || menuId.trim().length() < 10) {
                    OrderDtoIn.LOG.error("INVALID MENU ID OR HAS LESS THAN 10 CHARACTERS");
                    throw new InvalidFoodInformationException("INVALID MENU ID");
                }
            }
        }

    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<String> getMealsId() {
        return mealsId;
    }

    public void setMealsId(List<String> mealsId) {
        this.mealsId = mealsId;
    }

    public List<String> getMenusId() {
        return menusId;
    }

    public void setMenusId(List<String> menusId) {
        this.menusId = menusId;
    }
}
