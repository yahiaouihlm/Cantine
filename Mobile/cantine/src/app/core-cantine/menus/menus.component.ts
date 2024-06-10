import { Component, OnInit } from '@angular/core';
import {Observable, of} from "rxjs";
import {Meal} from "../../shared-module/models/meal";
import {Router} from "@angular/router";
import {CoreCantineService} from "../core-cantine.service";
import {connection} from "../../shared-module/functions/connection";
import {IConstantsURL} from "../../shared-module/constants/IConstantsURL";
import {Menu} from "../../shared-module/models/menu";

@Component({
  selector: 'app-menus',
  templateUrl: './menus.component.html',
  styleUrls: ['./menus.component.scss'],
  providers : [CoreCantineService]
})
export class MenusComponent  implements OnInit {


  menus$: Observable<Menu[]> = of([]);
  constructor(private router: Router, private coreCantineService : CoreCantineService) {
  }

  ngOnInit() {
    if (!connection.checkStudentConnection()) {
      this.router.navigate([IConstantsURL.STUDENT_SIGN_IN]).then()
      localStorage.clear();
    }
    this.menus$ = this.coreCantineService.getAllAvailableMenus();

  }

}
