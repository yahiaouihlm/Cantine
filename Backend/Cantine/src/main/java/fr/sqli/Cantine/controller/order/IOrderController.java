package fr.sqli.Cantine.controller.order;

import com.google.zxing.WriterException;
import fr.sqli.Cantine.dto.in.food.OrderDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.order.exception.*;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.Cantine.service.superAdmin.exception.TaxNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

public interface IOrderController {

    String ORDER_BASIC_URL = "cantine/student/order";

    String CANCEL_ORDER_URL = "/cancel";
    String ADD_ORDER_URL = "/add";

   String ORDER_ADDED_SUCCESSFULLY = "ORDER ADDED SUCCESSFULLY";
    String ORDER_CANCELLED_SUCCESSFULLY = "ORDER CANCELLED SUCCESSFULLY";
    @PostMapping(ADD_ORDER_URL)
    ResponseEntity <String>addOrder(OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, TaxNotFoundException, MealNotFoundException, InvalidMealInformationException, MenuNotFoundException, InsufficientBalanceException, StudentNotFoundException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException;


    @PostMapping(CANCEL_ORDER_URL)
    ResponseEntity<String> cancelOrder( Integer orderId) throws OrderNotFoundException, InvalidOrderException, UnableToCancelOrderException;


}
