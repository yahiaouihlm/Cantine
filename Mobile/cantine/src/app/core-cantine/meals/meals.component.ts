import {Component, OnInit} from '@angular/core';
import {connection} from "../../shared-module/functions/connection";
import {IConstantsURL} from "../../shared-module/constants/IConstantsURL";
import {Router} from "@angular/router";
import {Observable, of} from "rxjs";
import {Meal} from "../../shared-module/models/meals";
import {CoreCantineService} from "../core-cantine.service";

@Component({
  selector: 'app-meals',
  templateUrl: './meals.component.html',
  styleUrls: ['./meals.component.scss'],
  providers:[CoreCantineService]
})
export class MealsComponent implements OnInit {

  meals$: Observable<Meal[]> = of([]);
  constructor(private router: Router, private coreCantineService : CoreCantineService) {
  }


  ngOnInit() {
    if (!connection.checkStudentConnection()) {
      this.router.navigate([IConstantsURL.STUDENT_SIGN_IN]).then()
      localStorage.clear();
    }
    this.meals$ = this.coreCantineService.getAllAvailableMeals();

  }

}
