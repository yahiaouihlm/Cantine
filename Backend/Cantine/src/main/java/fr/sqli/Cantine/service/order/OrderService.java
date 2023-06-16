package fr.sqli.Cantine.service.order;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dao.IOrderDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.dto.in.food.OrderDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements IOrder {

    private static final Logger LOG = LogManager.getLogger();
    private IOrderDao orderDao;

    private IStudentDao studentDao;

    private IMealDao mealDao;

    private IMenuDao menuDao;
    @Autowired
    public OrderService(IOrderDao orderDao, IStudentDao studentDao, IMealDao mealDao, IMenuDao menuDao) {
        this.orderDao = orderDao;
    }


    @Override
    public void addOrder(OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, InvalidMealInformationException, StudentNotFoundException, MealNotFoundException, MenuNotFoundException {
        orderDtoIn.checkOrderIDsValidity();
        var  student = this.studentDao.findById(orderDtoIn.getStudentId());
        if(student.isEmpty())
            throw new StudentNotFoundException("STUDENT NOT FOUND");

        List<MealEntity> meals = new ArrayList<>();

        for (var mealId : orderDtoIn.getMealsId()) {
            var meal = this.mealDao.findById(mealId);
            if(meal.isEmpty()){
                OrderService.LOG.error("MEAL WITH  ID  = "+ mealId  +" NOT FOUND");
                throw new MealNotFoundException("MEAL WITH  ID  = "+ mealId  +" NOT FOUND");
            }
            meals.add(meal.get());
        }

        for (var menuId : orderDtoIn.getMenusId()) {
            var menu = this.menuDao.findById(menuId);
            if(menu.isEmpty()){
                OrderService.LOG.error("MENU WITH  ID  = "+ menuId  +" NOT FOUND");
                throw new MenuNotFoundException("MENU WITH  ID  = "+ menuId  +" NOT FOUND");
            }
               meals.addAll(menu.get().getMeals());
        }




    }
}
