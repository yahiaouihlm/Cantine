package fr.sqli.cantine.service.order;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.dto.out.food.OrderDtOut;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.order.exception.*;
import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IOrderService {


    void submitOrder(String orderUuid) throws InvalidOrderException, OrderNotFoundException, CancelledOrderException, MessagingException, IOException, WriterException;

    void addOrderByStudent(OrderDtoIn orderDtoIn) throws InvalidUserInformationException, TaxNotFoundException, InsufficientBalanceException, IOException, WriterException, InvalidOrderException, UnavailableFoodForOrderException, OrderLimitExceededException, MessagingException, InvalidFoodInformationException, FoodNotFoundException, UserNotFoundException;

    void cancelOrderByAdmin(String orderUuid) throws OrderNotFoundException, InvalidOrderException, MessagingException, CancelledOrderException, InvalidUserInformationException, UserNotFoundException;

    void cancelOrderByStudent(String orderUuid) throws InvalidOrderException, OrderNotFoundException, UnableToCancelOrderException, UserNotFoundException, MessagingException;

    List<OrderDtOut> getStudentOrder(String studentUuid) throws UserNotFoundException;

    List<OrderDtOut> getOrdersByDate(LocalDate date) throws InvalidOrderException, InvalidUserInformationException;

    List<OrderDtOut> getOrdersByDateAndStudentId(String studentUuid, LocalDate date) throws InvalidOrderException, OrderNotFoundException, InvalidUserInformationException, UserNotFoundException;


}
