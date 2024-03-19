package fr.sqli.cantine.service.food;

import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.food.exceptions.*;
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


  public MenuEntity updateMenu(MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException, UnavailableFoodException;


  public MenuEntity removeMenu(String menuUuid) throws ImagePathException, InvalidFoodInformationException, FoodNotFoundException, RemoveFoodException;


  public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UnavailableFoodException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException;


  public MenuDtOut getMenuByUuId(String menuUuid) throws InvalidFoodInformationException, FoodNotFoundException;


  List<String> searchLablesOfMenuContainsTerm(String label);

   List<MenuDtOut> getAvailableMenu();
   List<MenuDtOut> getAllMenus();

    List<MenuDtOut> getMenusInDeletionProcess();

    List<MenuDtOut> getUnavailableMenus();

  public Optional<MenuEntity> getMenuWithLabelAndDescriptionAndPrice(String label, String description, BigDecimal price) ;




}
