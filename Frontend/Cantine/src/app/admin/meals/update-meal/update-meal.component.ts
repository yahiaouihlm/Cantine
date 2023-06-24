import { Component } from '@angular/core';
import {Meal} from "../../../sharedmodule/models/meal";
import {MealServiceService} from "../meal-service.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-update-meal',
  templateUrl: './update-meal.component.html',
  styles: [],
  providers: [MealServiceService]
})
export class UpdateMealComponent {

  meal!  : Meal ;
  constructor( private mealServiceService : MealServiceService , private  router  :  Router ) { }





}
