package fr.sqli.Cantine.service.order;

import com.google.zxing.WriterException;
import fr.sqli.Cantine.dto.in.food.OrderDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.order.exception.InsufficientBalanceException;
import fr.sqli.Cantine.service.order.exception.InvalidOrderException;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.Cantine.service.superAdmin.exception.TaxNotFoundException;

import java.io.IOException;

public interface IOrderService {




    public  void   addOrder  (OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, InvalidMealInformationException, StudentNotFoundException, MealNotFoundException, MenuNotFoundException, TaxNotFoundException, InsufficientBalanceException, IOException, WriterException, InvalidOrderException;
}
