import {Component, OnInit} from '@angular/core';
import {Order} from "../../../sharedmodule/models/order";
import {MatDialog} from "@angular/material/dialog";
import {ModifyOrderDialogueComponent} from "./modify-order-dialogue/modify-order-dialogue.component";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {OrderService} from "../order.service";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";

@Component({
    selector: 'app-order-dashbord',
    templateUrl: './order-dashboard.component.html',
    styles: [],
    providers: [OrderService]
})
export class OrderDashboardComponent implements OnInit {

    private WOULD_YOU_LIKE_TO_SEND_ORDER = "Voulez-vous Valider votre commande ?";
    date = new Date();
    order: Order = new Order();
    isLoading = false;

    constructor(private matDialog: MatDialog, private orderService: OrderService) {
    }

    ngOnInit(): void {
        let order = Order.getOrderFromLocalStorage();
        if (order) {
            this.order = order;
        }
    }


    validateOrder() {

        let sendOrder = () => {

            let studentId = Malfunctions.getStudentIdFromLocalStorage();

            if (this.isOrderEmpty() || !studentId) {
                return;
            }
            this.order.studentId = +studentId;
            this.order.mealsId = this.order.meals.map(meal => meal.id);
            this.order.menusId = this.order.menus.map(menu => menu.id);
            this.orderService.addOrder(this.order).subscribe({
                next: (response) => {
                    console.log(response);
                }
            });

        }

        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_SEND_ORDER},
            width: '40%',
        });
        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                sendOrder();
            } else {
                this.isLoading = false;
                return;
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
        let dialogue = this.matDialog.open(ModifyOrderDialogueComponent, {data: this.order});
        dialogue.afterClosed().subscribe((order) => {
            window.location.reload();
        });
    }


    isOrderEmpty(): boolean {
        return this.order.meals.length == 0 && this.order.menus.length == 0;
    }
}
