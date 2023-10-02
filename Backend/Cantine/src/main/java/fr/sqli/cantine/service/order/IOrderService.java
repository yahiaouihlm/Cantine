package fr.sqli.cantine.service.order;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.dto.out.food.OrderDtout;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.cantine.service.order.exception.*;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IOrderService {





     void  submitOrder (Integer  orderId) throws InvalidOrderException, OrderNotFoundException, CancelledOrderException, MessagingException;
     void   addOrder  (OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, InvalidMealInformationException, StudentNotFoundException, MealNotFoundException, MenuNotFoundException, TaxNotFoundException, InsufficientBalanceException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException, MessagingException;

      void cancelOrder  (Integer orderId ) throws InvalidOrderException, OrderNotFoundException, UnableToCancelOrderException, StudentNotFoundException;


      List<OrderDtout> getOrdersByDate(LocalDate date ) throws InvalidOrderException, InvalidPersonInformationException;
      List<OrderDtout> getOrdersByDateAndStudentId(Integer studentId , LocalDate date ) throws InvalidOrderException, OrderNotFoundException, StudentNotFoundException, InvalidPersonInformationException;



}
