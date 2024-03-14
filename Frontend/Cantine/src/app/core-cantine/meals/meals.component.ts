import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../core-cantine.service";
import {Meal} from "../../sharedmodule/models/meal";
import {Observable, of} from "rxjs";
import {Router} from "@angular/router";
import {Order} from "../../sharedmodule/models/order";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";

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
    this.meals$ = this.coreCantineService.getAllAvailableMeals();
  }

  addToOrder(   meal:  Meal) {
    const  authObject = localStorage.getItem('authObject');
    if (authObject) {
      Order.addMealToOrder(meal);
    }
    else {
        localStorage.clear();
        this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(r => console.log("it  works"));
    }
  }

  goback() {
    this.router.navigate(['cantine/home'])
  }
}
