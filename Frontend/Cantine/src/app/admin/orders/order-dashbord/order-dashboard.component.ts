import {Component, OnInit} from '@angular/core';
import {Observable, of} from "rxjs";
import {Order} from "../../../sharedmodule/models/order";
import {AdminOrderService} from "../admin-order.service";

@Component({
  selector: 'app-order-dashbord',
  templateUrl: './order-dashboard.component.html',
  styles: [],
  providers: [AdminOrderService]
})
export class OrderDashboardComponent implements OnInit{
  ordersOfDay$  :  Observable <Order[]>  =  of([]);

    constructor( private adminOrderService: AdminOrderService) { }

  ngOnInit(): void {
    this.ordersOfDay$ = this.adminOrderService.getOrdersByDate();
    console.log(this.ordersOfDay$) ///2023-09-15
    }
}
