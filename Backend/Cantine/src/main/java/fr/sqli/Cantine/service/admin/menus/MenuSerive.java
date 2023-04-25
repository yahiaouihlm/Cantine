package fr.sqli.Cantine.service.admin.menus;


import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuSerive implements  IMenuService {

    MealService mealService;
    @Autowired
    public  MenuSerive(MealService mealService) {
        this.mealService = mealService;
    }

    @Override
    public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundAdminException {
          var menuEntity  =  menuDtoIn.toMenuEntity();

          if  (menuDtoIn.getMealIDs()== null || menuDtoIn.getMealIDs().size() == 0 ||menuDtoIn.getMealIDs().isEmpty()) {
              throw new InvalidMenuInformationException("The menu doesn't contain any meal");
          }

          for  (Integer mealID : menuDtoIn.getMealIDs()) {
                    this.mealService.getMealByID(mealID);
          }



        return  null ;
    }


}
