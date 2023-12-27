import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {IConstantsCoreCantine} from "../IConstantsCoreCantine";
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['../../../assets/styles/home.component.scss']
})
export class HomeComponent  {

      private  MEAL_URL = "cantine/meals";
      private MENU_URL = "cantine/menus";
      constructor(private router :  Router  ) {}



    goToMeals() {
        this.router.navigate([this.MEAL_URL]).then(() => {
            window.location.reload();
        });
    }

    goToMenus() {
        this.router.navigate([this.MENU_URL]).then(() => {
            window.location.reload();
        });
    }
}
