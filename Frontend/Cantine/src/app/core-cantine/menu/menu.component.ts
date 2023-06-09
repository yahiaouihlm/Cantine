import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../core-cantine.service";
import {Observable, of} from "rxjs";
import {Menu} from "../../sharedmodule/models/menu";

@Component({
  selector: 'app-menu',
  templateUrl: "menu.component.html",
  styles: [],
  providers: [CoreCantineService]
})
export class MenuComponent implements OnInit{

  menus$ : Observable<Menu[]> =  of([]);
   constructor(private   coreCantineService  : CoreCantineService) { }

    ngOnInit(): void {
      this.menus$ = this.coreCantineService.getAllMenus();
    }
}
