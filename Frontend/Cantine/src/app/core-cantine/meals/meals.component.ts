import {Component, ElementRef, OnInit} from '@angular/core';
import {CoreCantineService} from "../core-cantine.service";
import {Meal} from "../../sharedmodule/models/meal";
import {Observable, of} from "rxjs";
import {Router} from "@angular/router";
import {Order} from "../../sharedmodule/models/order";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";
import Malfunctions from 'src/app/sharedmodule/functions/malfunctions';

@Component({
    selector: 'app-meals',
    templateUrl: "meals.componenet.html",
    styles: [],
    providers: [CoreCantineService]
})
export class MealsComponent implements OnInit {

    order!: Order;
    optionsOfMeals: string[] = ['TOUS  LES  PLATS', 'ENTREE', 'PLAT', 'DESSERT', 'BOISSON', 'ACCOMPAGNEMENT', 'AUTRE'];
    selectedOption: string = 'TOUS  LES  PLATS'; // Pour stocker l'option sélectionnée
    meals$: Observable<Meal[]> = of([]);

    constructor(private coreCantineService: CoreCantineService, private router: Router, private elementRef: ElementRef<HTMLElement>) {
    }

    ngOnInit(): void {
        this.meals$ = this.coreCantineService.getAllAvailableMeals();
    }

    addToOrder(meal: Meal) {
        const userId = Malfunctions.getUserIdFromLocalStorage();
        if (userId != null && userId !== "") {

            const element = document.getElementById(meal.uuid) as HTMLButtonElement;
            if (element != null) {
                element.textContent = "plat Ajouté";
                element.disabled = true;
            }

            (function () {
                setTimeout(function () {
                    if (element != null) {
                        element.textContent = "Ajouter  ce plat ";
                        element.style.color = "white";
                        element.disabled = false;
                    }
                }, 2000); // 3000 millisecondes = 3 secondes
            })();


            Order.addMealToOrder(meal);
        } else {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }

    }

    goBack() {
        this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
    }

    validate() {
        if (this.selectedOption === this.optionsOfMeals[0]) {
            this.meals$ = this.coreCantineService.getAllAvailableMeals();

        } else {
            this.meals$ = this.coreCantineService.getMealsByType(this.selectedOption);
        }
    }


}
