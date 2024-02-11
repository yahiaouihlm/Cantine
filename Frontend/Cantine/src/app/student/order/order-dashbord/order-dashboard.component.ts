import {Component, OnInit} from '@angular/core';
import {Order} from "../../../sharedmodule/models/order";
import {MatDialog} from "@angular/material/dialog";
import {ModifyOrderDialogueComponent} from "./modify-order-dialogue/modify-order-dialogue.component";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {OrderService} from "../order.service";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {SuccessfulDialogComponent} from "../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {Observable, of} from "rxjs";
import {Meal} from "../../../sharedmodule/models/meal";

@Component({
    selector: 'app-order-dashbord',
    templateUrl: './order-dashboard.component.html',
    styles: [],
    providers: [OrderService]
})
export class OrderDashboardComponent implements OnInit {

    private WOULD_YOU_LIKE_TO_SEND_ORDER = "Voulez-vous Valider votre commande ?";

//http://localhost:8080/cantine/student/order/getByDate?studentId=21&date=2023-09-18
//http://localhost:8080/cantine/student/order/getByDate?date=2023-09-18&idStudent=21
    order: Order = new Order();
    isLoading = false;

    ordersOfDay$  :  Observable <Order[]>  =  of([]);
    constructor(private matDialog: MatDialog, private orderService: OrderService) {
    }

    ngOnInit(): void {

        let order = Order.getOrderFromLocalStorage();
        if (order) {
            this.order = order;
        }
        this.ordersOfDay$ = this.orderService.getOrdersOfDay();
    }


    validateOrder() {

        let sendOrder = () => {

            let studentId = Malfunctions.getUserIdFromLocalStorage();

            if (this.isOrderEmpty() || !studentId) {
                return;
            }
            this.order.studentId = +studentId;
            this.order.mealsId = this.order.meals.map(meal => meal.uuid);
            this.order.menusId = this.order.menus.map(menu => menu.id);
            this.orderService.addOrder(this.order).subscribe({
                next: (response) => {
                    let dialogue =  this.matDialog.open(SuccessfulDialogComponent, {
                        data: {message: " Votre  Commande a éte bien enregistrer il sera validé prochainement"},
                        width: '40%',
                    });

                    dialogue.afterClosed().subscribe((result) => {
                        window.location.reload()
                        Order.clearOrder();
                    });
                   // il faut   faire  un reloade  a la page apres   les  modification

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
