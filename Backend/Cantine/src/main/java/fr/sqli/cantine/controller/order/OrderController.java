package fr.sqli.cantine.controller.order;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.OrderDtOut;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.order.impl.OrderService;
import fr.sqli.cantine.service.order.exception.*;

import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static fr.sqli.cantine.constants.ConstCantine.ADMIN_ROLE_LABEL;

@RestController
@RequestMapping(OrderController.BASIC_ORDER_URL)
public class OrderController {

    final static String BASIC_ORDER_URL = "order/";
    final String ADMIN_GET_ALL_ORDERS_BY_DAY = "admin/getAllOrdersOfDay";
    final String ADMIN_SUBMIT_ORDER = "admin/submitOrder";
    final String ADMIN_CANCEL_ORDER = "admin/cancelOrder";
    final String GET_ORDER_BY_DATE_AND_STUDENT_ID_URL = "student/getByDateAndStudentId";
    final String CANCEL_ORDER_URL = "student/cancel";
    final String ADD_ORDER_URL = "student/add";
    final String STUDENT_ORDER_HISTORY = "student/getHistory";

    final String ORDER_SUBMITTED_SUCCESSFULLY = "ORDER  SUBMITTED SUCCESSFULLY";
    final String ORDER_ADDED_SUCCESSFULLY = "ORDER ADDED SUCCESSFULLY";
    final String ORDER_CANCELLED_SUCCESSFULLY = "ORDER CANCELLED SUCCESSFULLY";


    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PreAuthorize("hasRole(" + ADMIN_ROLE_LABEL + ")")
    @PostMapping(ADMIN_CANCEL_ORDER)
    public ResponseEntity<ResponseDtout> cancelOrder(@RequestParam("orderUuid") String orderUuid) throws OrderNotFoundException, UserNotFoundException, InvalidOrderException, MessagingException, CancelledOrderException, InvalidUserInformationException {
        this.orderService.cancelOrderByAdmin(orderUuid);
        return ResponseEntity.ok(new ResponseDtout(ORDER_CANCELLED_SUCCESSFULLY));
    }

    @PreAuthorize("hasRole(" + ADMIN_ROLE_LABEL + ")")
    @PostMapping(ADMIN_SUBMIT_ORDER)
    public ResponseEntity<ResponseDtout> submitOrder(@RequestParam("orderUuid") String orderUuid) throws OrderNotFoundException, InvalidOrderException, MessagingException, CancelledOrderException, IOException, WriterException {
        this.orderService.submitOrder(orderUuid);
        return ResponseEntity.ok(new ResponseDtout(ORDER_SUBMITTED_SUCCESSFULLY));
    }

    @PreAuthorize("hasRole(" + ADMIN_ROLE_LABEL + ")")
    @GetMapping(GET_ORDER_BY_DATE_AND_STUDENT_ID_URL)
    public ResponseEntity<List<OrderDtOut>> getOrdersByDateAndStudentId(@RequestParam("studentUuid") String studentUuid, @RequestParam("date") LocalDate date) throws InvalidOrderException, InvalidUserInformationException, UserNotFoundException {
        return ResponseEntity.ok(this.orderService.getOrdersByDateAndStudentId(studentUuid, date));
    }

    @PreAuthorize("hasRole(" + ADMIN_ROLE_LABEL + ")")
    @PostMapping(ADD_ORDER_URL)
    public ResponseEntity<ResponseDtout> addOrderByStudent(@RequestBody OrderDtoIn orderDtoIn) throws InvalidUserInformationException, TaxNotFoundException, InsufficientBalanceException, IOException, WriterException, InvalidOrderException, UnavailableFoodForOrderException, OrderLimitExceededException, MessagingException, InvalidFoodInformationException, FoodNotFoundException, UserNotFoundException {
        this.orderService.addOrderByStudent(orderDtoIn);
        return ResponseEntity.ok(new ResponseDtout(ORDER_ADDED_SUCCESSFULLY));
    }

    @GetMapping(STUDENT_ORDER_HISTORY)
    public ResponseEntity<List<OrderDtOut>> getStudentOrdersHistory(@RequestParam("studentUuid") String studentUuid) throws UserNotFoundException {
        return ResponseEntity.ok(this.orderService.getStudentOrder(studentUuid));
    }

    @GetMapping(ADMIN_GET_ALL_ORDERS_BY_DAY)
    public ResponseEntity<List<OrderDtOut>> getOrdersByDate(@RequestParam("date") LocalDate date) throws InvalidUserInformationException, InvalidOrderException {
        return ResponseEntity.ok(this.orderService.getOrdersByDate(date));
    }

    @PreAuthorize("hasRole(" + ADMIN_ROLE_LABEL + ")")
    @PostMapping(CANCEL_ORDER_URL)
    public ResponseEntity<ResponseDtout> cancelOrderByStudent(@RequestParam("orderUuid") String orderUuid) throws OrderNotFoundException, InvalidOrderException, UnableToCancelOrderException, UserNotFoundException, MessagingException {
        this.orderService.cancelOrderByStudent(orderUuid);
        return ResponseEntity.ok(new ResponseDtout(ORDER_CANCELLED_SUCCESSFULLY));
    }


}
