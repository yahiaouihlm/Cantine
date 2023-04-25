package fr.sqli.Cantine.service.admin.menus;

import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.dto.out.MenuDtout;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;

import java.io.IOException;
import java.util.List;

public interface IMenuService {



  public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException;

  public List<MenuDtout> getAllMenus();

}
