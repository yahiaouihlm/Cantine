import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../core-cantine.service";
import {Observable, of} from "rxjs";
import {Menu} from "../../sharedmodule/models/menu";
import {Router} from "@angular/router";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {SharedService} from "../../sharedmodule/shared.service";
import {Order} from "../../sharedmodule/models/order";

@Component({
  selector: 'app-menu',
  templateUrl: "menu.component.html",
  styles: [],
  providers: [CoreCantineService]
})
export class MenuComponent implements OnInit{

  menus$ : Observable<Menu[]> =  of([]);
   constructor(private   coreCantineService  : CoreCantineService,  private router : Router , private  sharesService : SharedService) { }

    ngOnInit(): void {
      this.menus$ = this.coreCantineService.getAllMenus();
    }


   async addToOrder(  menu :  Menu ) {
       console.log("add to order")
       await Malfunctions.checkStudentConnectivity(this.router, this.sharesService);
       console.log("after check connectivity")
       Order.addMenuToOrder(menu);
    }


    goback() {
      this.router.navigate(['cantine/home'])
    }
}
