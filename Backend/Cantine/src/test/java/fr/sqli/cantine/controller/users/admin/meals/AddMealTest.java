package fr.sqli.cantine.controller.users.admin.meals;

import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;

import fr.sqli.cantine.entity.MealTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
public class AddMealTest extends AbstractLoginRequest implements IMealTest {

    private static final Logger LOG = LogManager.getLogger();

    @Autowired
    private IStudentDao iStudentDao;
    @Autowired
    private IStudentClassDao iStudentClassDao;
    private IMealDao mealDao;
    private MockMvc mockMvc;
    private IAdminDao adminDao;
    private IFunctionDao functionDao;
    private MultiValueMap<String, String> formData;
    private MockMultipartFile imageData;
    private String authorizationToken;

    @Autowired
    public AddMealTest(MockMvc mockMvc, IAdminDao adminDao, IFunctionDao functionDao , IMealDao mealDao) throws Exception {
        this.mockMvc = mockMvc;
        this.adminDao = adminDao;
        this.functionDao = functionDao;
        this.mealDao = mealDao;
        clearDataBase();
        initFormData();
        initDataBase();

    }

    public void initFormData() throws IOException {
        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("label", "MealTest");
        this.formData.add("price", "1.5");
        this.formData.add("category", "MealTest category");
        this.formData.add("description", "MealTest description");
        this.formData.add("status", "1");
        this.formData.add("quantity", "10");
        this.formData.add("mealType", "ENTREE");
        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_MEAL_FOR_TEST_NAME,          // nom du fichier
                IMAGE_MEAL_FORMAT_FOR_TEST,                    // type MIME
                new FileInputStream(IMAGE_MEAL_FOR_TEST_PATH));

    }


    public void clearDataBase() {
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
        this.mealDao.findAll();
        this.mealDao.deleteAll();
    }

    /**
     * make  one  Meal in  the  database
     * this  meal  will  be  used  only for    the  tests  of  the  method  addMealTestWithExistingMeal because we have to make a meal in DataBase
     **/
    public void initDataBase() throws Exception {

        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);

        ImageEntity image = new ImageEntity();
        image.setImagename(IMAGE_MEAL_FOR_TEST_NAME);
        MealTypeEnum mealTypeEnum = MealTypeEnum.getMealTypeEnum("ENTREE");
        MealEntity mealEntity = new MealEntity("MealTest", "MealTest category", "MealTest description"
                , new BigDecimal("1.5"), 10, 1, mealTypeEnum, image);

        this.mealDao.save(mealEntity);
    }

   @Test
    void addMealTestWithAllValidateInformation() throws Exception {

        this.formData.set("label", "MealTest2");
        this.formData.set("price", "15");
        this.formData.set("category", "MealTest2 category");
        this.formData.set("description", "MealTest2 description");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

       result.andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(IMealTest.responseMap.get("MealAddedSuccessfully"))));

        //  clear  the  database  after
        //  we find  the  Unique Meal Added to  DataBase ,  get ImageName  and  delete  the  image  from  the  folder  images/meals
        // finally  we  delete  the  meal  from  the  database

        var meal = this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase("MealTest2", "MealTest2 category", "MealTest2 description");
        Assertions.assertTrue(meal.isPresent());
        var imageName = meal.get().getImage().getImagename();
        var  imageFile = new File(IMAGE_MEAL_DIRECTORY_PATH + imageName);
        Assertions.assertTrue(imageFile.delete());

    }
    /**************************************** Tests  For  Student Token    ********************************************/
    @Test
    void  addMealTestWithStudentToken() throws Exception {
        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();

        AbstractLoginRequest.saveAStudent(this.iStudentDao, this.iStudentClassDao);
        var studentAuthorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION,studentAuthorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

   /**************************************** Tests  For  Meal Type    ********************************************/

   @Test
   void  addMealTestWithWrongMealType() throws Exception {
       this.formData.set("mealType" , "wrongMealType");


       var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
               .file(this.imageData)
               .params(this.formData)
               .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
               .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


       result.andExpect(MockMvcResultMatchers.status().isBadRequest())
               .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidMealType"))));
   }

   @Test
   void  addMealTestWithOutMealType() throws Exception {
       this.formData.remove("mealType");


       var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
               .file(this.imageData)
               .params(this.formData)
               .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
               .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


         result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                 .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("mealType"))));
   }

    @Test
    void addMealTestWithExistingMealWithAddSpacesAndChangingCase4() throws Exception {

        this.formData.set("category", "   M e a l TEST c  ate gor y ");
        this.formData.set("label", "ME                  AlTES t");
        this.formData.set("description", "mEAlT E s t DESC          RI P T i oN");


        // 3  Test  With  Trying  to  add The Same Meal again
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void addMealTestWithExistingMealWithAddSpacesAndChangingCase3() throws Exception {

        this.formData.set("category", "   M e a l TEST c  ate gor y ".toLowerCase());
        this.formData.set("label", "ME                  AlTES t".toLowerCase());
        this.formData.set("description", "mEAlT E s t DESC          RI P T i oN");


                // 3  Test  With  Trying  to  add The Same Meal again
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void addMealTestWithExistingMealWithAddSpacesAndChangingCase2() throws Exception {

        this.formData.set("category", "   M e a l TEST c  ate gor y ");
        this.formData.set("label", "ME                  AlTES t");
        this.formData.set("description", "MEALTEST DESCRIPTION");


        // 3  Test  With  Trying  to  add The Same Meal again
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void addMealTestWithExistingMealWithAddSpacesAndChangingCase() throws Exception {

        this.formData.set("category", "   M e a l TEST c  ate gor y ");
        this.formData.set("label", "ME                  AlTES t");


        // 3  Test  With  Trying  to  add The Same Meal again
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());

    }

    @Test
    void addMealTestWithExistingMealWithAddSpacesToCategory() throws Exception {

        this.formData.set("category", "   M e a l Test c  ate gor y ");


        // 3  Test  With  Trying  to  add The Same Meal again
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());

    }

    @Test
    void addMealTestWithExistingMealWithAddSpacesToDescription() throws Exception {

        this.formData.set("description", "   MealTest description   ");

        // 3  Test  With  Trying  to  add The Same Meal again
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());
    }


    @Test
    @DisplayName("add Meal with  the  same  label+same spaces    and  the  same  category  and  the  same  description  of  an  existing  meal  in  the  database")
    void addMealTestWithExistingMealWithAddSpacesToLabel3() throws Exception {


        this.formData.set("label", "MealTes t");

        // 3  Test  With  Trying  to  add The Same Meal again
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());
    }


    @Test
    @DisplayName("add Meal with  the  same  label+same spaces    and  the  same  category  and  the  same  description  of  an  existing  meal  in  the  database")
    void addMealTestWithExistingMealWithAddSpacesToLabel2() throws Exception {

        this.formData.set("label", " M eal T e s t ");


        // 3  Test  With  Trying  to  add The Same Meal again
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());
      //  clearDataBase(); //  clear  the  database  after  all
    }



    @Test
    @DisplayName("add Meal with  the  same  label+same spaces    and  the  same  category  and  the  same  description  of  an  existing  meal  in  the  database")
    void addMealTestWithExistingMealWithAddSpacesToLabel() throws Exception {

        this.formData.set("label" , " M e a  l T e s t ");

        // 3  Test  With  Trying  to  add The Same Meal again
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());


    }


    @Test
    @DisplayName("add Meal with  the  same  label  and  the  same  category  and  the  same  description  of  an  existing  meal  in  the  database")
    void addMealTestWithExistingMeal() throws Exception {

        // 3  Test  With  Trying  to  add The Same Meal again
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());


    }


    /**********************************************  Tests  Fot Images  *********************************************/
    @Test
    void addMealWithWrongImageFormat() throws Exception {
        this.formData.set("label", "MealTest2"); //  we change the   label  to  avoid  the  conflict  with  the  existing  meal  in  the  database

        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_MEAL_FOR_TEST_NAME,          // nom du fichier
                "image/gif",                    // type MIME
                new FileInputStream("src/test/java/fr/sqli/Cantine/service/images/filesTests/Babidi_TestGifFormat.gif"));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidImageFormat"))));

    }

    @Test
    void addMealTestWithWrongImageName() throws Exception {
        this.imageData = new MockMultipartFile(
                "WrongImageName",                         // nom du champ de fichier
                IMAGE_MEAL_FOR_TEST_NAME,          // nom du fichier
                IMAGE_MEAL_FORMAT_FOR_TEST,                    // type MIME
                new FileInputStream(IMAGE_MEAL_FOR_TEST_PATH));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Image"))));

    }

    @Test
    void addMealTestWithOutImage() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Image"))));


    }


    /*******************************  Tests  For Price  **********************************/

    @Test
    void addMealTestWithTooLongPrice() throws Exception {
        this.formData.set("price", "1000.1");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("HighPrice"))));
    }

    @Test
    void addMealTestWithNegativePrice() throws Exception {
        this.formData.set("price", "-1.5");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("NegativePrice"))));
    }

    @Test
    void addMealTestWithInvalidPrice4() throws Exception {
        this.formData.set("price", ".5-");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMealTestWithInvalidPrice3() throws Exception {
        this.formData.set("price", "-1c");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMealTestWithInvalidPrice2() throws Exception {
        this.formData.set("price", "1.d");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMealTestWithInvalidPrice() throws Exception {
        this.formData.set("price", "0edez");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMealTestWitEmptyPrice() throws Exception {
        this.formData.set("price", "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Price"))));
    }

    @Test
    void addMealTestWithNullPrice() throws Exception {
        this.formData.set("price", null);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Price"))));
    }

    @Test
    void addMealTestWithOutPrice() throws Exception {
        this.formData.remove("price");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Price"))));
    }


    /************************************* Tests for  Quantity  *************************************/


    @Test
    void addMealTestWithQuantityOutBoundOfInteger() throws Exception {
        this.formData.set("quantity", "2000000000000000000000000000000000000000000000000000000000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMealTestWithTooLongQuantity() throws Exception {
        this.formData.set("quantity", Integer.toString(Integer.MAX_VALUE - 99));
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("HighQuantity"))));
    }

    @Test
    void addMealTestWithNegativeQuantity() throws Exception {
        this.formData.set("quantity", "-1");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("NegativeQuantity"))));
    }

    @Test
    void addMealTestWithInvalidQuantity3() throws Exception {
        this.formData.set("quantity", "-1.2");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMealTestWithInvalidQuantity2() throws Exception {
        this.formData.set("quantity", "1.2");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMealTestWithInvalidQuantity() throws Exception {
        this.formData.set("quantity", "null");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMealTestWithEmptyQuantity() throws Exception {
        this.formData.set("quantity", "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Quantity"))));
    }

    @Test
    void addMealTestWithNullQuantity() throws Exception {
        this.formData.set("quantity", null);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Quantity"))));
    }

    @Test
    void addMealTestWithOutQuantity() throws Exception {
        this.formData.remove("quantity");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Quantity"))));
    }


   /********************************* Tests for Status *********************************/
    @Test
    void AddMealTestWithOutSideStatusValue3() throws Exception {
        this.formData.set("status", "3 ");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithOutSideStatusValue2() throws Exception {
        this.formData.set("status", "-5");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithOutSideStatusValue() throws Exception {
        this.formData.set("status", "5");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithEmptyStatusValue() throws Exception {
        this.formData.set("status", " ");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Status"))));
    }

    @Test
    void AddMealTestWithInvalidStatusValue2() throws Exception {
        this.formData.set("status", "-5rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void AddMealTestWithInvalidStatusValue() throws Exception {
        this.formData.set("status", "564rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void AddMealTestWithNegativeStatusValue3() throws Exception {
        this.formData.set("status", "-1");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithNullStatusValue() throws Exception {
        this.formData.set("status", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Status"))));
    }

    @Test
    void AddMealWithoutStatus() throws Exception {
        this.formData.remove("status");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Status"))));
    }



    /*****************************************  Tests For   MealType  *****************************************/
    @Test
    void AddMealTestWithInvalidMealType() throws Exception {
        // given :  remove label from formData
        this.formData.set("mealType" , "invalidMealType");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("InvalidMealType"))));
    }

    @Test
    void AddMealTestWithOutMealType() throws Exception {
        // given :  remove label from formData
         this.formData.remove("mealType");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("mealType"))));
    }


    /*****************************************  Tests For   Description  *****************************************/
    @Test
    void AddMealTestWithEmptyDescription() throws Exception {
        // given :  remove label from formData
        this.formData.set("description", "         "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Description"))));
    }


    @Test
    void AddMealTestWithTooLongDescription() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters
        String tooLongLabel = "a".repeat(3001);
        this.formData.set("description", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("LongDescriptionLength"))));
    }

    @Test
    void AddMealTestWithNullDescriptionValue() throws Exception {
        this.formData.set("description", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Description"))));
    }

    @Test
    void AddMealWithoutDescription() throws Exception {

        // given :  remove label from formData
        this.formData.remove("description");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Description"))));
    }

    @Test
    void AddMealTestWithTooShortDescription() throws Exception {
        // given :  remove label from formData
        this.formData.set("description", "    ad     "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("ShortDescriptionLength"))));
    }


    /******************************************** Tests for Category ********************************************/

    @Test
    void AddMealWithEmptyCategory() throws Exception {

        // given :  remove label from formData
        this.formData.set("category",  "               ");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Category"))));
    }

    @Test
    void AddMealTestWithTooLongCategory() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters
        String tooLongLabel = "t".repeat(101);
        this.formData.set("category", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("LongCategoryLength"))));
    }

    @Test
    void AddMealTestWithNullCategoryValue() throws Exception {
        this.formData.set("category", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Category"))));
    }

    @Test
    void AddMealTestWithTooShortCategory() throws Exception {
        // given :  remove label from formData
        this.formData.set("category", "    ad     "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("ShortCategoryLength"))));
    }

    @Test
    void AddMealWithoutCategory() throws Exception {

        // given :  remove label from formData
        this.formData.remove("category");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Category"))));
    }


    /******************************************** Tests for Label ********************************************/
    @Test
    void AddMealWithoutLabel() throws Exception {
        // given :  remove label from formData
        this.formData.remove("label");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Label"))));

    }

    @Test
    void AddMealTestWithNullLabelValue() throws Exception {
        // given :  remove label from formData
        this.formData.set("label", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Label"))));

    }

    @Test
    void AddMealTestWithTooShortLabel() throws Exception {
        // given :  remove label from formData
        this.formData.set("label", "    a        d     "); // length  must be  < 3 without spaces  ( all spaces are removed )

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("ShortLabelLength"))));

    }

    @Test
    void AddMealTestWithTooLongLabel() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters
        String tooLongLabel = "test".repeat(26);
        this.formData.set("label", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("LongLabelLength"))));


    }

    @Test
    void AddMealTestWithEmptyLabel() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters

        this.formData.set("label", "    "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Label"))));


    }

    @Test
    void addMealTestWithNullRequestData() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters
        // when : call addMeal

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(IMealTest.exceptionsMap.get("Label"))));


    }
    @Test
    void addMealTestWithOutAuthToken() throws Exception {


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST ,ADD_MEAL_URL)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // then :
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }
}


























