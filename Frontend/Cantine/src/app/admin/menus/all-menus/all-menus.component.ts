import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../../../core-cantine/core-cantine.service";
import {Observable, of} from "rxjs";
import {Menu} from "../../../sharedmodule/models/menu";
import {Router} from "@angular/router";

@Component({
  selector: 'app-all-menus',
  templateUrl: './all-menus.component.html',
  styles: [],
  providers: [CoreCantineService]
})
export class AllMenusComponent implements  OnInit{

  constructor(private  coreCantineService : CoreCantineService , private  router  :   Router) { }
  menus$  :  Observable <Menu[]>  =  of([]);
  ngOnInit(): void {
    this.menus$ = this.coreCantineService.getAllMenus();
  }





  updateMenu(id :  number) {
    this.router.navigate(['/admin/menus/update', id]);
  }


}
