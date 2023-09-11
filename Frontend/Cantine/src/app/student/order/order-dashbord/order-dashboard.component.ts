import {Component, OnInit} from '@angular/core';
import {Order} from "../../../sharedmodule/models/order";
import {MatDialog} from "@angular/material/dialog";
import {ModifyOrderDialogueComponent} from "./modify-order-dialogue/modify-order-dialogue.component";

@Component({
  selector: 'app-order-dashbord',
  templateUrl: './order-dashboard.component.html',
  styles: [
  ]
})
export class OrderDashboardComponent  implements   OnInit{

   date  =  new Date();
    order :   Order = new  Order();

    constructor( private  matDialog: MatDialog) {
    }

    ngOnInit(): void {
        let order = Order.getOrderFromLocalStorage();
        if (order) {
            this.order = order;
        }
    }

 getTotalPrice () : number  {
    let total : number = 0;
    this.order.meals.forEach(meal => {  total += meal.price; });
    this.order.menus.forEach(menu => {  total += menu.price; });
    return parseFloat (total.toFixed(2));

}


  modifyOrder() {
      console.log("hello  world ")
       this.matDialog.open(ModifyOrderDialogueComponent, { data: this.order});
  }
}
