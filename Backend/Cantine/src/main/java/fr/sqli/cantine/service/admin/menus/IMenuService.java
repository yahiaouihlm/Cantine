package fr.sqli.cantine.service.admin.menus;

import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.food.MenuDtout;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.cantine.service.admin.menus.exceptions.UnavailableMealException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IMenuService {
       static final Logger LOG = LogManager.getLogger();
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


  public MenuEntity updateMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, MenuNotFoundException, ExistingMenuException;


  public MenuEntity removeMenu(Integer menuID) throws MenuNotFoundException, InvalidMenuInformationException, ImagePathException;


  public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, UnavailableMealException;


  public MenuDtout getMenuById(Integer menuID) throws MealNotFoundException, InvalidMenuInformationException;

  public List<MenuDtout> getAllMenus();



  public Optional<MenuEntity> checkExistingMenu(String label, String description, BigDecimal price) throws ExistingMenuException ;


  static  void  ValidateMealID ( MenuDtoIn menuDtoIn) throws InvalidMenuInformationException {
    if (menuDtoIn.getMealIDs() == null || menuDtoIn.getMealIDs().isEmpty() || menuDtoIn.getMealIDs().size() == 0  ) {
        LOG.error("The menu doesn't contain any meal");
      throw new InvalidMenuInformationException("THE MENU DOESN'T CONTAIN ANY MEAL");
    }

  }

}