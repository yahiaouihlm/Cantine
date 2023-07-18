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
    chosenMealsIDs : number[] = [];
    submitted = false;
    constructor(private   coreCantineService : CoreCantineService , private dialogRef: MatDialogRef<ListMealsComponent>) {
    }

    ngOnInit(): void {
        this.meals$ = this.coreCantineService.getAllMeals();

    }


    addToListToLinkMealToMenu(mealID : number) {
       this.chosenMealsIDs.push(mealID);
    }


    resetMealsList()  : void{
        this.chosenMealsIDs = [];
        this.submitted = false;
    }

    validate() :  void  {
        this.submitted = true;
        if  (this.chosenMealsIDs.length > 2)
            this.closeDialog();
    }



    closeDialog() {
        this.dialogRef.close(this.chosenMealsIDs);
    }

    mealAvailableToString(mealAvailable : number): string {
    return mealAvailable === 1 ? 'Available' : 'Unavailable';
  }

}
