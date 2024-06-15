import {Component, OnInit} from '@angular/core';
import {OrderService} from "../order.service";
import {Observable, of} from "rxjs";
import {Order} from "../../../sharedmodule/models/order";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";
import {Router} from "@angular/router";

@Component({
    selector: 'app-order-history',
    templateUrl: './order-history.component.html',
    styles: [],
    providers: [OrderService]
})
export class OrderHistoryComponent implements OnInit {

   studentOrderHistory$ : Observable<Order[]> =  of ([]);
    constructor(private orderService: OrderService, private router : Router) {
    }

    /*TODO lorque  il y'a plusieurs  coammdende  il  faut  faire  un    scroll  des  commande et  laisser  annuler visulle */
    ngOnInit(): void {
        const studentUuid = Malfunctions.getUserIdFromLocalStorage();
        if (studentUuid != null && studentUuid !== "") {
            this.studentOrderHistory$ =  this.orderService.getAllOrders(studentUuid);
        } else {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }


    }


    goBack() {
        this.router.navigate([IConstantsURL.STUDENT_ORDER]).then(window.location.reload);
    }
}
