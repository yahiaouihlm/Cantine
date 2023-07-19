import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../../../core-cantine/core-cantine.service";
import {Observable, of} from "rxjs";
import {Meal} from "../../../sharedmodule/models/meal";
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-list-meals',
  templateUrl: './list-meals.component.html',
  styles: [],
  providers: [CoreCantineService]
})
export class ListMealsComponent implements  OnInit{

    meals$  :  Observable <Meal[]>  =  of([]);
    chosenMeals : Meal[] = [];
    submitted = false;
    constructor(private   coreCantineService : CoreCantineService , private dialogRef: MatDialogRef<ListMealsComponent>) {
    }

    ngOnInit(): void {
        this.meals$ = this.coreCantineService.getAllMeals();

    }

    isInMealsList(meal : Meal) : boolean {
        return !!this.chosenMeals.find(pmeal => pmeal.id === meal.id);
    }

    addToListToLinkMealToMenu(meal : Meal) {
       this.chosenMeals.push(meal);
    }


    resetMealsList()  : void{
        this.chosenMeals = [];
        this.submitted = false;
    }

    validate() :  void  {
        this.submitted = true;
        if  (this.chosenMeals.length >= 2)
            this.closeDialog();
    }



    closeDialog() {
        this.dialogRef.close(this.chosenMeals);
    }

    mealAvailableToString(mealAvailable : number): string {
    return mealAvailable === 1 ? 'Available' : 'Unavailable';
  }

}
