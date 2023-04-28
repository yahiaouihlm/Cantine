package fr.sqli.Cantine.service.admin.menus;

import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.dto.out.MenuDtout;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface IMenuService {

  /**
   * Verify if the meal information is valid or not
   * @param messageException the message of the exception to throw
   * @param args the meal information to verify
   * @throws InvalidMenuInformationException the exception to throw if the meal information is invalid
   */
  static void verifyMealInformation(String messageException, Object... args) throws InvalidMenuInformationException {
    for (Object arg : args) {
      if (arg == null || (arg instanceof String && ((String) arg).isEmpty()))
        throw new InvalidMenuInformationException(messageException);
      if (arg == null || (arg instanceof Integer && (Integer) arg < 0))
        throw new InvalidMenuInformationException(messageException);

    }
  }



  public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException;


  public MenuDtout getMenuById(Integer menuID) throws MealNotFoundAdminException, InvalidMenuInformationException;

  public List<MenuDtout> getAllMenus();



  public void checkExistingMenu(String label, String description, BigDecimal price) throws ExistingMenuException ;


}
