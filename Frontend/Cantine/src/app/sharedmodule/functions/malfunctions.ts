import {AuthObject} from "../models/authObject";
import {Router} from "@angular/router";
import {SharedService} from "../shared.service";
import {HttpStatusCode} from "@angular/common/http";
import {ExceptionDialogComponent} from "../dialogs/exception-dialog/exception-dialog.component";
import {MatDialog} from "@angular/material/dialog";

export  default class Malfunctions {



    public static checkStudentConnectivity(router: Router ,  sharedService : SharedService)  {
        let  interdiction  = () => {
            localStorage.clear();
            router.navigate(['cantine/signIn']).then(r =>
                window.location.reload());
        }

        let authObj = localStorage.getItem('authObject');
        if (!authObj) {
            interdiction();
            return
        }
        else  {
            let authObject = JSON.parse(authObj) as AuthObject;
            let  studentId = authObject.id
           let result =   sharedService.getStudentById(studentId).subscribe({
                next: (response) => {
                    return true;
                },
                error: (error) => {
                    interdiction();
                }
            });

        }


    }
    public  static getTokenFromLocalStorage() :  string {
        let authObj = localStorage.getItem('authObject')
        if (!authObj) {
            return '';
        }
        let authObject = JSON.parse(authObj) as AuthObject;
        return authObject.Authorization;
    }


    public  static  getStudentIdFromLocalStorage() :  string {
        let authObj = localStorage.getItem('authObject')
        if (!authObj) {
            return '';
        }
        let authObject = JSON.parse(authObj) as AuthObject;
        return authObject.id;
    }

    /**
     *    @doc this function  is used to  get  the  current  date  in  the  format  yyyy-mm-dd
     */
    public static   getCurrentDate() : string {
        const  date = new Date();
        let   getMonth = date.toLocaleString("default", { month: "2-digit" });
        let   getDay = date.toLocaleString("default", { day: "2-digit" })
        let   getYear = date.toLocaleString("default", { year: "numeric" });
        return   getYear + "-" + getMonth + "-" + getDay;
    }
}