package fr.sqli.cantine.controller.order;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.OrderDtOut;
import fr.sqli.cantine.service.admin.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.order.OrderService;
import fr.sqli.cantine.service.order.exception.*;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(IOrderController.BASIC_ORDER_URL)
public class OrderController  implements IOrderController{

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @Override
    public ResponseEntity<ResponseDtout> submitOrder(Integer orderId) throws OrderNotFoundException, InvalidOrderException, MessagingException, CancelledOrderException {
        this.orderService.submitOrder(orderId);
        return ResponseEntity.ok(new ResponseDtout(ORDER_SUBMITTED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<List<OrderDtOut>> getOrdersByDateAndStudentId(Integer studentId, LocalDate date) throws OrderNotFoundException, InvalidOrderException, StudentNotFoundException, InvalidPersonInformationException {
        return ResponseEntity.ok(this.orderService.getOrdersByDateAndStudentId(studentId,date));
    }

    @Override
    public ResponseEntity<ResponseDtout>  addOrder(@RequestBody OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, TaxNotFoundException, InsufficientBalanceException, StudentNotFoundException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException, MessagingException, InvalidFoodInformationException, FoodNotFoundException {
        this.orderService.addOrder(orderDtoIn) ;
        return ResponseEntity.ok( new ResponseDtout(ORDER_ADDED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<List<OrderDtOut>> getOrdersByDate(LocalDate date) throws InvalidPersonInformationException, InvalidOrderException {
        return ResponseEntity.ok(this.orderService.getOrdersByDate(date));
    }

    @Override
    public ResponseEntity<String> cancelOrder(@RequestParam("orderId") Integer orderId) throws OrderNotFoundException, InvalidOrderException, UnableToCancelOrderException, StudentNotFoundException {
        this.orderService.cancelOrder(orderId);
        return ResponseEntity.ok( ORDER_CANCELLED_SUCCESSFULLY);
    }


}
