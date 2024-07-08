package fr.sqli.cantine.service.order;


import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.entity.*;

import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.mailer.OrderEmailSender;
import fr.sqli.cantine.service.order.exception.InsufficientBalanceException;
import fr.sqli.cantine.service.order.exception.InvalidOrderException;
import fr.sqli.cantine.service.order.exception.OrderLimitExceededException;
import fr.sqli.cantine.service.order.exception.UnavailableFoodForOrderException;

import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class AddOrderTest {
    @Mock
    private IOrderDao orderDao;
    @Mock
    private IMenuDao menuDao;
    @Mock
    private IMealDao mealDao;
    @Mock
    private IStudentDao studentDao;
    @Mock
    private ITaxDao taxDao;
    @Mock
    private MockEnvironment env;
    @Mock
    private OrderEmailSender orderEmailSender;
    @InjectMocks
    private OrderService orderService;
    private OrderDtoIn orderDtoIn;

    private StudentEntity studentEntity;
    final Integer MAXIMUM_ORDER_PER_DAY = 20;

    @BeforeEach
    void SetUp() {
        this.env = new MockEnvironment();

        this.env.setProperty("sqli.canine.order.qrcode.path", "images/orders/qrcode");
        this.env.setProperty("sqli.canine.order.qrcode.image.format", "png");


        String studentUuid = UUID.randomUUID().toString();


        this.orderDtoIn = new OrderDtoIn();
        this.orderDtoIn.setStudentUuid(studentUuid);
        this.orderDtoIn.setMealsId(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        this.orderDtoIn.setMenusId(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));

        //  student  entity  for  test
        this.studentEntity = new StudentEntity();
        this.studentEntity.setId(1);
        this.studentEntity.setFirstname("student");
        this.studentEntity.setLastname("student");
        this.studentEntity.setEmail("student@test,com");

    }


    @AfterEach
    void tearDown() {
        this.orderDtoIn = null;
        this.env = null;
    }

    @Test
    void addOrderWithTheSomePrice() throws UserNotFoundException, TaxNotFoundException, InvalidOrderException, InvalidFoodInformationException, MessagingException, OrderLimitExceededException, FoodNotFoundException, InsufficientBalanceException, InvalidUserInformationException, UnavailableFoodForOrderException {

        // Init
        MealEntity meal1 = new MealEntity();
        meal1.setUuid(UUID.randomUUID().toString());
        meal1.setPrice(BigDecimal.valueOf(8));
        meal1.setStatus(1);

        MealEntity meal2 = new MealEntity();
        meal2.setUuid(UUID.randomUUID().toString());
        meal2.setPrice(BigDecimal.valueOf(10));
        meal2.setStatus(1);

        MenuEntity menu1 = new MenuEntity();
        menu1.setUuid(UUID.randomUUID().toString());
        menu1.setPrice(BigDecimal.valueOf(14));
        menu1.setStatus(1);

        MenuEntity menu2 = new MenuEntity();
        menu2.setUuid(UUID.randomUUID().toString());
        menu2.setPrice(BigDecimal.valueOf(15));
        menu2.setStatus(1);

        TaxEntity taxEntity = new TaxEntity();
        taxEntity.setTax(BigDecimal.valueOf(1));

        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        var somePrice = BigDecimal.valueOf(14).add(BigDecimal.valueOf(10)).add(BigDecimal.valueOf(8)).add(BigDecimal.valueOf(15)).add(BigDecimal.valueOf(1));

        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        this.orderDtoIn.setMealsId(List.of(meal1.getUuid(), meal2.getUuid()));
        this.orderDtoIn.setMenusId(List.of(menu1.getUuid(), menu2.getUuid()));

        this.studentEntity.setWallet(somePrice);
        // when
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findByUuid(meal1.getUuid())).thenReturn(Optional.of(meal1));
        Mockito.when(this.mealDao.findByUuid(meal2.getUuid())).thenReturn(Optional.of(meal2));
        Mockito.when(this.menuDao.findByUuid(menu1.getUuid())).thenReturn(Optional.of(menu1));
        Mockito.when(this.menuDao.findByUuid(menu2.getUuid())).thenReturn(Optional.of(menu2));
        Mockito.when(this.taxDao.findAll()).thenReturn(List.of(taxEntity));


        this.orderService.addOrderByStudent(this.orderDtoIn);  //  we  make  the  test  here
        // then
        Assertions.assertEquals(this.studentEntity.getWallet(), BigDecimal.valueOf(0));


    }




    /* *********************** TEST ADD ORDER LIMIT  ************************/

    @Test
    void addOrderWitExceedMenuAndMealOrderLimitTest() {
        List<String> meals = Stream.generate(() -> UUID.randomUUID().toString()).limit(21).toList();
        List<String> menu = Stream.generate(() -> UUID.randomUUID().toString()).limit(21).toList();
        // we  make One  Test with  One  Menu  iN  The  Order
        this.orderDtoIn.setMenusId(menu);
        this.orderDtoIn.setMealsId(meals);

        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Assertions.assertThrows(OrderLimitExceededException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }

    @Test
    void addOrderWitExceedMenuOrderLimitTest() {
        List<String> menu = Stream.generate(() -> UUID.randomUUID().toString()).limit(21).toList();
        // we  make One  Test with  One  Menu  iN  The  Order
        this.orderDtoIn.setMenusId(menu);
        this.orderDtoIn.setMealsId(null);
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Assertions.assertThrows(OrderLimitExceededException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }

    @Test
    void addOrderWitExceedMealOrderLimitTest() {
        List<String> meals = Stream.generate(() -> UUID.randomUUID().toString()).limit(21).toList();
        // we  make One  Test with  One  Menu  iN  The  Order
        this.orderDtoIn.setMenusId(meals);
        this.orderDtoIn.setMealsId(null);
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Assertions.assertThrows(OrderLimitExceededException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }


    /*********************** TEST ADD ORDER  WITH   Student  Balance ************************/


    @Test
    void addOrderWithInsufficientStudentBalance() {
        //  Init
        // we  make One  Test with  One  Menu  iN  The  Order
        var menuIdFound = java.util.UUID.randomUUID().toString();
        this.orderDtoIn.setMenusId(List.of(menuIdFound));
        this.orderDtoIn.setMealsId(null);

        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setUuid(menuIdFound);
        menuEntity.setPrice(BigDecimal.valueOf(10));
        menuEntity.setStatus(1);

        this.studentEntity.setWallet(BigDecimal.valueOf(5));

        TaxEntity taxEntity = new TaxEntity();
        taxEntity.setTax(BigDecimal.valueOf(1));
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.menuDao.findByUuid(menuIdFound)).thenReturn(Optional.of(menuEntity));
        Mockito.when(this.taxDao.findAll()).thenReturn(List.of(taxEntity));
        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        // then

        Assertions.assertThrows(InsufficientBalanceException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.menuDao, Mockito.times(1)).findByUuid(menuIdFound);
        Mockito.verify(this.taxDao, Mockito.times(1)).findAll();
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }


    /************************** TESTS  TAX     ************************/


    @Test
    void addOrderWitTwoTaxInDataBase() {
        // we  make One  Test with  One  Menu  iN  The  Order
        var menuIdFound = java.util.UUID.randomUUID().toString();
        this.orderDtoIn.setMenusId(List.of(menuIdFound));
        this.orderDtoIn.setMealsId(null);


        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setUuid(menuIdFound);
        menuEntity.setPrice(BigDecimal.valueOf(10));
        menuEntity.setStatus(1);
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.menuDao.findByUuid(menuIdFound)).thenReturn(Optional.of(menuEntity));
        Mockito.when(this.taxDao.findAll()).thenReturn(List.of(new TaxEntity(), new TaxEntity()));
        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());

        Assertions.assertThrows(TaxNotFoundException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.menuDao, Mockito.times(1)).findByUuid(menuIdFound);
        Mockito.verify(this.taxDao, Mockito.times(1)).findAll();
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }


    @Test
    void addOrderWithOutTax() {
        // we  make One  Test with  One  Menu  iN  The  Order
        var menuIdFound = java.util.UUID.randomUUID().toString();
        this.orderDtoIn.setMenusId(List.of(menuIdFound));
        this.orderDtoIn.setMealsId(null);
        this.orderDtoIn.setStudentUuid(this.studentEntity.getUuid());

        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setUuid(menuIdFound);
        menuEntity.setPrice(BigDecimal.valueOf(10));
        menuEntity.setStatus(1);
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.menuDao.findByUuid(menuIdFound)).thenReturn(Optional.of(menuEntity));
        Mockito.when(this.taxDao.findAll()).thenReturn(List.of());
        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());

        Assertions.assertThrows(TaxNotFoundException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.menuDao, Mockito.times(1)).findByUuid(menuIdFound);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }

    /**************************  TESTS  ORDERS  WITH   Unavailable  MENUS ID AND  MEALS  *****************************/

    @Test
    void addOrderWithUnavailableMenu() {
        this.orderDtoIn.setMealsId(null);
        var menuIdFound = java.util.UUID.randomUUID().toString();
        this.orderDtoIn.setMenusId(List.of(menuIdFound));
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setUuid(menuIdFound);
        menuEntity.setPrice(BigDecimal.valueOf(10));
        menuEntity.setStatus(0);
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        ;
        //  When
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.menuDao.findByUuid(menuIdFound)).thenReturn(Optional.of(menuEntity));
        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());

        Assertions.assertThrows(UnavailableFoodForOrderException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.menuDao, Mockito.times(1)).findByUuid(menuIdFound);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithUnavailableMeal() {
        var mealIdFound = java.util.UUID.randomUUID().toString();
        this.orderDtoIn.setMenusId(null);
        this.orderDtoIn.setMealsId(List.of(mealIdFound));
        MealEntity mealEntity = new MealEntity();
        mealEntity.setUuid(mealIdFound);
        mealEntity.setPrice(BigDecimal.valueOf(10));
        mealEntity.setStatus(0);
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        ;

        //  When
        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findByUuid(mealIdFound)).thenReturn(Optional.of(mealEntity));


        Assertions.assertThrows(UnavailableFoodForOrderException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.mealDao, Mockito.times(1)).findByUuid(mealIdFound);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    /**************************  TESTS  ORDERS  WITH   EMPTY MENUS ID AND  MEALS  ****************************/


    @Test
    void addOrderWithEmptyMealsAndMenu() {
        this.orderDtoIn.setMealsId(List.of());
        this.orderDtoIn.setMenusId(List.of());
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        ;

        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Assertions.assertThrows(InvalidOrderException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(this.menuDao, Mockito.times(0)).findByUuid(Mockito.any());
        Mockito.verify(this.mealDao, Mockito.times(0)).findByUuid(Mockito.any());
    }


    /**************************  TESTS  ORDERS  WITH   EMPTY MENUS ID ****************************/

    @Test
    void addOrderWithMenuNotFoundAndOtherFoundMenu() {

        this.orderDtoIn.setMealsId(null); // avoid  to  check  meals  id validation
        var menuIdFound = java.util.UUID.randomUUID().toString();
        var menuIdNotFound = java.util.UUID.randomUUID().toString();

        //  make  only  the  information    that we  need  for  the  test  ( Our  Meal Mock  )
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setUuid(menuIdFound);
        menuEntity.setPrice(BigDecimal.valueOf(10));
        menuEntity.setStatus(1);

        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        ;


        this.orderDtoIn.setMenusId(List.of(menuIdFound, menuIdNotFound));
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.menuDao.findByUuid(menuIdFound)).thenReturn(Optional.of(menuEntity));
        Mockito.when(this.menuDao.findByUuid(menuIdNotFound)).thenReturn(Optional.empty());
        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());

        Assertions.assertThrows(FoodNotFoundException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.menuDao, Mockito.times(1)).findByUuid(menuIdNotFound);
        Mockito.verify(this.menuDao, Mockito.times(1)).findByUuid(menuIdFound);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addOrderWithMenuNotFoundTest() {
        var menuUuid = java.util.UUID.randomUUID().toString();
        this.orderDtoIn.setMealsId(null); // avoid  to  check  meals  id validation
        this.orderDtoIn.setMenusId(List.of(menuUuid));
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        ;

        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        Mockito.when(this.menuDao.findByUuid(menuUuid)).thenReturn(Optional.empty());
        Assertions.assertThrows(FoodNotFoundException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.menuDao, Mockito.times(1)).findByUuid(menuUuid);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());

    }


    /**************************  TESTS  ORDERS  WITH   EMPTY MEALS ID ****************************/


    @Test
    void addOrderWithMealNotFoundAndOtherFoundMeal() {
        var mealIdFound = java.util.UUID.randomUUID().toString();
        var mealIdNotFound = java.util.UUID.randomUUID().toString();
        ;
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        ;
        //  make  only  the  information    that we  need  for  the  test  ( Our  Meal Mock  )
        MealEntity mealEntity = new MealEntity();
        mealEntity.setUuid(mealIdFound);
        mealEntity.setPrice(new BigDecimal(10));
        mealEntity.setStatus(1);

        this.orderDtoIn.setMealsId(List.of(mealIdFound, mealIdNotFound));

        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findByUuid(mealIdNotFound)).thenReturn(Optional.empty());
        Mockito.when(this.mealDao.findByUuid(mealIdFound)).thenReturn(Optional.of(mealEntity));


        Assertions.assertThrows(FoodNotFoundException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.mealDao, Mockito.times(1)).findByUuid(mealIdNotFound);
        Mockito.verify(this.mealDao, Mockito.times(1)).findByUuid(mealIdFound);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addOrderWithMealNotFoundWithEmptyMenusIdTest() {
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        ;

        var mealUuid = java.util.UUID.randomUUID().toString();
        this.orderDtoIn.setMenusId(null);
        this.orderDtoIn.setMealsId(List.of(mealUuid));
        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findByUuid(mealUuid)).thenReturn(Optional.empty());
        Assertions.assertThrows(FoodNotFoundException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.mealDao, Mockito.times(1)).findByUuid(mealUuid);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());

    }


    @Test
    void addOrderWithMealNotFoundTest() {
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        ;

        var mealUuid = UUID.randomUUID().toString();

        this.orderDtoIn.setMealsId(List.of(mealUuid));

        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));

        Mockito.when(this.mealDao.findByUuid(mealUuid)).thenReturn(Optional.empty());
        Assertions.assertThrows(FoodNotFoundException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.mealDao, Mockito.times(1)).findByUuid(mealUuid);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());

    }


    /**************************  TESTS  ORDERS  WITH   EMPTY MEALS ID  AND MENUS ID  ****************************/


    @Test
    void addOrderWithEmptyMealsIdAndMenusIdTest() {

        this.orderDtoIn.setMealsId(List.of());
        this.orderDtoIn.setMenusId(List.of());

        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        ;
        Mockito.when(authentication.getPrincipal()).thenReturn(studentEntity.getEmail());

        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.of(this.studentEntity));

        Assertions.assertThrows(InvalidOrderException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());

        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    /**************************  TESTS  ORDERS  WITH  STUDENT NOT  FOUND  *****************************/


    @Test
    void addOrderWithStudentNotFound() {
        Mockito.when(this.studentDao.findByUuid(this.orderDtoIn.getStudentUuid())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderDtoIn.getStudentUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    /**************************  TESTS  ORDERS  INFORMATION  ***********************/


    @Test
    void addOrderWithNegativeMenusIdWithNullMealIdTest() {
        this.orderDtoIn.setMealsId(null);
        this.orderDtoIn.setMenusId(List.of("1", "1", "-1"));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithNegativeMenuIdTest() {
        this.orderDtoIn.setMenusId(List.of("1", "1", "-1"));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addOrderWithNegativeMealIdWithNullMenuIdTest() {
        this.orderDtoIn.setMenusId(null);
        this.orderDtoIn.setMealsId(List.of("1", "1", "-1"));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithInvalidMealIdTest() {
        this.orderDtoIn.setMealsId(List.of("1", "-1"));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addOrderWithNullMealIdAndNullMenuId() {
        this.orderDtoIn.setMealsId(null);
        this.orderDtoIn.setMenusId(null);
        Assertions.assertThrows(InvalidOrderException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithInvalidStudentUuidTest() {
        this.orderDtoIn.setStudentUuid("-4");
        Assertions.assertThrows(InvalidUserInformationException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithNullStudentIDTest() {
        this.orderDtoIn.setStudentUuid(null);
        Assertions.assertThrows(InvalidUserInformationException.class, () -> this.orderService.addOrderByStudent(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addOrderWithNullBody() {

        Assertions.assertThrows(InvalidOrderException.class, () -> this.orderService.addOrderByStudent(null));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


}
