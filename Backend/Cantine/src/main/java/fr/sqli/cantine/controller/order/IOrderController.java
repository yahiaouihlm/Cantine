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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IOrderController {

    String  BASIC_ORDER_URL =  "cantine/order";

    String ADMIN_GET_ALL_ORDERS_BY_DAY = "admin/getAllOrdersOfDay";

    String  ADMIN_SUBMIT_ORDER  =  "admin/submitOrder";
    String GET_ORDER_BY_DATE_AND_STUDENT_ID_URL = "student/getByDateAndStudentId";
    String CANCEL_ORDER_URL = "student/cancel";
    String ADD_ORDER_URL = "student/add";

    String  ORDER_SUBMITTED_SUCCESSFULLY = "ORDER  SUBMITTED SUCCESSFULLY" ;
    String ORDER_ADDED_SUCCESSFULLY = "ORDER ADDED SUCCESSFULLY";
    String ORDER_CANCELLED_SUCCESSFULLY = "ORDER CANCELLED SUCCESSFULLY";


    @PostMapping(ADMIN_SUBMIT_ORDER)
    ResponseEntity<ResponseDtout> submitOrder (@RequestParam("orderId") Integer orderId) throws OrderNotFoundException, InvalidOrderException, MessagingException, CancelledOrderException;


    @GetMapping(GET_ORDER_BY_DATE_AND_STUDENT_ID_URL)
    ResponseEntity<List<OrderDtOut>> getOrdersByDateAndStudentId(@RequestParam("studentId") Integer idStudent , @RequestParam("date") LocalDate date) throws OrderNotFoundException, InvalidOrderException, InvalidUserInformationException, UserNotFoundException;
    @PostMapping(ADD_ORDER_URL)
    ResponseEntity <ResponseDtout>addOrder(OrderDtoIn orderDtoIn) throws InvalidUserInformationException, TaxNotFoundException, InsufficientBalanceException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException, MessagingException, InvalidFoodInformationException, FoodNotFoundException, UserNotFoundException;

    @GetMapping(ADMIN_GET_ALL_ORDERS_BY_DAY)
    ResponseEntity<List<OrderDtOut>> getOrdersByDate(@RequestParam("date") LocalDate date) throws InvalidUserInformationException, InvalidOrderException;

    @PostMapping(CANCEL_ORDER_URL)
    ResponseEntity<String> cancelOrder( Integer orderId) throws OrderNotFoundException, InvalidOrderException, UnableToCancelOrderException, UserNotFoundException;


}
