import {Component, OnInit} from '@angular/core';
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {Order} from "../../../sharedmodule/models/order";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";
import {ActivatedRoute, Router} from "@angular/router";
import {CoreCantineService} from "../../core-cantine.service";
import {Observable, of} from "rxjs";
import {Menu} from "../../../sharedmodule/models/menu";

@Component({
    selector: 'app-menu-details',
    templateUrl: './menu-details.component.html',
    styles: [],
    providers: [CoreCantineService]
})
export class MenuDetailsComponent implements OnInit {
    menus$ : Observable<Menu[]> = of([]);

    constructor(private router: Router, private route: ActivatedRoute,  private coreCantineService: CoreCantineService) {
    }

    private mealLabel: string = "";

    ngOnInit(): void {

        this.route.queryParams.subscribe({
                next: (params) => {
                    this.mealLabel = params['mealLabel'];
                    this.menus$ = this.coreCantineService.getMenusByLabel(this.mealLabel);
                    console.log(this.menus$);
                },
                error: (error) => {
                    this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
                }
            }
        );
    }


    addMenuToOrder(menu: Menu) {
        const userId = Malfunctions.getUserIdFromLocalStorage();
        if (userId != null && userId !== "") {

            const element = document.getElementById(menu.id) as HTMLButtonElement;
            if (element != null) {
                element.textContent = "Menu ajouté";
                element.disabled = true;
            }
            (function () {
                setTimeout(function () {
                    if (element != null) {
                        element.textContent = "Ajouter  Le  Menu ";
                        element.style.color = "white";
                        element.disabled = false;
                    }
                }, 2000); // 3000 millisecondes = 3 secondes
            })();

            Order.addMenuToOrder(menu);
        } else {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }

    }
}
