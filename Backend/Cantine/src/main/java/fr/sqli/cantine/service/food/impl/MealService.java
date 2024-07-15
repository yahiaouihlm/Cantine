package fr.sqli.cantine.service.food.impl;

import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.dto.out.food.MealDtOut;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MealTypeEnum;
import fr.sqli.cantine.service.food.exceptions.ExistingFoodException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;


import fr.sqli.cantine.service.food.exceptions.RemoveFoodException;
import fr.sqli.cantine.service.food.IMealService;
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
    private final IMenuDao menuDao;
    private final String MEALS_IMAGES_URL;
    private final String MEALS_IMAGES_PATH;
    private final IImageService imageService;

    @Autowired
    public MealService(Environment env, IMealDao mealDao, IImageService imageService ,  IMenuDao menuDao) {
        this.mealDao = mealDao;
        this.imageService = imageService;
        this.menuDao = menuDao;
        this.MEALS_IMAGES_URL = env.getProperty("sqli.cantine.images.url.meals");
        this.MEALS_IMAGES_PATH = env.getProperty("sqli.cantine.images.meals.path");
    }
    @Override
    public MealEntity updateMeal(MealDtoIn mealDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException {

        if (mealDtoIn == null) {
            MealService.LOG.debug("THE MEAL_DTO_IN CAN NOT BE NULL IN THE updateMeal METHOD ");
            throw new InvalidFoodInformationException("THE MEAL CAN NOT BE NULL");
        }
        IMealService.checkMealUuidValidity(mealDtoIn.getId());
        mealDtoIn.checkMealInfoValidityWithoutImage();
        var meal = this.mealDao.findMealById(mealDtoIn.getId()).orElseThrow(() -> {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN ID = {} IN THE updateMeal METHOD ", mealDtoIn.getId());
            return new FoodNotFoundException("NO MEAL WAS FOUND");
        });

        meal.setLabel(mealDtoIn.getLabel().trim());
        meal.setCategory(mealDtoIn.getCategory().trim());
        meal.setDescription(mealDtoIn.getDescription().trim());
        meal.setPrice(mealDtoIn.getPrice());
        meal.setQuantity(mealDtoIn.getQuantity());
        meal.setMealType(mealDtoIn.getMealTypeEnum());

        //check  if the  meal  is  already  present  in  the  database despite  the  update
        Optional<MealEntity> mealEntity = this.getMealWithLabelAndCategoryAndDescription(meal.getLabel(), meal.getCategory(), meal.getDescription());
        // if  we  find another  meal with  different  uuid  tha mean the  updated  meal  is  already  present  in  the  database we have to  throw  an  ExistingMealException
        if (mealEntity.isPresent()) {
            if (!mealEntity.get().getId().equals(meal.getId())) {
                MealService.LOG.debug("THE MEAL WITH A LABEL = {} AND A CATEGORY = {} AND A DESCRIPTION = {} IS ALREADY PRESENT IN THE DATABASE", mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription());
                throw new ExistingFoodException("THE MEAL WITH A LABEL = " + mealDtoIn.getLabel() + " AND A CATEGORY = " + mealDtoIn.getCategory() + " AND A DESCRIPTION = " + mealDtoIn.getDescription() + " IS ALREADY EXIST");
            }
        }
        // if  the  image is  not  null  we  update  the  image of  the  meal
        if (mealDtoIn.getImage() != null && !mealDtoIn.getImage().isEmpty() && mealDtoIn.getImage().getSize() > 0) {
            var oldImageName = meal.getImage().getName();
            var newImageName = this.imageService.updateImage(oldImageName, mealDtoIn.getImage(), MEALS_IMAGES_PATH);
            meal.getImage().setName(newImageName);
        }
        meal.setStatus(mealDtoIn.getStatus());
        if (mealDtoIn.getStatus() == 0) {
            for (var menu  : meal.getMenus()){
                menu.setStatus(0);
                this.menuDao.save(menu);
            }
        }
        return this.mealDao.save(meal);
    }

    @Override
    public MealEntity deleteMeal(String uuid) throws RemoveFoodException, ImagePathException, InvalidFoodInformationException, FoodNotFoundException {

        IMealService.checkMealUuidValidity(uuid);

        var meal = this.mealDao.findMealById(uuid).orElseThrow(() -> {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN UUID = {} IN THE removeMeal METHOD ", uuid);
            return new FoodNotFoundException("NO MEAL WAS FOUND");
        });

        if (meal.getStatus() == 2) {
            MealService.LOG.debug("THE MEAL WITH AN UUID = {} IS IN PROCESS OF DELETION", uuid);
            throw new RemoveFoodException("THE MEAL WITH AN UUID = " + uuid + "IS IN PROCESS OF DELETION");
        }

        // check  if  meal is  not present in  any menu ( we can not delete a meal in association with a menu)
        if (meal.getMenus() != null && !meal.getMenus().isEmpty()) {
            MealService.LOG.debug("THE MEAL WITH AN UUID = {} IS PRESENT IN an  OTHER MENU(S) AND CAN NOT BE DELETED ", uuid);

            // make  the  status 2  it's mean  that the  meal  it  will  be removed  by  batch  traitement
            meal.setStatus(2);
            this.mealDao.save(meal);

         // make all  menu  have the  status 0  it's mean  that the  meal  it  will  be removed  by  batch  traitement
            for (var menu  : meal.getMenus()){
                menu.setStatus(0);
                this.menuDao.save(menu);
            }

            throw new RemoveFoodException("THE MEAL CAN NOT BE DELETED BECAUSE IT IS PRESENT IN AN OTHER MENU(S) PS -> THE  MEAL WILL  BE  AUTOMATICALLY  REMOVED IN  BATCH  TRAITEMENT");

        }

        // check  if  meal is  not present in  any order ( we can not delete a meal in association with an order)
        if (meal.getOrders() != null && !meal.getOrders().isEmpty()) {
            MealService.LOG.debug("THE MENU WITH AN UUID = {} IS PRESENT IN A ORDER AND CAN NOT BE DELETED ", uuid);

            // make  the  status 2  it's mean  that the  meal  it  will  be removed  by  batch  traitement
            meal.setStatus(2);
            this.mealDao.save(meal);

            throw new RemoveFoodException("THE MENU CAN NOT BE DELETED BECAUSE IT IS PRESENT IN AN ORDER(S)"
            +"PS -> THE  MEAL WILL  BE  AUTOMATICALLY  REMOVED IN  BATCH  TRAITEMENT");
        }


        var image = meal.getImage();
        this.imageService.deleteImage(image.getName(), MEALS_IMAGES_PATH);

        this.mealDao.delete(meal);
        return meal;
    }

    @Override
    public MealEntity addMeal(MealDtoIn mealDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException {

        if (mealDtoIn == null) {
            MealService.LOG.error("THE MEAL_DTO_IN CAN NOT BE NULL");
            throw new InvalidFoodInformationException("THE MEAL CAN NOT BE NULL");
        }

        mealDtoIn.checkMealInformation();

        //  check if  the  meal  is  already  present  in  the  database
        if (this.getMealWithLabelAndCategoryAndDescription(mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription()).isPresent()) {
            MealService.LOG.debug("THE MEAL WITH A LABEL = {} AND A CATEGORY = {} AND A DESCRIPTION = {} IS ALREADY PRESENT IN THE DATABASE", mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription());
            throw new ExistingFoodException("THE MEAL WITH A LABEL = " + mealDtoIn.getLabel() + " AND A CATEGORY = " + mealDtoIn.getCategory() + " AND A DESCRIPTION = " + mealDtoIn.getDescription() + " IS ALREADY EXIST");
        }

        MultipartFile image = mealDtoIn.getImage();
        var imageName = this.imageService.uploadImage(image, MEALS_IMAGES_PATH);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(imageName);

        MealEntity meal = new MealEntity(mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription(), mealDtoIn.getPrice(), mealDtoIn.getQuantity(), mealDtoIn.getStatus(),mealDtoIn.getMealTypeEnum() ,imageEntity);


        return this.mealDao.save(meal);
    }

    @Override
    public List<MealDtOut> getMealsByType(String type) {
        if (type == null || type.isBlank() || !MealTypeEnum.contains(type)) {
            MealService.LOG.debug("INVALID MEAL TYPE");
            return List.of();
        }
      return this.mealDao.findAllMealsWhereTypeEqualsTo(MealTypeEnum.getMealTypeEnum(type)).stream().map(
                mealEntity -> new MealDtOut(mealEntity,  this.MEALS_IMAGES_URL)
        ).toList();
    }


    @Override
    public List<MealDtOut> getAllMeals() {
        return this.mealDao.findAll().stream().map(meal -> new MealDtOut(meal, this.MEALS_IMAGES_URL)).toList();
    }

    @Override
    public List<MealDtOut> getOnlyAvailableMeals() {
        return this.mealDao.getAvailableMeals().stream().map(
                mealEntity -> new MealDtOut(mealEntity,  this.MEALS_IMAGES_URL)
        ).toList();
    }


    @Override
    public MealDtOut getMealByUUID(String uuid) throws InvalidFoodInformationException, FoodNotFoundException {
        return new MealDtOut(getMealEntityByUUID(uuid), this.MEALS_IMAGES_URL);
    }

    @Override
    public List<MealDtOut> getAvailableMeals() {
        return this.getOnlyAvailableMeals();
    }

    @Override
    public List<MealDtOut> getMealsInDeletionProcess() {
        return  this.mealDao.getMealsInDeletionProcess().stream().map(
                mealEntity -> new MealDtOut(mealEntity,  this.MEALS_IMAGES_URL)
        ).toList();
    }

    @Override
    public List<MealDtOut> getUnavailableMeals() {
        return this.mealDao.getUnavailableMeals().stream().map(
                mealEntity -> new MealDtOut(mealEntity,  this.MEALS_IMAGES_URL)
        ).toList();
    }

    @Override
    public MealEntity getMealEntityByUUID(String uuid) throws InvalidFoodInformationException, FoodNotFoundException {
        IMealService.checkMealUuidValidity(uuid);

        return this.mealDao.findMealById(uuid).orElseThrow(() -> {
            MealService.LOG.debug("NO MEAL WAS FOUND WITH AN UUID = {} IN THE getMealEntityByUUID METHOD", uuid);
            return new FoodNotFoundException("NO MEAL WAS FOUND");
        });
    }


    @Override
    public Optional<MealEntity> getMealWithLabelAndCategoryAndDescription(String label, String category, String description) {
        return this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase(label, category, description);

    }


}
