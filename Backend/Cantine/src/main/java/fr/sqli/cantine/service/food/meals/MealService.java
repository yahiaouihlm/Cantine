package fr.sqli.cantine.service.food.meals;

import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.dto.out.food.MealDtout;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.meals.exceptions.ExistingMealException;

import fr.sqli.cantine.service.food.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.food.meals.exceptions.RemoveMealException;
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
            MealService.LOG.debug("THE MEAL_DTO_IN CAN NOT BE NULL IN THE updateMeal METHOD ");
            throw new InvalidFoodInformationException("THE MEAL CAN NOT BE NULL");
        }

        IMealService.checkUuidValidity(mealDtoIn.getUuid());

        mealDtoIn.toMealEntityWithoutImage();

        var meal = this.mealDao.findByUuid(mealDtoIn.getUuid()).orElseThrow(() -> {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN ID = {} IN THE updateMeal METHOD ", mealDtoIn.getUuid());
            return new MealNotFoundException("NO MEAL WAS FOUND WITH THIS ID");
        });


        meal.setLabel(mealDtoIn.getLabel().trim());
        meal.setCategory(mealDtoIn.getCategory().trim());
        meal.setDescription(mealDtoIn.getDescription().trim());
        meal.setPrice(mealDtoIn.getPrice());
        meal.setQuantity(mealDtoIn.getQuantity());
        meal.setStatus(mealDtoIn.getStatus());

        //check  if the  meal  is  already  present  in  the  database despite  the  update
        Optional<MealEntity> mealEntity = this.getMealWithLabelAndCategoryAndDescription(meal.getLabel(), meal.getCategory(), meal.getDescription());
        // if  we  find another  meal with  different  uuid  tha mean the  updated  meal  is  already  present  in  the  database we have to  throw  an  ExistingMealException
        if (mealEntity.isPresent()) {
            if (!mealEntity.get().getUuid().equals(meal.getUuid())) {
                MealService.LOG.debug("THE MEAL WITH A LABEL = {} AND A CATEGORY = {} AND A DESCRIPTION = {} IS ALREADY PRESENT IN THE DATABASE", mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription());
                throw new ExistingMealException("THE MEAL WITH A LABEL = " + mealDtoIn.getLabel() + " AND A CATEGORY = " + mealDtoIn.getCategory() + " AND A DESCRIPTION = " + mealDtoIn.getDescription() + " IS ALREADY EXIST");
            }
        }

        // if  the  image is  not  null  we  update  the  image of  the  meal
        if (mealDtoIn.getImage() != null && !mealDtoIn.getImage().isEmpty() && mealDtoIn.getImage().getSize() > 0) {
            var oldImageName = meal.getImage().getImagename();
            var newImageName = this.imageService.updateImage(oldImageName, mealDtoIn.getImage(), MEALS_IMAGES_PATH);

            meal.getImage().setImagename(newImageName);

        }
        return this.mealDao.save(meal);
    }

    @Override
    public MealEntity removeMeal(String uuid) throws MealNotFoundException, RemoveMealException, ImagePathException, InvalidFoodInformationException {
        IMealService.checkUuidValidity(uuid);

        var overemotional = this.mealDao.findByUuid(uuid);
        if (overemotional.isEmpty()) {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN UUID = {} IN THE removeMeal METHOD ", uuid);
            throw new MealNotFoundException("NO MEAL WAS FOUND WITH THIS ID");
        }
        var meal = overemotional.get();
        if (meal.getMenus() != null && meal.getMenus().size() > 0) // check  that this  meal is  not present in  any menu ( we can not delete a meal in association with a menu)
        {
            MealService.LOG.debug("THE MEAL WITH AN UUID = {} IS PRESENT IN an  OTHER MENU(S) AND CAN NOT BE DELETED ", uuid);
            throw new RemoveMealException(" Le  Plat  \" " + meal.getLabel() + " \"  Ne  Pas Etre  Supprimé  Car  Il  Est  Present  Dans  d'autres  Menu(s)");
        }

        if (meal.getOrders() != null && meal.getOrders().size() > 0) {
            MealService.LOG.debug("THE MEAL WITH AN UUID = {} IS PRESENT IN A ORDER AND CAN NOT BE DELETED ", uuid);
            throw new RemoveMealException("Le  Plat  \" " + meal.getLabel() + " \"  Ne  Pas Etre  Supprimé  Car  Il  Est  Present  Dans  d'autres   Commande(s)");
        }

        var image = meal.getImage();
        this.imageService.deleteImage(image.getImagename(), MEALS_IMAGES_PATH);

        this.mealDao.delete(meal);
        return meal;
    }

    @Override
    public MealEntity addMeal(MealDtoIn mealDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidFoodInformationException {

        if (mealDtoIn == null) {
            MealService.LOG.error("THE MEAL_DTO_IN CAN NOT BE NULL");
            throw new InvalidFoodInformationException("THE MEAL CAN NOT BE NULL");
        }

        mealDtoIn.checkMealInformation();

        //  check if  the  meal  is  already  present  in  the  database
        if (this.getMealWithLabelAndCategoryAndDescription(mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription()).isPresent()) {
            MealService.LOG.debug("THE MEAL WITH A LABEL = {} AND A CATEGORY = {} AND A DESCRIPTION = {} IS ALREADY PRESENT IN THE DATABASE", mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription());
            throw new ExistingMealException("THE MEAL WITH A LABEL = " + mealDtoIn.getLabel() + " AND A CATEGORY = " + mealDtoIn.getCategory() + " AND A DESCRIPTION = " + mealDtoIn.getDescription() + " IS ALREADY EXIST");
        }

        MultipartFile image = mealDtoIn.getImage();
        var imageName = this.imageService.uploadImage(image, MEALS_IMAGES_PATH);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(imageName);

        MealEntity meal = new MealEntity(mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription(), mealDtoIn.getPrice(), mealDtoIn.getQuantity(), mealDtoIn.getStatus(), imageEntity);


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
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN UUID = {} IN THE getMealEntityByUUID METHOD", uuid);
            return new MealNotFoundException("NO MEAL WAS FOUND");
        });
    }


    @Override
    public Optional<MealEntity> getMealWithLabelAndCategoryAndDescription(String label, String category, String description) {
        return this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase(label, category, description);

    }


}
