import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-all-meals',
  templateUrl: './all-meals.component.html',
  styles: [
  ]
})
export class AllMealsComponent {

    constructor(private  router : Router) { }
  gotoNewMeal(): void {
     this.router.navigate(['/admin/meals/new']);
  }
}
