import {Component, OnInit} from '@angular/core';
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {User} from "../../sharedmodule/models/user";
import {Router} from "@angular/router";
import { IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";
import {AdminService} from "../dashbord/admin.service";
import {AdminOrderService} from "../orders/admin-order.service";

@Component({
    selector: 'app-main-admin',
    templateUrl: './main-admin.component.html',
    styleUrls: ["../../../assets/styles/main.component.scss"],
    providers: [AdminService ,  AdminOrderService]
})
export class MainAdminComponent implements OnInit {
    isConnected = false;
    numberOfOrders = 0;
     admin = new User();
    constructor(private router: Router , private adminService: AdminService, private adminOrderService: AdminOrderService) {
    }

    ngOnInit(): void {
        let adminId = Malfunctions.getUserIdFromLocalStorage();

        if (adminId == "") {
            this.logout();
            return
        }
      this.adminService.getAdminById(adminId).subscribe((admin) => {
            this.admin = admin;
            this.isConnected = true;
        });
        this.getNumberOfOrder();
    }


    getNumberOfOrder()  : void  {
        this.adminOrderService.getOrdersByDate().subscribe((orders) => {
            this.numberOfOrders = orders.filter(
                order=> order.status == 0
            ).length;

        });
    }

    goToStudents() : void  {
        this.router.navigate([IConstantsURL.ADMIN_STUDENTS_URL]).then();
    }

    goToOrders() {
        this.router.navigate([IConstantsURL.ADMIN_ORDERS_URL]).then();
    }

    logout(): void {
        localStorage.clear();
        this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
    }

    goToMeals() {
        this.router.navigate([IConstantsURL.ADMIN_MEALS_URL]).then(window.location.reload);
    }

    goToMenus() {
        this.router.navigate([IConstantsURL.ADMIN_MENUS_URL]).then(window.location.reload);
    }

    goToHome() {
        this.router.navigate([IConstantsURL.ADMIN_HOME_URL]).then(window.location.reload);
    }

    goToMyProfile() {
        this.router.navigate([IConstantsURL.ADMIN_PROFILE_URL]).then(window.location.reload);
    }
}
