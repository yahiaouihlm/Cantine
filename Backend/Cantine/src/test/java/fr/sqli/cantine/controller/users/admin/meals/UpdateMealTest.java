package fr.sqli.cantine.controller.users.admin.meals;


import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MealTypeEnum;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class UpdateMealTest extends AbstractContainerConfig implements IMealTest {

    @Autowired
    private IUserDao iStudentDao;
    @Autowired
    private IStudentClassDao iStudentClassDao;
    private IMealDao mealDao;
    private MockMvc mockMvc;
    private IUserDao adminDao;
    private IFunctionDao functionDao;
    private LinkedMultiValueMap<String, String> formData;
    private MockMultipartFile imageData;
    private String authorizationToken;

    @Autowired
    public UpdateMealTest(IUserDao adminDao, IMealDao mealDao, MockMvc mockMvc, IFunctionDao functionDao) throws Exception {
        this.adminDao = adminDao;
        this.mealDao = mealDao;
        this.mockMvc = mockMvc;
        this.functionDao = functionDao;
        cleanDatabase();
        initDatabase();
        initFormData();
    }

    public void initFormData() throws IOException {
        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("id", java.util.UUID.randomUUID().toString());
        this.formData.add("label", "MealTest");
        this.formData.add("price", "3.75");
        this.formData.add("category", "MealTest category");
        this.formData.add("description", "MealTest description");
        this.formData.add("status", "1");
        this.formData.add("quantity", "10");
        this.formData.add("mealType", "ENTREE");
        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MEAL_FOR_TEST_NAME,          // nom du fichier
                IMAGE_MEAL_FORMAT_FOR_TEST,                    // type MIME
                new FileInputStream(IMAGE_MEAL_FOR_TEST_PATH));

    }


    void initDatabase() throws Exception {
        // create  Admin and get token
        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);

        ImageEntity image = new ImageEntity();
        image.setName(IMAGE_MEAL_FOR_TEST_NAME);
        ImageEntity image1 = new ImageEntity();
        image1.setName(SECOND_IMAGE_MEAL_FOR_TEST_NAME);
        MealTypeEnum mealTypeEnum = MealTypeEnum.getMealTypeEnum("ENTREE");
        List<MealEntity> meals = List.of(new MealEntity("Entrée", "Salade de tomates", "Salade", new BigDecimal("2.3"), 1, 1, mealTypeEnum, image), new MealEntity("Plat", "Poulet", "Poulet", new BigDecimal("2.3"), 1, 1, mealTypeEnum, image1));
        this.mealDao.saveAll(meals);

    }


    void cleanDatabase() {
        this.mealDao.deleteAll();
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();

    }


    @BeforeAll
    static void copyImageTestFromTestDirectoryToImageMenuDirectory() throws IOException {
        String source = IMAGE_MEAL_TEST_DIRECTORY_PATH + IMAGE_MEAL_FOR_TEST_NAME;
        String destination = IMAGE_MEAL_DIRECTORY_PATH + IMAGE_MEAL_FOR_TEST_NAME;
        File sourceFile = new File(source);
        File destFile = new File(destination);
        if (!destFile.exists()) {
            Files.copy(sourceFile.toPath(), destFile.toPath());
        }
    }

    @AfterAll
    static void removeImageOfTestIFExist() {
        String destination = IMAGE_MEAL_DIRECTORY_PATH + IMAGE_MEAL_FOR_TEST_NAME;
        File destFile = new File(destination);
        if (destFile.exists()) {
            destFile.delete();
        }
    }


    /**
     * the method  is used  to  rename after update the image of the meal
     */


    @Test
    void updateMealWithImage() throws Exception {
        var mealUuid = this.mealDao.findAll().get(0).getId();
        this.formData.set("id", mealUuid);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(super.responseMessage(responseMap.get("MealUpdatedSuccessfully"))));

        // add other tests  to  check if the meal is updated in database


        var updatedMeal = this.mealDao.findMealById(mealUuid);
        Assertions.assertTrue(updatedMeal.isPresent());

        Assertions.assertEquals(this.formData.getFirst("label"), updatedMeal.get().getLabel());
        Assertions.assertEquals(this.formData.getFirst("category"), updatedMeal.get().getCategory());
        Assertions.assertEquals(this.formData.getFirst("description"), updatedMeal.get().getDescription());
        Assertions.assertEquals(Integer.parseInt(Objects.requireNonNull(this.formData.getFirst("status"))), updatedMeal.get().getStatus());
        Assertions.assertEquals(Integer.parseInt(Objects.requireNonNull(this.formData.getFirst("quantity"))), updatedMeal.get().getQuantity());
        Assertions.assertEquals(new BigDecimal(Objects.requireNonNull(this.formData.getFirst("price"))), updatedMeal.get().getPrice());
        var newImageName = updatedMeal.get().getImage().getName();

        Assertions.assertTrue(new File(IMAGE_MEAL_DIRECTORY_PATH + newImageName).delete());

    }


    @Test
    void updateMealWithOutImage() throws Exception {
        var mealUuid = this.mealDao.findAll().get(0).getId();
        this.formData.set("id", mealUuid);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(super.responseMessage(responseMap.get("MealUpdatedSuccessfully"))));

        // add other tests  to  check if the meal is updated in database

        var updatedMeal = this.mealDao.findMealById(mealUuid);
        Assertions.assertTrue(updatedMeal.isPresent());
        Assertions.assertEquals(this.formData.getFirst("label"), updatedMeal.get().getLabel());
        Assertions.assertEquals(this.formData.getFirst("category"), updatedMeal.get().getCategory());
        Assertions.assertEquals(this.formData.getFirst("description"), updatedMeal.get().getDescription());
        Assertions.assertEquals(Integer.parseInt(Objects.requireNonNull(this.formData.getFirst("status"))), updatedMeal.get().getStatus());
        Assertions.assertEquals(Integer.parseInt(Objects.requireNonNull(this.formData.getFirst("quantity"))), updatedMeal.get().getQuantity());
        Assertions.assertEquals(new BigDecimal(Objects.requireNonNull(this.formData.getFirst("price"))), updatedMeal.get().getPrice());
        Assertions.assertEquals(this.imageData.getOriginalFilename(), updatedMeal.get().getImage().getName());
    }

    @Test
    void updateMealWithWrongImageFormat() throws Exception {
        var mealUuid = this.mealDao.findAll().get(0).getId();
        this.formData.set("id", mealUuid);
        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MEAL_FOR_TEST_NAME,          // nom du fichier
                "image/gif",                    // type MIME
                new FileInputStream(IMAGE_MEAL_FOR_TEST_PATH));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidImageFormat"))));
    }

    @Test
    void updateMealWithStudentAuthToken() throws Exception {

        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();

        AbstractLoginRequest.saveAStudent(this.iStudentDao, this.iStudentClassDao);
        var studentAuthorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, studentAuthorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(status().isForbidden());
    }

    @Test
    void updateMealToExistingMeal() throws Exception {
        var existingMeal = this.mealDao.findAll().get(0);
        var MealToUpdate = this.mealDao.findAll().get(1);
        this.formData.set("id", MealToUpdate.getId());
        this.formData.set("label", existingMeal.getLabel());
        this.formData.set("category", existingMeal.getCategory());
        this.formData.set("description", existingMeal.getDescription());


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isConflict());

    }


    @Test
    void updateMealTestWithNotFoundMeal() throws Exception {


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("mealNotFound"))));


    }

    /******************************************* MEAL TYPE TESTS ********************************************************/

    @Test
    void updateMealTestWithInvalidMealType() throws Exception {
        this.formData.set("mealType", "INVALID_MEAL_TYPE");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidMealType"))));
    }


    @Test
    void updateMealTestWithOutMealType() throws Exception {
        this.formData.remove("mealType");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("mealType"))));
    }

    /******************************************* PRICE TESTS ********************************************************/


    @Test
    void updateMealTestWithTooLongPrice() throws Exception {
        this.formData.set("price", "1000.1");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("HighPrice"))));
    }

    @Test
    void updateMealTestWithNegativePrice() throws Exception {
        this.formData.set("price", "-1.5");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("NegativePrice"))));
    }

    @Test
    void updateMealTestWithInvalidPrice4() throws Exception {
        this.formData.set("price", ".5-");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMealTestWithInvalidPrice3() throws Exception {
        this.formData.set("price", "-1c");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMealTestWithInvalidPrice2() throws Exception {
        this.formData.set("price", "1.d");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMealTestWithInvalidPrice() throws Exception {
        this.formData.set("price", "0edez");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMealTestWitEmptyPrice() throws Exception {
        this.formData.set("price", "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void updateMealTestWithNullPrice() throws Exception {
        this.formData.set("price", null);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void updateMealTestWithOutPrice() throws Exception {
        this.formData.remove("price");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Price"))));
    }


    /***************************************** QUANTITY TESTS ********************************************************/


    @Test
    void updateMealTestWithQuantityOutBoundOfInger() throws Exception {
        this.formData.set("quantity", "2000000000000000000000000000000000000000000000000000000000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMealTestWithTooLongQuantity() throws Exception {
        this.formData.set("quantity", Integer.toString(Integer.MAX_VALUE - 99));
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("HighQuantity"))));
    }

    @Test
    void updateMealTestWithNegativeQuantity() throws Exception {
        this.formData.set("quantity", "-1");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("NegativeQuantity"))));
    }

    @Test
    void updateMealTestWithInvalidQuantity3() throws Exception {
        this.formData.set("quantity", "-1.2");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMealTestWithInvalidQuantity2() throws Exception {
        this.formData.set("quantity", "1.2");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMealTestWithInvalidQuantity() throws Exception {
        this.formData.set("quantity", "null");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMealTestWithEmptyQuantity() throws Exception {
        this.formData.set("quantity", "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void updateMealTestWithNullQuantity() throws Exception {
        this.formData.set("quantity", null);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void updateMealTestWithOutQuantity() throws Exception {
        this.formData.remove("quantity");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Quantity"))));
    }


    /******************************************* STATUS TESTS ********************************************************/


    @Test
    void updateMealTestWithOutSideStatusValue3() throws Exception {
        this.formData.set("status", "3 ");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMealTestWithOutSideStatusValue2() throws Exception {
        this.formData.set("status", "-5");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMealTestWithOutSideStatusValue() throws Exception {
        this.formData.set("status", "5");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMealTestWithEmptyStatusValue() throws Exception {
        this.formData.set("status", " ");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void updateMealTestWithInvalidStatusValue2() throws Exception {
        this.formData.set("status", "-5rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMealTestWithInvalidStatusValue() throws Exception {
        this.formData.set("status", "564rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMealTestWithNegativeStatusValue3() throws Exception {
        this.formData.set("status", "-1");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMealTestWithNullStatusValue() throws Exception {
        this.formData.set("status", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void updateMealWithoutStatus() throws Exception {
        this.formData.remove("status");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Status"))));
    }


    /*************************************** DESCRIPTION TESTS ********************************************/

    @Test
    void AddMealTestWithTooLongDescription() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters
        String tooLongLabel = "a".repeat(3001);
        this.formData.set("description", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("LongDescriptionLength"))));
    }

    @Test
    void AddMealTestWithNullDescriptionValue() throws Exception {
        this.formData.set("description", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Description"))));
    }

    @Test
    void AddMealWithoutDescription() throws Exception {

        // given :  remove label from formData
        this.formData.remove("description");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Description"))));
    }

    @Test
    void AddMealTestWithTooShortDescription() throws Exception {
        // given :  remove label from formData
        this.formData.set("description", "    ad     "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("ShortDescriptionLength"))));
    }


    /********************************************* Category   ************************************************/


    @Test
    void updateMealTestWithTooLongCategory() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters
        String tooLongLabel = "t".repeat(101);
        this.formData.set("category", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("LongCategoryLength"))));
    }

    @Test
    void updateMealTestWithNullCategoryValue() throws Exception {
        this.formData.set("category", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Category"))));
    }

    @Test
    void updateMealTestWithTooShortCategory() throws Exception {
        // given :  remove label from formData
        this.formData.set("category", "    ad     "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("ShortCategoryLength"))));
    }

    @Test
    void updateMealWithoutCategory() throws Exception {

        // given :  remove label from formData
        this.formData.remove("category");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Category"))));
    }


    /********************************************* Label  ************************************************/


    @Test
    void updateMealWithoutLabel() throws Exception {
        // given :  remove label from formData
        this.formData.remove("label");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Label"))));

    }

    @Test
    void updateMealTestWithNullLabelValue() throws Exception {
        // given :  remove label from formData
        this.formData.set("label", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("Label"))));

    }

    @Test
    void updateMealTestWithTooShortLabel() throws Exception {
        // given :  remove label from formData
        this.formData.set("label", "    a        d     "); // length  must be  < 3 without spaces  ( all spaces are removed )

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("ShortLabelLength"))));

    }

    @Test
    void updateMealTestWithTooLongLabel() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters
        String tooLongLabel = "test".repeat(26);
        this.formData.set("label", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).params(this.formData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));
        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(exceptionsMap.get("LongLabelLength"))));


    }


    /********************************************* ID MEAL ************************************************/


    @Test
    void updateMealWithInvalidID() throws Exception {
        this.formData.set("id", "");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(this.exceptionsMap.get("InvalidMealUuid"))));
    }

    @Test
    void updateMealWithIDNull() throws Exception {

        this.formData.set("id", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(this.exceptionsMap.get("InvalidMealUuid"))));
    }

    @Test
    void updateMealWithOutID() throws Exception {

        this.formData.remove("id");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(this.exceptionsMap.get("InvalidMealUuid"))));
    }


    @Test
    void updateMealWithInvalidIDArgument() throws Exception {
        this.formData.set("id", "erfzr");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).file(this.imageData).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(this.exceptionsMap.get("InvalidMealUuid"))));
    }


    @Test
    void updateMealWithNullRequestData() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).header(HttpHeaders.AUTHORIZATION, this.authorizationToken).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(exceptionMessage(this.exceptionsMap.get("InvalidMealUuid"))));
    }

    @Test
    void updateMealWithOutAuthToken() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_MEAL_URL).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}