import { Component, OnInit } from '@angular/core';
import {Observable, of} from "rxjs";
import {Meal} from "../../shared-module/models/meal";
import {Router} from "@angular/router";
import {CoreCantineService} from "../core-cantine.service";

@Component({
  selector: 'app-menus',
  templateUrl: './menus.component.html',
  styleUrls: ['./menus.component.scss'],
  providers : [CoreCantineService]
})
export class MenusComponent  implements OnInit {


  meals$: Observable<Meal[]> = of([]);
  constructor(private router: Router, private coreCantineService : CoreCantineService) {
  }

  ngOnInit() {}

}
