import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../core-cantine.service";
import {Meal} from "../../sharedmodule/models/meal";
import {Observable, of} from "rxjs";

@Component({
  selector: 'app-meals',
  templateUrl:"meals.componenet.html",
  styles: [],
  providers: [CoreCantineService]
})
export class MealsComponent  implements  OnInit{

  meals$ : Observable <Meal[]> = of([]) ;
  constructor( private  coreCantineService :CoreCantineService) {}

  ngOnInit(): void {
    this.meals$ = this.coreCantineService.getAllMeals();
  }
}
