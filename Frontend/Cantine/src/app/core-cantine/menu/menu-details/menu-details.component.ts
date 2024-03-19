import {Component, OnInit} from '@angular/core';
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {Order} from "../../../sharedmodule/models/order";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-menu-details',
    template: `
        <p>
            menu-details works!
        </p>
    `,
    styles: []
})
export class MenuDetailsComponent implements OnInit {

    constructor(private router: Router, private route: ActivatedRoute) {
    }

    private mealID: string = "";

    ngOnInit(): void {
        const userId = Malfunctions.getUserIdFromLocalStorage();
        if (userId != null && userId !== "") {
            this.route.queryParams.subscribe({
                    next: (params) => {
                      this.mealID = params['mealID'];
                      console.log(this.mealID);
                    },
                    error: (error) => {
                      this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
                    }
                }
            );
        } else {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }
    }

}
