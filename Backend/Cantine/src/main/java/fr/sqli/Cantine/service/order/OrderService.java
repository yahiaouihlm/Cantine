package fr.sqli.Cantine.service.order;

import com.google.zxing.WriterException;
import fr.sqli.Cantine.dao.*;
import fr.sqli.Cantine.dto.in.food.OrderDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.entity.OrderEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.order.exception.*;
import fr.sqli.Cantine.service.qrcode.QrCodeGenerator;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.Cantine.service.superAdmin.exception.TaxNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService implements IOrderService {

    private static final Logger LOG = LogManager.getLogger();
    final  Integer MAXIMUM_ORDER_PER_DAY = 20 ;
    final  String  ORDER_QR_CODE_PATH ;
    final  String ORDER_QR_CODE_IMAGE_FORMAT ;
    private IOrderDao orderDao;

    private ITaxDao taxDao;
    private IStudentDao studentDao;

    private IMealDao mealDao;

    private Environment env ;
    private IMenuDao menuDao;
    @Autowired
    public OrderService( Environment env ,IOrderDao orderDao, IStudentDao studentDao, IMealDao mealDao, IMenuDao menuDao , ITaxDao taxDao) {
        this.orderDao = orderDao;
        this.studentDao = studentDao;
        this.mealDao = mealDao;
        this.menuDao = menuDao;
        this.taxDao = taxDao;
        this.env = env ;
        this.ORDER_QR_CODE_PATH = env.getProperty("sqli.canine.order.qrcode.path");
        this.ORDER_QR_CODE_IMAGE_FORMAT = env.getProperty("sqli.canine.order.qrcode.image.format");

    }


    /* TODO  FRO  ALL  THE  METHODS  WE  HAVE TO  CHECK  THE  VALIDITY  OF  parameters  */

     /* TODO  add  order only  available  between 09h -> 11h:30   and   13h:30 -> 14:30 */
    /* TODO change  QRcode Data */
    /* TODO  SEND  THE NOTIFICATION  IF  STUDENT WALLET  IS  LESS THAN  10 EURO */
    @Override
    public void addOrder(OrderDtoIn orderDtoIn) throws InvalidPersonInformationException, InvalidMenuInformationException, InvalidMealInformationException, StudentNotFoundException, MealNotFoundException, MenuNotFoundException, TaxNotFoundException, InsufficientBalanceException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException {
         if  (orderDtoIn ==  null)
             throw  new InvalidOrderException("INVALID ORDER");


        orderDtoIn.checkOrderIDsValidity();
        var  student = this.studentDao.findById(orderDtoIn.getStudentId());
        var   totalPrice  =  BigDecimal.ZERO;

        //  check Information  validity

        if(student.isEmpty())
            throw new StudentNotFoundException("STUDENT NOT FOUND");


        if (orderDtoIn.getMealsId() !=  null && orderDtoIn.getMenusId() !=  null && orderDtoIn.getMealsId().size() == 0  &&   orderDtoIn.getMenusId().size() ==0 ) {
            OrderService.LOG.error("INVALID ORDER  THERE  IS NO  MEALS  OR  MENUS ");
            throw  new InvalidOrderException("INVALID ORDER  THERE  IS NO  MEALS  OR  MENUS ");
        }
       if  (orderDtoIn.getMealsId() !=  null  && orderDtoIn.getMealsId().size() > MAXIMUM_ORDER_PER_DAY){
           OrderService.LOG.error("INVALID ORDER  MAXIMUM ORDER PER DAY IS  : " + MAXIMUM_ORDER_PER_DAY);
           throw  new OrderLimitExceededException( "ORDER LIMIT EXCEEDED");
       }
       if (orderDtoIn.getMenusId()!=  null   && orderDtoIn.getMenusId().size() > MAXIMUM_ORDER_PER_DAY) {
            OrderService.LOG.error("INVALID ORDER  MAXIMUM ORDER PER DAY IS  : " + MAXIMUM_ORDER_PER_DAY);
            throw  new OrderLimitExceededException( "ORDER LIMIT EXCEEDED");
        }
        if (orderDtoIn.getMealsId() !=  null  && orderDtoIn.getMenusId()!=  null   && (orderDtoIn.getMenusId().size() + orderDtoIn.getMealsId().size()) > MAXIMUM_ORDER_PER_DAY) {
            OrderService.LOG.error("INVALID ORDER  MAXIMUM ORDER PER DAY IS  : " + MAXIMUM_ORDER_PER_DAY);
            throw  new OrderLimitExceededException( "ORDER LIMIT EXCEEDED");
        }




        List<MealEntity> meals = new ArrayList<>();

        if (orderDtoIn.getMealsId() !=  null ) {
            for (var mealId : orderDtoIn.getMealsId()) {
                var meal = this.mealDao.findById(mealId);
                if (meal.isEmpty()) {
                    OrderService.LOG.error("MEAL WITH  ID  = " + mealId + " NOT FOUND");
                    throw new MealNotFoundException("MEAL WITH  ID : " +mealId+ " NOT FOUND");
                }
                if  (meal.get().getStatus() ==  0) {
                    OrderService.LOG.error("MEAL WITH  ID  = " + mealId + " IS NOT AVAILABLE");
                    throw new UnavailableFoodException("MEAL  : " + meal.get().getLabel()+ " IS UNAVAILABLE");
                }
                meals.add(meal.get());
                totalPrice = totalPrice.add(meal.get().getPrice());
            }
        }

        List<MenuEntity> menus = new ArrayList<>();
        if (orderDtoIn.getMenusId() !=  null ) {
            for (var menuId : orderDtoIn.getMenusId()) {
                var menu = this.menuDao.findById(menuId);
                if (menu.isEmpty()) {
                    OrderService.LOG.error("MENU WITH  ID  = " + menuId + " NOT FOUND");
                    throw new MenuNotFoundException("MENU WITH  ID: "  + menuId + " NOT FOUND");
                }
                if (menu.get().getStatus() == 0) {
                    OrderService.LOG.error("MENU WITH  ID  = " + menuId + " IS NOT AVAILABLE");
                    throw new UnavailableFoodException("MENU  : " + menu.get().getLabel()+ " IS UNAVAILABLE" );
                }
                menus.add(menu.get());
                totalPrice = totalPrice.add(menu.get().getPrice());
            }
        }
        // Calculate  total  price  of  order

        var taxOpt = this.taxDao.findAll() ;

        if  (taxOpt.size() !=  1) {
            OrderService.LOG.info("TAX FOUND");
            throw  new TaxNotFoundException("TAX NOT FOUND");
        }

        var  tax = taxOpt.get(0).getTax();

        totalPrice = totalPrice.add(tax);
        System.out.println("TOTAL PRICE  = "+ totalPrice);

        // check  if  student  has  enough  money  to  pay  for  the  order

        if(student.get().getWallet().compareTo(totalPrice) < 0){
            OrderService.LOG.error("STUDENT WITH  ID  = "+ orderDtoIn.getStudentId()  +" DOES NOT HAVE ENOUGH MONEY TO PAY FOR THE ORDER");
            throw new InsufficientBalanceException("YOU  DON'T HAVE ENOUGH MONEY TO PAY FOR THE ORDER");
        }

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStudent(student.get());
        orderEntity.setMeals(meals);
        orderEntity.setMenus(menus);
        orderEntity.setStatus(1);

        //  create the  QrCode  and  save  the  order  in  the  database
        String  token = "qrcode" +  UUID.randomUUID();
        orderEntity.setQRCode(token + this.ORDER_QR_CODE_IMAGE_FORMAT );



        orderEntity.setPrice(totalPrice);
        orderEntity.setCreationDate(LocalDate.now());
        orderEntity.setCreationTime(new java.sql.Time(LocalDateTime.now().getHour(),LocalDateTime.now().getMinute(),LocalDateTime.now().getSecond()));
        var order  =  this.orderDao.save(orderEntity);

        //  update Student  Waller
        var  newStudentWallet  =  student.get().getWallet().subtract(totalPrice) ;
        student.get().setWallet(newStudentWallet);
        this.studentDao.save(student.get());



        String  qrCodeData = "Student  : " +student.get().getFirstname() + " " +student.get().getLastname()+
                "\n"+" Student Email : " +student.get().getEmail() +
                "\n" + " Order Id :" + order.getId() +
                "\n" +" Order Price : " + order.getPrice() + "£" +
                "\n"  +" Created At  " + order.getCreationDate() + " " + order.getCreationTime() ;

        var  filePath =this.ORDER_QR_CODE_PATH + token + this.ORDER_QR_CODE_IMAGE_FORMAT;
        QrCodeGenerator.generateQrCode(qrCodeData,filePath );



         if  ((newStudentWallet.compareTo(new BigDecimal(10 )))<= 0 ){
             /* SEND THE NOTIFICATION  */
        }



    }

    public  void cancelOrder  (Integer orderId ) throws InvalidOrderException, OrderNotFoundException, UnableToCancelOrderException {
        if  (orderId ==  null  || orderId < 0) {
            OrderService.LOG.error("INVALID ORDER ID");
            throw  new InvalidOrderException("INVALID ORDER ID");
        }

        var orderOpt = this.orderDao.findById(orderId);

        if  (orderOpt.isEmpty()) {
            OrderService.LOG.error("ORDER WITH  ID  = " + orderId + " NOT FOUND");
            throw  new OrderNotFoundException("ORDER WITH NOT FOUND");
        }

       if (orderOpt.get().getStatus() ==  1 ) {
           OrderService.LOG.error("ORDER WITH  ID  = " + orderId + " IS ALREADY CANCELED");
           throw  new UnableToCancelOrderException("ORDER CANNOT BE CANCELED");
       }

       this.orderDao.deleteById(orderId);

    }
}
