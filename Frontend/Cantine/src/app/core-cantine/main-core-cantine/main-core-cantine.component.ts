import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {SharedService} from "../../sharedmodule/shared.service";
import {User} from "../../sharedmodule/models/user";
import Malfunctions from 'src/app/sharedmodule/functions/malfunctions';
import {IConstantsURL} from 'src/app/sharedmodule/constants/IConstantsURL';
import {Order} from "../../sharedmodule/models/order";


@Component({
    selector: 'app-main-core-cantine',
    templateUrl: "./main-core-cantine.component.html",
    styleUrls: ['../../../assets/styles/main.component.scss'],
    providers: [SharedService]
})
export class MainCoreCantineComponent implements OnInit {
    isConnected = false;
    user: User = new User();
    nbOfMealsAndMenus = 0;

    constructor(private router: Router, private sharedService: SharedService) {
    }

    ngOnInit(): void {

        let userid = Malfunctions.getUserIdFromLocalStorage();
        if (userid != null && userid != "") {
            this.sharedService.getStudentById(userid).subscribe((response) => {
                this.user = response;
                this.isConnected = true;
                let order = Order.getOrderFromLocalStorage();
                if (order != null) {
                    this.nbOfMealsAndMenus = order.meals.length + order.menus.length;
                } else {
                    this.nbOfMealsAndMenus = 0;
                }
            });

        }


    }


    goToOrders() {
        this.router.navigate([IConstantsURL.STUDENT_ORDER]).then(() => window.location.reload());
    }

    goToHome() {
        this.router.navigate([IConstantsURL.HOME_URL]).then(() => window.location.reload());
    }

    gotoProfile() {
        this.router.navigate([IConstantsURL.STUDENT_PROFILE_URL],
            {queryParams: {id: this.user.uuid}}).then(window.location.reload);
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
