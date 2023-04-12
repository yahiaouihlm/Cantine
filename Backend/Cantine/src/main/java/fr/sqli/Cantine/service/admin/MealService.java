package fr.sqli.Cantine.service.admin;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.dto.out.MealDtout;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.admin.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.images.IImageService;
import fr.sqli.Cantine.service.images.ImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class MealService implements IMealService {
    private static final Logger LOG = LogManager.getLogger();
    private   IImageService imageService;
    private final  IMealDao mealDao ;

    private final String   MEALS_IMAGES_URL;
    @Autowired
    public  MealService(Environment env, IMealDao mealDao , IImageService imageService  ){
        this.mealDao = mealDao;
        this.imageService = imageService;
        this.MEALS_IMAGES_URL = env.getProperty("sqli.cantine.images.url.meals");
    }


    @Override
    public MealEntity addMeal(MealDtoIn mealDtoIn) throws InvalidMealInformationAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException {
       MealEntity meal =  mealDtoIn.toMealEntity();
        MultipartFile image = mealDtoIn.getImage();
        var imagename  =  this.imageService.uploadImage(image , "images/meals");
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(imagename);
        meal.setImage(imageEntity);
        return this.mealDao.save(meal);
    }

    @Override
    public List<MealDtout> getAllMeals() {
         return this.mealDao.findAll().stream().map(meal -> new MealDtout(meal, this.MEALS_IMAGES_URL)).toList();
    }

    @Override
    public MealDtout getMealByID (Integer id) throws InvalidMealInformationAdminException, MealNotFoundAdminException {

        this.verifyMealInformation( "THE CAN NOT BE NULL OR LESS THAN 0" , id);

            var  meal =  this.mealDao.findById(id);
            if (meal.isPresent()) {

                return  new MealDtout(meal.get(), this.MEALS_IMAGES_URL);
            }

            MealService.LOG.debug("NO DISH WAS FOUND WITH AN ID = {} IN THE getMealByID METHOD ", id);
        throw new MealNotFoundAdminException("NO MEAL WAS FOUND WITH THIS ID ");
    }



}
