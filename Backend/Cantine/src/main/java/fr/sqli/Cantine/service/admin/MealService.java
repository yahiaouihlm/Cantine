package fr.sqli.Cantine.service.admin;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.out.MealDtout;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.admin.exceptions.MealNotFoundAdminException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


@Service
public class MealService {
    private static final Logger LOG = LogManager.getLogger();

    private final  IMealDao mealDao ;

    private final Environment env ;
    @Autowired
    public  MealService(Environment env, IMealDao mealDao  ){
        this.mealDao = mealDao;
        this.env =env;
    }


    public MealDtout getMealByID (Integer id) throws InvalidMealInformationAdminException, MealNotFoundAdminException {
        if (id == null || id < 0) {
            MealService.LOG.debug("THE MEAL ID IS INVALID.IT CANNOT BE NULL OR LESS THAN 0 ,  IN THE getMealByID METHOD ");
            throw  new InvalidMealInformationAdminException("INVALID MEAL ID ");
        }

        var  meal =  this.mealDao.findById(id);
        if (meal.isPresent()) {
            return  new MealDtout(meal.get(), env.getProperty("sqli.cantine.images.url.meals") );
        }

        MealService.LOG.debug("NO DISH WAS FOUND WITH AN ID = {} IN THE getMealByID METHOD ", id);
        throw new MealNotFoundAdminException("NO DISH WAS FOUND WITH THIS ID ");
    }

}
