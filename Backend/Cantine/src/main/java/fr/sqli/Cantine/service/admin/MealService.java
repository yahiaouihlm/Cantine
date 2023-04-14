package fr.sqli.Cantine.service.admin;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.dto.out.MealDtout;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.exceptions.ExistingMeal;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.admin.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.exceptions.RemoveMealAdminException;
import fr.sqli.Cantine.service.images.IImageService;
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
    private final IMealDao mealDao;
    private final String MEALS_IMAGES_URL;
    private final IImageService imageService;

    @Autowired
    public MealService(Environment env, IMealDao mealDao, IImageService imageService) {
        this.mealDao = mealDao;
        this.imageService = imageService;
        this.MEALS_IMAGES_URL = env.getProperty("sqli.cantine.images.url.meals");
    }


    @Override
    public MealEntity updateMeal(MealDtoIn mealDtoIn, Integer idMeal) throws InvalidMealInformationAdminException, MealNotFoundAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException {
        IMealService.verifyMealInformation("THE CAN NOT BE NULL OR LESS THAN 0", idMeal);
        MealEntity mealEntity = mealDtoIn.toMealEntityWithoutImage();


        var overemotional = this.mealDao.findById(idMeal);
        if (overemotional.isEmpty()) {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN ID = {} IN THE updateMeal METHOD ", idMeal);
            throw new MealNotFoundAdminException("NO MEAL WAS FOUND WITH THIS ID ");
        }
        var meal = overemotional.get();
        meal.setPrice(mealEntity.getPrice());
        meal.setLabel(mealEntity.getLabel());
        meal.setDescription(mealEntity.getDescription());
        meal.setCategory(mealEntity.getCategory());
        meal.setQuantity(mealEntity.getQuantity());
        meal.setStatus(mealEntity.getStatus());

        // if  the  image is  not  null  we  update  the  image of  the  meal
        if (mealDtoIn.getImage() != null) {
            var oldImageName = meal.getImage().getImagename();
            var newImageName = this.imageService.updateImage(oldImageName, mealDtoIn.getImage(), "images/meals");
            var image = new ImageEntity();
            image.setImagename(newImageName);
            meal.setImage(image);
        }
        return this.mealDao.save(meal);
    }

    @Override
    public MealEntity removeMeal(Integer id) throws InvalidMealInformationAdminException, MealNotFoundAdminException, RemoveMealAdminException, ImagePathException {
        IMealService.verifyMealInformation("THE CAN NOT BE NULL OR LESS THAN 0", id);

        var overemotional = this.mealDao.findById(id);
        if (overemotional.isEmpty()) {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN ID = {} IN THE removeMeal METHOD ", id);
            throw new MealNotFoundAdminException("NO MEAL WAS FOUND WITH THIS ID ");
        }
        var meal = overemotional.get();
       /* if (meal.getMenus().size() > 0) // check  that this  meal is  not present in  any menu ( we can not delete a meal in association with a menu)
        {
            MealService.LOG.debug("THE MEAL WITH AN ID = {} IS PRESENT IN A MENU AND CAN NOT BE DELETED ", id);
            throw new RemoveMealAdminException("THE MEAL WITH AN label  = " + meal.getLabel() + " IS PRESENT IN A OTHER  MENU(S) AND CAN NOT BE DELETED ");
        }*/

        var image = meal.getImage();
        this.imageService.deleteImage(image.getImagename(), "images/meals");

        this.mealDao.delete(meal);
        return meal;
    }

    @Override
    public MealEntity addMeal(MealDtoIn mealDtoIn) throws InvalidMealInformationAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, ExistingMeal {
        MealEntity meal = mealDtoIn.toMealEntity();
        this.checkExistMeal(meal.getLabel(), meal.getCategory(), meal.getDescription());
        MultipartFile image = mealDtoIn.getImage();
        var imagename = this.imageService.uploadImage(image, "images/meals");
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
    public MealDtout getMealByID(Integer id) throws InvalidMealInformationAdminException, MealNotFoundAdminException {

        IMealService.verifyMealInformation("THE CAN NOT BE NULL OR LESS THAN 0", id);

        var meal = this.mealDao.findById(id);
        if (meal.isPresent()) {

            return new MealDtout(meal.get(), this.MEALS_IMAGES_URL);
        }

        MealService.LOG.debug("NO DISH WAS FOUND WITH AN ID = {} IN THE getMealByID METHOD ", id);
        throw new MealNotFoundAdminException("NO MEAL WAS FOUND WITH THIS ID ");
    }


    @Override
    public void checkExistMeal(String label, String category, String description) throws ExistingMeal {
         var result = this.mealDao.existsByLabelAndAndCategoryAndDescription(label,  category , description );
        if (result) {
            throw new ExistingMeal("THE MEAL WITH THE LABEL :  " + label + " AND THE DESCRIPTION : " + description + " AND THE CATEGORY : " + category + " ALREADY EXISTS");
        }

    }

}
