import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['../../../assets/styles/home.component.scss']
})
export class HomeComponent  {
      
      constructor(private router :  Router  ) {}

    goToMeals() {
        this.router.navigate(['cantine/meals']);
    }

    goToMenus() {
        this.router.navigate(['cantine/meals']);
    }
}
