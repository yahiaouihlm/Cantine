import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../core-cantine.service";
import {Observable, of} from "rxjs";
import {Menu} from "../../sharedmodule/models/menu";
import {Router} from "@angular/router";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {SharedService} from "../../sharedmodule/shared.service";
import {Order} from "../../sharedmodule/models/order";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-menu',
    templateUrl: "menu.component.html",
    styles: [],
    providers: [CoreCantineService]
})
export class MenuComponent implements OnInit {

    menus$: Observable<Menu[]> = of([]);

    constructor(private coreCantineService: CoreCantineService, private router: Router, private sharesService: SharedService) {
    }

    ngOnInit(): void {
        this.menus$ = this.coreCantineService.getAllMenus();

    }


    async addToOrder(menu: Menu) {
        const userIId = Malfunctions.getUserIdFromLocalStorage();
        if (userIId != null && userIId !== "") {

            const element = document.getElementById(menu.uuid) as HTMLButtonElement;
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


    goBack() {
        this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
    }
}
