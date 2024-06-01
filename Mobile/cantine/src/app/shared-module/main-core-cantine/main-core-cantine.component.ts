import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {IConstantsURL} from "../constants/IConstantsURL";
import {log} from "@angular-devkit/build-angular/src/builders/ssr-dev-server";

@Component({
  selector: 'app-main-core-cantine',
  templateUrl: './main-core-cantine.component.html',
  styleUrls: ['./main-core-cantine.component.scss'],
})
export class MainCoreCantineComponent {

  constructor(private router :  Router) { }


  goToMeals() {
    this.router.navigate([IConstantsURL.MEALS_URL]).then();
  }
}
