package fr.sqli.cantine.controller.order;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.OrderDtOut;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.order.exception.*;
import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IOrderController {

    String  BASIC_ORDER_URL =  "order/";
    String ADMIN_GET_ALL_ORDERS_BY_DAY = "admin/getAllOrdersOfDay";
    String  ADMIN_SUBMIT_ORDER  =  "admin/submitOrder";
    String  ADMIN_CANCEL_ORDER = "admin/cancelOrder";
    String GET_ORDER_BY_DATE_AND_STUDENT_ID_URL = "student/getByDateAndStudentId";
    String CANCEL_ORDER_URL = "student/cancel";
    String ADD_ORDER_URL = "student/add";
    String STUDENT_ORDER_HISTORY =  "student/getHistory";

    String  ORDER_SUBMITTED_SUCCESSFULLY = "ORDER  SUBMITTED SUCCESSFULLY" ;
    String ORDER_ADDED_SUCCESSFULLY = "ORDER ADDED SUCCESSFULLY";
    String ORDER_CANCELLED_SUCCESSFULLY = "ORDER CANCELLED SUCCESSFULLY";



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(ADMIN_CANCEL_ORDER)
    ResponseEntity<ResponseDtout> cancelOrder (@RequestParam("orderUuid") String orderUuid) throws OrderNotFoundException, UserNotFoundException, InvalidOrderException, MessagingException, CancelledOrderException, InvalidUserInformationException;
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(ADMIN_SUBMIT_ORDER)
    ResponseEntity<ResponseDtout> submitOrder (@RequestParam("orderUuid") String orderUuid) throws OrderNotFoundException, InvalidOrderException, MessagingException, CancelledOrderException, IOException, WriterException;
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(GET_ORDER_BY_DATE_AND_STUDENT_ID_URL)
    ResponseEntity<List<OrderDtOut>> getOrdersByDateAndStudentId(@RequestParam("studentUuid") String studentUuid , @RequestParam("date") LocalDate date) throws OrderNotFoundException, InvalidOrderException, InvalidUserInformationException, UserNotFoundException;
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping(ADD_ORDER_URL)
    ResponseEntity <ResponseDtout> addOrderByStudent(OrderDtoIn orderDtoIn) throws InvalidUserInformationException, TaxNotFoundException, InsufficientBalanceException, IOException, WriterException, InvalidOrderException, UnavailableFoodForOrderException, OrderLimitExceededException, MessagingException, InvalidFoodInformationException, FoodNotFoundException, UserNotFoundException;

    @GetMapping(STUDENT_ORDER_HISTORY)
    ResponseEntity<List<OrderDtOut>> getStudentOrdersHistory(@RequestParam("studentUuid") String studentUuid) throws UserNotFoundException;
    @GetMapping(ADMIN_GET_ALL_ORDERS_BY_DAY)
    ResponseEntity<List<OrderDtOut>> getOrdersByDate(@RequestParam("date") LocalDate date) throws InvalidUserInformationException, InvalidOrderException;

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping(CANCEL_ORDER_URL)
    ResponseEntity<ResponseDtout> cancelOrderByStudent(@RequestParam("orderUuid")String  orderUuid) throws OrderNotFoundException, InvalidOrderException, UnableToCancelOrderException, UserNotFoundException, MessagingException;


}
