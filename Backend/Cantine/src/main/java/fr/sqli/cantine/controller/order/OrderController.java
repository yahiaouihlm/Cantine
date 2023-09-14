package fr.sqli.cantine.controller.order;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.cantine.service.order.OrderService;
import fr.sqli.cantine.service.order.exception.*;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(IOrderController.ORDER_BASIC_URL)
public class OrderController  implements IOrderController{

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<ResponseDtout>  addOrder(@RequestBody OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, TaxNotFoundException, MealNotFoundException, InvalidMealInformationException, MenuNotFoundException, InsufficientBalanceException, StudentNotFoundException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException {
        this.orderService.addOrder(orderDtoIn);
        return ResponseEntity.ok( new ResponseDtout(ORDER_ADDED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<String> cancelOrder(@RequestParam("orderId") Integer orderId) throws OrderNotFoundException, InvalidOrderException, UnableToCancelOrderException, StudentNotFoundException {
        this.orderService.cancelOrder(orderId);
        return ResponseEntity.ok( ORDER_CANCELLED_SUCCESSFULLY);
    }


}
