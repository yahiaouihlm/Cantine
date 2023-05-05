package fr.sqli.Cantine.controller.admin.menus;

import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.util.LinkedMultiValueMap;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IMenuTest {
    final  String BASE_MENU_URL  =  "/cantine/api/admin/menus";
    final  String DIRECTORY_IMAGE_MENU = "images/menus/";
    final  String  UPDATE_MENU_URL = BASE_MENU_URL + "/update";
    final String ADD_MENU_URL = BASE_MENU_URL + "/add";
    final String DELETE_MENU_URL = BASE_MENU_URL + "/delete";

    final String GET_ONE_MENU_URL = BASE_MENU_URL + "/get";

    final  String  GET_ALL_MENUS_URL = BASE_MENU_URL + "/getAll";


    /********************** Image **************************/
    String  IMAGE_MENU_FOR_TEST_NAME = "ImageMenuForTest.jpg";
    String  SECOND_IMAGE_MENU_FOR_TEST_NAME = "ImageMealForTest1.jpg";
    String IMAGE_MENU_DIRECTORY_TESTS_PATH = "imagesForTests/menus/";

    String  IMAGE_MENU_FORMAT_FOR_TEST="image/jpg";
    String   IMAGE_MENU_FOR_TEST_PATH = IMAGE_MENU_DIRECTORY_TESTS_PATH +IMAGE_MENU_FOR_TEST_NAME;
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
            Map.entry("InvalidImageFormat", "INVALID IMAGE TYPE ONLY PNG , JPG , JPEG   ARE ACCEPTED"),
            Map.entry("ExistingMenu",  "THE MENU ALREADY EXISTS IN THE DATABASE"),
            Map.entry("missingParam", "MISSING PARAMETER"),
            Map.entry("InvalidParameter", "THE ID CAN NOT BE NULL OR LESS THAN 0"),
            Map.entry("MenuNotFound", "NO MENU WAS FOUND WITH THIS ID "),
            Map.entry("MenuWithOutMeals", "THE MENU DOESN'T CONTAIN ANY MEAL"),
            Map.entry("NoMealFound", "NO MEAL WAS FOUND WITH THIS ID"),
            Map.entry("MenuUpdatedSuccessfully", "MENU UPDATED SUCCESSFULLY")
    );


      @BeforeAll
      static void  checkExistingTestFiles () {
          File image = new File(IMAGE_MENU_FOR_TEST_PATH);
            if (!image.exists()) {
               throw  new RuntimeException("The image ' ImageMenuForTest ' for test doesn't exist");
            }
      }




    static LinkedMultiValueMap initFormData(){
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("label", "MenuTest");
        map.add("description", "Menu  description  test");
        map.add("price", "3.87");
        map.add("status", "1");
        map.add("quantity", "10");
        return map;
    }
    static  MealEntity createMeal  () {

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(IMAGE_MENU_FOR_TEST_NAME);

        MealEntity mealEntity = createMealWith("MealTest"  , "MealTest  description","MealTest  category", new BigDecimal(10.0) , 1 , 10 , imageEntity);

        return  mealEntity ;
    }

     static  MealEntity createMealWith( String  label , String description,  String category, BigDecimal price  , int status , int quantity , ImageEntity imageEntity){
        MealEntity mealEntity = new MealEntity();
        mealEntity.setLabel(label);
        mealEntity.setPrice(price);
        mealEntity.setDescription(description);
        mealEntity.setStatus(status);
        mealEntity.setQuantity(quantity);
        mealEntity.setImage(imageEntity);
        mealEntity.setCategory(category);
        return  mealEntity ;
    }
     static MenuEntity createMenu (List <MealEntity> mealAssociated ) {
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setLabel("Tacos");
        menuEntity.setPrice(new BigDecimal("3.87"));
        menuEntity.setDescription("Tacos  description  Menu");
        menuEntity.setStatus(1);
        menuEntity.setQuantity(10);
        menuEntity.setMeals(mealAssociated);
        menuEntity.setCreatedDate(LocalDate.now());
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(IMAGE_MENU_FOR_TEST_NAME);

        menuEntity.setImage(imageEntity);


        return  menuEntity ;
    }

    static BufferedImage saveTestFile() throws IOException {
        File image = new File(IMAGE_MENU_FOR_TEST_PATH);
        return ImageIO.read(image);
    }
}
