import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from "@angular/material/sidenav";
import {BreakpointObserver} from "@angular/cdk/layout";
import {AuthObject} from "../../sharedmodule/models/authObject";
import {Router} from "@angular/router";
import {SharedService} from "../../sharedmodule/shared.service";
import {User} from "../../sharedmodule/models/user";

@Component({
  selector: 'app-main-core-cantine',
  templateUrl: "./main-core-cantine.component.html",
  styleUrls:['../../../assets/styles/main.component.scss']
})
export class MainCoreCantineComponent  implements OnInit{
    isconnected = false;
    authObj :  AuthObject = new AuthObject();
    user : User = new User();
    constructor (private  router : Router,   private sharedService: SharedService) {}

    ngOnInit(): void {
        let  authObj = localStorage.getItem('authObject');
        if (authObj) {
            this.isconnected = true;
            this.authObj = JSON.parse(authObj);
            this.getStudentById();
        }
        else {
            this.isconnected = false;
        }


    }

    getStudentById() {
     this.sharedService.getStudentById(this.authObj.id).subscribe((response) => {
         this.user = response;
     });
    }
    goToHome() {
        this.router.navigate(['cantine/home']);
    }
    gotoProfile() {
        this.router.navigate(['cantine/student/profile']);
    }
    logout() {
        localStorage.clear();
        this.isconnected = false;
        window.location.reload()
    }

    toLogin() {
        console.log(" Hello world");
       this.router.navigate(['cantine/signIn']);
    }
}
