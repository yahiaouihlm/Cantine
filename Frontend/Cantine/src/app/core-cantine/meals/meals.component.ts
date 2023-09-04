import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../core-cantine.service";
import {Meal} from "../../sharedmodule/models/meal";
import {Observable, of} from "rxjs";
import {Router} from "@angular/router";

@Component({
  selector: 'app-meals',
  templateUrl:"meals.componenet.html",
  styles: [],
  providers: [CoreCantineService]
})
export class MealsComponent  implements  OnInit{

  meals$ : Observable <Meal[]> = of([]) ;
  constructor( private  coreCantineService :CoreCantineService ,  private router :  Router ) {}

  ngOnInit(): void {
    this.meals$ = this.coreCantineService.getAllMeals();
  }

  addToOrder() {
    const  authObject = localStorage.getItem('authObject');
    if (authObject) {
      console.log("it  works");
    }
    else {
        this.router.navigate(['cantine/signIn']);
    }
  }

  goback() {
    this.router.navigate(['cantine/home'])
  }
}
