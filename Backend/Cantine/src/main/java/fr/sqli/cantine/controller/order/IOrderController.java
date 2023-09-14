package fr.sqli.cantine.controller.order;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.cantine.service.order.exception.*;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

public interface IOrderController {

    String ORDER_BASIC_URL = "cantine/student/order";

    String CANCEL_ORDER_URL = "/cancel";
    String ADD_ORDER_URL = "/add";

   String ORDER_ADDED_SUCCESSFULLY = "ORDER ADDED SUCCESSFULLY";
    String ORDER_CANCELLED_SUCCESSFULLY = "ORDER CANCELLED SUCCESSFULLY";

    @PostMapping(ADD_ORDER_URL)
    ResponseEntity <ResponseDtout>addOrder(OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, TaxNotFoundException, MealNotFoundException, InvalidMealInformationException, MenuNotFoundException, InsufficientBalanceException, StudentNotFoundException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException;


    @PostMapping(CANCEL_ORDER_URL)
    ResponseEntity<String> cancelOrder( Integer orderId) throws OrderNotFoundException, InvalidOrderException, UnableToCancelOrderException, StudentNotFoundException;


}
