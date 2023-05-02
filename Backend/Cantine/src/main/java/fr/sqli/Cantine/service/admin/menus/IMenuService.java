package fr.sqli.Cantine.service.admin.menus;

import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.dto.out.MenuDtout;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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


  public MenuEntity updateMenu(MenuDtoIn menuDtoIn ,  Integer idMenu ) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, MenuNotFoundException;


  public MenuEntity removeMenu(Integer menuID) throws MenuNotFoundException, InvalidMenuInformationException, ImagePathException;


  public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException;


  public MenuDtout getMenuById(Integer menuID) throws MealNotFoundAdminException, InvalidMenuInformationException;

  public List<MenuDtout> getAllMenus();



  public Optional<MenuEntity> checkExistingMenu(String label, String description, BigDecimal price) throws ExistingMenuException ;


}
