import {Component, OnInit} from '@angular/core';
import {Order} from "../../../sharedmodule/models/order";

@Component({
  selector: 'app-order-dashbord',
  templateUrl: './order-dashboard.component.html',
  styles: [
  ]
})
export class OrderDashboardComponent  implements   OnInit{

  private  order :   Order = new  Order();

  ngOnInit(): void {
  }




}
