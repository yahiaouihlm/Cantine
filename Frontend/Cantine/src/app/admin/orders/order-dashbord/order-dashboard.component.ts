import {Component, OnInit} from '@angular/core';
import {Observable, of} from "rxjs";
import {Order} from "../../../sharedmodule/models/order";
import {AdminOrderService} from "../admin-order.service";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {SuccessfulDialogComponent} from "../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";

@Component({
    selector: 'app-order-dashbord',
    templateUrl: './order-dashboard.component.html',
    styles: [],
    providers: [AdminOrderService]
})
export class OrderDashboardComponent implements OnInit {
    ordersOfDay$: Observable<Order[]> = of([]);
    private WOULD_YOU_LIKE_TO_SUBMIT_THE_ORDER = "Voulez vous Vraiment valider cette Commande ?";
    private ORDER_SUBMITTED_SUCCESSFULLY  = "Commande  Validé avec succes  ! " ;
    constructor(private adminOrderService: AdminOrderService ,  private  matDialog :  MatDialog) {
    }

    ngOnInit(): void {
        this.ordersOfDay$ = this.adminOrderService.getOrdersByDate();
        console.log(this.ordersOfDay$) ///2023-09-15

    }

  goToStudentProfile() {}

    validateOrder(orderId :  number){
        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_SUBMIT_THE_ORDER},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
               this.submitOrder(orderId);
            } else {
                return;
            }
        });

    }



    submitOrder (orderId  :  number) {
        this.adminOrderService.submitOrder(orderId).subscribe({
            next : next  => {
              this.matDialog.open(SuccessfulDialogComponent, {data: {message: this.ORDER_SUBMITTED_SUCCESSFULLY }, width: '40%',});
            }
        })
    }
}
