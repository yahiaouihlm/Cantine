import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {CoreCantineService} from "../../../core-cantine/core-cantine.service";
import {Observable, of} from "rxjs";
import {Meal} from "../../../sharedmodule/models/meal";
import {MealsService} from "../meals.service";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";

@Component({
    selector: 'app-all-meals',
    templateUrl: './all-meals.component.html',
    styles: [],
    providers: [CoreCantineService]
})
export class AllMealsComponent implements OnInit {

    meals$: Observable<Meal[]> = of([]);

    constructor(private router: Router, private mealService: MealsService) {
    }

    ngOnInit(): void {
        Malfunctions.checkAdminConnectivity(this.router);
        this.meals$ = this.mealService.getAllMeals();
    }

    gotoNewMeal(): void {
        this.router.navigate(['/admin/meals/new']);
    }

    updateMeal(id: number): void {
        this.router.navigate(['/admin/meals/update', id]);
    }

    mealAvailableToString(mealAvailable: number): string {
        return mealAvailable === 1 ? 'Available' : 'Unavailable';
    }

}
