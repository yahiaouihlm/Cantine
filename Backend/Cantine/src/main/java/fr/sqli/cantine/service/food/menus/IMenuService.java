package fr.sqli.cantine.service.food.menus;

import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;

import fr.sqli.cantine.service.food.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.food.menus.exceptions.ExistingMenuException;
import fr.sqli.cantine.service.food.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.food.menus.exceptions.MenuNotFoundException;
import fr.sqli.cantine.service.food.menus.exceptions.UnavailableFoodException;
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

  static void checkMenuUuidValidity(String uuid ) throws InvalidFoodInformationException {
     if (uuid == null || uuid.isEmpty() || uuid.isBlank() ||  uuid.length() < 20 ) {
       IMenuService.LOG.error("THE MENU UUID CAN NOT BE NULL OR EMPTY OR LESS THAN 20 CHARACTERS ");
       throw new InvalidFoodInformationException("INVALID MENU UUID");
     }
  }


  public MenuEntity updateMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, MenuNotFoundException, ExistingMenuException, InvalidFoodInformationException;


  public MenuEntity removeMenu(String menuUuid) throws MenuNotFoundException, InvalidMenuInformationException, ImagePathException, InvalidFoodInformationException;


  public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, UnavailableFoodException, InvalidFoodInformationException;


  public MenuDtOut getMenuByUuId(String menuUuid) throws MenuNotFoundException, InvalidFoodInformationException;

  public List<MenuDtOut> getAllMenus();



  public Optional<MenuEntity> getMenuWithLabelAndDescriptionAndPrice(String label, String description, BigDecimal price) throws ExistingMenuException ;




}
