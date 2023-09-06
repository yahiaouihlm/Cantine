import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from "@angular/material/sidenav";
import {BreakpointObserver} from "@angular/cdk/layout";
import {AuthObject} from "../../sharedmodule/models/authObject";
import {Router} from "@angular/router";
import {SharedService} from "../../sharedmodule/shared.service";
import {User} from "../../sharedmodule/models/user";
import {HttpStatusCode} from "@angular/common/http";

@Component({
  selector: 'app-main-core-cantine',
  templateUrl: "./main-core-cantine.component.html",
  styleUrls:['../../../assets/styles/main.component.scss']
})
export class MainCoreCantineComponent  implements OnInit{
    disconnected = false;
    authObj :  AuthObject =  new AuthObject();
    user : User = new User();
    constructor (private  router : Router,   private sharedService: SharedService) {}

    ngOnInit(): void {
        let  authObj = localStorage.getItem('authObject');
        if (authObj) {
            this.disconnected = true;
            this.authObj = JSON.parse(authObj);
           this.getStudentById(this.authObj.id);
        }
        else {
            this.disconnected = false;
        }


    }

    getStudentById( id : string) {
     this.sharedService.getStudentById(id).subscribe( (response) => {
            this.user = response;
     });
    }
    goToHome() {
        this.router.navigate(['cantine/home']);
    }
    gotoProfile() {
        this.router.navigate(['cantine/student/profile'],  { queryParams: { id: this.authObj.id } });
    }
    logout() {
        localStorage.clear();
        this.disconnected = false;
        window.location.reload()
    }

    toLogin() {
       this.router.navigate(['cantine/signIn']);
    }
}
