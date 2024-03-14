import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-home-admin',
    templateUrl: './home-admin.component.html',
    styles: []
})
export class HomeAdminComponent {

    constructor(private router: Router) {
    }

    /** TODO : make the images in  the  html home-admin.component.html with the same size */

    gotoMeals(): void {
        this.router.navigate([IConstantsURL.ADMIN_MEALS_URL]).then(window.location.reload);
    }

    gotoMenus() {
        this.router.navigate([IConstantsURL.ADMIN_MENUS_URL]).then(window.location.reload);
    }
}
