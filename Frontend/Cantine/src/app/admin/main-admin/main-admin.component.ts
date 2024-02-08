import {Component, OnInit} from '@angular/core';
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {GlobalAdminService} from "../global-admin.service";
import {User} from "../../sharedmodule/models/user";
import {Router} from "@angular/router";
import {AuthObject} from "../../sharedmodule/models/authObject";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";

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
        console.log(adminId)
        if (adminId == "") {
            this.logout();
        }
      this.globalAdminService.getAdminById(adminId).subscribe((admin) => {
            console.log(admin)
            this.admin = admin;
            this.isConnected = true;
        });
    }

    getAdminById() {

    }
    goToStudents() : void  {
        this.router.navigate(['cantine/admin/students']).then();
    }

    goToOrders() {
        this.router.navigate(['cantine/admin/orders']).then();
    }

    logout(): void {
        localStorage.clear();
        this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
    }
}
