package fr.sqli.Cantine.service.admin.menus;

import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;

public interface IMenuService {



  public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundAdminException;



}
