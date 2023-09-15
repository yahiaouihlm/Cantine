package fr.sqli.cantine.service.order;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
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

public interface IOrderService {




     void   addOrder  (OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, InvalidMealInformationException, StudentNotFoundException, MealNotFoundException, MenuNotFoundException, TaxNotFoundException, InsufficientBalanceException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException, MessagingException;

      void cancelOrder  (Integer orderId ) throws InvalidOrderException, OrderNotFoundException, UnableToCancelOrderException, StudentNotFoundException;
}
