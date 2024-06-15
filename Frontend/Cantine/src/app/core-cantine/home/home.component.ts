import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['../../../assets/styles/home.component.scss']
})
export class HomeComponent {

    constructor(private router: Router) {
    }


    goToMeals() {
        this.router.navigate([IConstantsURL.MEALS_URL]).then(() => {
            window.location.reload();
        });
    }

    goToMenus() {
        this.router.navigate([IConstantsURL.MENUS_URL]).then(() => {
            window.location.reload();
        });
    }
}
