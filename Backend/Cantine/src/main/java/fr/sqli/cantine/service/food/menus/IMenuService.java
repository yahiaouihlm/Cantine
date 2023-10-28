package fr.sqli.cantine.service.food.menus;

import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.food.MenuDtout;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;

import fr.sqli.cantine.service.food.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.food.menus.exceptions.ExistingMenuException;
import fr.sqli.cantine.service.food.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.food.menus.exceptions.MenuNotFoundException;
import fr.sqli.cantine.service.food.menus.exceptions.UnavailableMealException;
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

   * @throws InvalidMenuInformationException the exception to throw if the meal information is invalid
   */
  static void checkMenuUuidValidity(String uuid ) throws InvalidMenuInformationException {
     if (uuid == null || uuid.isEmpty() || uuid.isBlank()) {
       IMenuService.LOG.error("THE MEAL UUID CAN NOT BE NULL OR EMPTY");
       throw new InvalidMenuInformationException("THE MEAL UUID CAN NOT BE NULL OR EMPTY");
     }
  }


  public MenuEntity updateMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, MenuNotFoundException, ExistingMenuException, InvalidFoodInformationException;


  public MenuEntity removeMenu(String menuUuid) throws MenuNotFoundException, InvalidMenuInformationException, ImagePathException;


  public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, UnavailableMealException, InvalidFoodInformationException;


  public MenuDtout getMenuById(String menuUuid) throws MealNotFoundException, InvalidMenuInformationException;

  public List<MenuDtout> getAllMenus();



  public Optional<MenuEntity> checkExistingMenu(String label, String description, BigDecimal price) throws ExistingMenuException ;




}
