package fr.sqli.cantine.controller.order;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.OrderDtOut;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.order.OrderService;
import fr.sqli.cantine.service.order.exception.*;

import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
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
    public ResponseEntity<ResponseDtout> cancelOrder(String orderUuid) throws OrderNotFoundException, UserNotFoundException, InvalidOrderException, MessagingException, CancelledOrderException, InvalidUserInformationException {
         this.orderService.cancelOrderByAdmin(orderUuid);
        return ResponseEntity.ok(new ResponseDtout(ORDER_CANCELLED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<ResponseDtout> submitOrder(Integer orderId) throws OrderNotFoundException, InvalidOrderException, MessagingException, CancelledOrderException {
        this.orderService.submitOrder(orderId);
        return ResponseEntity.ok(new ResponseDtout(ORDER_SUBMITTED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<List<OrderDtOut>> getOrdersByDateAndStudentId(String studentUuid, LocalDate date) throws InvalidOrderException, InvalidUserInformationException, UserNotFoundException {
        return ResponseEntity.ok(this.orderService.getOrdersByDateAndStudentId(studentUuid,date));
    }

    @Override
    public ResponseEntity<ResponseDtout> addOrderByStudent(@RequestBody OrderDtoIn orderDtoIn) throws InvalidUserInformationException, TaxNotFoundException, InsufficientBalanceException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException, MessagingException, InvalidFoodInformationException, FoodNotFoundException, UserNotFoundException {
        this.orderService.addOrderByStudent(orderDtoIn) ;
        return ResponseEntity.ok( new ResponseDtout(ORDER_ADDED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<List<OrderDtOut>> getStudentOrdersHistory(String studentUuid) throws UserNotFoundException {
        return ResponseEntity.ok(this.orderService.getStudentOrder(studentUuid));
    }

    @Override
    public ResponseEntity<List<OrderDtOut>> getOrdersByDate(LocalDate date) throws InvalidUserInformationException, InvalidOrderException {
        return ResponseEntity.ok(this.orderService.getOrdersByDate(date));
    }

    @Override
    public ResponseEntity<ResponseDtout> cancelOrderByStudent(String orderUuid) throws OrderNotFoundException, InvalidOrderException, UnableToCancelOrderException, UserNotFoundException, MessagingException {
        this.orderService.cancelOrderByStudent(orderUuid);
        return ResponseEntity.ok( new ResponseDtout(ORDER_CANCELLED_SUCCESSFULLY));
    }


}
