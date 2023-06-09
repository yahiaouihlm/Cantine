package fr.sqli.Cantine.service.admin.meals;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.food.MealDtoIn;
import fr.sqli.Cantine.dto.out.food.MealDtout;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.ExistingMealException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.meals.exceptions.RemoveMealAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.IImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
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
    private final String MEALS_IMAGES_PATH;
    private final IImageService imageService;


    @Autowired
    public MealService(Environment env, IMealDao mealDao, IImageService imageService) {
        this.mealDao = mealDao;
        this.imageService = imageService;
        this.MEALS_IMAGES_URL = env.getProperty("sqli.cantine.images.url.meals");
        this.MEALS_IMAGES_PATH = env.getProperty("sqli.cantine.images.meals.path");
    }


    @Override
    public MealEntity updateMeal(MealDtoIn mealDtoIn) throws InvalidMealInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException {

        if (mealDtoIn == null) {
            MealService.LOG.debug("THE MEAL DTO CAN NOT BE NULL IN THE updateMeal METHOD ");
            throw new InvalidMealInformationException("THE MEAL DTO CAN NOT BE NULL");
        }

        IMealService.verifyMealInformation("THE ID  CAN NOT BE NULL OR LESS THAN 0", mealDtoIn.getId());

        MealEntity mealEntity = mealDtoIn.toMealEntityWithoutImage();

        var overemotional = this.mealDao.findById(mealDtoIn.getId());
        if (overemotional.isEmpty()) {

            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN ID = {} IN THE updateMeal METHOD ", mealDtoIn.getId());
            throw new MealNotFoundException(" LE PLAT :  " + mealDtoIn.getLabel() + " N'EXISTE PAS DANS LA BASE DE DONNEES ");
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
        if (mealEntity1.isPresent()) {
            if (mealEntity1.get().getId() != meal.getId()) { // if the  meal  is  already  present  in  the  database and  the  id  are   different  from  the  id  of  the  meal  we  want  to  update  we  throw  an  exception
                throw new ExistingMealException(" LE PLAT :  " + meal.getLabel() + " AVEC  " + meal.getCategory()+ " ET " + meal.getDescription() + " EST DEJA PRESENT DANS LA BASE DE DONNEES ");
            }
        }

        // if  the  image is  not  null  we  update  the  image of  the  meal
        if (mealDtoIn.getImage() != null && !mealDtoIn.getImage().isEmpty()) {
            var oldImageName = meal.getImage().getImagename();
            var newImageName = this.imageService.updateImage(oldImageName, mealDtoIn.getImage(), MEALS_IMAGES_PATH);

            meal.getImage().setImagename(newImageName);

        }
        return this.mealDao.save(meal);
    }

    @Override
    public MealEntity removeMeal(Integer id) throws InvalidMealInformationException, MealNotFoundException, RemoveMealAdminException, ImagePathException {
        IMealService.verifyMealInformation("THE ID CAN NOT BE NULL OR LESS THAN 0", id);

        var overemotional = this.mealDao.findById(id);
        if (overemotional.isEmpty()) {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN ID = {} IN THE removeMeal METHOD ", id);
            throw new MealNotFoundException("NO MEAL WAS FOUND WITH THIS ID");
        }
        var meal = overemotional.get();
        if  ( meal.getMenus() != null   &&   meal.getMenus().size() > 0) // check  that this  meal is  not present in  any menu ( we can not delete a meal in association with a menu)
        {
            MealService.LOG.debug("THE MEAL WITH AN ID = {} IS PRESENT IN A MENU AND CAN NOT BE DELETED ", id);
            throw new RemoveMealAdminException("THE MEAL WITH AN label  = " + meal.getLabel() + " IS PRESENT IN A OTHER  MENU(S) AND CAN NOT BE DELETED");
        }

        if  ( meal.getOrders() != null   && meal.getOrders().size() > 0 ) {
            MealService.LOG.debug("THE MEAL WITH AN ID = {} IS PRESENT IN A ORDER AND CAN NOT BE DELETED ", id);
            throw new RemoveMealAdminException("THE MEAL WITH AN label  = " + meal.getLabel() + " IS PRESENT IN A OTHER  ORDER(S) AND CAN NOT BE DELETED");
        }

        var image = meal.getImage();
        this.imageService.deleteImage(image.getImagename(), MEALS_IMAGES_PATH);

        this.mealDao.delete(meal);
        return meal;
    }

    @Override
    public MealEntity addMeal(MealDtoIn mealDtoIn) throws InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException {
         if (mealDtoIn == null) {
             MealService.LOG.error("THE MEAL CAN NOT BE NULL");
             throw new InvalidMealInformationException("THE MEAL CAN NOT BE NULL");
         }

            MealEntity meal = mealDtoIn.toMealEntity();

        //  check if  the  meal  is  already  present  in  the  database
        if (this.checkExistMeal(meal.getLabel(), meal.getCategory(), meal.getDescription()).isPresent()) {
                throw new ExistingMealException(" LE PLAT :  " + meal.getLabel() + " AVEC  " + meal.getCategory()+ " ET " + meal.getDescription() + " EST DEJA PRESENT DANS LA BASE DE DONNEES");
        }
        MultipartFile image = mealDtoIn.getImage();
        var imageName = this.imageService.uploadImage(image, MEALS_IMAGES_PATH);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(imageName);
        meal.setImage(imageEntity);
        return this.mealDao.save(meal);
    }

    @Override
    public List<MealDtout> getAllMeals() {
        return this.mealDao.findAll().stream().map(meal -> new MealDtout(meal, this.MEALS_IMAGES_URL)).toList();
    }

    @Override
    public MealDtout getMealByID(Integer id) throws InvalidMealInformationException, MealNotFoundException {
        return new MealDtout(getMealEntityByID(id), this.MEALS_IMAGES_URL);
    }

    @Override
    public MealEntity getMealEntityByID(Integer id) throws InvalidMealInformationException, MealNotFoundException {
        IMealService.verifyMealInformation("THE ID CAN NOT BE NULL OR LESS THAN 0", id);

        var meal = this.mealDao.findById(id);

        if (meal.isPresent()) {
            return meal.get();
        }

        MealService.LOG.debug("NO DISH WAS FOUND WITH AN ID = {} IN THE getMealByID METHOD ", id);
        throw new MealNotFoundException("NO MEAL WAS FOUND WITH THIS ID");
    }


    @Override
    public Optional<MealEntity> checkExistMeal(String label, String category, String description) throws ExistingMealException {
        return this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase(label, category, description);

    }


}
