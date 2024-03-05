import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {CoreCantineService} from "../../../core-cantine/core-cantine.service";
import {Observable, of} from "rxjs";
import {Meal} from "../../../sharedmodule/models/meal";
import {MealsService} from "../meals.service";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-all-meals',
    templateUrl: './all-meals.component.html',
    styles: [],
    providers: [CoreCantineService]
})
export class AllMealsComponent implements OnInit {
    /** TODO :  revoir  le reponsive avec plusieur  plat */
    meals$: Observable<Meal[]> = of([]);

    constructor(private router: Router, private mealService: MealsService) {
    }

    ngOnInit(): void {
        if  (Malfunctions.checkAdminConnectivityAndMakeRedirection(this.router)){
            this.meals$ =this.mealService.getAllMeals();
        }
    }

    gotoNewMeal(): void {
        this.router.navigate([IConstantsURL.ADMIN_NEW_MEAL_URL]).then(r => window.location.reload());
    }

    updateMeal(id: string): void {
        console.log("update meal  id  : ", id)
        this.router.navigate([IConstantsURL.ADMIN_UPDATE_MEAL_URL, id]).then(r => window.location.reload());
    }

    mealAvailableToString(mealAvailable: number): string {
        return mealAvailable === 1 ? 'Available' : 'Unavailable';
    }

}
