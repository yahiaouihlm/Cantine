package fr.sqli.Cantine.service.admin.meals;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.dto.out.MealDtout;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.ExistingMeal;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.meals.exceptions.RemoveMealAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
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
import java.util.Optional;


@Service
public class MealService implements IMealService {
    private static final Logger LOG = LogManager.getLogger();
    private final IMealDao mealDao;
    private final String MEALS_IMAGES_URL;
    private  final String  MEALS_IMAGES_PATH ;
    private final IImageService imageService;



    @Autowired
    public MealService(Environment env, IMealDao mealDao, IImageService imageService) {
        this.mealDao = mealDao;
        this.imageService = imageService;
        this.MEALS_IMAGES_URL = env.getProperty("sqli.cantine.images.url.meals");
        this.MEALS_IMAGES_PATH = env.getProperty("sqli.cantine.images.meals.path");
    }


    @Override
    public MealEntity updateMeal(MealDtoIn mealDtoIn, Integer idMeal) throws InvalidMealInformationException, MealNotFoundAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, ExistingMeal, InvalidMenuInformationException {
        IMealService.verifyMealInformation("THE ID  CAN NOT BE NULL OR LESS THAN 0", idMeal);
        MealEntity mealEntity = mealDtoIn.toMealEntityWithoutImage();

        var overemotional = this.mealDao.findById(idMeal);
        if (overemotional.isEmpty()) {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN ID = {} IN THE updateMeal METHOD ", idMeal);
            throw new MealNotFoundAdminException("NO MEAL WAS FOUND WITH THIS ID");
        }
        var meal = overemotional.get(); // change the  meal  with  the  new  values
        meal.setPrice(mealEntity.getPrice());
        meal.setLabel(mealEntity.getLabel());
        meal.setDescription(mealEntity.getDescription());
        meal.setCategory(mealEntity.getCategory());
        meal.setQuantity(mealEntity.getQuantity());
        meal.setStatus(mealEntity.getStatus());

        //check  if the  meal  is  already  present  in  the  database despite  the  update
        Optional<MealEntity> mealEntity1 = this.checkExistMeal(meal.getLabel(), meal.getCategory(), meal.getDescription());
        if (mealEntity1.isPresent()){
            if (mealEntity1.get().getId() != meal.getId()) { // if the  meal  is  already  present  in  the  database and  the  id  are   different  from  the  id  of  the  meal  we  want  to  update  we  throw  an  exception
                throw new ExistingMeal("THE MEAL WITH AN LABEL = " + meal.getLabel() + " AND A CATEGORY = " + meal.getCategory() + " AND A DESCRIPTION = " + meal.getDescription() + " IS ALREADY PRESENT IN THE DATABASE ");
            }
        }

        // if  the  image is  not  null  we  update  the  image of  the  meal
        if (mealDtoIn.getImage() != null && !mealDtoIn.getImage().isEmpty()) {
            var oldImageName = meal.getImage().getImagename();
            var newImageName = this.imageService.updateImage(oldImageName, mealDtoIn.getImage(),MEALS_IMAGES_PATH);

            meal.getImage().setImagename(newImageName);

        }
        return this.mealDao.save(meal);
    }

    @Override
    public MealEntity removeMeal(Integer id) throws InvalidMealInformationException, MealNotFoundAdminException, RemoveMealAdminException, ImagePathException {
        IMealService.verifyMealInformation("THE ID CAN NOT BE NULL OR LESS THAN 0", id);

        var overemotional = this.mealDao.findById(id);
        if (overemotional.isEmpty()) {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN ID = {} IN THE removeMeal METHOD ", id);
            throw new MealNotFoundAdminException("NO MEAL WAS FOUND WITH THIS ID");
        }
        var meal = overemotional.get();
        if (meal.getMenus().size() > 0) // check  that this  meal is  not present in  any menu ( we can not delete a meal in association with a menu)
        {
            MealService.LOG.debug("THE MEAL WITH AN ID = {} IS PRESENT IN A MENU AND CAN NOT BE DELETED ", id);
            throw new RemoveMealAdminException("THE MEAL WITH AN label  = " + meal.getLabel() + " IS PRESENT IN A OTHER  MENU(S) AND CAN NOT BE DELETED");
        }

        var image = meal.getImage();
        this.imageService.deleteImage(image.getImagename(), MEALS_IMAGES_PATH);

        this.mealDao.delete(meal);
        return meal;
    }

    @Override
    public MealEntity addMeal(MealDtoIn mealDtoIn) throws InvalidMealInformationException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, ExistingMeal, InvalidMenuInformationException {

        MealEntity meal = mealDtoIn.toMealEntity();

       //  check if  the  meal  is  already  present  in  the  database
        if (this.checkExistMeal(meal.getLabel(), meal.getCategory(), meal.getDescription()).isPresent()) {
            throw new ExistingMeal("THE MEAL WITH AN LABEL = " + meal.getLabel() + " AND A CATEGORY = " + meal.getCategory() + " AND A DESCRIPTION = " + meal.getDescription() + " IS ALREADY PRESENT IN THE DATABASE ");
        }
        MultipartFile image = mealDtoIn.getImage();
        var imagename = this.imageService.uploadImage(image,MEALS_IMAGES_PATH );
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
    public MealDtout getMealByID(Integer id) throws InvalidMealInformationException, MealNotFoundAdminException {

        IMealService.verifyMealInformation("THE CAN NOT BE NULL OR LESS THAN 0", id);

        var meal = this.mealDao.findById(id);

        if (meal.isPresent()) {
            return new MealDtout(meal.get(), this.MEALS_IMAGES_URL);
        }

        MealService.LOG.debug("NO DISH WAS FOUND WITH AN ID = {} IN THE getMealByID METHOD ", id);
        throw new MealNotFoundAdminException("NO MEAL WAS FOUND WITH THIS ID ");
    }



    @Override
    public Optional<MealEntity> checkExistMeal(String label, String category, String description) throws ExistingMeal {
        return this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase(label,  category , description );

    }


}
