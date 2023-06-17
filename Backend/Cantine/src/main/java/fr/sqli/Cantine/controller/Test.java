package fr.sqli.Cantine.controller;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IOrderDao;
import fr.sqli.Cantine.dto.out.food.OrderDtout;
import fr.sqli.Cantine.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Test  {

    @Autowired
    private IOrderDao iOrderDao ;

    @Autowired
    private IMealDao iMealDao ;

   @GetMapping("test")
   public ResponseEntity<List<OrderDtout>> getOrder() {

       System.out.println(this.iMealDao.findAll().get(0).getOrders());
       return ResponseEntity.ok().body(
               this.iOrderDao.findAll().stream().map( order -> new OrderDtout(order , "test" , "test")).toList()
               );
   }


}
