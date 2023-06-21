package fr.sqli.Cantine.controller.order;

import com.google.zxing.WriterException;
import fr.sqli.Cantine.dto.in.food.OrderDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.order.exception.InsufficientBalanceException;
import fr.sqli.Cantine.service.order.exception.InvalidOrderException;
import fr.sqli.Cantine.service.order.exception.OrderLimitExceededException;
import fr.sqli.Cantine.service.order.exception.UnavailableFoodException;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.Cantine.service.superAdmin.exception.TaxNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

public interface IOrderController {

    String ORDER_BASIC_URL = "cantine/student/order";


    String ADD_ORDER_URL = "/add";

   String ORDER_ADDED_SUCCESSFULLY = "ORDER ADDED SUCCESSFULLY";
    @PostMapping(IOrderController.ADD_ORDER_URL)
    ResponseEntity <String>addOrder(OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, TaxNotFoundException, MealNotFoundException, InvalidMealInformationException, MenuNotFoundException, InsufficientBalanceException, StudentNotFoundException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException;
}
