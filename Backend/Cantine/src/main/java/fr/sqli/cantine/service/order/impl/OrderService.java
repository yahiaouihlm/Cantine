package fr.sqli.cantine.service.order.impl;

import com.google.zxing.WriterException;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.dto.out.food.OrderDtOut;
import fr.sqli.cantine.entity.*;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.mailer.OrderEmailSender;
import fr.sqli.cantine.service.mailer.UserEmailSender;
import fr.sqli.cantine.service.order.IOrderService;
import fr.sqli.cantine.service.order.exception.*;
import fr.sqli.cantine.service.order.qrcode.QrCodeGenerator;
import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private final IUserDao userDao;
    private final IMealDao mealDao;

    private final IMenuDao menuDao;
    private final OrderEmailSender orderEmailSender;
    private final UserEmailSender userEmailSender;
    private final IPaymentDao paymentDao;

    @Autowired
    public OrderService(Environment env, IOrderDao orderDao, IUserDao userDao, IPaymentDao iPaymentDao, IMealDao mealDao,
                        IMenuDao menuDao, ITaxDao taxDao, UserEmailSender userEmailSender, OrderEmailSender orderEmailSender) {
        this.orderDao = orderDao;
        this.userDao = userDao;
        this.mealDao = mealDao;
        this.menuDao = menuDao;
        this.taxDao = taxDao;
        this.paymentDao = iPaymentDao;
        this.orderEmailSender = orderEmailSender;
        this.userEmailSender = userEmailSender;
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
    public void submitOrder(String orderUuid) throws InvalidOrderException, OrderNotFoundException, CancelledOrderException, MessagingException, IOException, WriterException {

        if (orderUuid == null || orderUuid.trim().length() < 10) {
            OrderService.LOG.error("INVALID ORDER ID");
            throw new InvalidOrderException("INVALID ORDER ID");
        }
        var orderEntity = this.orderDao.findOrderById(orderUuid).orElseThrow(() -> {
            OrderService.LOG.error("NO  ORDER  HAS  BEEN   FOUND  IN 'submit Order   function '  With  Order  ID  = {} ", orderUuid);
            return new OrderNotFoundException("ORDER  NOT FOUND");
        });

        if (!orderEntity.getCreationDate().equals(LocalDate.now())) {
            OrderService.LOG.error("ORDER  CAN NOT BE  SUBMITTED ");
            throw new CancelledOrderException("EXPIRED ORDER");
        }

        if (orderEntity.isCancelled()) {
            OrderService.LOG.error(" ORDER  HAS BEEN  CANCELLED ");
            throw new CancelledOrderException(" ORDER IS  ALREADY CANCELLED ");
        }

        String token = "qrcode" + UUID.randomUUID();
        orderEntity.setQRCode(token + this.ORDER_QR_CODE_IMAGE_FORMAT);

        String qrCodeData = "Student  : " + orderEntity.getStudent().getFirstname() + " " + orderEntity.getStudent().getLastname() +
                "\n" + " Student Email : " + orderEntity.getStudent().getEmail() +
                "\n" + " Order Id :" + orderEntity.getId() +
                "\n" + " Order Price : " + orderEntity.getPrice() + "£" +
                "\n" + " Created At  " + orderEntity.getCreationDate() + " " + orderEntity.getCreationTime();

        var filePath = this.ORDER_QR_CODE_PATH + token + this.ORDER_QR_CODE_IMAGE_FORMAT;
        QrCodeGenerator.generateQrCode(qrCodeData, filePath);


        orderEntity.setStatus(1);
        this.orderDao.save(orderEntity);
        this.orderEmailSender.validatedOrderByAdmin(orderEntity.getStudent(), orderEntity, filePath);
    }

    @Override
    public void addOrderByStudent(OrderDtoIn orderDtoIn) throws InvalidUserInformationException, TaxNotFoundException, InsufficientBalanceException, InvalidOrderException, UnavailableFoodForOrderException, OrderLimitExceededException, MessagingException, InvalidFoodInformationException, FoodNotFoundException, UserNotFoundException {
        if (orderDtoIn == null) {
            OrderService.LOG.error("INVALID ORDER ORDER IS NULL");
            throw new InvalidOrderException("INVALID ORDER");
        }
        orderDtoIn.checkOrderIDsValidity();
        var student = this.userDao.findStudentById(orderDtoIn.getStudentUuid()).orElseThrow(() -> {
            OrderService.LOG.error("STUDENT WITH  UUID  = {} NOT FOUND", orderDtoIn.getStudentUuid());
            return new UserNotFoundException("STUDENT NOT FOUND");
        });
        var username = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!student.getEmail().equals(username)) {
            OrderService.LOG.error("STUDENT WITH  ID  = {} IS NOT THE OWNER OF THE ORDER  OF THE STUDENT  AUTHENTICATED {} ", orderDtoIn.getStudentUuid(), username);
            throw new InvalidUserInformationException("ERROR  STUDENT  ID");
        }

        var totalPrice = BigDecimal.ZERO;

        if (orderDtoIn.getMealsId() != null && orderDtoIn.getMenusId() != null && orderDtoIn.getMealsId().isEmpty() && orderDtoIn.getMenusId().isEmpty()) {
            OrderService.LOG.error("INVALID ORDER  THERE  IS NO  MEALS  OR  MENUS");
            throw new InvalidOrderException("INVALID ORDER  THERE  IS NO  MEALS  OR  MENUS");
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
                var meal = this.mealDao.findMealById(mealId).orElseThrow(() -> {
                    OrderService.LOG.error("MEAL WITH UUID = {} NOT FOUND", mealId);
                    return new FoodNotFoundException("MEAL NOT FOUND");
                });

                if (meal.getStatus() == 0 || meal.getStatus() == 2) {
                    OrderService.LOG.error("MEAL WITH  ID  = {} AND LABEL= {} IS NOT AVAILABLE OR REMOVED", mealId, meal.getLabel());
                    throw new UnavailableFoodForOrderException("MEAL  : " + meal.getLabel() + " IS UNAVAILABLE OR REMOVED");
                }
                meals.add(meal);
                totalPrice = totalPrice.add(meal.getPrice());
            }
        }

        List<MenuEntity> menus = new ArrayList<>();
        if (orderDtoIn.getMenusId() != null) {
            for (var menuId : orderDtoIn.getMenusId()) {
                var menu = this.menuDao.findMenuById(menuId).orElseThrow(() -> {
                    OrderService.LOG.error("MENU WITH  ID  = {} NOT FOUND", menuId);
                    return new FoodNotFoundException("MENU NOT FOUND");
                });

                if (menu.getStatus() == 0 || menu.getStatus() == 2) {
                    OrderService.LOG.error("MENU WITH  ID  = {} AND  LABEL = {} IS NOT AVAILABLE OR DELETED", menuId, menu.getLabel());
                    throw new UnavailableFoodForOrderException("MENU  : " + menu.getLabel() + " IS UNAVAILABLE OR REMOVED");
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
            OrderService.LOG.error("STUDENT WITH  ID  = " + orderDtoIn.getStudentUuid() + " DOES NOT HAVE ENOUGH MONEY TO PAY FOR THE ORDER");
            throw new InsufficientBalanceException("YOU  DON'T HAVE ENOUGH MONEY TO PAY FOR THE ORDER");
        }

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStudent(student);
        orderEntity.setMeals(meals);
        orderEntity.setMenus(menus);
        orderEntity.setStatus(0);
        orderEntity.setCancelled(false);


        orderEntity.setPrice(totalPrice);
        orderEntity.setCreationDate(LocalDate.now());
        orderEntity.setCreationTime(new java.sql.Time(LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(), LocalDateTime.now().getSecond()));
        var order = this.orderDao.save(orderEntity);

        //  update Student  Waller
        var newStudentWallet = student.getWallet().subtract(totalPrice);
        student.setWallet(newStudentWallet);
        this.userDao.save(student);


        this.orderEmailSender.confirmOrder(student, order, tax);

        if ((newStudentWallet.compareTo(new BigDecimal(this.SMALL_STUDENT_WALLET))) <= 0) {
            this.orderEmailSender.sendNotificationForSmallStudentWallet(student);
        }


    }

    @Override
    public void cancelOrderByAdmin(String orderUuid) throws OrderNotFoundException, InvalidOrderException, MessagingException, CancelledOrderException, InvalidUserInformationException, UserNotFoundException {
        if (orderUuid == null || orderUuid.trim().length() < 10) {
            OrderService.LOG.error("INVALID ORDER ID");
            throw new InvalidOrderException("INVALID ORDER ID");
        }
        var adminEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var admin = this.userDao.findAdminByEmail(adminEmail.toString()).orElseThrow(() -> {
            OrderService.LOG.error("INVALID ADMIN INFORMATION");
            return new InvalidUserInformationException("INVALID ADMIN INFORMATION");
        });


        var order = this.orderDao.findOrderById(orderUuid).orElseThrow(() -> {
            OrderService.LOG.error("ORDER WITH  UUID  = {} NOT FOUND ", orderUuid);
            return new OrderNotFoundException("ORDER NOT FOUND");
        });

        if (order.getStatus() == 1 || order.getStatus() == 2) {
            OrderService.LOG.error("ORDER WITH  ID  = {} IS ALREADY VALIDATED OR TAKEN", order.getId());
            throw new CancelledOrderException("ORDER IS ALREADY VALIDATED");
        }

        if (order.isCancelled()) {
            OrderService.LOG.error("ORDER WITH  ID  ={} IS ALREADY CANCELED", order.getId());
            throw new CancelledOrderException("ORDER IS ALREADY CANCELED");
        }

        var student = this.userDao.findStudentById(order.getStudent().getId()).orElseThrow(() -> {
            OrderService.LOG.error("STUDENT WITH  ID  =  {} NOT FOUND", order.getStudent().getId());
            return new UserNotFoundException("STUDENT WITH : " + order.getStudent().getFirstname() + " NOT FOUND");
        });

        var orderPrice = order.getPrice(); //  get  the  price  of  the  order

        order.setCancelled(true);
        this.orderDao.save(order);
        student.setWallet(student.getWallet().add(orderPrice)); //  get  the  student  wallet
        this.paymentDao.save(new PaymentEntity(admin, student, orderPrice, TransactionType.REFUNDS));
        this.userDao.save(student);
        this.userEmailSender.sendNotificationAboutNewStudentAmount(student, student.getWallet().doubleValue(), orderPrice.doubleValue());
        this.orderEmailSender.cancelledOrderByAdmin(student, order);
    }


    @Override
    public void cancelOrderByStudent(String orderUuid) throws InvalidOrderException, OrderNotFoundException, UnableToCancelOrderException, UserNotFoundException, MessagingException {
        if (orderUuid == null || orderUuid.trim().length() < 10) {
            OrderService.LOG.error("INVALID ORDER ID");
            throw new InvalidOrderException("INVALID ORDER ID");
        }
        var username = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var order = this.orderDao.findOrderById(orderUuid).orElseThrow(() -> {
            OrderService.LOG.error("ORDER WITH  UUID  = {} NOT FOUND ", orderUuid);
            return new OrderNotFoundException("ORDER NOT FOUND");
        });

        if (!order.getStudent().getEmail().equals(username)) {
            OrderService.LOG.error("STUDENT WITH  ID  = {} IS NOT THE OWNER OF THE ORDER  OF THE STUDENT  AUTHENTICATED {} ", order.getStudent().getId(), username);
            throw new UserNotFoundException("ERROR  STUDENT  ID");
        }

        if (order.getStatus() == 1 || order.getStatus() == 2) {
            OrderService.LOG.error("ORDER WITH  ID  = {} IS ALREADY VALIDATED OR TAKEN", order.getId());
            throw new UnableToCancelOrderException("ORDER IS ALREADY VALIDATED");
        }

        if (order.isCancelled()) {
            OrderService.LOG.error("ORDER WITH  ID  ={} IS ALREADY CANCELED", order.getId());
            throw new UnableToCancelOrderException("ORDER IS ALREADY CANCELED");
        }

        var student = this.userDao.findStudentById(order.getStudent().getId()).orElseThrow(() -> {
            OrderService.LOG.error("STUDENT WITH  ID  =  {} NOT FOUND", order.getStudent().getId());
            return new UserNotFoundException("STUDENT WITH : " + order.getStudent().getFirstname() + " NOT FOUND");
        });

        var orderPrice = order.getPrice(); //  get  the  price  of  the  order

        student.setWallet(student.getWallet().add(orderPrice)); //  get  the  student  wallet

        this.userDao.save(student);


        order.setCancelled(true);
        this.orderDao.save(order);

        var randomAdmin = this.userDao.findRandomAdmin().orElseThrow(() -> new UserNotFoundException("NO ADMIN FOUND"));
        this.paymentDao.save(new PaymentEntity(randomAdmin, student, orderPrice, TransactionType.REFUNDS));
        this.orderEmailSender.cancelledOrder(student, order);
        this.userEmailSender.sendNotificationAboutNewStudentAmount(student, student.getWallet().doubleValue(), orderPrice.doubleValue());

    }

    @Override
    public List<OrderDtOut> getStudentOrder(String studentUuid) throws UserNotFoundException {
        if (studentUuid == null || studentUuid.trim().length() < 10)
            return List.of();
        var student = this.userDao.findStudentById(studentUuid).orElseThrow(() -> {
            OrderService.LOG.error("STUDENT  NOT  FOUND  WITH  UUID = {} WHILE  HIS  ORDERS ", studentUuid);
            return new UserNotFoundException("STUDENT  NOT  FOUND");
        });

        return this.orderDao.findByStudentOrderByCreationDateDesc(student)
                .stream()
                .map(order -> new OrderDtOut(order, this.MEAL_IMAGE_URL, this.MENU_IMAGE_URL, this.STUDENT_IMAGE_URL))
                .toList();

    }

    @Override
    public List<OrderDtOut> getOrdersByDate(LocalDate date) throws InvalidOrderException, InvalidUserInformationException {
        if (date == null) {
            OrderService.LOG.error("INVALID DATE");
            throw new InvalidOrderException("INVALID DATE");
        }
        var admin = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.userDao.findAdminByEmail(admin.toString()).orElseThrow(() -> new InvalidUserInformationException("INVALID ADMIN INFORMATION"));


        return this.orderDao.findByCreationDate(date)
                .stream()
                .map(order -> new OrderDtOut(order, this.MEAL_IMAGE_URL, this.MENU_IMAGE_URL, this.STUDENT_IMAGE_URL))
                .toList();


    }

    @Override
    public List<OrderDtOut> getOrdersByDateAndStudentId(String studentUuid, LocalDate date) throws InvalidOrderException, InvalidUserInformationException, UserNotFoundException {

        var username = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (studentUuid == null || studentUuid.trim().length() < 10 || username == null) {
            OrderService.LOG.error("INVALID STUDENT UUID");
            throw new InvalidOrderException("INVALID STUDENT ID");
        }

        if (date == null) {
            OrderService.LOG.error("INVALID DATE");
            throw new InvalidOrderException("INVALID DATE");
        }
        var student = this.userDao.findStudentById(studentUuid).orElseThrow(() -> {
            OrderService.LOG.error("STUDENT WITH  UUID  = {} NOT FOUND", studentUuid);
            return new UserNotFoundException("STUDENT WITH : " + studentUuid + " NOT FOUND");
        });

        if (!student.getEmail().equals(username)) {
            OrderService.LOG.error("STUDENT WITH  ID  = {} IS NOT THE OWNER OF THE ORDER  OF THE STUDENT  AUTHENTICATED {} ", studentUuid, username);
            throw new InvalidUserInformationException("ERROR  STUDENT  ID");
        }


        return this.orderDao.findByStudentIdAndCreationDate(studentUuid, date).stream().map(order -> new OrderDtOut(order, this.MEAL_IMAGE_URL, this.MENU_IMAGE_URL, this.STUDENT_IMAGE_URL)).toList();


    }


}
