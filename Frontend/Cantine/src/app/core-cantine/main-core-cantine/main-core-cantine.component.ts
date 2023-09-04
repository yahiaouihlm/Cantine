import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from "@angular/material/sidenav";
import {BreakpointObserver} from "@angular/cdk/layout";
import {AuthObject} from "../../sharedmodule/models/authObject";

@Component({
  selector: 'app-main-core-cantine',
  templateUrl: "./main-core-cantine.component.html",
  styleUrls:['../../../assets/styles/main.component.scss']
})
export class MainCoreCantineComponent  implements OnInit{
    isconnected = false;
    authObj :  AuthObject = new AuthObject();
    constructor () {}

    ngOnInit(): void {
        let  authObj = localStorage.getItem('authObject');
        if (authObj) {
            this.isconnected = true;
            this.authObj = JSON.parse(authObj);
        }
        else {
            this.isconnected = false;
        }


    }




}
