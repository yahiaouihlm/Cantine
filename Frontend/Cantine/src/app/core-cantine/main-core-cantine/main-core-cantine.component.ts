import {Component, OnInit} from '@angular/core';
import {AuthObject} from "../../sharedmodule/models/authObject";
import {Router} from "@angular/router";
import {SharedService} from "../../sharedmodule/shared.service";
import {User} from "../../sharedmodule/models/user";
import Malfunctions from 'src/app/sharedmodule/functions/malfunctions';
import {IConstantsURL} from 'src/app/sharedmodule/constants/IConstantsURL';


@Component({
    selector: 'app-main-core-cantine',
    templateUrl: "./main-core-cantine.component.html",
    styleUrls: ['../../../assets/styles/main.component.scss'],
    providers: [SharedService]
})
export class MainCoreCantineComponent implements OnInit {
    isConnected = false;
    authObj: AuthObject = new AuthObject();
    user: User = new User();

    constructor(private router: Router, private sharedService: SharedService) {
    }

    ngOnInit(): void {

        let userid = Malfunctions.getUserIdFromLocalStorage();
        if (userid != null && userid != "") {
            this.sharedService.getStudentById(userid).subscribe((response) => {
                this.user = response;
                this.isConnected = true;
            });

        }


    }


    goToOrders() {
        this.router.navigate(['cantine/student/orders']).then(() => window.location.reload());
    }

    goToHome() {
        this.router.navigate([IConstantsURL.HOME_URL]).then(() => window.location.reload());
    }

    gotoProfile() {
        this.router.navigate(['cantine/student/profile'], {queryParams: {id: this.authObj.id}});
    }

    logout() {
        localStorage.clear();
        this.isConnected = false;
        this.router.navigate([IConstantsURL.HOME_URL]).then(() => window.location.reload());
    }

    toLogin() {
        localStorage.clear();
        this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(() => window.location.reload());
    }

    goToMeals() {
        this.router.navigate([IConstantsURL.MEALS_URL]).then(() => window.location.reload());
    }

    goToMenus() {
        this.router.navigate([IConstantsURL.MENUS_URL]).then(() => window.location.reload());
    }
}
