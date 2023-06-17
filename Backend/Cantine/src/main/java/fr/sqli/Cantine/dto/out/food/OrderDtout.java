package fr.sqli.Cantine.dto.out.food;

import java.util.List;

public class OrderDtout  {

    private Integer studentId;

    private List<MealDtout> mealsId;

    private List<MenuDtout> menusId;


    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public List<MealDtout> getMealsId() {
        return mealsId;
    }

    public void setMealsId(List<MealDtout> mealsId) {
        this.mealsId = mealsId;
    }

    public List<MenuDtout> getMenusId() {
        return menusId;
    }

    public void setMenusId(List<MenuDtout> menusId) {
        this.menusId = menusId;
    }
}
