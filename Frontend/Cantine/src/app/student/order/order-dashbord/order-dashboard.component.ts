import {Component, OnInit} from '@angular/core';
import {Order} from "../../../sharedmodule/models/order";
import {MatDialog} from "@angular/material/dialog";
import {ModifyOrderDialogueComponent} from "./modify-order-dialogue/modify-order-dialogue.component";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {OrderService} from "../order.service";

@Component({
    selector: 'app-order-dashbord',
    templateUrl: './order-dashboard.component.html',
    styles: [],
    providers: [OrderService]
})
export class OrderDashboardComponent implements OnInit {

    date = new Date();
    order: Order = new Order();

    constructor(private matDialog: MatDialog, private orderService: OrderService) {
    }

    ngOnInit(): void {
        let order = Order.getOrderFromLocalStorage();
        if (order) {
            this.order = order;
        }
        console.log(this.order.getMenusIds());
    }


    sendOrder() {

        let studentId = Malfunctions.getStudentIdFromLocalStorage();

        if ((this.order.meals.length == 0 && this.order.menus.length == 0) || !studentId) {
            return;
        }
        this.order.studentId = +studentId;
        this.orderService.addOrder(this.order).subscribe({
            next: (response) => {
                console.log(response);
            }
        });

    }


    getTotalPrice(): number {
        let total: number = 0;
        this.order.meals.forEach(meal => {
            total += meal.price;
        });
        this.order.menus.forEach(menu => {
            total += menu.price;
        });
        return parseFloat(total.toFixed(2));

    }


    modifyOrder() {
        console.log("hello  world ")
        this.matDialog.open(ModifyOrderDialogueComponent, {data: this.order});
    }
}
