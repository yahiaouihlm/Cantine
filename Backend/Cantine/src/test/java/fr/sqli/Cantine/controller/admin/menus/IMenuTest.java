package fr.sqli.Cantine.controller.admin.menus;

import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IMenuTest {
    final  String BASE_MENU_URL  =  "/cantine/api/admin/menus";
    final String ADD_MENU_URL = BASE_MENU_URL + "/add";


    String  IMAGE_MENU_FOR_TEST_NAME = "ImageMenuForTest.jpg";
    String IMAGE_MENU_DIRECTORY_PATH = "images/menus/";

    String  IMAGE_MENU_FORMAT_FOR_TEST="image/jpg";
    String   IMAGE_MENU_FOR_TEST_PATH = IMAGE_MENU_DIRECTORY_PATH+IMAGE_MENU_FOR_TEST_NAME;
    Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("Label", "LABEL_IS_MANDATORY"),
            Map.entry("InvalidArgument", "ARGUMENT NOT VALID"),
            Map.entry("LongLabelLength", "LABEL_IS_TOO_LONG"),
            Map.entry("ShortLabelLength", "LABEL_IS_TOO_SHORT"),
            Map.entry("Description", "DESCRIPTION_IS_MANDATORY"),
            Map.entry("Price", "PRICE_IS_MANDATORY"),
            Map.entry("Image", "IMAGE_IS_MANDATORY"),
            Map.entry("ShortDescriptionLength", "DESCRIPTION_IS_TOO_SHORT"),
            Map.entry("LongDescriptionLength", "DESCRIPTION_IS_TOO_LONG"),
            Map.entry("Status", "STATUS_IS_MANDATORY"),
            Map.entry("OutSideStatusValue", "STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE"),
            Map.entry("Quantity", "QUANTITY_IS_MANDATORY"),
            Map.entry("HighQuantity", "QUANTITY_IS_TOO_HIGH"),
            Map.entry("NegativeQuantity", "QUANTITY MUST BE GREATER THAN 0"),
            Map.entry("HighPrice", "PRICE MUST BE LESS THAN 1000"),
            Map.entry("NegativePrice", "PRICE MUST BE GREATER THAN 0"),
            Map.entry("InvalidImageFormat", "INVALID IMAGE TYPE ONLY PNG , JPG , JPEG   ARE ACCEPTED")


    );

    static  MealEntity createMeal  () {

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(IMAGE_MENU_FOR_TEST_NAME);

        MealEntity mealEntity = createMealWith("MealTest" , new BigDecimal(10.0) , "MealTest  description" , 1 , 10 , imageEntity);

        return  mealEntity ;
    }

     static  MealEntity createMealWith( String  label , BigDecimal price , String description , int status , int quantity , ImageEntity imageEntity){
        MealEntity mealEntity = new MealEntity();
        mealEntity.setLabel(label);
        mealEntity.setPrice(price);
        mealEntity.setDescription(description);
        mealEntity.setStatus(status);
        mealEntity.setQuantity(quantity);
        mealEntity.setImage(imageEntity);
        return  mealEntity ;
    }
    static MenuEntity createMenu (List <MealEntity> mealAssociated ) {
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setLabel("Tacos");
        menuEntity.setPrice(new BigDecimal("3.87"));
        menuEntity.setDescription("Menu  description  of Tacos menu");
        menuEntity.setStatus(1);
        menuEntity.setQuantity(10);
        menuEntity.setMeals(mealAssociated);
        return  menuEntity ;
    }
}
