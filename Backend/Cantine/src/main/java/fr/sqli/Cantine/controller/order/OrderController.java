package fr.sqli.Cantine.controller.order;

import com.google.zxing.WriterException;
import fr.sqli.Cantine.dto.in.food.OrderDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.order.OrderService;
import fr.sqli.Cantine.service.order.exception.*;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.Cantine.service.superAdmin.exception.TaxNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String>  addOrder( @RequestBody OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, TaxNotFoundException, MealNotFoundException, InvalidMealInformationException, MenuNotFoundException, InsufficientBalanceException, StudentNotFoundException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException {
        this.orderService.addOrder(orderDtoIn);
        return ResponseEntity.ok(ORDER_ADDED_SUCCESSFULLY);
    }

    @Override
    public ResponseEntity<String> cancelOrder(@RequestParam("orderId") Integer orderId) throws OrderNotFoundException, InvalidOrderException, UnableToCancelOrderException {
        this.orderService.cancelOrder(orderId);
        return ResponseEntity.ok( ORDER_CANCELLED_SUCCESSFULLY);
    }


}
