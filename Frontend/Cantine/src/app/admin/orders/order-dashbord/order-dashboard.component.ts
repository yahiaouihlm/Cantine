import {Component, OnInit} from '@angular/core';
import {Observable, of} from "rxjs";
import {Order} from "../../../sharedmodule/models/order";
import {AdminOrderService} from "../admin-order.service";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-order-dashboard',
    templateUrl: './order-dashboard.component.html',
    styles: [],
    providers: [AdminOrderService]
})
export class OrderDashboardComponent implements OnInit {
    ordersOfDay$: Observable<Order[]> = of([]);
    private WOULD_YOU_LIKE_TO_SUBMIT_THE_ORDER = "Voulez vous Vraiment valider cette Commande ?";
    private ORDER_SUBMITTED_SUCCESSFULLY = "Commande  Validé avec succes  ! ";

    isLoading = false;
    constructor(private adminOrderService: AdminOrderService, private matDialog: MatDialog, private   router : Router) {
    }

    ngOnInit(): void {
        this.ordersOfDay$ = this.adminOrderService.getOrdersByDate();

    }

    goToStudentProfile(studentUuid: string) {
        let  url  =  IConstantsURL.ADMIN_STUDENT_PROFILE + studentUuid;
        this.router.navigate([IConstantsURL.ADMIN_STUDENT_PROFILE], {queryParams: {studentUuid: studentUuid}})
                   .then(window.location.reload);
     }

        validateOrder(orderUuid: string) {

        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_SUBMIT_THE_ORDER},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.isLoading = true;
                this.submitOrder(orderUuid);
            } else {
                return;
            }
        });

    }


    submitOrder(orderId: string) {
        this.adminOrderService.submitOrder(orderId).subscribe(data => {
            this.isLoading = false;
            window.location.reload();
        });
    }


    protected readonly Date = Date;
}
