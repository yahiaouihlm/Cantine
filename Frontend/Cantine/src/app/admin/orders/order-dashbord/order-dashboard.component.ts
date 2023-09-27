import {Component, OnInit} from '@angular/core';
import {Observable, of} from "rxjs";
import {Order} from "../../../sharedmodule/models/order";
import {OrderService} from "../../../student/order/order.service";

@Component({
  selector: 'app-order-dashbord',
  templateUrl: './order-dashboard.component.html',
  styles: [
  ], providers: [OrderService]
})
export class OrderDashboardComponent implements OnInit{
  ordersOfDay$  :  Observable <Order[]>  =  of([]);

    constructor( private orderService: OrderService) { }

  ngOnInit(): void {
    this.ordersOfDay$ = this.orderService.getOrdersOfDay();
    console.log(this.ordersOfDay$) ///2023-09-15
    }
}
