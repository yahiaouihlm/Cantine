package fr.sqli.cantine.service.food.meals;

import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.dto.out.food.MealDtout;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.meals.exceptions.ExistingMealException;

import fr.sqli.cantine.service.food.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.food.meals.exceptions.RemoveMealAdminException;
import fr.sqli.cantine.service.food.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.images.IImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
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
    public MealEntity updateMeal(MealDtoIn mealDtoIn) throws MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidFoodInformationException {

        if (mealDtoIn == null) {
            MealService.LOG.debug("THE MEAL DTO CAN NOT BE NULL IN THE updateMeal METHOD ");
            throw new InvalidFoodInformationException("THE MEAL CAN NOT BE NULL");
        }

         IMealService.checkUuidValidity(mealDtoIn.getUuid());

          mealDtoIn.toMealEntityWithoutImage();

        var overemotional = this.mealDao.findByUuid(mealDtoIn.getUuid());
        if (overemotional.isEmpty()) {

            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN ID = {} IN THE updateMeal METHOD ", mealDtoIn.getUuid());
            throw new MealNotFoundException(" LE PLAT :  " + mealDtoIn.getLabel() + " N'EXISTE PAS DANS LA BASE DE DONNEES ");
        }
        var meal = overemotional.get(); // change the  meal  with  the  new  values
        meal.setPrice(mealDtoIn.getPrice());
        meal.setLabel(mealDtoIn.getLabel());
        meal.setDescription(mealDtoIn.getDescription());
        meal.setCategory(mealDtoIn.getCategory());
        meal.setQuantity(mealDtoIn.getQuantity());
        meal.setStatus(mealDtoIn.getStatus());

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
    public MealEntity removeMeal(String uuid) throws  MealNotFoundException, RemoveMealAdminException, ImagePathException, InvalidFoodInformationException {
        IMealService.checkUuidValidity(uuid);

        var overemotional = this.mealDao.findByUuid(uuid);
        if (overemotional.isEmpty()) {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN UUID = {} IN THE removeMeal METHOD ", uuid);
            throw new MealNotFoundException("NO MEAL WAS FOUND WITH THIS ID");
        }
        var meal = overemotional.get();
        if  ( meal.getMenus() != null   &&   meal.getMenus().size() > 0) // check  that this  meal is  not present in  any menu ( we can not delete a meal in association with a menu)
        {
            MealService.LOG.debug("THE MEAL WITH AN UUID = {} IS PRESENT IN an  OTHER MENU(S) AND CAN NOT BE DELETED ", uuid);
            throw new RemoveMealAdminException(" Le  Plat  \" " + meal.getLabel() + " \"  Ne  Pas Etre  Supprimé  Car  Il  Est  Present  Dans  d'autres  Menu(s)");
        }

        if  ( meal.getOrders() != null   && meal.getOrders().size() > 0 ) {
            MealService.LOG.debug("THE MEAL WITH AN UUID = {} IS PRESENT IN A ORDER AND CAN NOT BE DELETED ", uuid);
            throw new RemoveMealAdminException("Le  Plat  \" " + meal.getLabel() + " \"  Ne  Pas Etre  Supprimé  Car  Il  Est  Present  Dans  d'autres   Commande(s)");
        }

        var image = meal.getImage();
        this.imageService.deleteImage(image.getImagename(), MEALS_IMAGES_PATH);

        this.mealDao.delete(meal);
        return meal;
    }

    @Override
    public MealEntity addMeal(MealDtoIn mealDtoIn) throws  InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException, InvalidFoodInformationException {
        if (mealDtoIn == null) {
            MealService.LOG.error("THE MEAL CAN NOT BE NULL");
            throw new InvalidFoodInformationException("THE MEAL CAN NOT BE NULL");
        }

          mealDtoIn.checkMealInformation();

        //  check if  the  meal  is  already  present  in  the  database
        if (this.checkExistMeal(mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription()).isPresent()) {
            throw new ExistingMealException(" LE PLAT :  " + mealDtoIn.getLabel().trim() + " AVEC  " + mealDtoIn.getCategory().trim() + " ET " + mealDtoIn.getDescription().trim() + " EST DEJA PRESENT DANS LA BASE DE DONNEES");
        }
        MultipartFile image = mealDtoIn.getImage();
        var imageName = this.imageService.uploadImage(image, MEALS_IMAGES_PATH);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(imageName);

        MealEntity  meal =  new  MealEntity(mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription(), mealDtoIn.getPrice(), mealDtoIn.getQuantity(), mealDtoIn.getStatus(), imageEntity);


        return this.mealDao.save(meal);
    }

    @Override
    public List<MealDtout> getAllMeals() {
        return this.mealDao.findAll().stream().map(meal -> new MealDtout(meal, this.MEALS_IMAGES_URL)).toList();
    }

    @Override
    public MealDtout getMealByUUID(String uuid) throws MealNotFoundException, InvalidFoodInformationException {
        return new MealDtout(getMealEntityByUUID(uuid), this.MEALS_IMAGES_URL);
    }

    @Override
    public MealEntity getMealEntityByUUID(String uuid) throws MealNotFoundException, InvalidFoodInformationException {
        IMealService.checkUuidValidity(uuid);

        return this.mealDao.findByUuid(uuid).orElseThrow(() -> {
            MealService.LOG.debug("NO DISH WAS FOUND WITH AN UUID = {} IN THE getMealByID METHOD", uuid);
            return new MealNotFoundException("NO MEAL WAS FOUND WITH THIS ID");
        });
    }


    @Override
    public Optional<MealEntity> checkExistMeal(String label, String category, String description) {
        return this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase(label, category, description);

    }


}
