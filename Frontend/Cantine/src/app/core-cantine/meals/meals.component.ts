import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../core-cantine.service";
import {Meal} from "../../sharedmodule/models/meal";
import {Observable, of} from "rxjs";
import {Router} from "@angular/router";
import {Order} from "../../sharedmodule/models/order";

@Component({
  selector: 'app-meals',
  templateUrl:"meals.componenet.html",
  styles: [],
  providers: [CoreCantineService]
})
export class MealsComponent  implements  OnInit{

  order! : Order;
  meals$ : Observable <Meal[]> = of([]) ;
  constructor( private  coreCantineService :CoreCantineService ,  private router :  Router ) {}

  ngOnInit(): void {
    this.meals$ = this.coreCantineService.getAllMeals();
  }

  addToOrder( idMeal :  number) {
    const  authObject = localStorage.getItem('authObject');
    if (authObject) {
      Order.addMealToOrder(idMeal);
    }
    else {
        localStorage.clear();
        this.router.navigate(['cantine/signIn']).then(r => console.log("it  works"));
    }
  }

  goback() {
    this.router.navigate(['cantine/home'])
  }
}
