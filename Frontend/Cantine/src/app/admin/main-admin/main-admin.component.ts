import {Component, OnInit} from '@angular/core';
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {GlobalAdminService} from "../global-admin.service";
import {User} from "../../sharedmodule/models/user";
import {Router} from "@angular/router";

@Component({
    selector: 'app-main-admin',
    templateUrl: './main-admin.component.html',
    styleUrls: ["../../../assets/styles/main.component.scss"],
    providers: [GlobalAdminService]
})
export class MainAdminComponent implements OnInit {
    isconnected = false;
    admin = new User();

    constructor(private globalAdminService: GlobalAdminService, private router: Router) {
    }

    ngOnInit(): void {
        console.log("hello world from main admin component");
        let adminId = Malfunctions.getUserIdFromLocalStorage();
        console.log("admin  Id = " + adminId);
        /*if (adminId === '') {
            this.isconnected = false;
            this.router.navigate(['cantine/home']).then();

        }*/
        this.getAdminById(adminId);

    }

    goToStudents() : void  {
        this.router.navigate(['cantine/admin/students']).then();
    }
    getAdminById(adminId: string) {
        this.globalAdminService.getAdminById(adminId).subscribe((response) => {
            this.admin = response;
            this.isconnected = true;
        });
    }


    goToOrders() {
        this.router.navigate(['cantine/admin/orders']).then();
    }

    logout(): void {
        localStorage.clear();
        this.router.navigate(["cantine/home"]).then();
    }
}
