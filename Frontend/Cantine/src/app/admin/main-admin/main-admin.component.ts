import {Component, OnInit} from '@angular/core';
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {GlobalAdminService} from "../global-admin.service";
import {User} from "../../sharedmodule/models/user";
import {Router} from "@angular/router";
import { IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-main-admin',
    templateUrl: './main-admin.component.html',
    styleUrls: ["../../../assets/styles/main.component.scss"],
    providers: [GlobalAdminService]
})
export class MainAdminComponent implements OnInit {
    isConnected = false;

     admin = new User();
    constructor(private router: Router , private globalAdminService: GlobalAdminService) {
    }

    ngOnInit(): void {
        let adminId = Malfunctions.getUserIdFromLocalStorage();

        if (adminId == "") {
            this.logout();
            return
        }
      this.globalAdminService.getAdminById(adminId).subscribe((admin) => {
            console.log(admin)
            this.admin = admin;
            this.isConnected = true;
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
}
