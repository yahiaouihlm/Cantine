import {Component, OnInit} from '@angular/core';
import {Order} from "../../../sharedmodule/models/order";

@Component({
  selector: 'app-order-dashbord',
  templateUrl: './order-dashboard.component.html',
  styles: [
  ]
})
export class OrderDashboardComponent  implements   OnInit{

   date  =  new Date();
    order :   Order = new  Order();

  ngOnInit(): void {
    this.order = Order.getOrderFromLocalStorage();
  }

 getTotalPrice () : number  {
    let total : number = 0;
    this.order.meals.forEach(meal => {  total += meal.price; });
    this.order.menus.forEach(menu => {  total += menu.price; });
    return parseFloat (total.toFixed(2));

}



}
