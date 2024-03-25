package fr.sqli.cantine.service.order;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.dto.out.food.OrderDtOut;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.entity.OrderEntity;

import fr.sqli.cantine.service.mailer.OrderEmailSender;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.mailer.ConfirmationOrderSender;
import fr.sqli.cantine.service.order.exception.*;
import fr.sqli.cantine.service.qrcode.QrCodeGenerator;
import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderService implements IOrderService {

    private static final Logger LOG = LogManager.getLogger();
    final Integer MAXIMUM_ORDER_PER_DAY = 20;
    final Integer SMALL_STUDENT_WALLET = 10;
    final String ORDER_QR_CODE_PATH;
    final String ORDER_QR_CODE_IMAGE_FORMAT;
    final String STUDENT_IMAGE_URL;
    final String MENU_IMAGE_URL;
    final String MEAL_IMAGE_URL;
    private final IOrderDao orderDao;

    private final ITaxDao taxDao;
    private final IStudentDao studentDao;
    private final IAdminDao adminDao;
    private final IMealDao mealDao;

    private final IMenuDao menuDao;
    private  final OrderEmailSender orderEmailSender;
    private final ConfirmationOrderSender confirmationOrderSender; /*TODO a  supprimé  */

    @Autowired
    public OrderService(Environment env, IOrderDao orderDao, IAdminDao adminDao, IStudentDao studentDao, IMealDao mealDao, IMenuDao menuDao, ITaxDao taxDao,OrderEmailSender orderEmailSender , ConfirmationOrderSender confirmationOrderSender) {
        this.orderDao = orderDao;
        this.adminDao = adminDao;
        this.studentDao = studentDao;
        this.mealDao = mealDao;
        this.menuDao = menuDao;
        this.taxDao = taxDao;
        this.orderEmailSender = orderEmailSender;
        this.confirmationOrderSender = confirmationOrderSender;
        this.ORDER_QR_CODE_PATH = env.getProperty("sqli.canine.order.qrcode.path");
        this.ORDER_QR_CODE_IMAGE_FORMAT = env.getProperty("sqli.canine.order.qrcode.image.format");
        this.STUDENT_IMAGE_URL = env.getProperty("sqli.cantine.images.url.student");
        this.MENU_IMAGE_URL = env.getProperty("sqli.cantine.images.url.menus");
        this.MEAL_IMAGE_URL = env.getProperty("sqli.cantine.images.url.meals");
    }


    /* TODO  FRO  ALL  THE  METHODS  WE  HAVE TO  CHECK  THE  VALIDITY  OF  parameters  */

    /* TODO  add  order only  available  between 09h -> 11h:30   and   13h:30 -> 14:30 */
    /* TODO change  QRcode Data */
    /* TODO  SEND  THE NOTIFICATION  IF  STUDENT WALLET  IS  LESS THAN  10 EURO */


    @Override
    public void submitOrder(Integer orderId) throws InvalidOrderException, OrderNotFoundException, CancelledOrderException, MessagingException {
        if (orderId == null) {
            OrderService.LOG.error("ORDER ID  IS NULL IN   Submit  Order  ");
            throw new InvalidOrderException("INVALID ORDER  ID ");
        }
        var ordered = this.orderDao.findById(orderId);
        if (ordered.isEmpty()) {
            OrderService.LOG.error("  NO  ORDER  HAS  BEEN   FOUND  IN 'submit Order   function ' ");
            throw new OrderNotFoundException("ORDER  NOT  FOUND ");
        }
        var order = ordered.get();

        if (!order.getCreationDate().equals(LocalDate.now())) {
            OrderService.LOG.error("ORDER  CAN NOT BE  SUBMITTED ");
            throw new CancelledOrderException("EXPIRED ORDER");
        }

        if (order.isCancelled()) {
            OrderService.LOG.error(" ORDER  HAS BEEN  CANCLLED ");
            throw new CancelledOrderException(" ORDER IS   CANCELLED ");
        }


     /*   String qrCodeData = "Student  : " + student.getFirstname() + " " + student.getLastname() +
                "\n" + " Student Email : " + student.getEmail() +
                "\n" + " Order Id :" + order.getId() +
                "\n" + " Order Price : " + order.getPrice() + "£" +
                "\n" + " Created At  " + order.getCreationDate() + " " + order.getCreationTime();

        var filePath = this.ORDER_QR_CODE_PATH + token + this.ORDER_QR_CODE_IMAGE_FORMAT;
        QrCodeGenerator.generateQrCode(qrCodeData, filePath);
*/

        order.setStatus(1);
        this.orderDao.save(order);
        this.confirmationOrderSender.sendSubmittedOrder(order);

    }

    @Override
    public void addOrder(OrderDtoIn orderDtoIn) throws InvalidUserInformationException, TaxNotFoundException, InsufficientBalanceException, IOException, WriterException, InvalidOrderException, UnavailableFoodException, OrderLimitExceededException, MessagingException, InvalidFoodInformationException, FoodNotFoundException, UserNotFoundException {
        if (orderDtoIn == null) {
            OrderService.LOG.error("INVALID ORDER ORDER IS NULL");
            throw new InvalidOrderException("INVALID ORDER");
        }


        orderDtoIn.checkOrderIDsValidity();
        var student = this.studentDao.findByUuid(orderDtoIn.getStudentId()).orElseThrow(() -> {
            OrderService.LOG.error("STUDENT WITH  UUID  = {} NOT FOUND", orderDtoIn.getStudentId());
            return new UserNotFoundException("STUDENT NOT FOUND");
        });

        var totalPrice = BigDecimal.ZERO;



        if (orderDtoIn.getMealsId() != null && orderDtoIn.getMenusId() != null && orderDtoIn.getMealsId().isEmpty() && orderDtoIn.getMenusId().isEmpty()) {
            OrderService.LOG.error("INVALID ORDER  THERE  IS NO  MEALS  OR  MENUS ");
            throw new InvalidOrderException("INVALID ORDER  THERE  IS NO  MEALS  OR  MENUS ");
        }
        if (orderDtoIn.getMealsId() != null && orderDtoIn.getMealsId().size() > MAXIMUM_ORDER_PER_DAY) {
            OrderService.LOG.error("INVALID ORDER  MAXIMUM ORDER PER DAY IS  : " + MAXIMUM_ORDER_PER_DAY);
            throw new OrderLimitExceededException("ORDER LIMIT EXCEEDED");
        }
        if (orderDtoIn.getMenusId() != null && orderDtoIn.getMenusId().size() > MAXIMUM_ORDER_PER_DAY) {
            OrderService.LOG.error("INVALID ORDER  MAXIMUM ORDER PER DAY IS  : " + MAXIMUM_ORDER_PER_DAY);
            throw new OrderLimitExceededException("ORDER LIMIT EXCEEDED");
        }
        if (orderDtoIn.getMealsId() != null && orderDtoIn.getMenusId() != null && (orderDtoIn.getMenusId().size() + orderDtoIn.getMealsId().size()) > MAXIMUM_ORDER_PER_DAY) {
            OrderService.LOG.error("INVALID ORDER  MAXIMUM ORDER PER DAY IS  : " + MAXIMUM_ORDER_PER_DAY);
            throw new OrderLimitExceededException("ORDER LIMIT EXCEEDED");
        }


        List<MealEntity> meals = new ArrayList<>();

        if (orderDtoIn.getMealsId() != null) {
            for (var mealId : orderDtoIn.getMealsId()) {
                var meal = this.mealDao.findByUuid(mealId).orElseThrow( () -> {
                    OrderService.LOG.error("MEAL WITH UUID = {} NOT FOUND" , mealId);
                    return new FoodNotFoundException("MEAL WITH  ID = " + mealId  +" NOT FOUND");
                });

                if (meal.getStatus() == 0  || meal.getStatus()  == 2 ) {
                    OrderService.LOG.error("MEAL WITH  ID  = {} AND LABEL= {} IS NOT AVAILABLE OR REMOVED" , mealId , meal.getLabel());
                    throw new UnavailableFoodException("MEAL  : " + meal.getLabel() + " IS UNAVAILABLE OR REMOVED");
                }
                meals.add(meal);
                totalPrice = totalPrice.add(meal.getPrice());
            }
        }

        List<MenuEntity> menus = new ArrayList<>();
        if (orderDtoIn.getMenusId() != null) {
            for (var menuId : orderDtoIn.getMenusId()) {
                var menu = this.menuDao.findByUuid(menuId).orElseThrow(() -> {
                    OrderService.LOG.error("MENU WITH  ID  = {} NOT FOUND" , menuId);
                    return new FoodNotFoundException("MENU WITH  ID: " + menuId + " NOT FOUND");
                });

                if (menu.getStatus() == 0 || menu.getStatus() == 2){
                    OrderService.LOG.error("MENU WITH  ID  = {} AND  LABEL = {} IS NOT AVAILABLE OR DELETED" , menuId , menu.getLabel());
                    throw new UnavailableFoodException("MENU  : " + menu.getLabel() + " IS UNAVAILABLE OR REMOVED");
                }
                menus.add(menu);
                totalPrice = totalPrice.add(menu.getPrice());
            }
        }
        // Calculate  total  price  of  order

        var taxOpt = this.taxDao.findAll();

        if (taxOpt.size() != 1) {
            OrderService.LOG.info("TAX FOUND");
            throw new TaxNotFoundException("TAX NOT FOUND");
        }

        var tax = taxOpt.get(0).getTax();

        totalPrice = totalPrice.add(tax);

        // check  if  student  has  enough  money  to  pay  for  the  order

        if (student.getWallet().compareTo(totalPrice) < 0) {
            OrderService.LOG.error("STUDENT WITH  ID  = " + orderDtoIn.getStudentId() + " DOES NOT HAVE ENOUGH MONEY TO PAY FOR THE ORDER");
            throw new InsufficientBalanceException("YOU  DON'T HAVE ENOUGH MONEY TO PAY FOR THE ORDER");
        }

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStudent(student);
        orderEntity.setMeals(meals);
        orderEntity.setMenus(menus);
        orderEntity.setStatus(0);
        orderEntity.setCancelled(false);
        //  create the  QrCode  and  save  the  order  in  the  database
        String token = "qrcode" + UUID.randomUUID();
        orderEntity.setQRCode(token + this.ORDER_QR_CODE_IMAGE_FORMAT);


        orderEntity.setPrice(totalPrice);
        orderEntity.setCreationDate(LocalDate.now());
        orderEntity.setCreationTime(new java.sql.Time(LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(), LocalDateTime.now().getSecond()));
        var order = this.orderDao.save(orderEntity);

        //  update Student  Waller
        var newStudentWallet = student.getWallet().subtract(totalPrice);
        student.setWallet(newStudentWallet);
        this.studentDao.save(student);


        this.confirmationOrderSender.sendConfirmationOrder(student, order);

        this.orderEmailSender.confirmOrder(student, order, tax);

        if ((newStudentWallet.compareTo(new BigDecimal(this.SMALL_STUDENT_WALLET))) <= 0) {
          this.orderEmailSender.sendNotificationForSmallStudentWallet(student);
        }


    }


    @Override
    public void cancelOrder(Integer orderId) throws InvalidOrderException, OrderNotFoundException, UnableToCancelOrderException, UserNotFoundException {
        if (orderId == null || orderId < 0) {
            OrderService.LOG.error("INVALID ORDER ID");
            throw new InvalidOrderException("INVALID ORDER ID");
        }

        var orderOpt = this.orderDao.findById(orderId);

        if (orderOpt.isEmpty()) {
            OrderService.LOG.error("ORDER WITH  ID  = " + orderId + " NOT FOUND");
            throw new OrderNotFoundException("ORDER NOT FOUND");
        }

        if (orderOpt.get().getStatus() == 1) {
            OrderService.LOG.error("ORDER WITH  ID  = " + orderId + " IS ALREADY VALIDATED");
            throw new UnableToCancelOrderException("ORDER IS ALREADY VALIDATED");
        }

        if (orderOpt.get().isCancelled()) {
            OrderService.LOG.error("ORDER WITH  ID  = " + orderId + " IS ALREADY CANCELED");
            throw new UnableToCancelOrderException("ORDER IS ALREADY CANCELED");
        }

        var student = this.studentDao.findById(orderOpt.get().getStudent().getId());
        if (student.isEmpty()) {  // it can not   be happened
            OrderService.LOG.error("STUDENT WITH  ID  = " + orderOpt.get().getStudent().getFirstname() + " NOT FOUND");
            throw new UserNotFoundException("STUDENT WITH : " + orderOpt.get().getStudent().getFirstname() + " NOT FOUND");
        }

        var orderprice = orderOpt.get().getPrice(); //  get  the  price  of  the  order

        student.get().setWallet(student.get().getWallet().add(orderprice)); //  get  the  student  wallet

        this.studentDao.save(student.get());

        var message = "YOUR ORDER WITH ID : " + orderOpt.get().getId() + " HAS BEEN CANCELED";

        orderOpt.get().setCancelled(true);
        this.orderDao.save(orderOpt.get());


    }

    @Override
    public List<OrderDtOut> getOrdersByDate(LocalDate date) throws InvalidOrderException, InvalidUserInformationException {
        if (date == null) {
            OrderService.LOG.error("INVALID DATE");
            throw new InvalidOrderException("INVALID DATE");
        }
        var admin = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.adminDao.findByEmail(admin.toString()).orElseThrow(() -> new InvalidUserInformationException("INVALID ADMIN INFORMATION"));


        return this.orderDao.findByCreationDate(date).stream().map(order -> new OrderDtOut(order, this.MEAL_IMAGE_URL, this.MENU_IMAGE_URL, this.STUDENT_IMAGE_URL)).toList();


    }

    @Override
    public List<OrderDtOut> getOrdersByDateAndStudentId(Integer studentId, LocalDate date) throws InvalidOrderException, OrderNotFoundException, InvalidUserInformationException, UserNotFoundException {

        var username = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (studentId == null || studentId < 0 || username == null) {
            OrderService.LOG.error("INVALID STUDENT ID");
            throw new InvalidOrderException("INVALID STUDENT ID");
        }

        if (date == null) {
            OrderService.LOG.error("INVALID DATE");
            throw new InvalidOrderException("INVALID DATE");
        }

        var student = this.studentDao.findById(studentId);
        if (student.isEmpty()) {
            OrderService.LOG.error("STUDENT WITH  ID  = " + studentId + " NOT FOUND");
            throw new UserNotFoundException("STUDENT WITH : " + studentId + " NOT FOUND");
        }

        if (!student.get().getEmail().equals(username)) {
            OrderService.LOG.error("STUDENT WITH  ID  = " + studentId + " IS NOT THE OWNER OF THE ORDER");
            throw new InvalidUserInformationException("ERROR  STUDENT  ID");
        }


        return this.orderDao.findByStudentIdAndCreationDate(studentId, date).stream().map(order -> new OrderDtOut(order, this.MEAL_IMAGE_URL, this.MENU_IMAGE_URL, this.STUDENT_IMAGE_URL)).toList();


    }


}
