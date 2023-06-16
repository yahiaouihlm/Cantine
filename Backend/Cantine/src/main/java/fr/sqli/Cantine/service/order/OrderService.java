package fr.sqli.Cantine.service.order;

import fr.sqli.Cantine.dao.*;
import fr.sqli.Cantine.dto.in.food.OrderDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.order.exception.InsufficientBalanceException;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.Cantine.service.superAdmin.exception.TaxNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements IOrderService {

    private static final Logger LOG = LogManager.getLogger();

    final  String  ORDER_QR_CODE_PATH ;
    private IOrderDao orderDao;

    private ITaxDao taxDao;
    private IStudentDao studentDao;

    private IMealDao mealDao;

    private Environment env ;
    private IMenuDao menuDao;
    @Autowired
    public OrderService( Environment env ,IOrderDao orderDao, IStudentDao studentDao, IMealDao mealDao, IMenuDao menuDao , ITaxDao taxDao) {
        this.orderDao = orderDao;
        this.studentDao = studentDao;
        this.mealDao = mealDao;
        this.menuDao = menuDao;
        this.taxDao = taxDao;
        this.env = env ;
        this.ORDER_QR_CODE_PATH = env.getProperty("sqli.canine.order.qrcode.path");

    }


    @Override
    public void addOrder(OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, InvalidMealInformationException, StudentNotFoundException, MealNotFoundException, MenuNotFoundException, TaxNotFoundException, InsufficientBalanceException {
        orderDtoIn.checkOrderIDsValidity();
        var  student = this.studentDao.findById(orderDtoIn.getStudentId());
        var   totalPrice  =  BigDecimal.ZERO;

        //  check Information  validity

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
            totalPrice = totalPrice.add(meal.get().getPrice());
        }

        for (var menuId : orderDtoIn.getMenusId()) {
            var menu = this.menuDao.findById(menuId);
            if(menu.isEmpty()){
                OrderService.LOG.error("MENU WITH  ID  = "+ menuId  +" NOT FOUND");
                throw new MenuNotFoundException("MENU WITH  ID  = "+ menuId  +" NOT FOUND");
            }
              meals.addAll(menu.get().getMeals());
              totalPrice = totalPrice.add(menu.get().getPrice());
        }

        // Calculate  total  price  of  order

        var taxOpt = this.taxDao.findAll() ;

        if  (taxOpt.size() !=  1) {
            OrderService.LOG.info("TAX FOUND");
            throw  new TaxNotFoundException("TAX NOT FOUND");
        }

        var  tax = taxOpt.get(0).getTax();

        totalPrice = totalPrice.add(totalPrice.multiply(tax));


        // check  if  student  has  enough  money  to  pay  for  the  order

        if(student.get().getWallet().compareTo(totalPrice) < 0){
            OrderService.LOG.error("STUDENT WITH  ID  = "+ orderDtoIn.getStudentId()  +" DOES NOT HAVE ENOUGH MONEY TO PAY FOR THE ORDER");
            throw new InsufficientBalanceException("YOU  DON'T HAVE ENOUGH MONEY TO PAY FOR THE ORDER");
        }

        //  create the  QrCode  and  save  the  order  in  the  database



    }
}
